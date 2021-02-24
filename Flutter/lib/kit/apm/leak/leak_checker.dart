import 'package:dokit/kit/apm/leak/leak_obj.dart';
import 'package:dokit/kit/apm/vm/iso_pool.dart';
import 'package:dokit/kit/apm/vm/vm_helper.dart';
import 'package:dokit/kit/apm/vm/vm_service_wrapper.dart';
import 'package:vm_service/vm_service.dart';

import '../leak_kit.dart';

Expando<LeakInfo> expando;

Future<void> leakCheck(dynamic obj) async {
  // print(obj);
  if (!VMServiceWrapper.instance.connected || obj == null) {
    return;
  }
  expando = new Expando();
  expando[obj] = new LeakInfo();
  VMServiceWrapper.instance.service
      .getAllocationProfile(VMServiceWrapper.instance.main.id, gc: true);
  Future.delayed(Duration(seconds: 1), () {
    obj2Id(expando).then((id) => getRetainingPath(obj, id));
  });
}

void getRetainingPath(dynamic obj, String expandoId) async {
  String leakObjId = await IsoPool().start(_leakCheck, expandoId);
  expando[obj] = null;
  if (leakObjId != null) {
    // String RetainingPath = await IsoPool().start(_getRetainingPath, leakObjId);
    _getRetainingPath(leakObjId);
    // print(RetainingPath);
  } else {
    print('no leak:$obj');
  }
}

Future<String> _getRetainingPath(String leakObjId) async {
  if (!VMServiceWrapper.instance.connected) {
    await VMServiceWrapper.instance.connect();
  }
  if (VMServiceWrapper.instance.connected) {
    final mainId = VMServiceWrapper.instance.main.id;
    final service = VMServiceWrapper.instance.service;
    RetainingPath path =
        await service.getRetainingPath(mainId, leakObjId, 10000);
    print(path);
    path.elements.forEach((element) {
      if (element.value.type == '@Instance') {
        InstanceRef ref = element.value;
        // 过滤隐藏类，一般是系统类
        if (ref.classRef != null) {
          service.invoke(mainId, element.value.id, 'toString', []).then(
              (value) => printElement(ref.classRef.name, value as InstanceRef));
        }
      }
    });
    return path.toString();
  }
}

printElement(String className, InstanceRef value) {
  print('className:$className detail:${value.valueAsString}');
}

Future<String> _leakCheck(String objectId) async {
  if (!VMServiceWrapper.instance.connected) {
    await VMServiceWrapper.instance.connect();
  }
  if (VMServiceWrapper.instance.connected) {
    final mainId = VMServiceWrapper.instance.main.id;
    final service = VMServiceWrapper.instance.service;
    Instance instance = await service.getObject(mainId, objectId);
    BoundField field =
        instance.fields.firstWhere((element) => element.decl.name == '_data');
    instance = await service.getObject(mainId, field.value.id);
    InstanceRef ref = instance.elements.firstWhere((element) =>
        element != null && (element as InstanceRef).kind == 'WeakProperty');
    instance = await service.getObject(mainId, ref.id);
    return instance.propertyKey.id;
  }
}
