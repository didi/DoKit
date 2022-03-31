import 'dart:async';
import 'dart:collection';

import 'leaks_doctor_data.dart';
import 'leaks_doctor_event.dart';
import 'leaks_doctor_task.dart';

class LeaksDoctorController {
  int? maxRetainingPathLimit = 300;
  LeaksDoctorTask? _currentTask;

  // Queue 检测内存泄漏，先进先出
  final Queue<LeaksDoctorTask> _checkTaskQueue = Queue();

  // memory leak监听控制器
  final StreamController<LeaksMsgInfo?> _onLeakedStreamCtrl =
      StreamController.broadcast();
  final StreamController<LeaksDoctorEvent> _onEventStreamCtrl =
      StreamController.broadcast();

  // 策略池 存储期望类对象实例个数
  final Map<String, int> _policyCachePool = {};

  // 查询设置的策略
  int? _searchePolicy(String clsName) => _policyCachePool[clsName];

  // 存储策略
  void savePolicy(String clsName, int expectedTotalCount) =>
      _policyCachePool[clsName] = expectedTotalCount;

  
  Stream<LeaksMsgInfo?> get onLeakedStream => _onLeakedStreamCtrl.stream;
  Stream<LeaksDoctorEvent> get onEventStream => _onEventStreamCtrl.stream;

  // 添加任务
  void addTask(Expando expando) {
    _checkTaskQueue.add(
      LeaksDoctorTask(expando,
          sink: _onEventStreamCtrl.sink,
          onCompleted: () {
            _currentTask = null;
            runTask();
          },
          searchPolicy: (clsName) => _searchePolicy(clsName),
          onLeaked: (leakInfo) {
            // if (leakInfo != null) {
            //   LeaksDoctor.add(leakInfo);
            // }
            _onLeakedStreamCtrl.add(leakInfo);
          },
          maxRetainingPathLimit: maxRetainingPathLimit)
    );
  }

  // 初始化task
  void runTask() {
    if (_checkTaskQueue.isNotEmpty && _currentTask == null) {
      _currentTask = _checkTaskQueue.removeFirst();
      _currentTask?.start();
    }
  }

  void postLeaksEvent(LeaksDoctorEvent event) {
    _onEventStreamCtrl.add(event);
  }

}