import 'package:dokit/kit/apm/leak/leak_checker.dart';
import 'package:flutter/material.dart';


class DoKitLeakObserver extends NavigatorObserver {

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
    //TODO 需要找到正确的泄漏检测对象，合适的目标为持有_OverlayEntryWidgetState的element
    await leakCheck(route);
  }
}
