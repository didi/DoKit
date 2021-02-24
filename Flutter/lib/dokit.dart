import 'dart:async';
import 'dart:core';

import 'package:dokit/engine/dokit_binding.dart';
import 'package:dokit/kit/apm/log_kit.dart';
import 'package:dokit/kit/kit_page.dart';
import 'package:dokit/ui/dokit_app.dart';
import 'package:dokit/ui/dokit_btn.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart' as dart;

export 'package:dokit/ui/dokit_app.dart';

typedef DoKitAppCreator = Future<IDoKitApp> Function();
typedef LogCallback = void Function(String);
typedef ExceptionCallback = void Function(dynamic, StackTrace);

// ignore: avoid_classes_with_only_static_members
class DoKit {
  static const String PACKAGE_NAME = 'dokit';

  //默认release模式不开启该功能
  static bool release = kReleaseMode;

  //记录当前zone
  static Zone _zone;

  // 如果在runApp之前执行了WidgetsFlutterBinding.ensureInitialized，会导致methodchannel功能不可用，可以在runApp前先调用一下ensureDoKitBinding
  static void _ensureDoKitBinding({bool useInRelease = false}) {
    if (!release || useInRelease) {
      DoKitWidgetsFlutterBinding.ensureInitialized();
    }
  }

  // 初始化方法,app或者appCreator必须设置一个
  static Future<void> runApp(
      {DoKitApp app,
      DoKitAppCreator appCreator,
      bool useInRelease = false,
      LogCallback logCallback,
      ExceptionCallback exceptionCallback,
      Function releaseAction}) async {
    assert(
        app != null || appCreator != null, 'app and appCreator are both null');
    if (release && !useInRelease) {
      if (releaseAction != null) {
        releaseAction.call();
      } else {
        if (app != null) {
          dart.runApp(app.origin);
        } else {
          dart.runApp((await appCreator())?.origin);
        }
      }
      return;
    }
    runZoned(
      () async => <void>{
        _ensureDoKitBinding(useInRelease: useInRelease),
        _runWrapperApp(app ?? await appCreator()),
        _zone = Zone.current
      },
      zoneSpecification: ZoneSpecification(
        print: (Zone self, ZoneDelegate parent, Zone zone, String line) {
          _collectLog(line); //手机日志
          parent.print(zone, line);
          if (logCallback != null) {
            _zone?.runUnary(logCallback, line);
          }
        },
      ),
      onError: (Object obj, StackTrace stack) {
        _collectError(obj, stack);
        if (exceptionCallback != null) {
          _zone?.runBinary(exceptionCallback, obj, stack);
        }
      },
    );
  }

  static void _runWrapperApp(IDoKitApp wrapper) {
    DoKitWidgetsFlutterBinding.ensureInitialized()
      // ignore: invalid_use_of_protected_member
      ..scheduleAttachRootWidget(wrapper)
      ..scheduleWarmUpFrame();
    addEntrance();
  }

  static void _collectLog(String line) {
    LogManager.instance.addLog(LogBean.TYPE_INFO, line);
  }

  static void _collectError(Object details, Object stack) {
    LogManager.instance.addLog(
        LogBean.TYPE_ERROR, '${details?.toString()}\n${stack?.toString()}');
  }

  static void addEntrance() {
    WidgetsBinding.instance.addPostFrameCallback((_) {
      final DoKitBtn floatBtn = DoKitBtn();
      floatBtn.addToOverlay();
      KitPageManager.instance.loadCache();
    });
  }

  static void dispose({@required BuildContext context}) {
    final List<OverlayEntry> initialEntries =
        doKitOverlayKey.currentState.widget.initialEntries;
    // ignore: unused_local_variable
    for (final OverlayEntry element in initialEntries) {
      // element.remove();
    }
  }
}
