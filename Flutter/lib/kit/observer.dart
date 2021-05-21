import 'package:dokit/kit/apm/launch/route_observer.dart';
import 'package:flutter/material.dart';

/// Dokit对外的路由观察者
/// 向内转发各个路由事件
class DokitNavigatorObserver extends NavigatorObserver {
  List<NavigatorObserver> _observers = [LaunchObserver()];


  @override
  void didPush(Route route, Route? previousRoute) {
    super.didPush(route, previousRoute);
    _observers.forEach((element) {
      element.didPush(route, previousRoute);
    });
  }

  @override
  void didPop(Route route, Route? previousRoute) {
    super.didPop(route, previousRoute);
    _observers.forEach((element) {
      element.didPop(route, previousRoute);
    });
  }

  @override
  void didRemove(Route route, Route? previousRoute) {
    super.didRemove(route, previousRoute);
    _observers.forEach((element) {
      element.didRemove(route, previousRoute);
    });
  }

  @override
  void didReplace({Route? newRoute, Route? oldRoute}) {
    super.didReplace(newRoute: newRoute, oldRoute: oldRoute);
    _observers.forEach((element) {
      element.didReplace(newRoute: newRoute, oldRoute: oldRoute);
    });
  }

  @override
  void didStartUserGesture(Route route, Route? previousRoute) {
    super.didStartUserGesture(route, previousRoute);
    _observers.forEach((element) {
      element.didStartUserGesture(route, previousRoute);
    });
  }

  @override
  void didStopUserGesture() {
    super.didStopUserGesture();
    _observers.forEach((element) {
      element.didStopUserGesture();
    });
  }
}
