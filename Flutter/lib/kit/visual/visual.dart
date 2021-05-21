// Copyright© Dokit for Flutter.
//
// visual.dart
// Flutter
//
// Created by linusflow on 2021/3/05
// Modified by linusflow on 2021/5/11 下午4:35
//

// 视觉功能
import 'package:dokit/kit/kit.dart';
import 'package:dokit/kit/visual/color_pick.dart';
import 'package:dokit/kit/visual/view_check.dart';
import 'package:dokit/util/screen_util.dart';

/// 可视化Kit的信息展示widget的margin
const double infoWidgetHorizontalMargin = 20;

/// 可视化Kit的信息展示widget的bottom margin
final double infoWidgetTopMargin = ScreenUtil.instance.statusBarHeight + 20;

abstract class VisualKit implements IKit {}

class VisualKitManager {
  VisualKitManager._privateConstructor();

  Map<String, IKit> kitMap = <String, IKit>{
    VisualKitName.KIT_VIEW_CHECK: ViewCheckerKit.instance,
    VisualKitName.KIT_COLOR_PICK: ColorPickerKit.instance,
  };

  static final VisualKitManager _instance =
      VisualKitManager._privateConstructor();

  static VisualKitManager get instance => _instance;

  // 如果想要自定义实现，可以用这个方式进行覆盖。后续扩展入口
  void addKit(String tag, IKit kit) {
    kitMap[tag] = kit;
  }

  T? getKit<T extends IKit>(String name) {
    if (kitMap.containsKey(name)) {
      return kitMap[name] as T;
    }
    return null;
  }
}

class VisualKitName {
  static const String KIT_VIEW_CHECK = '控件检查';
  static const String KIT_COLOR_PICK = '颜色拾取';
}
