// ignore_for_file: omit_local_variable_types

import 'dart:async';

import 'package:dokit/kit/apm/leaks/leaks_doctor_conf.dart';
import 'package:dokit/kit/apm/vm/vm_service_toolset.dart';
import 'package:flutter/foundation.dart';
import 'package:vm_service/vm_service.dart';

import 'leaks_doctor_data.dart';
import 'leaks_doctor_event.dart';

abstract class _Task<T> {
  void start() async {
    T? result;
    try {
      result = await run();
    } catch (e) {
      print('_Task $e');
    } finally {
      done(result);
    }
  }

  Future<T?> run();
  void done(T? result);
}

class LeaksDoctorTask extends _Task {
  Expando? expando;

  final Function()? onCompleted;
  final Function(LeaksMsgInfo? leakInfo)? onLeaked;
  final StreamSink<LeaksDoctorEvent>? sink;
  final String? group;

  LeaksDoctorTask(this.expando,this.group,
      {required this.onCompleted,
      required this.onLeaked,
      this.sink});

  @override
  void done(Object? result) {
    onCompleted?.call();
  }

  @override
  Future<LeaksMsgInfo?> run() async {
    if (expando != null) {
      if (await _maybeLeaked()) {
        // 强制GC，确保对象Release
        sink?.add(LeaksDoctorEvent(LeaksDoctorEventType.GcStart,data: group));
        await VmserviceToolset().forceGC(); //GC
        sink?.add(LeaksDoctorEvent(LeaksDoctorEventType.GcEnd,data: group));
        return _afterGC();
      }
    }
    return null;
  }

  //完全GC后，检查是否存在泄漏，
  Future<LeaksMsgInfo?> _afterGC() async {
    List<dynamic> weakPropertyList = await _getWeakPropertys(expando!);
    expando = null; //一定要释放引用
    for (var weakProperty in weakPropertyList) {
      if (weakProperty != null) {
        final leakedInstance = await _getWeakPropertyKey(weakProperty.id);
        if (leakedInstance != null) {
          final start = DateTime.now();
          sink?.add(LeaksDoctorEvent(LeaksDoctorEventType.AnalyzeStart,data: '$group : ${DateTime.now()}'));

          LeaksMsgInfo? leaksMsgInfo = await compute(analyzeLeaks,leakedInstance); 

          sink?.add(LeaksDoctorEvent(LeaksDoctorEventType.AnalyzeEnd,
              data: '$group : ${DateTime.now()} diff = ${DateTime.now().difference(start)}'));

          onLeaked?.call(leaksMsgInfo);
        }
      }
    }
    sink?.add(LeaksDoctorEvent(LeaksDoctorEventType.AllEnd,data: group));
  }

  // 可能有泄漏
  Future<bool> _maybeLeaked() async {
    List<dynamic> weakPropertyList =
        await _getWeakPropertys(expando!);
    for (var weakProperty in weakPropertyList) {
      if (weakProperty != null) {
        final leakedInstance = await _getWeakPropertyKey(weakProperty.id);
        if (leakedInstance != null) return true;
      }
    }
    return false;
  }

  // 获取WeakProperty列表
  Future<List<dynamic>> _getWeakPropertys(Expando expando) async {
    // Dart 语言中也有着弱引用，它叫 Expando. Expando 弱引用持有的是 key，这个key就是它若持有的对象
    // 如果 GC 之后对象为 null，说明被回收了，如果不为 null 就可能是泄漏了。
    // 这个 key 对象是放到了 Expando的 _data 数组内，用了一个 _WeakProperty 来包裹。
    // _WeakProperty这个类有我们想要的 key，可以用于判断对象是否还在。
    final data = (await VmserviceToolset().getInstanceByObject(expando))
        ?.getFieldValueInstance('_data');
    if (data?.id != null) {
      final dataObj = await VmserviceToolset().getObjectInstanceById(data.id);
      if (dataObj?.json != null) {
        Instance? weakListInstance = Instance.parse(dataObj!.json!);
        if (weakListInstance != null) {
          return weakListInstance.elements ?? [];
        }
      }
    }

    return [];
  }

  // 获取 PropertyKey in [Expando]
  Future<InstanceRef?> _getWeakPropertyKey(String weakPropertyId) async {
    final weakPropertyObj =
        await VmserviceToolset().getObjectInstanceById(weakPropertyId);

    if (weakPropertyObj != null) {
      final weakPropertyInstance = Instance.parse(weakPropertyObj.json);

      return weakPropertyInstance?.propertyKey;
    }
    return null;
  }

  static Future<LeaksMsgInfo?> analyzeLeaks(InstanceRef? leakedInstance) async {
    if (leakedInstance?.id != null) {
      RetainingPath? retainingPath = await _getRetainPath(leakedInstance!);
      if (retainingPath != null && retainingPath.elements != null &&
          retainingPath.elements!.isNotEmpty) {
        final retainObjList = retainingPath.elements!;

        final instanceSet = await VmserviceToolset()
            .getInstances(leakedInstance.classRef!.id!, 300);
        int? leaksInstanceCounts = instanceSet.totalCount;
        String? clzName = leakedInstance.classRef!.name;

        final stream = Stream.fromIterable(retainObjList)
            .asyncMap<LeaksMsgNode?>(_buildAnalyzeNode);
        List<LeaksMsgNode> retainingPathList = [];
        (await stream.toList()).forEach((e) {
          if (e != null) {
            retainingPathList.add(e);
          }
        });

        // 支持用户自定义设定条件
        var expectedTotalCount = 0;
        if (clzName != null) {
          final ret = LeaksDoctorConf().searchPolicy(clzName) ?? 0;
          if (ret > 0 && leaksInstanceCounts != null) {
            // 如果期望的对象数和实际对象数相同或者期望对象数大于实际对象数，符号预期，不认为是一次泄漏
            if (ret >= leaksInstanceCounts) {
              return null;
            }
            expectedTotalCount = ret;
          }
        }

        return LeaksMsgInfo(retainingPathList, retainingPath.gcRootType!,
            leaksInstanceCounts: leaksInstanceCounts, leaksClsName: clzName, expectedTotalCount: expectedTotalCount);
      }
    }
    return null;
  }

   static Future<RetainingPath?> _getRetainPath(InstanceRef ref) async {
    try {
      final maxRetainingPathLimit = LeaksDoctorConf().maxRetainingPathLimit!;
      return await VmserviceToolset()
          .getRetainingPath(ref.id!, maxRetainingPathLimit);
    } on SentinelException catch (e) {
      // 有可能已经被回收了 ~
      if (e.sentinel.kind == SentinelKind.kCollected || e.sentinel.kind == SentinelKind.kExpired) {
        return null;
      }
      // 其它错误
      rethrow;
    }
  }

  static void _analyzeRetainingObject(RetainingObject ele) {
    // 引用路径其中一个节点
    ObjRef ref = ele.value!;
    String name = ref.id!;
    if (ref is InstanceRef) {
      // 函数引用，如匿名函数 <anonymous closure>
      if (ref.kind == InstanceKind.kClosure) {
        List<String?> chain = [ref.closureFunction!.name];
        var owner = ref.closureFunction!.owner;
        while (owner is FuncRef) {
          chain.add(owner.name);
          owner = owner.owner;
        }
        if (owner != null) {
          chain.add(owner.name);
        }
        name = chain.reversed.join('.');
      }
    } else if (ref is FieldRef) {
      if (ref.isStatic == true) {
        // 这是个全局静态field
        name = '${ref.name} (static)';
      }
    } else if (ref is ContextRef) {
      // 匿名函数的 context
      name = '<Closure Context>';
    }
    // 引用路径节点的父节点 field
    final String parentField = ele.parentField ?? '';
    print('+ $name   $parentField');
}

  static Future<LeaksMsgNode?> _buildAnalyzeNode(
      RetainingObject retainingObject) async {

    _analyzeRetainingObject(retainingObject); 
      
    if (retainingObject.value is InstanceRef) {
      InstanceRef instanceRef = retainingObject.value as InstanceRef;
      final String name = instanceRef.classRef?.name ?? '';
      final String type = instanceRef.kind ?? '';

      CodeLocation? codeLocation;

      codeLocation =
          await _getCodeLocation(instanceRef.classRef!.location, name);

      //if is Map, get Key info.
      String? keyString = await _getKeyInfo(retainingObject);
      ClosureNode? closureNode;
      if (retainingObject.value?.json != null) {
        closureNode =
            await _getClosureNode(Instance.parse(retainingObject.value!.json));
      }

      Class? clazz;
      if (instanceRef.classRef?.id != null) {
        clazz = (await VmserviceToolset()
            .getObjectInstanceById(instanceRef.classRef!.id!)) as Class?;
      }
      final leakedNodeType = await _getObjectType(clazz);

      return LeaksMsgNode(name,
          declaredType: type,
          parentField: retainingObject.parentField?.toString(),
          parentListIndex: retainingObject.parentListIndex,
          parentKey: keyString,
          libraries: codeLocation?.uri,
          codeLocation: codeLocation,
          closureNode: closureNode,
          leakedNodeType: leakedNodeType);
    } else if (retainingObject.value is FieldRef) {
      FieldRef fieldRef = retainingObject.value as FieldRef;
      final String name = fieldRef.name!;
      final String? declaredType = fieldRef.declaredType?.name;

      // 可以拿到引用到当前Field的代码信息，比如查看哪哪哪代码使用了这个field
      // vmst.getInboundReferences(fieldRef.id!).then((value) {
      //   print(value);
      // });

      CodeLocation? codeLocation =
          await _getCodeLocation(fieldRef.location, null);

      ClassRef ownerClassRef = fieldRef.owner as ClassRef;
      String? ownerName = ownerClassRef.name;
      String? ownerType = ownerClassRef.type;
      String? ownerLibraries = ownerClassRef.library!.uri;

      FieldOwnerNode fieldOwnerNode = FieldOwnerNode(
          ownerName: ownerName,
          ownerType: ownerType,
          libraries: ownerLibraries);
      return LeaksMsgNode(name,
          declaredType: declaredType,
          fieldOwnerNode: fieldOwnerNode,
          libraries: codeLocation?.uri,
          codeLocation: codeLocation);
    } else if (retainingObject.value?.type != '@Context') {
      return LeaksMsgNode(
        retainingObject.value?.type ?? '',
        parentField: retainingObject.parentField?.toString(),
      );
    }
  }

  static Future<String?> _getKeyInfo(RetainingObject retainingObject) async {
    String? keyString;
    if (retainingObject.parentMapKey?.id != null) {
      Obj? keyObj = await VmserviceToolset()
          .getObjectInstanceById(retainingObject.parentMapKey!.id!);
      if (keyObj?.json != null) {
        Instance? keyInstance = Instance.parse(keyObj!.json!);
        if (keyInstance != null &&
            (keyInstance.kind == 'String' ||
                keyInstance.kind == 'Int' ||
                keyInstance.kind == 'Double' ||
                keyInstance.kind == 'Bool')) {
          keyString = '${keyInstance.kind}: \'${keyInstance.valueAsString}\'';
        } else {
          if (keyInstance?.id != null) {
            keyString =
                'Object: class=${keyInstance?.classRef?.name}, ${await VmserviceToolset().invokeMethod(keyInstance!.id!, 'toString', [])}';
          }
        }
      }
    }
    return keyString;
  }

  static Future<LeakedNodeType> _getObjectType(Class? clazz) async {
    if (clazz?.name == null) return LeakedNodeType.unknown;
    if (clazz!.name == 'Widget') {
      return LeakedNodeType.widget;
    } else if (clazz.name == 'Element') {
      return LeakedNodeType.element;
    }
    if (clazz.superClass?.id != null) {
      Class? superClass = (await VmserviceToolset()
          .getObjectInstanceById(clazz.superClass!.id!)) as Class?;
      return _getObjectType(superClass);
    } else {
      return LeakedNodeType.unknown;
    }
  }

  static Future<CodeLocation?> _getCodeLocation(
      SourceLocation? location, String? clazzName) async {
    if (location == null) {
      return null;
    }

    CodeLocation? codeLocation;
    if (location.script!.id != null) {
      Script? script = (await VmserviceToolset()
          .getObjectInstanceById(location.script!.id!)) as Script?;
      if (script != null) {
        int? line = script.getLineNumberFromTokenPos(location.tokenPos!);
        int? column = script.getColumnNumberFromTokenPos(location.tokenPos!);
        String? code;
        code = script.source
            ?.substring(location.tokenPos!, location.endTokenPos)
            .split('\n')
            .first;
        codeLocation = CodeLocation(code, line, column, clazzName, script.uri);
      }
    }
    return codeLocation;
  }

  static Future<ClosureNode?> _getClosureNode(Instance? instance) async {
    if (instance != null && instance.kind == 'Closure') {
      final name = instance.closureFunction?.name;
      final owner = instance.closureFunction?.owner;
      final info = ClosureNode(functionName: name, closureOwner: owner?.name);
      await _getClosureOwner(owner, info);
      return info;
    }
    return null;
  }

  static Future<void> _getClosureOwner(dynamic ref, ClosureNode node) async {
    if (ref?.id == null) return;
    if (ref is LibraryRef) {
      Library? library = (await VmserviceToolset()
          .getObjectInstanceById((ref).id!)) as Library?;
      node.libraries = library?.uri;
    } else if (ref is ClassRef) {
      Class? clazz =
          (await VmserviceToolset().getObjectInstanceById(ref.id!)) as Class?;
      node.ownerClass = clazz?.name;
      node.libraries = clazz?.library?.uri;
    } else if (ref is FuncRef) {
      if (node.funLine == null) {
        Func? func =
            (await VmserviceToolset().getObjectInstanceById(ref.id!)) as Func?;
        if (func?.location?.script?.id != null) {
          Script? script = (await VmserviceToolset()
              .getObjectInstanceById(func!.location!.script!.id!)) as Script?;
          if (script != null && func.location?.tokenPos != null) {
            node.funLine =
                script.getLineNumberFromTokenPos(func.location!.tokenPos!);
            node.funColumn =
                script.getColumnNumberFromTokenPos(func.location!.tokenPos!);
          }
        }
      }
      await _getClosureOwner(ref.owner, node);
    }
  }
}
