import 'package:dokit/kit/apm/vm_helper.dart';
import 'package:vm_service/vm_service.dart';

int _key = 0;

/// 顶级函数，必须常规方法，生成 key 用
String generateNewKey() {
  return "${++_key}";
}

Map<String, dynamic> _objCache = Map();

/// 顶级函数，根据 key 返回指定对象
dynamic keyToObj(String key) {
  return _objCache[key];
}

/// 对象转 id
Future<String> obj2Id(dynamic obj) async {
  VmService service = VmHelper.instance.serviceClient;
  String isolateId = VmHelper.instance.main.id;
  Isolate isolate = await service.getIsolate(isolateId);
  String libraryId = isolate.libraries
      .firstWhere(
          (element) => element.uri.contains('dokit/engine/leak_obj.dart'))
      .id;

  // 用 vm service 执行 generateNewKey 函数
  InstanceRef keyRef =
      await service.invoke(isolateId, libraryId, "generateNewKey",
          // 无参数，所以是空数组
          []);
  // 获取 keyRef 的 String 值
  // 这是唯一一个能把 ObjRef 类型转为数值的 api
  String key = keyRef.valueAsString;

  _objCache[key] = obj;
  try {
    // 调用 keyToObj 顶级函数，传入 key，获取 obj
    InstanceRef valueRef =
        await service.invoke(isolateId, libraryId, "keyToObj",
            // 这里注意，vm_service 需要的是 id，不是值
            [keyRef.id]);
    // 这里的 id 就是 obj 对应的 id
    return valueRef.id;
  } finally {
    _objCache.remove(key);
  }
  return null;
}
