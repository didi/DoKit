import 'package:dokit/dokit.dart';
import 'package:dokit/kit/apm/leak/leak_obj.dart';
import 'package:dokit/kit/apm/apm.dart';
import 'package:dokit/kit/apm/fps_kit.dart';
import 'package:dokit/kit/apm/vm/vm_helper.dart';
import 'package:dokit/kit/apm/vm/vm_service_wrapper.dart';
import 'package:dokit/kit/kit.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/src/widgets/framework.dart';
import "package:vm_service/vm_service.dart";

class LeakKit extends ApmKit {
  Expando<LeakInfo> _expando = new Expando();

  @override
  Widget createDisplayPage() {
    return Container();
  }

  @override
  IStorage createStorage() {
    return CommonStorage(maxCount: 60);
  }

  @override
  String getKitName() {
    return ApmKitName.KIT_LEAK;
  }

  @override
  String getIcon() {
    Uri uri = Uri.parse('http://www.baidu.com');
    return 'images/dk_log_info.png';
  }

  static Element e;
  static FpsInfo fpsInfo = new FpsInfo();

  @override
  void start() {
    Future.delayed(Duration(seconds: 1), () {
      e = DoKitApp.rootKey.currentContext;
      _expando[e] = new LeakInfo();
      _expando[fpsInfo] = new LeakInfo();
      VMServiceWrapper.instance.gc();
      Future.delayed(Duration(seconds: 1), () {
        obj2Id(_expando).then((id) => printExpando(id));
      });
    });
  }

  void printExpando(String objectId) {}

  @override
  void stop() {}
}

class LeakInfo extends IInfo {
  @override
  getValue() {
    return 0;
  }
}
