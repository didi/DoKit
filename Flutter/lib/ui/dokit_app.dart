import 'package:flutter/material.dart';

// 谷歌提供的DevTool会判断入口widget是否在主工程内申明(runApp(new MyApp())，MyApp必须在主工程内定义，估计是根据source file来判断的)，
// 如果在package内去申明这个入口widget，则在Flutter Inspector上的左边树会被折叠，影响开发使用。故这里要求在main文件内使用DoKitApp(MyApp())的形式来初始化入口
class DoKitApp extends MaterialApp {
  // 放置dokit悬浮窗的容器
  static GlobalKey rootKey = new GlobalKey();

  // 放置应用真实widget的容器
  static GlobalKey appKey = new GlobalKey();

  Widget get origin => _origin;
  Widget _origin;

  DoKitApp(this._origin)
      : super(key: DoKitApp.rootKey, home: _DoKitWrapper(_origin));
}

class _DoKitWrapper extends StatelessWidget {
  final Widget _origin;

  _DoKitWrapper(this._origin) : super(key: DoKitApp.appKey);

  @override
  Widget build(BuildContext context) {
    return _origin;
  }
}
