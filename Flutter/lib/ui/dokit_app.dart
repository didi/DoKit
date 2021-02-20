import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

final GlobalKey<OverlayState> doKitOverlayKey = GlobalKey<OverlayState>();

// 谷歌提供的DevTool会判断入口widget是否在主工程内申明(runApp(new MyApp())，MyApp必须在主工程内定义，估计是根据source file来判断的)，
// 如果在package内去申明这个入口widget，则在Flutter Inspector上的左边树会被折叠，影响开发使用。故这里要求在main文件内使用DoKitApp(MyApp())的形式来初始化入口
class DoKitApp extends StatefulWidget {
  // 放置dokit悬浮窗的容器
  static GlobalKey rootKey = new GlobalKey();

  // 放置应用真实widget的容器
  static GlobalKey appKey = new GlobalKey();

  Widget get origin => _origin;
  Widget _origin;

  DoKitApp(Widget widget)
      : _origin = _DoKitWrapper(widget),
        super(key: rootKey);

  @override
  State<StatefulWidget> createState() {
    return _DoKitAppState();
  }
}

class _DoKitWrapper extends StatelessWidget {
  final Widget _origin;

  _DoKitWrapper(this._origin) : super(key: DoKitApp.appKey);

  @override
  Widget build(BuildContext context) {
    return _origin;
  }
}

class _DoKitAppState extends State<DoKitApp> {
  final List<OverlayEntry> entries = <OverlayEntry>[];
  final supportedLocales = const <Locale>[Locale('en', 'US')];

  @override
  void initState() {
    super.initState();
    entries.clear();
    entries.add(new OverlayEntry(builder: (context) {
      return widget.origin;
    }));
  }

  Iterable<LocalizationsDelegate<dynamic>> get _localizationsDelegates sync* {
    yield DefaultMaterialLocalizations.delegate;
    yield DefaultCupertinoLocalizations.delegate;
    yield DefaultWidgetsLocalizations.delegate;
  }

  @override
  Widget build(BuildContext context) {
    return Directionality(
      textDirection: TextDirection.ltr,
      child: Stack(
        children: <Widget>[
          widget.origin,
          _MediaQueryFromWindow(
              child: Localizations(
                  locale: supportedLocales.first,
                  delegates: _localizationsDelegates.toList(),
                  child: Overlay(
                    key: doKitOverlayKey,
                  )))
        ],
      ),
    );
  }
}

class _MediaQueryFromWindow extends StatefulWidget {
  const _MediaQueryFromWindow({Key key, this.child}) : super(key: key);

  final Widget child;

  @override
  _MediaQueryFromWindowsState createState() => _MediaQueryFromWindowsState();
}

class _MediaQueryFromWindowsState extends State<_MediaQueryFromWindow>
    with WidgetsBindingObserver {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
  }

  // ACCESSIBILITY

  @override
  void didChangeAccessibilityFeatures() {
    setState(() {
      // The properties of window have changed. We use them in our build
      // function, so we need setState(), but we don't cache anything locally.
    });
  }

  // METRICS

  @override
  void didChangeMetrics() {
    setState(() {
      // The properties of window have changed. We use them in our build
      // function, so we need setState(), but we don't cache anything locally.
    });
  }

  @override
  void didChangeTextScaleFactor() {
    setState(() {
      // The textScaleFactor property of window has changed. We reference
      // window in our build function, so we need to call setState(), but
      // we don't need to cache anything locally.
    });
  }

  // RENDERING
  @override
  void didChangePlatformBrightness() {
    setState(() {
      // The platformBrightness property of window has changed. We reference
      // window in our build function, so we need to call setState(), but
      // we don't need to cache anything locally.
    });
  }

  @override
  Widget build(BuildContext context) {
    return MediaQuery(
      data: MediaQueryData.fromWindow(WidgetsBinding.instance.window),
      child: widget.child,
    );
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }
}
