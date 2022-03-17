// ignore_for_file: omit_local_variable_types

import 'dart:developer';
import 'dart:isolate' as iso; // 这里避免和dart:isolate中的Isolate对象应用二义性
import 'package:vm_service/utils.dart';
import 'package:vm_service/vm_service.dart';
import 'package:vm_service/vm_service_io.dart';

const String _vmToolsetLibrary =
    'package:dokit/kit/apm/vm/vm_service_toolset.dart';

class VmserviceToolset {
  static VmserviceToolset? _instance;
  static VmService? _service;
  static String? _isolateId;

  factory VmserviceToolset() {
    _instance ??= VmserviceToolset._();
    return _instance!;
  }

  VmserviceToolset._();

  // 获取vm service
  Future<VmService> getVMService() async {
    if (_service != null) {
      return _service!;
    }
    ServiceProtocolInfo info = await Service.getInfo();
    String url = info.serverUri.toString();
    Uri uri = Uri.parse(url);
    Uri socketUri = convertToWebSocketUrl(serviceProtocolUrl: uri);
    _service = await vmServiceConnectUri(socketUri.toString());

    return _service!;
  }

  // VM 这里可获取到hostCPU等信息
  Future<VM> getVM() async {
    VmService vmService = await getVMService();
    return vmService.getVM();
  }

  String? get isolateId {
    if (_isolateId != null) {
      return _isolateId;
    }
    // 这里的Isolate.current是main Isolate
    _isolateId = Service.getIsolateID(iso.Isolate.current);
    return _isolateId;
  }

  // 用于按其id查找Isolate对象。
  // 如果isolateId指已退出的Isolate，则返回收集的哨兵。
  Future<Isolate> getIsolate(String id) async {
    VmService vmService = await getVMService();
    return vmService.getIsolate(id);
  }

  Future<Isolate> getMainIsolate() {
    return getIsolate(isolateId!);
  }

  // Isolate的所有库的列表。
  Future<List<LibraryRef>?> getLibraries() async {
    Isolate isolate = await getMainIsolate();
    return isolate.libraries;
  }

  // 通过uri查找 [Library] on [Isolate]
  Future<LibraryRef?> searchLibrary(String uri) async {
    final libraries = await getLibraries();
    if (libraries != null) {
      for (int i = 0; i < libraries.length; i++) {
        var lib = libraries[i];
        if (lib.uri == uri) {
          return lib;
        }
      }
    }
    return null;
  }

  // 用于检索属于特定类的一组实例。这不包括给定类的子类的实例。它可以获取某个 classId 的所有子类实例
  // 实例的顺序未定义（即，与分配顺序无关）且不稳定（即，对同一类多次调用此方法可能会给出不同的答案，即使在调用之间没有执行Dart代码）。
  // 实例集可能包括无法访问但尚未被垃圾收集的对象。
  // objectId：为其检索实例的类的ID。objectId必须是类的ID，否则将返回RPC错误。
  // limit是要返回的最大实例数。
  // 如果isolateId指已退出的Isolate，则返回收集的哨兵。
  Future<InstanceSet> getInstances(String objectId, int limit) async {
    VmService vmService = await getVMService();
    return vmService.getInstances(isolateId!, objectId, limit);
  }

  // 用于根据对象id从某个对象中查找对象。
  // 如果objectId是一个已过期的临时id，则返回过期的Sentinel。
  // 如果isolateId指已退出的Isolate，则返回收集的哨兵。
  // 如果objectId引用了一个由VM的垃圾收集器收集的堆对象，则返回收集的Sentinel。
  // 如果objectId引用已删除的非堆对象，则返回收集的Sentinel。
  // 如果对象句柄尚未过期且对象尚未收集，则将返回一个对象。
  // offset和count参数用于请求具有以下类型的实例对象的子范围：String、List、Map、Uint8ClampedList、Uint8List、Uint16List、Uint64List、Int16List、Int32List、Int64List、Flooat32List、Float64List、Inst32x3List、Float32x4List和Float64x2List。否则将忽略这些参数。
  Future<Obj?> getObject(String objectId, {int? offset, int? count}) async {
    VmService? vmService = await getVMService();
    if (vmService != null) {
      return vmService.getObject(isolateId!, objectId,
          offset: offset, count: count);
    }
    return null;
  }

  Future<Instance?> getInstanceByObjectId(String? objectId) async {
    var value = await getObject(objectId!);
    return value as Instance?;
  }

  // 在 VM 中通过 Object 获取 ObjectId
  // 为什么不用getInstance？
  // 这个问题比较麻烦，vm_service 中没有实例对象和 id 转换的 API
  // 有个 getInstance(isolateId, classId, limit) 的 API，可以获取某个 classId 的所有子类实例，
  // 先不说如何获取到想要的 classId，此 API 的性能和 limit 都让人担忧。
  Future<String?> getObjectId(dynamic obj) async {
    final library = await searchLibrary(_vmToolsetLibrary);
    if (library == null || library.id == null) return null;
    VmService vmService = await getVMService();

    final mainIsolate = await getMainIsolate();
    if (mainIsolate.id == null) return null;

    // 通过invoke进行方法调用 返回Response
    // 可以用来执行某个常规函数（getter、setter、构造函数、私有函数属于非常规函数），
    // 其中如果 targetId 是 Library 的 id，那么 invoke 执行的就是 Library 的顶级函数。
    Response keyResponse =
        await vmService.invoke(mainIsolate.id!, library.id!, 'generateKey', []);
    final keyRef = InstanceRef.parse(keyResponse.json);
    String? key = keyRef?.valueAsString;
    if (key == null) return null;
    _objCache[key] = obj;

    try {
      Response valueResponse = await vmService
          .invoke(mainIsolate.id!, library.id!, 'keyToObj', [keyRef!.id!]);
      final valueRef = InstanceRef.parse(valueResponse.json);
      return valueRef?.id;
    } catch (e) {
      print('getObjectId $e');
    } finally {
      _objCache.remove(key);
    }
    return null;
  }

  // 通过Object获取Instance
  Future<Instance?> getInstanceByObject(dynamic obj) async {
    VmService vmService = await getVMService();

    final mainIsolate = await getMainIsolate();
    if (mainIsolate.id != null) {
      try {
        final objId = await getObjectId(obj);
        if (objId != null) {
          Obj object = await vmService.getObject(mainIsolate.id!, objId);
          final instance = Instance.parse(object.json);
          return instance;
        }
      } catch (e) {
        print('getInstanceByObject error:$e');
      }
    }
    return null;
  }

  ///通过ObjectId获取Instance
  Future<Obj?> getObjectInstanceById(String objId) async {
    final vmService = await getVMService();

    final mainIsolate = await getMainIsolate();
    if (mainIsolate.id != null) {
      try {
        Obj object = await vmService.getObject(mainIsolate.id!, objId);
        return object;
      } catch (e) {
        print('getObjectInstanceById error:$e');
      }
    }
    return null;
  }

  Future<RetainingPath> getRetainingPath(String targetId, int limit) async {
    VmService vmService = await getVMService();
    return vmService.getRetainingPath(isolateId!, targetId, limit);
  }

  Future<String?> invokeMethod(
      String targetId, String method, List<String> argumentIds) async {
    VmService vmService = await getVMService();

    final mainIsolate = await getMainIsolate();
    if (mainIsolate.id != null) {
      try {
        Response valueResponse = await vmService.invoke(
            mainIsolate.id!, targetId, method, argumentIds);
        final valueRef = InstanceRef.parse(valueResponse.json);
        return valueRef?.valueAsString;
      } catch (e) {}
    }
    return null;
  }

  // 强制触发GC
  Future<void> forceGC() async {
    VmService vmService = await VmserviceToolset().getVMService();

    final isolate = await VmserviceToolset().getMainIsolate();
    if (isolate.id != null) {
      await vmService.getAllocationProfile(isolate.id!, gc: true);
    }
  }
}

// ------ ObjectId 的获取 ------
int _key = 0;

/// 顶级函数，必须常规方法，生成 key 用
String generateKey() {
  return '${++_key}';
}

Map<String, dynamic> _objCache = {};

/// 顶级函数，根据 key 返回指定对象
dynamic keyToObj(String key) {
  return _objCache[key];
}

extension FieldValueInstance on Instance {
  BoundField? getField(String name) {
    if (fields == null) return null;
    for (int i = 0; i < fields!.length; i++) {
      var field = fields![i];
      if (field.decl?.name == name) {
        return field;
      }
    }
    return null;
  }

  dynamic getFieldValueInstance(String name) {
    final field = getField(name);
    if (field != null) {
      return field.value;
    }
    return null;
  }
}
