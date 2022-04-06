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

            // 人为制造泄漏
            // LeaksCache.cache.add(element);

            var _className = element.widget.toStringShort();
            var _expectedTotalCount = _checkPolicy(_className);
            if (_expectedTotalCount != null) {
              _ObservedElement(route, element, expectedTotalCount: _expectedTotalCount);
            } else {
              _ObservedElement(route, element);
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

        if (element is StatefulElement || element is StatelessElement) {
          final elementKey = _getKey(route, GenerateKeyType.Element, element);
          LeaksDoctor().memoryLeakScan(group: elementKey, delay: checkLeakDelay);

          final widgetKey = _getKey(route, GenerateKeyType.Widget, element);
          LeaksDoctor().memoryLeakScan(group: widgetKey, delay: checkLeakDelay);
          if (element is StatefulElement) {
            final stateKey = _getKey(route, GenerateKeyType.State, element);
            LeaksDoctor().memoryLeakScan(group: stateKey, delay: checkLeakDelay);
          }
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

  void _ObservedElement(Route route, Element element, {int? expectedTotalCount = 0}) { 
    final elementKey = _getKey(route, GenerateKeyType.Element, element);
    _addObserved(element, elementKey); //Element

    final widgetKey = _getKey(route, GenerateKeyType.Widget, element);
    _addObserved(element.widget, widgetKey); //Widget

    if (element is StatefulElement) {
      final stateKey = _getKey(route, GenerateKeyType.State, element);
      _addObserved(element.state, stateKey); //State
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
  String _getKey(Route route, GenerateKeyType type, Element element) {
    var key;

    var routeName = route.settings.name??'';
    var openPageName = element.widget.toStringShort();
    
    var hasCode = '';
    var keyType = '';
    switch (type) {
      case GenerateKeyType.Element: {
        hasCode = element.hashCode.toString();
        keyType = 'Element';
      }break;
      case GenerateKeyType.Widget:{
        hasCode = element.widget.hashCode.toString();
        keyType = 'Widget';
      }break;
      default: {
        hasCode = (element.hashCode.toString()+'state').hashCode.toString();
        keyType = 'State';
      }
    }
    
    key = '$routeName-$openPageName-$keyType-$hasCode';
  
    return key;
  }
}

enum GenerateKeyType {
  Element,
  Widget,
  State
}

class LeaksCache {
  static List cache = [];
}
