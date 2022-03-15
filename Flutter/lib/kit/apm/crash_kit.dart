import 'package:dokit/kit/kit.dart';
import 'package:flutter/material.dart';
import 'package:flutter/scheduler.dart';
import 'package:flutter/services.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:dokit/util/FileOperation.dart';
import 'package:path_provider/path_provider.dart';

import 'apm.dart';

class CrashKit extends ApmKit {
  @override
  Widget createDisplayPage() {
    return LogPage();
  }

  @override
  IStorage createStorage() {
    return CommonStorage(maxCount: 120);
  }

  @override
  String getKitName() {
    return ApmKitName.KIT_CARSH;
  }

  @override
  String getIcon() {
    return 'images/dokit_crash.png';
  }

  @override
  void start() {}

  @override
  void stop() {}
}

class CrashLogManager {
  CrashLogManager._privateConstructor();

  bool crashSwitch = false;

  static final CrashLogManager _instance =
      CrashLogManager._privateConstructor();

  Function? listener;

  void registerListener(Function listener) {
    this.listener = listener;
  }

  void unregisterListener() {
    listener = null;
  }

  static CrashLogManager get instance {
    return _instance;
  }

  List<IInfo>? getLogs() {
    return ApmKitManager.instance
        .getKit(ApmKitName.KIT_CARSH)
        ?.getStorage()
        .getAll();
  }

  void addLog(int type, String msg) {
    if (ApmKitManager.instance.getKit(ApmKitName.KIT_CARSH) != null) {
      var log = CrashLogBean(type, msg);
      var kit = ApmKitManager.instance.getKit(ApmKitName.KIT_CARSH);
      kit!.save(log);
      if (listener != null) {
        listener?.call(log);
      }
    }
  }

  void addException(String exception) {
    addLog(CrashLogBean.TYPE_ERROR, exception);
  }
}

class CrashLogBean implements IInfo {
  CrashLogBean(this.type, this.msg) {
    timestamp = DateTime.now().millisecondsSinceEpoch;
    expand = false;
  }
  static const int TYPE_ERROR = 1;

  final int type;
  final String msg;
  late int timestamp;
  late bool expand;
  @override
  int getValue() {
    return 0;
  }
}

class LogPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return LogPageState();
  }
}

class LogPageState extends State<LogPage> {
  var listStatus = true;

  ScrollController _offsetController = ScrollController();

  get localDocPath async {
    final directory = await getApplicationDocumentsDirectory();
    return directory.path;
  }

  Future<void> _listener(CrashLogBean logBean) async {
    if (!mounted) return;
    // if there's a current frame,
    if (SchedulerBinding.instance?.schedulerPhase != SchedulerPhase.idle) {
      // wait for the end of that frame.
      await SchedulerBinding.instance?.endOfFrame;
      if (!mounted) return;
    }
    setState(() {
      _offsetController.jumpTo(0);
    });
  }

  @override
  void initState() {
    FileUtil.shared.getAllSubFile('carshDoc');
    super.initState();
    CrashLogManager.instance.registerListener(_listener);
  }

  @override
  void dispose() {
    super.dispose();
    CrashLogManager.instance.unregisterListener();
  }

  @override
  Widget build(BuildContext context) {
    var items = CrashLogManager.instance.getLogs()!.reversed.toList();

    return Container(
        alignment: Alignment.topLeft,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: <Widget>[
            Row(
              children: <Widget>[
                Expanded(
                    child: FlatButton(
                        color: Colors.white,
                        onPressed: () {},
                        child: Text('检测开关',
                            style: TextStyle(
                                color: Color(0xff666666),
                                fontSize: 16,
                                fontFamily: 'PingFangSC-Medium')))),
                SizedBox(width: 20),
                Switch(
                  value: CrashLogManager.instance.crashSwitch,
                  activeColor: Colors.green,
                  onChanged: (value) {
                    setState(() {
                      CrashLogManager.instance.crashSwitch =
                          !CrashLogManager.instance.crashSwitch;
                    });
                  },
                ),
                Expanded(
                    child: FlatButton(
                  color: Colors.white,
                  onPressed: () {
                    FileUtil.shared.deleteFile('carshDoc');
                    var kit =
                        ApmKitManager.instance.getKit(ApmKitName.KIT_CARSH);
                    kit?.removeAllItem();
                    setState(() {});
                  },
                  child: Text('清理全部缓存',
                      style: TextStyle(
                          color: Color(0xffFC9153),
                          fontFamily: 'PingFangSC-Medium',
                          fontSize: 16)),
                ))
              ],
            ),
            Expanded(
              child: ListView.builder(
                  controller: _offsetController,
                  itemCount: items.length,
                  reverse: false,
                  shrinkWrap: true,
                  padding:
                      EdgeInsets.only(left: 0, right: 0, bottom: 0, top: 0),
                  itemBuilder: (context, index) {
                    return LogItemWidget(
                      item: items[index] as CrashLogBean,
                      index: index,
                      isLast: index == items.length - 1,
                    );
                  }),
            )
          ],
        ));
  }
}

class LogTitleItemWidget extends StatefulWidget {
  final String title;
  final int index;
  final bool isLast;
  final LogPageState parentState;

  LogTitleItemWidget(
      {required Key key,
      required this.title,
      required this.index,
      required this.isLast,
      required this.parentState})
      : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return _LogTitleItemWidget();
  }
}

class _LogTitleItemWidget extends State<LogTitleItemWidget> {
  @override
  Widget build(BuildContext context) {
    return GestureDetector(
        onTap: () {
          widget.parentState.setState(() {
            widget.parentState.listStatus = false;
          });
        },
        child: Container(
          padding: EdgeInsets.only(left: 16, right: 16),
          decoration: BoxDecoration(
              color: Colors.white,
              border: Border(
                  bottom: BorderSide(width: 0.5, color: Color(0xffeeeeee)))),
          child: Container(
              margin: EdgeInsets.only(top: 10, bottom: 10),
              child: RichText(
                maxLines: 7,
                overflow: TextOverflow.ellipsis,
                text: TextSpan(children: [
                  TextSpan(
                      text: widget.title,
                      style: TextStyle(
                          color: Color(0xff333333), height: 2.8, fontSize: 20))
                ]),
              )),
        ));
  }
}

class LogItemWidget extends StatefulWidget {
  const LogItemWidget(
      {Key? key, required this.item, required this.index, required this.isLast})
      : super(key: key);

  final CrashLogBean item;
  final int index;
  final bool isLast;

  @override
  State<StatefulWidget> createState() {
    return _LogItemWidgetState();
  }
}

class _LogItemWidgetState extends State<LogItemWidget> {
  static const String KEY_SHOW_LOG_EXPAND_TIPS = 'key_show_log_expand_tips';

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onLongPress: () {
        Clipboard.setData(ClipboardData(text: widget.item.msg));
        ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
          duration: Duration(milliseconds: 500),
          content: Text('已拷贝至剪贴板'),
        ));
      },
      onTap: () {
        setState(() {
          widget.item.expand = !widget.item.expand;
          SharedPreferences.getInstance().then<dynamic>(
            (SharedPreferences prefs) {
              if (!prefs.containsKey(KEY_SHOW_LOG_EXPAND_TIPS)) {
                prefs.setBool(KEY_SHOW_LOG_EXPAND_TIPS, true);
                ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
                  duration: Duration(milliseconds: 2000),
                  content: Text('日志超过7行时，点击可展开日志详情'),
                ));
              }
            },
          );
        });
      },
      child: Container(
        padding: const EdgeInsets.only(left: 16, right: 16),
        decoration: BoxDecoration(
            color: widget.item.expand ? Colors.black : Colors.white,
            border: const Border(
                bottom: BorderSide(width: 0.5, color: Color(0xffeeeeee)))),
        child: Container(
          margin: const EdgeInsets.only(top: 10, bottom: 10),
          child: RichText(
            maxLines: widget.item.expand ? 9999 : 7,
            overflow: TextOverflow.ellipsis,
            text: TextSpan(children: <TextSpan>[
              TextSpan(
                  text: widget.item.msg,
                  style: TextStyle(
                      color: widget.item.type == CrashLogBean.TYPE_ERROR
                          ? Colors.red
                          : (widget.item.expand
                              ? Colors.white
                              : const Color(0xff333333)),
                      height: 1.4,
                      fontSize: 10))
            ]),
          ),
        ),
      ),
    );
  }
}
