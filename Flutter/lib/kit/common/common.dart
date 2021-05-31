// 视觉功能
import 'package:dokit/kit/common/basic_info.dart';
import 'package:dokit/kit/kit.dart';
import 'package:dokit/ui/resident_page.dart';
import 'package:flutter/material.dart';

abstract class CommonKit implements IKit {
  @override
  void tabAction() {
    // ignore: invalid_use_of_protected_member
    ResidentPage.residentPageKey.currentState?.setState(() {
      ResidentPage.tag = getKitName();
    });
  }

  Widget createDisplayPage();
}

class CommonKitManager {
  Map<String, CommonKit> kitMap = {
    CommonKitName.KIT_BASIC_INFO: BasicInfoKit(),
  };

  CommonKitManager._privateConstructor();

  static final CommonKitManager _instance =
      CommonKitManager._privateConstructor();

  static CommonKitManager get instance => _instance;

  // 如果想要自定义实现，可以用这个方式进行覆盖。后续扩展入口
  void addKit(String tag, CommonKit kit) {
    kitMap[tag] = kit;
  }

  T? getKit<T extends CommonKit?>(String name) {
    if (kitMap.containsKey(name)) {
      return kitMap[name] as T;
    }
    return null;
  }
}

class CommonKitName {
  static const String KIT_BASIC_INFO = '基本信息';
}
