import 'dart:async';
import 'package:dokit/engine/dokit_binding.dart';
import 'package:dokit/kit/apm/log_kit.dart';
import 'package:dokit/ui/dokit_btn.dart';
import 'package:dokit/ui/dokit_app.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'dart:core';
import 'package:flutter/widgets.dart' as dart;
import 'package:dokit/kit/kit_page.dart';
export 'package:dokit/ui/dokit_app.dart';

typedef DoKitAppCreator = Future<DoKitApp> Function();

class DoKit {
  static final String PACKAGE_NAME = 'dokit';

  //默认release模式不开启该功能
  static bool release = kReleaseMode;

  // 如果在runApp之前执行了WidgetsFlutterBinding.ensureInitialized，会导致methodchannel功能不可用，可以在runApp前先调用一下ensureDoKitBinding
  static void _ensureDoKitBinding({bool useInRelease = false}) {
    if (!release || useInRelease) {
      DoKitWidgetsFlutterBinding.ensureInitialized();
    }
  }

  // 初始化方法,app或者appCreator必须设置一个
  static void runApp(
      {DoKitApp app,
      DoKitAppCreator appCreator,
      bool useInRelease = false,
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
      () async => {
        _ensureDoKitBinding(useInRelease: useInRelease),
        _runWrapperApp(app != null ? app : await appCreator())
      },
      zoneSpecification: ZoneSpecification(
        print: (Zone self, ZoneDelegate parent, Zone zone, String line) {
          _collectLog(line); //手机日志
          parent.print(zone, line);
        },
      ),
      onError: (Object obj, StackTrace stack) {
        _collectError(obj, stack);
        Zone.current.handleUncaughtError(obj, stack);
      },
    );
    FlutterError.onError = (FlutterErrorDetails details) {
      // FlutterErrorDetails.toString方法偶尔会报错
      _collectError(details.exception, details.stack);
      FlutterError.dumpErrorToConsole(details);
    };
  }

  static void _runWrapperApp(DoKitApp wrapper) {
    DoKitWidgetsFlutterBinding.ensureInitialized()
      ..scheduleAttachRootWidget(wrapper)
      ..scheduleWarmUpFrame();
    addEntrance();
  }

  static void _collectLog(String line) {
    LogManager.instance.addLog(LogBean.TYPE_INFO, line);
  }

  static void _collectError(Object details, Object stack) {
    LogManager.instance.addLog(
        LogBean.TYPE_ERROR, '${details.toString()}\n${stack.toString()}');
  }

  static void addEntrance() {
    WidgetsBinding.instance.addPostFrameCallback((_) {
      DoKitBtn floatBtn = DoKitBtn();
      floatBtn.addToOverlay();
      KitPageManager.instance.loadCache();
    });
  }

  static void dispose({@required BuildContext context}) {
    Overlay.of(context).widget.initialEntries.forEach((element) {
      // element.remove();
    });
  }
}
