// Copyright© Dokit for Flutter. All rights reserved.
//
// dokit.dart
// Flutter
//
// Created by linusflow on 2021/3/05
// Modified by linusflow on 2021/5/12 下午3:47
//

import 'dart:async';
import 'dart:convert';
import 'dart:core';
import 'dart:io';

import 'package:dokit/engine/dokit_binding.dart';
import 'package:dokit/kit/apm/log_kit.dart';
import 'package:dokit/kit/apm/vm/version.dart';
import 'package:dokit/ui/dokit_app.dart';
import 'package:dokit/ui/dokit_btn.dart';
import 'package:dokit/ui/kit_page.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart' as dart;
import 'package:package_info/package_info.dart';

import 'kit/apm/vm/vm_service_wrapper.dart';
import 'kit/biz/biz.dart';

export 'package:dokit/ui/dokit_app.dart';

typedef DoKitAppCreator = Future<IDoKitApp> Function();
typedef LogCallback = void Function(String);
typedef ExceptionCallback = void Function(dynamic, StackTrace);

const String DK_PACKAGE_NAME = 'dokit';
const String DK_PACKAGE_VERSION = '0.8.0-nullsafety.0';

//默认release模式不开启该功能
const bool release = kReleaseMode;

//记录当前zone
Zone? _zone;

// ignore: avoid_classes_with_only_static_members
class DoKit {
  // 初始化方法,app或者appCreator必须设置一个
  static Future<void> runApp(
      {DoKitApp? app,
      bool useRunZoned = true,
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

    if (useRunZoned != true) {
      var f = () async => <void>{
            _ensureDoKitBinding(useInRelease: useInRelease),
            _runWrapperApp(app != null ? app : await appCreator!()),
            _zone = Zone.current
          };
      await f();
      return;
    }
    await runZonedGuarded(
      () async => <void>{
        _ensureDoKitBinding(useInRelease: useInRelease),
        _runWrapperApp(app != null ? app : await appCreator!()),
        _zone = Zone.current
      },
      (Object obj, StackTrace stack) {
        _collectError(obj, stack);
        if (exceptionCallback != null) {
          _zone?.runBinary(exceptionCallback, obj, stack);
        }
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
    );
  }

  /// 暴露出来的除[runApp]外的所有接口
  static final i = _DoKitInterfaces._instance;
}

abstract class IDoKit {/* Just empty. */}

class _DoKitInterfaces extends IDoKit with _BizKitMixin {
  _DoKitInterfaces._();

  static final _DoKitInterfaces _instance = _DoKitInterfaces._();

  late DoKitBtnClickedCallback callback = (b) => {};

  /// doKit是否打开了页面（只要是通过doKit打开的页面）
  void isDoKitPageShow(DoKitBtnClickedCallback callback) {
    this.callback = callback;
  }
}

mixin _BizKitMixin on IDoKit {
  /// 更新group信息，详见[addKitGroupTip]
  void updateKitGroupTip(String name, String tip) {
    BizKitManager.instance.updateBizKitGroupTip(name, tip);
  }

  /// 详见[addBizKit]
  void addKit<S extends BizKit>({String? key, required S kit}) {
    BizKitManager.instance.addBizKit<S>(key, kit);
  }

  /// 详见[addBizKits]
  void addBizKits(List<BizKit> bizKits) {
    BizKitManager.instance.addBizKits(bizKits);
  }

  /// 创建BizKit对象
  T newBizKit<T extends BizKit>(
      {String? key,
      required String name,
      String? icon,
      required String group,
      String? desc,
      KitPageBuilder? kitBuilder,
      Function? action}) {
    return BizKitManager.instance.createBizKit(
        name: name,
        group: group,
        key: key,
        icon: icon,
        desc: desc,
        action: action,
        kitBuilder: kitBuilder);
  }

  /// [key] kit的唯一标识，全局不可重复，不传则默认使用[BizKit._defaultKey];
  /// [name] kit显示的名字;
  /// [icon] kit的显示的图标，不传则使用默认图标;
  /// [group] kit归属的组，如果该组不存在，则会自动创建;
  /// [desc] kit的描述信息，不会以任何形式显示出来;
  /// [kitBuilder] kit对应的页面的WidgetBuilder，点击该kit的图标后跳转到的Widget页面，不要求有Navigator，详见[BizKit.tapAction].
  void buildBizKit(
      {String? key,
      required String name,
      String? icon,
      required String group,
      String? desc,
      KitPageBuilder? kitBuilder,
      Function? action}) {
    BizKitManager.instance.buildBizKit(
        key: key,
        name: name,
        icon: icon,
        group: group,
        desc: desc,
        kitBuilder: kitBuilder,
        action: action);
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
    floatBtn.btnClickCallback = DoKit.i.callback;
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
  final appName =
      packageInfo.appName.isEmpty ? 'DoKitFlutterDefault' : packageInfo.appName;
  final appVersion = packageInfo.version;
  final version = DK_PACKAGE_VERSION;
  final from = '1';
  var type = 'flutter_';
  if (Platform.isIOS) {
    type += 'iOS';
  } else if (Platform.isAndroid) {
    type += 'android';
  } else {
    type += 'other';
  }
  final language = locale?.toString() ?? '';
  final playload = <String, dynamic>{};
  await VMServiceWrapper.instance
      .callExtensionService('flutterVersion')
      .then((value) {
    if (value != null) {
      final flutter = FlutterVersion.parse(value.json);
      playload['flutter_version'] = flutter.version;
      playload['dart_sdk_version'] = flutter.dartSdkVersion;
      type +=
          '-flutter_version_${flutter.version}-dart_sdk_version_${flutter.dartSdkVersion}';
    }
  });

  final params = <String, dynamic>{};
  params['appId'] = appId;
  params['appName'] = appName;
  params['appVersion'] = appVersion;
  params['version'] = version;
  params['from'] = from;
  params['type'] = type;
  params['language'] = language;
  params['playload'] = playload;

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
