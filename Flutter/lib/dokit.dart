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
import 'package:flutter/rendering.dart';
import 'package:flutter/widgets.dart' as dart;
import 'package:package_info/package_info.dart';

import 'kit/biz/biz.dart' show BizKitManager, KitPageBuilder, IBizKit;

export 'package:dokit/ui/dokit_app.dart' show DoKitApp;

typedef DoKitAppCreator = Future<IDoKitApp> Function();
typedef LogCallback = void Function(String);
typedef ExceptionCallback = void Function(dynamic, StackTrace);

const String DK_PACKAGE_NAME = 'dokit';
const String DK_PACKAGE_VERSION = '0.6.2';

/// 默认release模式不开启该功能
const bool _release = kReleaseMode;

/// 记录当前zone
Zone _zone;

// ignore: avoid_classes_with_only_static_members
class DoKit {
  // 初始化方法,app或者appCreator必须设置一个
  static Future<void> runApp(
      {DoKitApp app,
      DoKitAppCreator appCreator,
      bool useInRelease = false,
      LogCallback logCallback,
      ExceptionCallback exceptionCallback,
      List<String> methodChannelBlackList = const <String>[],
      Function releaseAction}) async {
    // 统计用户信息，便于了解该开源产品的使用量 (请大家放心，我们不用于任何恶意行为)
    _DoKitPrivateExt._upLoadUserInfo();

    assert(
        app != null || appCreator != null, 'app and appCreator are both null');
    if (_release && !useInRelease) {
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
    blackList = methodChannelBlackList;
    await runZoned(
      () async => <void>{
        _DoKitPrivateExt._ensureDoKitBinding(useInRelease: useInRelease),
        _DoKitPrivateExt._runWrapperApp(app != null ? app : await appCreator()),
        _zone = Zone.current,
      },
      zoneSpecification: ZoneSpecification(
        print: (Zone self, ZoneDelegate parent, Zone zone, String line) {
          _DoKitPrivateExt._collectLog(line); //手机日志
          parent.print(zone, line);
          if (logCallback != null) {
            _zone?.runUnary(logCallback, line);
          }
        },
      ),
      onError: (Object obj, StackTrace stack) {
        _DoKitPrivateExt._collectError(obj, stack);
        if (exceptionCallback != null) {
          _zone?.runBinary(exceptionCallback, obj, stack);
        }
      },
    );
  }

  /// 暴露出来的除[runApp]外的所有接口
  /// 具体暴露出来的接口请见[_DoKitInterfaces]with的mixins:
  /// [_DoKitBtnMixin]
  /// [_BizKitMixin]
  static final i = _DoKitInterfaces._instance;
}

abstract class IDoKit {/* Just empty. */}

class _DoKitInterfaces extends IDoKit with _BizKitMixin, _DoKitBtnMixin {
  _DoKitInterfaces._();

  static final _DoKitInterfaces _instance = _DoKitInterfaces._();

  // Common interfaces.

  /// doKit是否打开了页面（只要是通过doKit打开的页面）
  bool isDoKitPageShow() {
    int overlayCount = 0;
    doKitOverlayKey.currentContext?.findRenderObject()?.visitChildren((child) {
      // _OverlayEntryWidget(element) => RenderPointerListener(renderObject)
      overlayCount++;
    });

    return overlayCount > 1;
  }
}

mixin _DoKitBtnMixin on IDoKit {
  /// 设置dokitBtn的位置
  void setPosition(Offset newPosition) {
    setDoKitBtnPosition(newPosition);
  }
}

mixin _BizKitMixin on IDoKit {
  /// 添加group的tip信息，不可在添加group前操作
  void addKitGroupTip(String name, String tip) {
    BizKitManager.instance.addKitGroupTip(name, tip);
  }

  /// 更新group信息，详见[addKitGroupTip]
  void updateKitGroupTip(String name, String tip) {
    BizKitManager.instance.updateKitGroupTip(name, tip);
  }

  /// 详见[addKitWith]
  void addKit<S extends IBizKit>(String key, S kit) {
    BizKitManager.instance.addKit<S>(key, kit);
  }

  /// [key] kit的唯一标识，全局不可重复，不传则默认使用[BizKit._defaultKey];
  /// [name] kit显示的名字;
  /// [icon] kit的显示的图标，不传则使用默认图标;
  /// [group] kit归属的组，如果该组不存在，则会自动创建;
  /// [desc] kit的描述信息，不会以任何形式显示出来;
  /// [kitBuilder] kit对应的页面的WidgetBuilder，点击该kit的图标后跳转到的Widget页面，不要求有Navigator，详见[BizKit.tapAction].
  void addKitWith({
    String key,
    String name,
    String icon,
    String group,
    String desc,
    KitPageBuilder kitBuilder,
  }) {
    BizKitManager.instance.addKitWith(
      key: key,
      name: name,
      icon: icon,
      group: group,
      desc: desc,
      kitBuilder: kitBuilder,
    );
  }

  /// 关闭kitPage，清空上一次的打开记录，区别于[hide]
  void close() {
    BizKitManager.instance.close();
  }

  /// 隐藏kitPage，不清空上一次的打开记录，区别于[close]
  void hide() {
    BizKitManager.instance.hide();
  }

  /// 打开指定的kit页面，如已有打开的kitPage，则会先关闭原有的kitPage;
  /// 如kit未注册，则走断言报错;
  /// 通常不会主动去调用该方法.
  void open(String key) {
    BizKitManager.instance.open(key);
  }

  /// 无断言报错的打开指定的kitPage;
  /// 通常不会主动去调用该方法.
  void safeOpen(String key) {
    BizKitManager.instance.safeOpen(key);
  }
}

extension _DoKitPrivateExt on DoKit {
  // 如果在runApp之前执行了WidgetsFlutterBinding.ensureInitialized，会导致methodchannel功能不可用，可以在runApp前先调用一下ensureDoKitBinding
  static void _ensureDoKitBinding({bool useInRelease = false}) {
    if (!_release || useInRelease) {
      DoKitWidgetsFlutterBinding.ensureInitialized();
    }
  }

  static void _runWrapperApp(IDoKitApp wrapper) {
    DoKitWidgetsFlutterBinding.ensureInitialized()
// ignore: invalid_use_of_protected_member
      ..scheduleAttachRootWidget(wrapper)
      ..scheduleWarmUpFrame();
    _addEntrance();
  }

  static void _collectLog(String line) {
    LogManager.instance.addLog(LogBean.TYPE_INFO, line);
  }

  static void _collectError(Object details, Object stack) {
    LogManager.instance.addLog(
        LogBean.TYPE_ERROR, '${details?.toString()}\n${stack?.toString()}');
  }

  static void _addEntrance() {
    WidgetsBinding.instance.addPostFrameCallback((_) {
      final DoKitBtn floatBtn = DoKitBtn();
      floatBtn.addToOverlay();
      KitPageManager.instance.loadCache();
    });
  }

  static void dispose({@required BuildContext context}) {
    doKitOverlayKey.currentState.widget.initialEntries.forEach((element) {
// element.remove();
    });
  }

  static void _upLoadUserInfo() async {
    final client = HttpClient();
    const url = 'https://doraemon.xiaojukeji.com/uploadAppData';
    final request = await client.postUrl(Uri.parse(url));
    final packageInfo = await PackageInfo.fromPlatform();

    Locale locale;
    void finder(Element element) {
      if (element.widget is Localizations) {
        locale ??= (element.widget as Localizations).locale;
      } else {
        element.visitChildren(finder);
      }
    }

    DoKitApp.appKey.currentContext.visitChildElements(finder);

    final appId = packageInfo.packageName;
    // 在iOS上可能获取不到appName
    // https://github.com/flutter/flutter/issues/42510
    // 当info.plist文件中只有CFBundleName，没有CFBundleDisplayName时，则无法获取
    final appName = packageInfo.appName ?? 'DoKitFlutterDefault';
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
    if (response.statusCode == HttpStatus.ok) {}
    client.close();
  }
}
