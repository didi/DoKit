import 'package:dokit/kit/apm/apm.dart';
import 'package:dokit/kit/common/common.dart';
import 'package:dokit/kit/kit.dart';
import 'package:dokit/kit/visual/visual.dart';
import 'package:dokit/ui/resident_page.dart';
import 'package:dokit/widget/dash_decoration.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../dokit.dart';

class KitPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return _KitPage();
  }
}

class _KitPage extends State<KitPage> {
  bool onDrag = false;
  GlobalKey _residentContainerKey = new GlobalKey();

  @override
  void initState() {
    super.initState();
    onDrag = false;
  }

  @override
  Widget build(BuildContext context) {
    double width = MediaQuery.of(context).size.width;

    return SingleChildScrollView(
        child: Container(
            color: Color(0xffffffff),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.start,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: <Widget>[
                Container(
                  margin:
                      EdgeInsets.only(left: 10, right: 10, top: 15, bottom: 10),
                  child: Container(
                    key: _residentContainerKey,
                    decoration: onDrag
                        ? DashedDecoration(
                            dashedColor: Colors.red,
                            borderRadius:
                                const BorderRadius.all(Radius.circular(8.0)))
                        : null,
                    padding: EdgeInsets.only(left: 5, right: 5, bottom: 20),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: <Widget>[
                        Container(
                          alignment: Alignment.topLeft,
                          margin:
                              EdgeInsets.only(left: 10, top: 10, bottom: 15),
                          child: RichText(
                              text: TextSpan(children: [
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
                Container(width: width, height: 12, color: Color(0xfff5f6f7)),
                Container(
                  margin:
                      EdgeInsets.only(left: 10, right: 10, top: 15, bottom: 10),
                  child: Container(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: <Widget>[
                        Container(
                          alignment: Alignment.topLeft,
                          margin:
                              EdgeInsets.only(left: 10, top: 10, bottom: 15),
                          child: RichText(
                              text: TextSpan(children: [
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

  bool inResidentContainerEdge(Offset offset) {
    if (offset == null) {
      return false;
    }
    Size size = _residentContainerKey.currentContext.size;
    Offset position =
        (_residentContainerKey.currentContext.findRenderObject() as RenderBox)
            .localToGlobal(Offset.zero);
    Rect rc1 = Rect.fromLTWH(offset.dx, offset.dy, 80, 80);
    Rect rc2 = Rect.fromLTWH(position.dx, position.dy, size.width, size.height);

    return (rc1.left + rc1.width > rc2.left &&
        rc2.left + rc2.width > rc1.left &&
        rc1.top + rc1.height > rc2.top &&
        rc2.top + rc2.height > rc1.top);
  }

  Widget buildResidentView(context) {
    List<Widget> widgets = <Widget>[];
    double round = (MediaQuery.of(context).size.width - 80 * 4 - 30) / 3;
    KitPageManager.instance.getResidentKit().forEach((key, value) {
      widgets.add(
        Draggable(
          child: MaterialButton(
              child: KitItem(value),
              onPressed: () {
                setState(() {
                  value.tabAction();
                });
              },
              padding: EdgeInsets.all(0),
              minWidth: 40),
          feedback: KitItem(value),
          onDragStarted: () => {
            setState(() {
              onDrag = true;
            })
          },
          onDraggableCanceled: (velocity, offset) => {
            setState(() {
              onDrag = false;
              if (!inResidentContainerEdge(offset)) {
                KitPageManager.instance.removeResidentKit(key);
              }
            })
          },
          onDragEnd: (detail) => {
            setState(() {
              onDrag = false;
              if (!inResidentContainerEdge(detail.offset)) {
                KitPageManager.instance.removeResidentKit(key);
              }
            })
          },
        ),
      );
    });
    Wrap wrap = Wrap(
      spacing: round,
      runSpacing: 15,
      children: widgets,
    );
    return wrap;
  }

  Widget buildOtherView(context) {
    List<Widget> widgets = <Widget>[];
    double round = (MediaQuery.of(context).size.width - 80 * 4 - 30) / 3;
    KitPageManager.instance.getOtherKit().forEach((key, value) {
      widgets.add(
        Draggable(
          child: MaterialButton(
              child: KitItem(value),
              onPressed: () {
                setState(() {
                  value.tabAction();
                });
              },
              padding: EdgeInsets.all(0),
              minWidth: 40),
          feedback: KitItem(value),
          onDragStarted: () => {
            setState(() {
              onDrag = true;
            })
          },
          onDragEnd: (detail) => {
            setState(() {
              if (inResidentContainerEdge(detail.offset)) {
                KitPageManager.instance.addResidentKit(key);
              }
              onDrag = false;
            })
          },
          onDraggableCanceled: (v, offset) => {
            setState(() {
              if (inResidentContainerEdge(offset)) {
                KitPageManager.instance.addResidentKit(key);
              }
              onDrag = false;
            })
          },
        ),
      );
    });
    Wrap wrap = Wrap(
      spacing: round,
      runSpacing: 15,
      children: widgets,
    );
    return wrap;
  }
}

class KitItem extends StatelessWidget {
  final IKit kit;

  const KitItem(this.kit);

  @override
  Widget build(BuildContext context) {
    return Container(
      child: Column(
        children: <Widget>[
          Image.asset(
            kit.getIcon(),
            width: 34,
            height: 34,
            fit: BoxFit.fitWidth,
            package: DoKit.PACKAGE_NAME,
          ),
          Container(
            margin: EdgeInsets.only(top: 6),
            child: Text(kit.getKitName(),
                style: TextStyle(
                    fontFamily: 'PingFang SC',
                    fontSize: 12,
                    fontWeight: FontWeight.normal,
                    decoration: TextDecoration.none,
                    color: Color(0xff666666))),
          ),
        ],
      ),
      width: 80,
      alignment: Alignment.center,
    );
  }
}

class KitPageManager {
  static const String KIT_ALL = '全部';
  static const String KEY_KIT_PAGE_CACHE = 'key_kit_page_cache';
  List<String> residentList = [ApmKitName.KIT_LOG, ApmKitName.KIT_CHANNEL];

  KitPageManager._privateConstructor() {}

  static final KitPageManager _instance = KitPageManager._privateConstructor();

  static KitPageManager get instance => _instance;

  String listToString(List<String> list) {
    if (list == null || list.length == 0) {
      return '';
    }
    String result;
    list.forEach((string) =>
        {if (result == null) result = string else result = '$result,$string'});
    return result.toString();
  }

  bool addResidentKit(String tag) {
    assert(tag != null);
    if (!residentList.contains(tag)) {
      if (residentList.length >= 4) {
        return false;
      }
      residentList.add(tag);
      SharedPreferences.getInstance().then((prefs) =>
          prefs.setString(KEY_KIT_PAGE_CACHE, listToString(residentList)));
      return true;
    }
    return false;
  }

  bool removeResidentKit(String tag) {
    assert(tag != null);
    if (residentList.contains(tag)) {
      residentList.remove(tag);
      SharedPreferences.getInstance().then((prefs) =>
          prefs.setString(KEY_KIT_PAGE_CACHE, listToString(residentList)));
      return true;
    }
    return false;
  }

  Map<String, IKit> getOtherKit() {
    Map<String, IKit> kits = {};
    CommonKitManager.instance.kitMap.forEach((key, value) {
      if (!residentList.contains(key)) {
        kits[key] = value;
      }
    });
    ApmKitManager.instance.kitMap.forEach((key, value) {
      if (!residentList.contains(key)) {
        kits[key] = value;
      }
    });
    VisualKitManager.instance.kitMap.forEach((key, value) {
      if (!residentList.contains(key)) {
        kits[key] = value;
      }
    });
    return kits;
  }

  Map<String, IKit> getResidentKit() {
    Map<String, IKit> kits = {};
    residentList.forEach((element) {
      if (ApmKitManager.instance.getKit(element) != null) {
        kits[element] = ApmKitManager.instance.getKit(element);
      } else if (VisualKitManager.instance.getKit(element) != null) {
        kits[element] = VisualKitManager.instance.getKit(element);
      } else if (CommonKitManager.instance.getKit(element) != null) {
        kits[element] = CommonKitManager.instance.getKit(element);
      }
    });
    return kits;
  }

  void loadCache() {
    SharedPreferences.getInstance().then((prefs) => {
          if (prefs.getString(KitPageManager.KEY_KIT_PAGE_CACHE) != null)
            {
              if (prefs.getString(KitPageManager.KEY_KIT_PAGE_CACHE) == '')
                {KitPageManager.instance.residentList = []}
              else
                {
                  KitPageManager.instance.residentList = prefs
                      .getString(KitPageManager.KEY_KIT_PAGE_CACHE)
                      .split(',')
                }
            },
          if (KitPageManager.instance.residentList.length > 0)
            {ResidentPage.tag = KitPageManager.instance.residentList.first}
          else
            {ResidentPage.tag = KitPageManager.KIT_ALL}
        });
  }
}
