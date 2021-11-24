// ignore_for_file: sort_child_properties_last

import 'package:dokit/dokit.dart';
import 'package:dokit/kit/apm/apm.dart';
import 'package:dokit/kit/biz/biz.dart';
import 'package:dokit/kit/common/common.dart';
import 'package:dokit/kit/kit.dart';
import 'package:dokit/kit/visual/visual.dart';
import 'package:dokit/ui/resident_page.dart';
import 'package:dokit/widget/dash_decoration.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

class KitPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return _KitPage();
  }
}

class _KitPage extends State<KitPage> {
  bool onDrag = false;
  final GlobalKey _residentContainerKey = GlobalKey();

  @override
  void initState() {
    super.initState();
    onDrag = false;
  }

  @override
  Widget build(BuildContext context) {
    final double width = MediaQuery.of(context).size.width;

    return SingleChildScrollView(
        child: Container(
            color: const Color(0xffffffff),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.start,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: <Widget>[
                Container(
                  margin: const EdgeInsets.only(
                      left: 10, right: 10, top: 15, bottom: 10),
                  child: Container(
                    key: _residentContainerKey,
                    decoration: onDrag
                        ? const DashedDecoration(
                            dashedColor: Colors.red,
                            borderRadius:
                                BorderRadius.all(Radius.circular(8.0)))
                        : null,
                    padding:
                        const EdgeInsets.only(left: 5, right: 5, bottom: 20),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: <Widget>[
                        Container(
                          alignment: Alignment.topLeft,
                          margin: const EdgeInsets.only(
                              left: 10, top: 10, bottom: 15),
                          child: RichText(
                              text: const TextSpan(children: <TextSpan>[
                            TextSpan(
                                text: '常驻工具',
                                style: TextStyle(
                                    fontSize: 16,
                                    color: Color(0xff333333),
                                    fontWeight: FontWeight.bold)),
                            TextSpan(
                                text: '  [最多放置4个]',
                                style: TextStyle(
                                  fontSize: 12,
                                  color: Color(0xff333333),
                                )),
                          ])),
                        ),
                        buildResidentView(context)
                      ],
                    ),
                    alignment: Alignment.center,
                  ),
                ),
                buildBizGroupView(context), // 自定义工具
                Container(
                    width: width, height: 12, color: const Color(0xfff5f6f7)),
                Container(
                  margin: const EdgeInsets.only(
                      left: 10, right: 10, top: 15, bottom: 10),
                  child: Container(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: <Widget>[
                        Container(
                          alignment: Alignment.topLeft,
                          margin: const EdgeInsets.only(
                              left: 10, top: 10, bottom: 15),
                          child: RichText(
                              text: const TextSpan(children: <TextSpan>[
                            TextSpan(
                                text: '其他工具',
                                style: TextStyle(
                                    fontSize: 16,
                                    color: Color(0xff333333),
                                    fontWeight: FontWeight.bold)),
                            TextSpan(
                                text: '  [拖动图标放入常驻工具]',
                                style: TextStyle(
                                  fontSize: 12,
                                  color: Color(0xff333333),
                                )),
                          ])),
                        ),
                        buildOtherView(context)
                      ],
                    ),
                    alignment: Alignment.center,
                  ),
                ),
              ],
            )));
  }

  bool inResidentContainerEdge(Offset? offset) {
    final Size? size = _residentContainerKey.currentContext?.size;
    if (offset == null || size == null) {
      return false;
    }

    final Offset position =
        (_residentContainerKey.currentContext?.findRenderObject() as RenderBox)
            .localToGlobal(Offset.zero);
    final Rect rc1 = Rect.fromLTWH(offset.dx, offset.dy, 80, 80);
    final Rect rc2 =
        Rect.fromLTWH(position.dx, position.dy, size.width, size.height);

    return rc1.left + rc1.width > rc2.left &&
        rc2.left + rc2.width > rc1.left &&
        rc1.top + rc1.height > rc2.top &&
        rc2.top + rc2.height > rc1.top;
  }

  Widget buildBizGroupView(BuildContext context) {
    final widgets = <Widget>[];
    final width = MediaQuery.of(context).size.width;
    var groupKeys = BizKitManager.instance.groupKeys();
    var counts = groupKeys.length;

    if (counts == 0) {
      return SizedBox();
    }

    for (var i = 0; i < counts; i++) {
      var key = groupKeys[i];
      var tip = BizKitManager.instance.kitGroupTips[key];
      widgets.add(
          Container(width: width, height: 12, color: const Color(0xfff5f6f7)));

      widgets.add(Container(
        margin: const EdgeInsets.only(left: 10, right: 10),
        child: Container(
          alignment: Alignment.center,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: <Widget>[
              Container(
                alignment: Alignment.topLeft,
                margin: const EdgeInsets.only(left: 10, top: 10, bottom: 15),
                child: RichText(
                    text: TextSpan(children: <TextSpan>[
                  TextSpan(
                      text: key, // 这块也是外部获取
                      style: TextStyle(
                          fontSize: 16,
                          color: Color(0xff333333),
                          fontWeight: FontWeight.bold)),
                  TextSpan(
                      text: '  $tip', // 外部获取
                      style: TextStyle(
                        fontSize: 12,
                        color: Color(0xff333333),
                      )),
                ])),
              ),
              // 这里的数据从BizMananger中获取
              buildBizKitView(context, key)
            ],
          ),
        ),
      ));

      widgets.add(Container(
          width: width,
          height: 12,
          color: Colors.white));
    }

    final wrap = Wrap(
      children: widgets,
    );
    return wrap;
  }

  Widget buildBizKitView(BuildContext context, String key) {
    final List<Widget> widgets = <Widget>[];
    final double round = (MediaQuery.of(context).size.width - 80 * 4 - 30) / 3;
    BizKitManager.instance.kitGroupMap[key]!.forEach((IKit value) {
      widgets.add(
        MaterialButton(
            child: KitItem(value),
            onPressed: () {
              setState(() {
                value.tabAction();
              });
            },
            padding: const EdgeInsets.all(0),
            minWidth: 40),
      );
    });
    final wrap = Wrap(
      spacing: round,
      runSpacing: 15,
      children: widgets,
    );
    return wrap;
  }

  Widget buildResidentView(BuildContext context) {
    final List<Widget> widgets = <Widget>[];
    final double round = (MediaQuery.of(context).size.width - 80 * 4 - 30) / 3;
    KitPageManager.instance.getResidentKit().forEach((String key, IKit value) {
      widgets.add(
        Draggable(
          child: MaterialButton(
              child: KitItem(value),
              onPressed: () {
                setState(() {
                  value.tabAction();
                });
              },
              padding: const EdgeInsets.all(0),
              minWidth: 40),
          feedback: KitItem(value),
          onDragStarted: () {
            setState(() {
              onDrag = true;
            });
          },
          onDraggableCanceled: (Velocity velocity, Offset offset) {
            setState(() {
              onDrag = false;
              if (!inResidentContainerEdge(offset)) {
                KitPageManager.instance.removeResidentKit(key);
              }
            });
          },
          onDragEnd: (DraggableDetails detail) {
            setState(() {
              onDrag = false;
              if (!inResidentContainerEdge(detail.offset)) {
                KitPageManager.instance.removeResidentKit(key);
              }
            });
          },
        ),
      );
    });
    final Wrap wrap = Wrap(
      spacing: round,
      runSpacing: 15,
      children: widgets,
    );
    return wrap;
  }

  Widget buildOtherView(BuildContext context) {
    final List<Widget> widgets = <Widget>[];
    final double round = (MediaQuery.of(context).size.width - 80 * 4 - 30) / 3;
    KitPageManager.instance.getOtherKit().forEach((String key, IKit value) {
      widgets.add(
        Draggable(
          child: MaterialButton(
              child: KitItem(value),
              onPressed: () {
                setState(() {
                  value.tabAction();
                });
              },
              padding: const EdgeInsets.all(0),
              minWidth: 40),
          feedback: KitItem(value),
          onDragStarted: () {
            setState(() {
              onDrag = true;
            });
          },
          onDragEnd: (DraggableDetails detail) {
            setState(() {
              if (inResidentContainerEdge(detail.offset)) {
                KitPageManager.instance.addResidentKit(key);
              }
              onDrag = false;
            });
          },
          onDraggableCanceled: (Velocity v, Offset offset) {
            setState(() {
              if (inResidentContainerEdge(offset)) {
                KitPageManager.instance.addResidentKit(key);
              }
              onDrag = false;
            });
          },
        ),
      );
    });
    final wrap = Wrap(
      spacing: round,
      runSpacing: 15,
      children: widgets,
    );
    return wrap;
  }
}

class KitItem extends StatelessWidget {
  const KitItem(this.kit);

  final IKit kit;

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 80,
      alignment: Alignment.center,
      child: Column(
        children: <Widget>[
          Image.asset(
            kit.getIcon(),
            width: 34,
            height: 34,
            fit: BoxFit.fitWidth,
            package: DK_PACKAGE_NAME,
          ),
          Container(
            margin: const EdgeInsets.only(top: 6),
            child: Text(kit.getKitName(),
                style: const TextStyle(
                    fontFamily: 'PingFang SC',
                    fontSize: 12,
                    fontWeight: FontWeight.normal,
                    decoration: TextDecoration.none,
                    color: Color(0xff666666))),
          ),
        ],
      ),
    );
  }
}

class KitPageManager {
  KitPageManager._privateConstructor();

  static const String KIT_ALL = '全部';
  static const String KEY_KIT_PAGE_CACHE = 'key_kit_page_cache';
  List<String> residentList = <String>[
    ApmKitName.KIT_LOG,
    ApmKitName.KIT_CHANNEL
  ];

  static final KitPageManager _instance = KitPageManager._privateConstructor();

  static KitPageManager get instance => _instance;

  String listToString(List<String>? list) {
    if (list == null || list.isEmpty) {
      return '';
    }
    String? result;
    for (final String item in list) {
      if (result == null) {
        result = item;
      } else {
        result = '${result},${item}';
      }
    }

    return result.toString();
  }

  bool addResidentKit(String? tag) {
    assert(tag != null);
    if (!residentList.contains(tag)) {
      if (residentList.length >= 4) {
        return false;
      }
      residentList.add(tag!);
      SharedPreferences.getInstance().then((SharedPreferences prefs) =>
          prefs.setString(KEY_KIT_PAGE_CACHE, listToString(residentList)));
      return true;
    }
    return false;
  }

  bool removeResidentKit(String tag) {
    if (residentList.contains(tag)) {
      residentList.remove(tag);
      SharedPreferences.getInstance().then((SharedPreferences prefs) =>
          prefs.setString(KEY_KIT_PAGE_CACHE, listToString(residentList)));
      return true;
    }
    return false;
  }

  Map<String, IKit> getOtherKit() {
    final Map<String, IKit> kits = <String, IKit>{};
    CommonKitManager.instance.kitMap.forEach((String key, CommonKit value) {
      if (!residentList.contains(key)) {
        kits[key] = value;
      }
    });
    ApmKitManager.instance.kitMap.forEach((String key, ApmKit value) {
      if (!residentList.contains(key)) {
        kits[key] = value;
      }
    });
    VisualKitManager.instance.kitMap.forEach((String key, IKit value) {
      if (!residentList.contains(key)) {
        kits[key] = value;
      }
    });
    return kits;
  }

  Map<String, IKit> getResidentKit() {
    final Map<String, IKit> kits = <String, IKit>{};

    for (final String element in residentList) {
      if (ApmKitManager.instance.getKit(element) != null) {
        kits[element] = ApmKitManager.instance.getKit(element)!;
      } else if (VisualKitManager.instance.getKit(element) != null) {
        kits[element] = VisualKitManager.instance.getKit(element)!;
      } else if (CommonKitManager.instance.getKit(element) != null) {
        kits[element] = CommonKitManager.instance.getKit(element)!;
      }
    }
    return kits;
  }

  void loadCache() {
    SharedPreferences.getInstance().then<dynamic>((SharedPreferences prefs) {
      if (prefs.getString(KitPageManager.KEY_KIT_PAGE_CACHE) != null) {
        if (prefs.getString(KitPageManager.KEY_KIT_PAGE_CACHE) == '') {
          KitPageManager.instance.residentList = <String>[];
        } else {
          KitPageManager.instance.residentList =
              prefs.getString(KitPageManager.KEY_KIT_PAGE_CACHE)?.split(',') ??
                  [];
        }
      }
      if (KitPageManager.instance.residentList.isNotEmpty) {
        ResidentPage.tag = KitPageManager.instance.residentList.first;
      } else {
        ResidentPage.tag = KitPageManager.KIT_ALL;
      }
    });
  }
}
