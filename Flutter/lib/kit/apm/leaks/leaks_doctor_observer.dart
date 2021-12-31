// ignore_for_file: always_declare_return_types

import 'package:flutter/widgets.dart';

import 'leaks_doctor.dart';

const int _defaultCheckLeakDelay = 500;
typedef ShouldAddedRoute = bool Function(Route route);

class LeaksDoctorObserver extends NavigatorObserver {
  final ShouldAddedRoute? shouldCheck;
  final int checkLeakDelay;

  LeaksDoctorObserver(
      {this.checkLeakDelay = _defaultCheckLeakDelay, this.shouldCheck});

  @override
  void didPop(Route route, Route? previousRoute) {
    _remove(route);
  }

  @override
  void didPush(Route route, Route? previousRoute) {
    _add(route);
  }

  @override
  void didRemove(Route route, Route? previousRoute) {
    _remove(route);
  }

  @override
  void didReplace({Route? newRoute, Route? oldRoute}) {
    if (newRoute != null) {
      _add(newRoute);
    }
    if (oldRoute != null) {
      _remove(oldRoute);
    }
  }

  void _add(Route route) {
    assert(() {
      if (route is ModalRoute &&
          (shouldCheck == null || shouldCheck!.call(route))) {
        route.didPush().then((_) {
          final element = _getElementByRoute(route);

          if (element != null) {
            if (_isHitWhiteList(element)) {
              return true;
            }
            final key = _getRouteKey(route, element.widget.toStringShort());

            // 人为制造泄漏
            // LeaksCache.cache.add(element.widget);

            addObserved(element, key); //Element
            addObserved(element.widget, key); //Widget
            if (element is StatefulElement) {
              addObserved(element.state, key); //State
            }
          }
        });
      }

      return true;
    }());
  }

  // 检查 & 路由分析
  void _remove(Route route) {
    assert(() {
      final element = _getElementByRoute(route);
      if (element != null) {
        if (_isHitWhiteList(element)) {
          return true;
        }

        final key = _getRouteKey(route, element.widget.toStringShort());
        if (element is StatefulElement || element is StatelessElement) {
          LeaksDoctor()
              .memoryLeakScan(group: key, delay: _defaultCheckLeakDelay);
        }
      }

      return true;
    }());
  }

  // 白名单，不参与内存泄漏检测
  bool _isHitWhiteList(Element element) {
    final whiteList = ['LeaksDoctorPage', 'LeaksDoctorDetailPage'];

    var ret = false;
    final curRuntimeType = element.widget.runtimeType.toString();
    if (whiteList.contains(curRuntimeType)) {
      ret = true;
    }
    return ret;
  }

  // 添加被观察的obj
  addObserved(Object obj, String name) {
    assert(() {
      LeaksDoctor().addObserved(obj, group: name);
      return true;
    }());
  }

  // 获取我们页面的‘Element’
  Element? _getElementByRoute(Route route) {
    Element? element;
    if (route is ModalRoute &&
        (shouldCheck == null || shouldCheck!.call(route))) {
      // RepaintBoundary
      route.subtreeContext?.visitChildElements((child) {
        // Builder
        child.visitChildElements((child) {
          // Semantics
          child.visitChildElements((child) {
            // Page
            element = child;
          });
        });
      });
    }
    return element;
  }

  // 通过 [Route] 生成 key
  String _getRouteKey(Route route, String openPageName) {
    final hasCode = route.hashCode.toString();
    var key = route.settings.name;
    if (key == null || key.isEmpty) {
      key = '$openPageName($hasCode)';
    } else {
      key = '$key->$openPageName($hasCode)';
    }
    return key;
  }
}

class LeaksCache {
  static List cache = [];
}
