// 视觉功能
import 'package:dokit/kit/apm/apm.dart';
import 'package:dokit/kit/visual/view_check.dart';
import 'package:dokit/ui/resident_page.dart';
import 'package:flutter/material.dart';

import 'basic_info.dart';

abstract class CommonKit implements IKit {
  @override
  void tabAction() {
    ResidentPage.residentPageKey.currentState.setState(() {
      ResidentPage.tag = getKitName();
    });
  }
  Widget createDisplayPage();
}

class CommonKitManager {
  Map<String, CommonKit> kitMap = {
    CommonKitName.KIT_BASIC_INFO: BasicInfoKit(),
  };

  CommonKitManager._privateConstructor() {}

  static final CommonKitManager _instance =
      CommonKitManager._privateConstructor();

  static CommonKitManager get instance => _instance;

  // 如果想要自定义实现，可以用这个方式进行覆盖。后续扩展入口
  void addKit(String tag, CommonKit kit) {
    assert(tag != null && kit != null);
    kitMap[tag] = kit;
  }

  T getKit<T extends CommonKit>(String name) {
    assert(name != null);
    if (kitMap.containsKey(name)) {
      return kitMap[name];
    }
    return null;
  }
}

class CommonKitName {
  static const String KIT_BASIC_INFO = '基本信息';
}
