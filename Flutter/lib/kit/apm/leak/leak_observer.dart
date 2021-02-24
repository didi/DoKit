import 'package:dokit/kit/apm/leak/leak_checker.dart';
import 'package:flutter/material.dart';

import '../fps_kit.dart';

class DoKitLeakObserver extends NavigatorObserver {
  static dynamic element;
  static dynamic test;

  @override
  void didPop(Route<dynamic> route, Route<dynamic> previousRoute) =>
      _doCheck(route, previousRoute);

  @override
  void didReplace({Route<dynamic> newRoute, Route<dynamic> oldRoute}) =>
      _doCheck(newRoute, oldRoute);

  @override
  void didRemove(Route<dynamic> route, Route<dynamic> previousRoute) =>
      _doCheck(route, previousRoute);

  Future<bool> get enableCheck => Future.value(true);

  void _doCheck(Route<dynamic> route, Route<dynamic> previousRoute) async {
    final enableCheck = await this.enableCheck;
    if (!enableCheck) return;
    dynamic obj = element;
    element = null;
    await leakCheck(route);
  }
}
