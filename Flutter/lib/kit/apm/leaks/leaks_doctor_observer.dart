// ignore_for_file: always_declare_return_types

import 'package:flutter/widgets.dart';

import 'leaks_doctor.dart';

const int _defaultCheckLeakDelay = 500;
typedef ShouldAddedRoute = bool Function(Route route);
typedef ConfigPolicyPool = Map<String,int>? Function();

class LeaksDoctorObserver extends NavigatorObserver {
  final ShouldAddedRoute? shouldCheck;
  final int checkLeakDelay;
  // 策略  列表中的item是一个Map对象：key是类名，value存储期望类名对应实例对象的个数
  final ConfigPolicyPool? confPolicyPool;

  LeaksDoctorObserver(
      {this.checkLeakDelay = _defaultCheckLeakDelay, this.shouldCheck, this.confPolicyPool});

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
            LeaksCache.cache.add(element.widget);

            var _className = element.widget.toStringShort();
            var _expectedTotalCount = _checkPolicy(_className);
            if (_expectedTotalCount != null) {
              _ObservedElement(element, key, expectedTotalCount: _expectedTotalCount);
            } else {
              _ObservedElement(element,key);
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
              .memoryLeakScan(group: key, delay: checkLeakDelay);
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

  // 检查策略
  int? _checkPolicy(String className) {
    if (confPolicyPool != null) {
      var policyPool = confPolicyPool!();
      return policyPool?[className];
    }
  }

  // 添加被观察的obj
  _addObserved(Object obj, String name, {int? expectedTotalCount = 0}) {
    assert(() {
      LeaksDoctor().addObserved(obj, group: name, expectedTotalCount: expectedTotalCount);
      return true;
    }());
  }

  void _ObservedElement(Element element, String key, {int? expectedTotalCount = 0}) {
    // _addObserved(element, key); //Element
    _addObserved(element.widget, key); //Widget
    if (element is StatefulElement) {
      _addObserved(element.state, key+'+state'); //State
    }
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
