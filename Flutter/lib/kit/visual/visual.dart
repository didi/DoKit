// 视觉功能
import 'package:dokit/kit/kit.dart';
import 'package:dokit/kit/visual/view_check.dart';
import 'package:dokit/util/screen_util.dart';

import 'color_pick.dart';

/// 可视化Kit的信息展示widget的margin
const double infoWidgetHorizontalMargin = 20;

/// 可视化Kit的信息展示widget的bottom margin
final double infoWidgetTopMargin = ScreenUtil.instance.statusBarHeight + 20;

abstract class VisualKit extends IKit {
  @override
  KitType get type => KitType.builtin;
}

class VisualKitManager extends IKitManager<VisualKit> {
  VisualKitManager._();

  Map<String, VisualKit> _kitMap = {
    VisualKitName.KIT_VIEW_CHECK: ViewCheckerKit.instance,
    VisualKitName.KIT_COLOR_PICK: ColorPickerKit.instance,
  };

  static final VisualKitManager _instance = VisualKitManager._();

  static VisualKitManager get instance => _instance;

  @override
  Map<String, VisualKit> get kitMap => _kitMap;
}

class VisualKitName {
  static const String KIT_VIEW_CHECK = '控件检查';
  static const String KIT_COLOR_PICK = '颜色拾取';
}
