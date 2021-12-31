// ignore_for_file: omit_local_variable_types

import 'dart:async';
import 'dart:collection';
import 'dart:ffi';

import 'package:dokit/kit/apm/vm/vm_service_toolset.dart';
import 'package:flutter/material.dart';

import 'leaks_doctor_data.dart';

import 'leaks_doctor_task.dart';
import 'leaks_doctor_widget.dart';

typedef GetBuildContext = BuildContext Function();

///泄漏检测
class LeaksDoctor {
  static LeaksDoctor? _instance;

  static int? maxRetainingPathLimit;

  GetBuildContext? getBuildContext;

  // Expando缓存 : 通过navigator动态的添加
  // manual
  final Map<String, Expando> _dynamicWatchGroup = {};

  // 策略池 存储期望类对象实例个数
  final Map<String, int> _policyCachePool = {};

  // Queue 检测内存泄漏，先进先出
  final Queue<LeaksDoctorTask> _checkTaskQueue = Queue();

  // memory leak监听控制器
  final StreamController<LeaksMsgInfo?> _onLeakedStreamCtrl =
      StreamController.broadcast();
  final StreamController<LeaksDoctorEvent> _onEventStreamCtrl =
      StreamController.broadcast();

  LeaksDoctorTask? _currentTask;

  Stream<LeaksMsgInfo?> get onLeakedStream => _onLeakedStreamCtrl.stream;

  Stream<LeaksDoctorEvent> get onEventStream => _onEventStreamCtrl.stream;

  factory LeaksDoctor() {
    _instance ??= LeaksDoctor._();
    return _instance!;
  }

  LeaksDoctor._() {
    VmserviceToolset().getVMService();
  }

  // 线上泄漏详情页
  void showLeaksPage(LeaksDoctorEvent event) {
    if (event.type == LeaksDoctorEventType.AllEnd) {
      if (getBuildContext != null) {
        BuildContext context = getBuildContext!();

        Navigator.push(
          context,
          MaterialPageRoute(
              builder: (context) => LeaksDoctorPage(
                    title: '泄漏结果',
                  )),
        );
      }
    }
  }

  // 泄漏信息数据池，用于为展示发现的泄漏对象提供数据源
  final List<LeaksMsgInfo> _leaksInfoStorePool = [];

  // 读取泄漏结果数据
  static List<LeaksMsgInfo> getAll() => LeaksDoctor()._leaksInfoStorePool;

  // 清空泄漏结果数据
  static void clear() => LeaksDoctor()._leaksInfoStorePool.clear();

  // 添加一条泄漏结果信息到数据池
  static void add(LeaksMsgInfo info) {
    LeaksDoctor()._leaksInfoStorePool.removeWhere((item) {
      return item.leaksClsName!.isNotEmpty &&
          (item.leaksClsName == info.leaksClsName);
    });
    LeaksDoctor()._leaksInfoStorePool.add(info);
  }

  // 泄漏结果信息到数据池为空？
  static bool isEmpty() => LeaksDoctor()._leaksInfoStorePool.isEmpty;

  // 查询设置的策略
  int? searchePolicy(String clsName) => _policyCachePool[clsName];

  // 存储策略
  void savePolicy(String clsName, int expectedTotalCount) =>
      _policyCachePool[clsName] = expectedTotalCount;

  void init(BuildContext Function() func, {int maxRetainingPathLimit = 300}) {
    LeaksDoctor.maxRetainingPathLimit = maxRetainingPathLimit;
    getBuildContext = func;

    onEventStream.listen((LeaksDoctorEvent event) {
      if (!isEmpty()) {
        showLeaksPage(event);
      }
    });
  }

  // 开始检测是否存在内存泄漏
  // 使用Timer是为了延时检测，有些state会在页面退出之后延迟释放，这并不表示就一定是内存泄漏。
  // 比如runZone就会延时释放
  void memoryLeakScan({String? group, int delay = 0}) async {
    if (group == null && _dynamicWatchGroup.isNotEmpty) {
      Timer(Duration(milliseconds: delay), () async {
        _dynamicWatchGroup.forEach((key, expando) {
          _addTask(expando);
        });
        _initTask();
      });
    } else {
      Expando? expando = _dynamicWatchGroup[group];
      _dynamicWatchGroup.remove(group);
      if (expando != null) {
        Timer(Duration(milliseconds: delay), () async {
          _addTask(expando);
          _initTask();
        });
      }
    }
  }

  // 添加任务
  void _addTask(Expando expando) {
    _checkTaskQueue.add(
      LeaksDoctorTask(expando,
          sink: _onEventStreamCtrl.sink,
          onCompleted: () {
            _currentTask = null;
            _initTask();
          },
          searchPolicy: (clsName) => searchePolicy(clsName),
          onLeaked: (leakInfo) {
            if (leakInfo != null) {
              LeaksDoctor.add(leakInfo);
            }
            _onLeakedStreamCtrl.add(leakInfo);
          },
          maxRetainingPathLimit: maxRetainingPathLimit),
    );
  }

  // 初始化task
  void _initTask() {
    if (_checkTaskQueue.isNotEmpty && _currentTask == null) {
      _currentTask = _checkTaskQueue.removeFirst();
      _currentTask?.start();
    }
  }

  // [group] 认为可以在一块释放的对象组
  // 如果缺省,默认值’manual‘
  // [expectedTotalCount] 期望被观察对象的创建的实例个数
  void addObserved(Object obj,
      {String group = 'manual', int? expectedTotalCount}) {
    if (LeaksDoctor.maxRetainingPathLimit == null) return;

    // 存储策略
    if (expectedTotalCount != null) {
      VmserviceToolset().getInstanceByObject(obj).then((value) {
        final clsName = value!.classRef!.name;
        if (clsName != null) {
          savePolicy(clsName, expectedTotalCount);
        }
      });
    }

    if ((obj is num) ||
        (obj is bool) ||
        (obj is String) ||
        (obj is Struct) ||
        (obj is Pointer)) {
      throw ArgumentError.value(obj,
          'Expandos 不允许用于下类型: strings, numbers, booleans, null, Pointers, Structs or Unions.');
    }

    _onEventStreamCtrl
        .add(LeaksDoctorEvent(LeaksDoctorEventType.AddObject, data: group));

    String key = group;
    Expando? expando = _dynamicWatchGroup[key];
    expando ??= Expando('LeakDoctor-$key');
    expando[obj] = true;
    _dynamicWatchGroup[key] = expando;
  }
}

// 事件节点
class LeaksDoctorEvent {
  final LeaksDoctorEventType type;
  final dynamic data;

  @override
  String toString() {
    return '$type, ${data ?? ''}';
  }

  LeaksDoctorEvent(this.type, {this.data});
}

enum LeaksDoctorEventType {
  AddObject,
  GcStart,
  GcEnd,
  AnalyzeStart,
  AnalyzeEnd,
  AllEnd
}
