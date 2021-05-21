import 'dart:ui';

import 'package:dokit/dokit.dart';
import 'package:dokit/kit/apm/apm.dart';
import 'package:dokit/kit/apm/vm/vm_helper.dart';
import 'package:dokit/kit/apm/vm/vm_service_wrapper.dart';
import 'package:dokit/kit/kit.dart';
import 'package:dokit/util/byte_util.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:vm_service/vm_service.dart';

class MemoryInfo implements IInfo {
  int? fps;
  String? pageName;

  @override
  int? getValue() {
    return fps;
  }
}

class MemoryKit extends ApmKit {
  int lastFrame = 0;

  @override
  String getKitName() {
    return ApmKitName.KIT_MEMORY;
  }

  @override
  String getIcon() {
    return 'images/dk_ram.png';
  }

  @override
  void start() {
    VMServiceWrapper.instance.connect();
    VmHelper vmHelper = VmHelper.instance;
    VMServiceWrapper.instance
        .connect()
        .then((value) => vmHelper.resolveVMInfo());
  }

  void update() {
    VmHelper.instance.updateAllocationProfile();
    VmHelper.instance.updateFlutterVersion();
    VmHelper.instance.updateMemoryUsage();
  }

  AllocationProfile? getAllocationProfile() {
    return VmHelper.instance.allocationProfile;
  }

  @override
  void stop() {
    VMServiceWrapper.instance.disConnect();
  }

  @override
  IStorage createStorage() {
    return CommonStorage(maxCount: 120);
  }

  @override
  Widget createDisplayPage() {
    return MemoryPage();
  }
}

class MemoryPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return MemoryPageState();
  }
}

class MemoryPageState extends State<MemoryPage> {
  MemoryKit? kit =
      ApmKitManager.instance.getKit<MemoryKit>(ApmKitName.KIT_MEMORY);
  List<ClassHeapStats> heaps = [];
  TextEditingController editingController = TextEditingController();

  @override
  void initState() {
    super.initState();
    kit?.update();
    initHeaps();
  }

  void initHeaps() {
    if (kit?.getAllocationProfile() != null) {
      kit!.getAllocationProfile()?.members?.sort((left, right) =>
          right.bytesCurrent?.compareTo(left.bytesCurrent ?? 0) ?? 0);
      kit?.getAllocationProfile()?.members?.forEach((element) {
        if (heaps.length < 32) {
          heaps.add(element);
        }
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
        child: Container(
            margin: EdgeInsets.all(16),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.start,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: <Widget>[
                Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: <Widget>[
                      Text('Memory Info',
                          style: TextStyle(
                              color: Color(0xff333333),
                              fontWeight: FontWeight.bold,
                              fontSize: 16)),
                      StreamBuilder(
                        stream: Stream.periodic(Duration(seconds: 2), (value) {
                          VmHelper.instance.updateAllocationProfile();
                          VmHelper.instance.updateMemoryUsage();
                        }),
                        builder: (context, snapshot) {
                          return Container(
                            margin: EdgeInsets.only(top: 3),
                            alignment: Alignment.topLeft,
                            child: VmHelper.instance.memoryInfo.isNotEmpty
                                ? Column(
                                    children: getMemoryInfo(
                                        VmHelper.instance.memoryInfo))
                                : Text('获取Memory数据失败(release模式下无法获取数据)',
                                    style: TextStyle(
                                        color: Color(0xff999999),
                                        fontSize: 12)),
                          );
                        },
                      )
                    ]),
                Container(
                  margin: EdgeInsets.only(top: 10),
                  alignment: Alignment.centerLeft,
                  padding: EdgeInsets.only(left: 13),
                  height: 50,
                  decoration: BoxDecoration(
                    border: Border.all(
                        color: Color(0xff337cc4),
                        width: 0.5,
                        style: BorderStyle.solid),
                    borderRadius: BorderRadius.all(Radius.circular(4)),
                  ),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: <Widget>[
                      Container(
                        child: TextField(
                          controller: editingController,
                          style:
                              TextStyle(color: Color(0xff333333), fontSize: 16),
                          inputFormatters: [
                            FilteringTextInputFormatter.deny(RegExp(
                                '[^\\u0020-\\u007E\\u00A0-\\u00BE\\u2E80-\\uA4CF\\uF900-\\uFAFF\\uFE30-\\uFE4F\\uFF00-\\uFFEF\\u0080-\\u009F\\u2000-\\u201f\r\n]'))
                          ],
                          onSubmitted: (value) => {filterAllocations()},
                          decoration: InputDecoration(
                            border: InputBorder.none,
                            hintStyle: TextStyle(
                                color: Color(0xffbebebe), fontSize: 16),
                            hintText: '输入类名，查看内存占用',
                          ),
                        ),
                        width: MediaQuery.of(context).size.width - 150,
                      ),
                      Container(
                        width: 60,
                        child: TextButton(
                          style: ButtonStyle(
                            padding: MaterialStateProperty.all(EdgeInsets.only(
                                left: 15, right: 0, top: 15, bottom: 15)),
                          ),
                          child: Image.asset('images/dk_memory_search.png',
                              package: DK_PACKAGE_NAME, height: 16, width: 16),
                          onPressed: filterAllocations,
                        ),
                      )
                    ],
                  ),
                ),
                Container(
                  margin: EdgeInsets.only(top: 12),
                  height: 34,
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: <Widget>[
                      Container(
                        width: 80,
                        decoration: BoxDecoration(
                            color: Color(0xff337cc4),
                            borderRadius: BorderRadius.only(
                                topLeft: Radius.circular(4),
                                bottomLeft: Radius.circular(4))),
                        alignment: Alignment.center,
                        child: Text('Size',
                            style: TextStyle(
                                color: Color(0xffffffff), fontSize: 14)),
                      ),
                      VerticalDivider(
                        width: 0.5,
                        color: Color(0xffffffff),
                      ),
                      Container(
                        width: 80,
                        decoration: BoxDecoration(
                          color: Color(0xff337cc4),
                        ),
                        alignment: Alignment.center,
                        child: Text('Count',
                            style: TextStyle(
                                color: Color(0xffffffff), fontSize: 14)),
                      ),
                      VerticalDivider(
                        width: 0.5,
                        color: Color(0xffffffff),
                      ),
                      Container(
                          decoration: BoxDecoration(
                              color: Color(0xff337cc4),
                              borderRadius: BorderRadius.only(
                                  topRight: Radius.circular(4),
                                  bottomRight: Radius.circular(4))),
                          width: MediaQuery.of(context).size.width - 193,
                          alignment: Alignment.center,
                          child: Text('ClassName',
                              style: TextStyle(
                                  color: Color(0xffffffff), fontSize: 14))),
                    ],
                  ),
                ),
                Container(
                  height: MediaQuery.of(context).size.height - 200 - 210,
                  child: ListView.builder(
                      padding: EdgeInsets.all(0),
                      itemCount: heaps.length,
                      itemBuilder: (context, index) {
                        return HeapItemWidget(
                          item: heaps[index],
                          index: index,
                        );
                      }),
                ),
              ],
            )));
  }

  void filterAllocations() {
    String className = editingController.text;
    heaps.clear();
    if (className.length >= 3 && kit?.getAllocationProfile() != null) {
      kit?.getAllocationProfile()?.members?.forEach((element) {
        if (element.classRef?.name
                ?.toLowerCase()
                .contains(className.toLowerCase()) ==
            true) {
          heaps.add(element);
        }
      });
      heaps.sort((left, right) =>
          right.bytesCurrent?.compareTo(left.bytesCurrent ?? 0) ?? 0);
    }
    setState(() {});
  }

  List<Widget> getMemoryInfo(Map<IsolateRef, MemoryUsage> map) {
    List<Widget> widgets = <Widget>[];
    map.forEach((key, value) {
      widgets.add(RichText(
          text: TextSpan(children: [
        TextSpan(
            text: 'IsolateName: ',
            style:
                TextStyle(fontSize: 10, color: Color(0xff333333), height: 1.5)),
        TextSpan(
            text: '${key.name}',
            style:
                TextStyle(fontSize: 10, height: 1.5, color: Color(0xff666666))),
        TextSpan(
            text: '\nHeapUsage: ',
            style:
                TextStyle(height: 1.5, fontSize: 10, color: Color(0xff333333))),
        TextSpan(
            text: '${toByteString(value.heapUsage)}',
            style:
                TextStyle(fontSize: 10, height: 1.5, color: Color(0xff666666))),
        TextSpan(
            text: '\nHeapCapacity: ',
            style:
                TextStyle(fontSize: 10, height: 1.5, color: Color(0xff333333))),
        TextSpan(
            text: '${toByteString(value.heapCapacity)}',
            style:
                TextStyle(fontSize: 10, height: 1.5, color: Color(0xff666666))),
        TextSpan(
            text: '\nExternalUsage: ',
            style:
                TextStyle(fontSize: 10, height: 1.5, color: Color(0xff333333))),
        TextSpan(
            text: '${toByteString(value.externalUsage)}',
            style:
                TextStyle(fontSize: 10, height: 1.5, color: Color(0xff666666))),
      ])));
    });
    return widgets;
  }
}

class HeapItemWidget extends StatelessWidget {
  final ClassHeapStats item;
  final int index;

  HeapItemWidget({Key? key, required this.item, required this.index})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 40,
      color: index % 2 == 1 ? Color(0xfffafafa) : Colors.white,
      child: Row(
        mainAxisAlignment: MainAxisAlignment.start,
        children: <Widget>[
          Container(
            width: 80,
            alignment: Alignment.center,
            child: Text('${toByteString(item.bytesCurrent)}',
                style: TextStyle(color: Color(0xff333333), fontSize: 12)),
          ),
          Container(
            width: 80,
            alignment: Alignment.center,
            child: Text('${item.instancesCurrent}',
                style: TextStyle(color: Color(0xff333333), fontSize: 12)),
          ),
          Container(
              width: MediaQuery.of(context).size.width - 193,
              alignment: Alignment.center,
              child: Text('${item.classRef?.name}',
                  style: TextStyle(color: Color(0xff333333), fontSize: 12))),
        ],
      ),
    );
  }
}
