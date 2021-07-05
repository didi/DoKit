import 'package:dokit/ui/resident_page.dart';
import 'package:flutter/material.dart';

import '../kit.dart';
import 'basic_info.dart';

abstract class CommonKit extends IKit {
  @override
  VoidCallback get tapAction => () {
        // ignore: invalid_use_of_protected_member
        ResidentPage.residentPageKey.currentState.setState(() {
          ResidentPage.tag = name;
        });
      };

  Widget createDisplayPage();

  @override
  KitType get type => KitType.builtin;
}

class CommonKitManager extends IKitManager<CommonKit> {
  Map<String, CommonKit> _kitMap = {
    CommonKitName.KIT_BASIC_INFO: BasicInfoKit(),
  };

  CommonKitManager._();

  static final CommonKitManager _instance = CommonKitManager._();

  static CommonKitManager get instance => _instance;

  @override
  Map<String, CommonKit> get kitMap => _kitMap;
}

class CommonKitName {
  static const String KIT_BASIC_INFO = '基本信息';
}
