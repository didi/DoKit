// Copyright© Dokit for Flutter. All rights reserved.
//
// dk_material_page.dart
// Flutter
//
// Created by linusflow on 2021/7/2
// Modified by linusflow on 2021/7/2 上午11:21
//
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class DKMaterialPageRoute<T> extends PageRoute<T>
    with DKMaterialRouteTransitionMixin<T> {
  DKMaterialPageRoute({
    @required this.builder,
    RouteSettings settings,
    this.maintainState = true,
    bool fullscreenDialog = false,
  })  : assert(builder != null),
        assert(maintainState != null),
        assert(fullscreenDialog != null),
        assert(opaque),
        super(settings: settings, fullscreenDialog: fullscreenDialog);

  final WidgetBuilder builder;

  @override
  Widget buildContent(BuildContext context) => builder(context);

  @override
  final bool maintainState;

  @override
  String get debugLabel => '${super.debugLabel}(${settings.name})';
}

mixin DKMaterialRouteTransitionMixin<T> on PageRoute<T> {
  @protected
  Widget buildContent(BuildContext context);

  WidgetBuilder get builder;

  @override
  Duration get transitionDuration => const Duration(milliseconds: 300);

  @override
  Color get barrierColor => null;

  @override
  String get barrierLabel => null;

  @override
  bool canTransitionTo(TransitionRoute<dynamic> nextRoute) {
    return (nextRoute is MaterialRouteTransitionMixin &&
            !nextRoute.fullscreenDialog) ||
        (nextRoute is CupertinoRouteTransitionMixin &&
            !nextRoute.fullscreenDialog);
  }

  @override
  Widget buildPage(
    BuildContext context,
    Animation<double> animation,
    Animation<double> secondaryAnimation,
  ) {
    final Widget result = buildContent(context);
    assert(() {
      if (result == null) {
        throw FlutterError(
            'The builder for route "${settings.name}" returned null.\n'
            'Route builders must never return null.');
      }
      return true;
    }());
    return Semantics(
      scopesRoute: true,
      explicitChildNodes: true,
      child: result,
    );
  }

  @override
  Widget buildTransitions(BuildContext context, Animation<double> animation,
      Animation<double> secondaryAnimation, Widget child) {
    final PageTransitionsTheme theme = Theme.of(context).pageTransitionsTheme;
    return theme.buildTransitions<T>(
        this, context, animation, secondaryAnimation, child);
  }
}

// 适配1.20.4和1.22.4版本之间MaterialPage在child和builder属性不兼容的问题
class DKMaterialPage<T> extends Page<T> {
  const DKMaterialPage({
    @required this.child,
    this.maintainState = true,
    this.fullscreenDialog = false,
    LocalKey key,
    String name,
    Object arguments,
  })  : assert(child != null),
        assert(maintainState != null),
        assert(fullscreenDialog != null),
        super(key: key, name: name, arguments: arguments);

  final Widget child;

  final bool maintainState;

  final bool fullscreenDialog;

  @override
  Route<T> createRoute(BuildContext context) {
    return _DKPageBasedMaterialPageRoute<T>(page: this);
  }
}

class _DKPageBasedMaterialPageRoute<T> extends PageRoute<T>
    with DKMaterialRouteTransitionMixin<T> {
  _DKPageBasedMaterialPageRoute({
    @required DKMaterialPage<T> page,
  })  : assert(page != null),
        assert(opaque),
        super(settings: page);

  DKMaterialPage<T> get _page => settings as DKMaterialPage<T>;

  @override
  bool get maintainState => _page.maintainState;

  @override
  bool get fullscreenDialog => _page.fullscreenDialog;

  @override
  String get debugLabel => '${super.debugLabel}(${_page.name})';

  Widget get child => _page.child;

  @override
  WidgetBuilder get builder => (_) => child;

  @override
  Widget buildContent(_) => child;
}
