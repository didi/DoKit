enum LeaksDoctorEventType {
  AddObject,
  GcStart,
  GcEnd,
  AnalyzeStart,
  AnalyzeEnd,
  AllEnd
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