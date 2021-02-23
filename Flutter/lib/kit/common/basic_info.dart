import 'package:dokit/kit/apm/vm_helper.dart';
import 'package:dokit/kit/common/common.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:vm_service/src/vm_service.dart';

class BasicInfoKit extends CommonKit {
  @override
  String getIcon() {
    return 'images/dk_sys_info.png';
  }

  @override
  String getKitName() {
    return CommonKitName.KIT_BASIC_INFO;
  }

  @override
  Widget createDisplayPage() {
    return BasicInfoPage();
  }
}

class BasicInfoPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
        child: Container(
            padding: const EdgeInsets.only(left: 16, right: 16),
            color: const Color(0xffffff),
            child: Column(
              children: buildAppInfo(),
            )));
  }

  List<Widget> buildAppInfo() {
    final List<Widget> list = <Widget>[];
    list.add(Container(
        height: 56,
        alignment: Alignment.centerLeft,
        child: const Text('VM信息 ' + (!kReleaseMode ? '' : '[release模式下不可用]'),
            style: TextStyle(fontSize: 14, color: Color(0xff999999)))));
    list.add(InfoItem('CPU', VmHelper.instance.vm?.hostCPU));
    list.add(const Divider(height: 0.5, color: Color(0xffeeeeee)));
    list.add(InfoItem('Dart虚拟机', VmHelper.instance.vm?.version));
    list.add(const Divider(height: 0.5, color: Color(0xffeeeeee)));
    list.add(InfoItem('Flutter版本', VmHelper.instance.flutterVersion));
    list.add(const Divider(height: 0.5, color: Color(0xffeeeeee)));
    String isolate;
    const int index = 1;
    final List<IsolateRef> isolates = VmHelper.instance.vm?.isolates;
    for (final IsolateRef element in isolates) {
      if (isolate == null) {
        isolate = '[isolate$index]: ${element.name} ${element.type}\n';
      } else {
        isolate += '[isolate$index]: ${element.name} ${element.type}\n';
      }
    }

    if (isolate != null && isolate.length > 1) {
      isolate = isolate.substring(0, isolate.length - 1);
    }
    list.add(InfoItem('Isolates', isolate));
    list.add(const Divider(height: 0.5, color: Color(0xffeeeeee)));

    list.add(Container(
        height: 56,
        alignment: Alignment.centerLeft,
        child: const Text('Package信息',
            style: TextStyle(fontSize: 14, color: Color(0xff999999)))));
    list.add(InfoItem('App包名', VmHelper.instance.packageInfo?.packageName));
    list.add(const Divider(height: 0.5, color: Color(0xffeeeeee)));
    list.add(InfoItem(
        'Module版本',
        (VmHelper.instance.packageInfo == null)
            ? '-'
            : '${VmHelper.instance.packageInfo?.version}+${VmHelper.instance.packageInfo?.buildNumber}'));
    list.add(const Divider(height: 0.5, color: Color(0xffeeeeee)));
    return list;
  }
}

class InfoItem extends StatelessWidget {
  const InfoItem(this.label, this.text);

  final String label;
  final String text;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.only(top: 14, bottom: 14),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: <Widget>[
          Text(
            label,
            style: const TextStyle(
              fontSize: 16,
              color: Color(0xff333333),
            ),
          ),
          Expanded(
            child: Container(
                margin: const EdgeInsets.only(left: 10),
                child: Text(
                  text ?? '-',
                  textAlign: TextAlign.end,
                  style: const TextStyle(
                    fontSize: 16,
                    color: Color(0xff666666),
                  ),
                )),
          )
        ],
      ),
    );
  }
}
