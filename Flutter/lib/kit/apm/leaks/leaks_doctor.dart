// ignore_for_file: omit_local_variable_types

import 'dart:async';
import 'dart:ffi';

import 'package:dokit/kit/apm/vm/vm_service_toolset.dart';
import 'package:flutter/material.dart';

import 'leaks_doctor_controller.dart';
import 'leaks_doctor_data.dart';

import 'leaks_doctor_event.dart';
import 'leaks_doctor_widget.dart';

typedef GetBuildContext = BuildContext Function();

///泄漏检测
class LeaksDoctor {
  static LeaksDoctor? _instance;

  GetBuildContext? getBuildContext;

  final LeaksDoctorController  _leakController = LeaksDoctorController();

  // Expando缓存 : 通过navigator动态的添加
  // manual
  final Map<String, Expando> _dynamicWatchGroup = {};

  Stream<LeaksMsgInfo?> get onLeakedStream => _leakController.onLeakedStream;

  Stream<LeaksDoctorEvent> get onEventStream => _leakController.onEventStream;

  bool isRunning = false;

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
        // BuildContext context = getBuildContext!();
        // TODO 暂时关掉，这块需要优化
        // Navigator.push(
        //   context,
        //   MaterialPageRoute(
        //       builder: (context) => LeaksDoctorPage(
        //             title: '泄漏结果',
        //           )),
        // );
      }
    }
  }

  void showLeaksPageWhenClick() {
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

  void init(BuildContext Function()? func, {int maxRetainingPathLimit = 300}) {
    getBuildContext = func;
    _leakController.maxRetainingPathLimit = maxRetainingPathLimit;
    onEventStream.listen((LeaksDoctorEvent event) {
      if (event.type == LeaksDoctorEventType.AddObject || event.type == LeaksDoctorEventType.AllEnd) {
        isRunning = false;
      } else {
        isRunning = true;
      }
      if (!isEmpty()) {
        showLeaksPage(event);
      }
    });
  }

  // 开始检测是否存在内存泄漏
  // 使用Timer是为了延时检测，有些state会在页面退出之后延迟释放，这并不表示就一定是内存泄漏。
  // 比如runZone就会延时释放
  void memoryLeakScan({String? group, int delay = 0}) async {
    if (isRunning == true) {
      print('LeaksDoctor is running');
      return;
    }
    bool isNotEmpty = _dynamicWatchGroup.isNotEmpty;
    if (group == null && isNotEmpty) {
      Map<String, Expando> tmpMap = Map.from(_dynamicWatchGroup);
      _dynamicWatchGroup.clear();
      tmpMap.forEach((key, expando) {
          _leakController.addTask(expando);
        });
    } else if (isNotEmpty) {
      Expando? expando = _dynamicWatchGroup[group];
      _dynamicWatchGroup.remove(group);
      if (expando != null) {
        _leakController.addTask(expando);
      }
    }
    _leakController.runTask();
  }

  // [group] 认为可以在一块释放的对象组
  // 如果缺省,默认值’manual‘
  // [expectedTotalCount] 期望被观察对象的创建的实例个数
  void addObserved(Object obj,
      {String group = 'manual', int? expectedTotalCount, String? className}) {
    if (_objHasAdded(group, obj) == true) return ;
    _savePolicy(obj, expectedTotalCount, className);

    if ((obj is num) ||
        (obj is bool) ||
        (obj is String) ||
        (obj is Struct) ||
        (obj is Pointer)) {
      throw ArgumentError.value(obj,
          'Expandos 不允许用于下类型: strings, numbers, booleans, null, Pointers, Structs or Unions.');
    }

    _leakController.postLeaksEvent(LeaksDoctorEvent(LeaksDoctorEventType.AddObject, data: group));

    String key = group;
    Expando? expando = _dynamicWatchGroup[key];
    expando ??= Expando('LeakDoctor-$key');
    expando[obj] = true;
    _dynamicWatchGroup[key] = expando;
  }

  bool _objHasAdded(String key, Object obj) {
    Expando? expando = _dynamicWatchGroup[key];
    if (expando != null) {
      final ret = expando[obj];
      if (ret != null && ret==true) {
        return true;
      }
    }
    return false;
  }

  // 存储自定义的策略
  void _savePolicy(Object obj, int? expectedTotalCount, String? className) {
    if (expectedTotalCount != null) {
      if (className != null) {
        _leakController.savePolicy(className, expectedTotalCount);
      } else {
        VmserviceToolset().getInstanceByObject(obj).then((value) {
          final clsName = value!.classRef!.name;
          if (clsName != null) {
            _leakController.savePolicy(clsName, expectedTotalCount);
          }
        });
      }
    }
  }
}
