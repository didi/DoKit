import 'package:flutter/material.dart';

import 'leaks_doctor.dart';

mixin StateLeaksDoctorMixin<T extends StatefulWidget> on State<T> {
  int get checkLeakDelay => 500;
  String get watchGroup => hashCode.toString()+'mixin';

  @override
  @mustCallSuper
  void initState() {
    super.initState();
    assert(() {
      LeaksDoctor().addObserved(this, group: watchGroup);
      LeaksDoctor().addObserved(context, group: watchGroup);
      
      return true;
    }());
  }

  @override
  @mustCallSuper
  void dispose() {
    super.dispose();
    assert(() {
      //start check
      LeaksDoctor().memoryLeakScan(group: watchGroup, delay: checkLeakDelay);
      return true;
    }());
  }
}