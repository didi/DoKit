
import 'dart:collection';

abstract class IInfo {
  dynamic getValue();
}

abstract class IStorage {
  bool save(IInfo info);

  bool contains(IInfo info);

  List<IInfo> getAll();
}

abstract class IKit {
  String getKitName();

  String getIcon();

  void tabAction();
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
}
