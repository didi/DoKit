import 'package:dokit/kit/apm/launch/model.dart';
import 'package:flutter/material.dart';

ValueNotifier<LaunchInfo> notifier = ValueNotifier(LaunchInfo(0, '', ''));

bool enabled = false;

class LaunchObserver extends NavigatorObserver {
  @override
  void didPush(Route route, Route? previousRoute) {
    super.didPush(route, previousRoute);
    if (enabled) {
      int before = DateTime.now().millisecondsSinceEpoch;
      WidgetsBinding.instance?.addPostFrameCallback((timeStamp) {
        int now = DateTime.now().millisecondsSinceEpoch;
        notifier.value = LaunchInfo(
            now - before, previousRoute?.settings.name, route.settings.name);
      });
    }
  }
}
