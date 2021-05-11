import 'dart:async';
import 'dart:convert';
import 'dart:core';
import 'dart:io';

import 'package:dokit/engine/dokit_binding.dart';
import 'package:dokit/kit/apm/log_kit.dart';
import 'package:dokit/ui/dokit_app.dart';
import 'package:dokit/ui/dokit_btn.dart';
import 'package:dokit/ui/kit_page.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart' as dart;
import 'package:package_info/package_info.dart';

export 'package:dokit/ui/dokit_app.dart';

typedef DoKitAppCreator = Future<IDoKitApp> Function();
typedef LogCallback = void Function(String);
typedef ExceptionCallback = void Function(dynamic, StackTrace);

const String DK_PACKAGE_NAME = 'dokit';
const String DK_PACKAGE_VERSION = '0.8.0';

//默认release模式不开启该功能
const bool release = kReleaseMode;

//记录当前zone
Zone? _zone;

// ignore: avoid_classes_with_only_static_members
class DoKit {
  // 初始化方法,app或者appCreator必须设置一个
  static Future<void> runApp(
      {DoKitApp? app,
      DoKitAppCreator? appCreator,
      bool useInRelease = false,
      LogCallback? logCallback,
      ExceptionCallback? exceptionCallback,
      List<String> methodChannelBlackList = const <String>[],
      Function? releaseAction}) async {
    // 统计用户信息，便于了解该开源产品的使用量 (请大家放心，我们不用于任何恶意行为)
    upLoadUserInfo();

    assert(
        app != null || appCreator != null, 'app and appCreator are both null');
    if (release && !useInRelease) {
      if (releaseAction != null) {
        releaseAction.call();
      } else {
        if (app != null) {
          dart.runApp(app.origin);
        } else {
          dart.runApp((await appCreator!()).origin);
        }
      }
      return;
    }
    blackList = methodChannelBlackList;
    await runZoned(
      () async => <void>{
        _ensureDoKitBinding(useInRelease: useInRelease),
        _runWrapperApp(app != null ? app : await appCreator!()),
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
}

// 如果在runApp之前执行了WidgetsFlutterBinding.ensureInitialized，会导致methodchannel功能不可用，可以在runApp前先调用一下ensureDoKitBinding
void _ensureDoKitBinding({bool useInRelease = false}) {
  if (!release || useInRelease) {
    DoKitWidgetsFlutterBinding.ensureInitialized();
  }
}

void _runWrapperApp(IDoKitApp wrapper) {
  DoKitWidgetsFlutterBinding.ensureInitialized()
// ignore: invalid_use_of_protected_member
    ?..scheduleAttachRootWidget(wrapper)
    ..scheduleWarmUpFrame();
  addEntrance();
}

void _collectLog(String line) {
  LogManager.instance.addLog(LogBean.TYPE_INFO, line);
}

void _collectError(Object? details, Object? stack) {
  LogManager.instance.addLog(
      LogBean.TYPE_ERROR, '${details?.toString()}\n${stack?.toString()}');
}

void addEntrance() {
  WidgetsBinding.instance?.addPostFrameCallback((_) {
    final DoKitBtn floatBtn = DoKitBtn();
    floatBtn.addToOverlay();
    KitPageManager.instance.loadCache();
  });
}

void dispose({required BuildContext context}) {
  doKitOverlayKey.currentState?.widget.initialEntries.forEach((element) {
// element.remove();
  });
}

void upLoadUserInfo() async {
  final client = HttpClient();
  const url = 'https://doraemon.xiaojukeji.com/uploadAppData';
  final request = await client.postUrl(Uri.parse(url));
  final packageInfo = await PackageInfo.fromPlatform();

  Locale? locale;
  void finder(Element element) {
    if (element.widget is Localizations) {
      locale ??= (element.widget as Localizations).locale;
    } else {
      element.visitChildren(finder);
    }
  }

  DoKitApp.appKey.currentContext?.visitChildElements(finder);

  final appId = packageInfo.packageName;
  // 在iOS上可能获取不到appName
  // https://github.com/flutter/flutter/issues/42510
  // 当info.plist文件中只有CFBundleName，没有CFBundleDisplayName时，则无法获取
  final appName = packageInfo.appName;
  final appVersion = packageInfo.version;
  final version = DK_PACKAGE_VERSION;
  final from = '1';
  final type = 'flutter';
  final language = locale?.toString() ?? '';

  final params = <String, dynamic>{};
  params['appId'] = appId;
  params['appName'] = appName;
  params['appVersion'] = appVersion;
  params['version'] = version;
  params['from'] = from;
  params['type'] = type;
  params['language'] = language;

  request.headers
    ..add('Content-Type', 'application/json')
    ..add('Accept', 'application/json');
  request.add(utf8.encode(json.encode(params)));

  final response = await request.close();
//  final responseBody = await response.transform(utf8.decoder).join();
  if (response.statusCode == HttpStatus.ok) {
//    print('用户统计数据上报成功！');
  }
  client.close();
}
