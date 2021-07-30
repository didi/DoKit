import 'dart:collection';

import 'package:flutter/cupertino.dart';

const String BUILTIN_DEFAULT_GROUP = '__builtin__';
const String BIZ_DEFAULT_GROUP = '__biz__';
const String UNKNOWN_GROUP = '__unknown__';
const double kitIconSize = 34.0;
const String defaultIcon = 'images/dk_sys_info.png';

enum KitType {
  builtin,
  biz,
}

extension KitTypeExt on KitType {
  String get defaultGroup {
    if (KitType.builtin == this) {
      return BUILTIN_DEFAULT_GROUP;
    }
    if (KitType.biz == this) {
      return BIZ_DEFAULT_GROUP;
    }
    return UNKNOWN_GROUP;
  }
}

abstract class IInfo {
  dynamic getValue();
}

abstract class IStorage {
  bool save(IInfo info);

  bool contains(IInfo info);

  List<IInfo> getAll();

  void clear();
}

abstract class IKit {
  String get name;

  String get icon;

  KitType get type;

  String get group => type?.defaultGroup ?? UNKNOWN_GROUP;

  VoidCallback get tapAction;
}

abstract class IKitManager<T> {
  Map<String, T> get kitMap;

  void addKit<S extends T>(String name, S kit) {
    assert(name != null && kit != null);
    if (kitMap == null) {
      return;
    }
    kitMap[name] = kit;
  }

  S getKit<S extends T>(String name) {
    assert(name != null);
    if (kitMap == null) {
      return null;
    }
    if (kitMap.containsKey(name)) {
      return kitMap[name] as S;
    }

    return null;
  }
}

class CommonStorage implements IStorage {
  final int maxCount;
  Queue<IInfo> items = new Queue();

  CommonStorage({this.maxCount = 100});

  @override
  List<IInfo> getAll() {
    return items.toList();
  }

  @override
  bool save(IInfo info) {
    if (items.length >= maxCount) {
      items.removeFirst();
    }
    items.add(info);
    return true;
  }

  @override
  bool contains(IInfo info) {
    return items.contains(info);
  }

  @override
  void clear() {
    return items.clear();
  }
}
