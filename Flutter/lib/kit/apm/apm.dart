import 'package:dokit/kit/apm/route_kit.dart';
import 'package:dokit/kit/apm/source_code_kit.dart';
import 'package:dokit/ui/resident_page.dart';
import 'package:flutter/material.dart';

import '../kit.dart';
import 'fps_kit.dart';
import 'http_kit.dart';
import 'launch/page_launch_kit.dart';
import 'log_kit.dart';
import 'memory_kit.dart';
import 'method_channel_kit.dart';

class ApmKitManager extends IKitManager<ApmKit> {
  Map<String, ApmKit> _kitMap = {
    ApmKitName.KIT_LOG: LogKit(),
    ApmKitName.KIT_CHANNEL: MethodChannelKit(),
    ApmKitName.KIT_ROUTE: RouteKit(),
    ApmKitName.KIT_FPS: FpsKit(),
    ApmKitName.KIT_MEMORY: MemoryKit(),
    ApmKitName.KIT_HTTP: HttpKit(),
    ApmKitName.KIT_SOURCE_CODE: SourceCodeKit(),
    ApmKitName.KIT_PAGE_LAUNCH: PageLaunchKit()
  };

  ApmKitManager._();

  static final ApmKitManager _instance = ApmKitManager._();

  static ApmKitManager get instance => _instance;

  void startUp() {
    kitMap.forEach((key, kit) {
      kit.start();
    });
  }

  @override
  Map<String, ApmKit> get kitMap => _kitMap;
}

abstract class ApmKit extends IKit {
  IStorage storage;

  void start();

  void stop();

  IStorage createStorage();

  Widget createDisplayPage();

  ApmKit() {
    storage = createStorage();
    assert(storage != null, 'storage should not be null');
  }

  @override
  VoidCallback get tapAction => () {
        // ignore: invalid_use_of_protected_member
        ResidentPage.residentPageKey.currentState.setState(() {
          ResidentPage.tag = name;
        });
      };

  bool save(IInfo info) {
    return info != null &&
        storage != null &&
        !storage.contains(info) &&
        storage.save(info);
  }

  IStorage getStorage() {
    return storage;
  }

  @override
  KitType get type => KitType.builtin;
}

class ApmKitName {
  static const String KIT_FPS = '帧率';
  static const String KIT_MEMORY = '内存';
  static const String KIT_LOG = '日志查看';
  static const String KIT_ROUTE = '路由信息';
  static const String KIT_CHANNEL = '方法通道';
  static const String KIT_HTTP = '网络请求';
  static const String KIT_SOURCE_CODE = '查看源码';
  static const String KIT_PAGE_LAUNCH = '启动耗时';
}
