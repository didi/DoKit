// 视觉功能
import 'package:dokit/kit/apm/apm.dart';
import 'package:dokit/kit/visual/view_check.dart';

abstract class VisualKit implements IKit {}

class VisualKitManager {
  Map<String, IKit> kitMap = {
    VisualKitName.KIT_VIEW_CHECK: ViewCheckerKit.instance,
  };

  VisualKitManager._privateConstructor() {}

  static final VisualKitManager _instance =
      VisualKitManager._privateConstructor();

  static VisualKitManager get instance => _instance;

  // 如果想要自定义实现，可以用这个方式进行覆盖。后续扩展入口
  void addKit(String tag, IKit kit) {
    assert(tag != null && kit != null);
    kitMap[tag] = kit;
  }

  T getKit<T extends IKit>(String name) {
    assert(name != null);
    if (kitMap.containsKey(name)) {
      return kitMap[name];
    }
    return null;
  }
}

class VisualKitName {
  static const String KIT_VIEW_CHECK = '控件检查';
}
