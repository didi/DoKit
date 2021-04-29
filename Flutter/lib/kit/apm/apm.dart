import 'package:dokit/kit/apm/fps_kit.dart';
import 'package:dokit/kit/apm/launch/page_launch_kit.dart';
import 'package:dokit/kit/apm/log_kit.dart';
import 'package:dokit/kit/apm/memory_kit.dart';
import 'package:dokit/kit/apm/method_channel_kit.dart';
import 'package:dokit/kit/apm/route_kit.dart';
import 'package:dokit/kit/apm/source_code_kit.dart';
import 'package:dokit/ui/resident_page.dart';
import 'package:flutter/material.dart';

import '../kit.dart';
import 'http_kit.dart';

class ApmKitManager {
  Map<String, ApmKit> kitMap = {
    ApmKitName.KIT_LOG: LogKit(),
    ApmKitName.KIT_CHANNEL: MethodChannelKit(),
    ApmKitName.KIT_ROUTE: RouteKit(),
    ApmKitName.KIT_FPS: FpsKit(),
    ApmKitName.KIT_MEMORY: MemoryKit(),
    ApmKitName.KIT_HTTP: HttpKit(),
    ApmKitName.KIT_SOURCE_CODE: SourceCodeKit(),
    ApmKitName.KIT_PAGE_LAUNCH: PageLaunchKit()
  };

  ApmKitManager._privateConstructor() {}

  static final ApmKitManager _instance = ApmKitManager._privateConstructor();

  static ApmKitManager get instance => _instance;

  // 如果想要自定义实现，可以用这个方式进行覆盖。后续扩展入口
  void addKit(String tag, ApmKit kit) {
    assert(tag != null && kit != null);
    kitMap[tag] = kit;
  }

  T getKit<T extends ApmKit>(String name) {
    assert(name != null);
    if (kitMap.containsKey(name)) {
      return kitMap[name] as T;
    }
    return null;
  }

  void startUp() {
    kitMap.forEach((key, kit) {
      kit.start();
    });
  }
}

abstract class ApmKit implements IKit {
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
  void tabAction() {
    ResidentPage.residentPageKey.currentState.setState(() {
      ResidentPage.tag = getKitName();
    });
  }

  @override
  bool save(IInfo info) {
    return info != null &&
        storage != null &&
        !storage.contains(info) &&
        storage.save(info);
  }

  IStorage getStorage() {
    return storage;
  }
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
