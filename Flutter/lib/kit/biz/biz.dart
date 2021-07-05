// Copyright© Dokit for Flutter. All rights reserved.
//
// biz.dart
// Flutter
//
// Created by linusflow on 2021/6/30
// Modified by linusflow on 2021/6/30 下午4:59
//
import 'dart:async';
import 'dart:collection';

import 'package:dokit/ui/dokit_app.dart';
import 'package:dokit/ui/dokit_btn.dart';
import 'package:flutter/material.dart';

import '../kit.dart';
import 'dk_material_page.dart';

typedef KitPageBuilder = FutureOr<Widget> Function();
bool get bizKitPageIsVisible => _kitOverlayIsVisible;

/// 是否有kitPage存在于[doKitOverlayKey.currentState]对应的overlay层级中，
bool _kitOverlayIsVisible = false;

/// 是否存在kitPage
/// doKitOverlayKey.currentState.debugIsVisible(_kitOverlay)
bool get bizKitPageIsExisted => _kitOverlay != null;
IBizKit _currentOpenedKit;
OverlayEntry _kitOverlay;
void _removeOverlay() {
  if (_kitOverlayIsVisible) {
    _kitOverlay?.remove();
    _kitOverlayIsVisible = false;
  }
}

void _addOverlay() {
  // 添加新的overlay之前先移除debugPage
  hideDebugPage();
  _removeOverlay();
  if (_kitOverlay != null) {
    doKitOverlayKey.currentState
        .insert(_kitOverlay, below: DoKitBtn.doKitBtnKey.currentState.owner);
  } else {
    if (_currentOpenedKit?.kitPageBuilder == null) {
      return;
    }
    _currentOpenedKit?.openPage();
  }
  _kitOverlayIsVisible = true;
}

class BizKitManager extends IKitManager<IBizKit> {
  BizKitManager._();

  static final BizKitManager _instance = BizKitManager._();

  static BizKitManager get instance => _instance;

  @override
  Map<String, IBizKit> get kitMap {
    final allKitList = _kitGroupMap.values.expand((e) => e).toList();
    final _kitMap = Map<String, IBizKit>();
    allKitList.forEach((e) {
      _kitMap.putIfAbsent(e.key, () => e);
    });

    return UnmodifiableMapView(_kitMap);
  }

  Map<String, List<BizKit>> __kitGroupMap;
  Map<String, List<BizKit>> get _kitGroupMap {
    __kitGroupMap ??= Map<String, List<BizKit>>();

    return __kitGroupMap;
  }

  Map<String, List<BizKit>> get kitGroupMap =>
      UnmodifiableMapView(_kitGroupMap);

  bool get noKit => __kitGroupMap?.isEmpty ?? true;

  Map<String, String> __kitGroupTips;
  Map<String, String> get _kitGroupTips {
    __kitGroupTips ??= Map<String, String>();
    return __kitGroupTips;
  }

  Map<String, String> get kitGroupTips => UnmodifiableMapView(_kitGroupTips);

  void _registerKitGroupAndInsertOneKit(String groupName, String kitName,
      String icon, String desc, KitPageBuilder kitBuilder,
      [String key]) {
    _registerKitGroup(groupName);
    final kit = BizKit(icon, kitName, groupName, desc, kitBuilder, key);
    final kitList = _kitGroupMap[groupName];
    kitList.add(kit);
  }

  void _registerKitGroup(String groupName) {
    final kitList = List<BizKit>();
    _kitGroupMap.putIfAbsent(groupName, () => kitList);
  }

  /// 添加group的tip信息，不可在添加group前操作
  void addKitGroupTip(String name, String tip) {
    assert(name != null);
    final groupExist = __kitGroupMap?.keys?.contains(name) ?? false;
    assert(groupExist,
        'Can not add [$name] group tip before adding [$name] group.');

    if (tip == null) {
      _kitGroupTips.remove(name);
      return;
    }
    _kitGroupTips[name] = tip;
  }

  /// 更新group信息，详见[addKitGroupTip]
  void updateKitGroupTip(String name, String tip) {
    addKitGroupTip(name, tip);
  }

  /// 详见[addKitWith]
  @override
  void addKit<S extends IBizKit>(String key, S kit) {
    IBizKit ikit = kit;
    addKitWith(
      key: key,
      name: ikit.name,
      group: ikit.group,
      icon: ikit.icon,
      desc: ikit.desc,
      kitBuilder: ikit.kitPageBuilder,
    );
  }

  /// [key] kit的唯一标识，全局不可重复，不传则默认使用[BizKit._defaultKey];
  /// [name] kit显示的名字;
  /// [icon] kit的显示的图标，不传则使用默认图标;
  /// [group] kit归属的组，如果该组不存在，则会自动创建;
  /// [desc] kit的描述信息，不会以任何形式显示出来;
  /// [kitBuilder] kit对应的页面的WidgetBuilder，点击该kit的图标后跳转到的Widget页面，不要求有Navigator，详见[BizKit.tapAction].
  void addKitWith({
    String key,
    String name,
    String icon,
    String group,
    String desc,
    KitPageBuilder kitBuilder,
  }) {
    assert(name != null && group != null);

    if (!_kitGroupMap.containsKey(group)) {
      _registerKitGroupAndInsertOneKit(
          group, name, icon, desc, kitBuilder, key);
    } else {
      final keyList = kitMap.keys;
      final kit = BizKit(icon, name, group, desc, kitBuilder, key);
      final exist = keyList.contains(kit.key);
      assert(!exist, 'The ${kit.toString()} kit already exists.');
      _kitGroupMap[group].add(kit);
    }
  }

  /// 关闭kitPage，清空上一次的打开记录，区别于[hide]
  void close() {
    hide();
    _kitOverlay = null;
    _currentOpenedKit = null;
  }

  /// 隐藏kitPage，不清空上一次的打开记录，区别于[close]
  void hide() {
    _removeOverlay();
  }

  /// 打开隐藏的kitPage，暂不开放
  void _show() {
    if (_kitOverlayIsVisible) {
      return;
    }
    _addOverlay();
  }

  /// 打开指定的kit页面，如已有打开的kitPage，则会先关闭原有的kitPage;
  /// 如kit未注册，则走断言报错;
  /// 通常不会主动去调用该方法.
  void open(String key) {
    assert(key != null);
    final Map<String, IBizKit> _kitMap = kitMap;
    final IBizKit bizKit = _kitMap[key];
    assert(bizKit != null);
    bizKit.openPage();
  }

  /// 无断言报错的打开指定的kitPage;
  /// 通常不会主动去调用该方法.
  void safeOpen(String key) {
    if (key == null) {
      return;
    }
    final Map<String, IBizKit> _kitMap = kitMap;
    final IBizKit bizKit = _kitMap[key];
    bizKit?.openPage();
  }
}

abstract class IBizKit extends IKit {
  @override
  KitType get type => KitType.biz;

  String get desc;

  String get key;

  KitPageBuilder get kitPageBuilder;

  /// 打开kit页面
  void openPage() => tapAction();
}

class BizKit extends IBizKit {
  final String _key;
  final String _icon;
  final String _name;
  final String _group;
  final String _desc;
  final KitPageBuilder _kitBuilder;

  /// [_name]不能为空
  BizKit(this._icon, this._name, this._group, this._desc, this._kitBuilder,
      [this._key])
      : assert(_name != null);

  void _bizTapAction(KitPageBuilder kitBuilder) async {
    if (kitBuilder == null) {
      return;
    }
    final kitPage = await kitBuilder();

    if (_currentOpenedKit?.key != this.key || _kitOverlay == null) {
      // 打开一个新的kitPage，要先remove，否则_kitOverlay被赋新的值，后面再remove则报错
      _removeOverlay();
      _kitOverlay = OverlayEntry(
        builder: (BuildContext context) {
          return _BizKitPageWrapper(kitPage: kitPage);
        },
        maintainState: true,
      );
    }

    _addOverlay();
    _currentOpenedKit = this;
  }

  @override
  String get icon => _icon ?? defaultIcon;

  @override
  String get name => _name;

  @override
  get tapAction => () => _bizTapAction(kitPageBuilder);

  @override
  String get group => _group ?? super.group;

  @override
  String get desc => _desc ?? '';

  @override
  String get key => _key ?? _defaultKey;

  String get _defaultKey =>
      '$group' + '_' + '$name' + '_' + '$icon' + '_' + '$desc';

  @override
  KitPageBuilder get kitPageBuilder => _kitBuilder;

  @override
  String toString() {
    final descToString = _desc == null ? 'null' : '"$_desc"';
    return '[group:"$group", name:"$name", icon:"$icon", desc:$descToString]';
  }
}

class _BizKitPageWrapper extends StatefulWidget {
  final Widget kitPage;

  const _BizKitPageWrapper({Key key, this.kitPage}) : super(key: key);

  @override
  State<StatefulWidget> createState() => _BizKitPageWrapperState();
}

class _BizKitPageWrapperState extends State<_BizKitPageWrapper>
    with AutomaticKeepAliveClientMixin {
  @override
  Widget build(BuildContext context) {
    super.build(context);
    return _DKNavigator(
      pages: [
        DKMaterialPage(
          child: widget.kitPage,
        ),
      ],
      onPopPage: (route, result) => route.didPop(result),
    );
  }

  @override
  bool get wantKeepAlive => true;
}

class _DKNavigator extends Navigator {
  const _DKNavigator({
    Key key,
    List<Page<dynamic>> pages = const <Page<dynamic>>[],
    PopPageCallback onPopPage,
    String initialRoute,
    RouteListFactory onGenerateInitialRoutes =
        Navigator.defaultGenerateInitialRoutes,
    RouteFactory onGenerateRoute,
    RouteFactory onUnknownRoute,
    DefaultTransitionDelegate transitionDelegate =
        const DefaultTransitionDelegate<dynamic>(),
    List<NavigatorObserver> observers = const <NavigatorObserver>[],
  }) : super(
          key: key,
          pages: pages,
          onPopPage: onPopPage,
          initialRoute: initialRoute,
          onGenerateInitialRoutes: onGenerateInitialRoutes,
          onGenerateRoute: onGenerateRoute,
          transitionDelegate: transitionDelegate,
          onUnknownRoute: onUnknownRoute,
          observers: observers,
        );

  @override
  NavigatorState createState() => _DKNavigatorState();
}

class _DKNavigatorState extends NavigatorState {
  @override
  void pop<T extends Object>([T result]) {
    // 拦截pop操作，当canPop为false时，说明是当前只有一个页面，则直接隐藏当前kitPage
    if (!this.canPop()) {
      BizKitManager.instance.hide();
      return;
    }
    super.pop(result);
  }
}
