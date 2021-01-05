import 'package:flutter/material.dart';
import 'package:flutter/scheduler.dart';
import 'package:dokit/util/util.dart';
import 'package:flutter/services.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'apm.dart';

class LogKit extends ApmKit {
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
    return ApmKitName.KIT_LOG;
  }

  @override
  String getIcon() {
    return 'images/dk_log_info.png';
  }

  @override
  void start() {}

  @override
  void stop() {}
}

class LogManager {
  LogManager._privateConstructor();

  static final LogManager _instance = LogManager._privateConstructor();

  Function listener;

  void registerListener(Function listener) {
    this.listener = listener;
  }

  void unregisterListener() {
    this.listener = null;
  }

  static LogManager get instance {
    return _instance;
  }

  List<IInfo> getLogs() {
    return ApmKitManager.instance
        .getKit(ApmKitName.KIT_LOG)
        ?.getStorage()
        ?.getAll();
  }

  void addLog(int type, String msg) {
    if (ApmKitManager.instance.getKit(ApmKitName.KIT_LOG) != null) {
      LogBean log = new LogBean(type, msg);
      LogKit kit = ApmKitManager.instance.getKit(ApmKitName.KIT_LOG);
      kit.save(log);
      listener?.call(log);
    }
  }

  void addException(String exception) {
    addLog(LogBean.TYPE_EXCEPTION, exception);
  }
}

class LogBean implements IInfo {
  static const int TYPE_LOG = 1;
  static const int TYPE_DEBUG = 2;
  static const int TYPE_INFO = 3;
  static const int TYPE_WARN = 4;
  static const int TYPE_ERROR = 5;
  static const int TYPE_EXCEPTION = 6;

  final int type;
  final String msg;
  int timestamp;
  bool expand;

  LogBean(this.type, this.msg) {
    this.timestamp = new DateTime.now().millisecondsSinceEpoch;
    expand = false;
  }

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
  ScrollController _offsetController =
      ScrollController(); //定义ListView的controller
  Future<void> _listener(LogBean logBean) async {
    if (!mounted) return;
    // if there's a current frame,
    if (SchedulerBinding.instance.schedulerPhase != SchedulerPhase.idle) {
      // wait for the end of that frame.
      await SchedulerBinding.instance.endOfFrame;
      if (!mounted) return;
    }
    setState(() {
      _offsetController.jumpTo(0);
    });
  }

  @override
  void initState() {
    super.initState();
    LogManager.instance.registerListener(_listener);
  }

  @override
  void dispose() {
    super.dispose();
    LogManager.instance.unregisterListener();
  }

  @override
  Widget build(BuildContext context) {
    List<IInfo> items = LogManager.instance.getLogs().reversed.toList();
    return Container(
      alignment: Alignment.topLeft,
      child: ListView.builder(
          controller: _offsetController,
          itemCount: items.length,
          reverse: true,
          shrinkWrap: true,
          padding: EdgeInsets.only(left: 0, right: 0, bottom: 0, top: 0),
          itemBuilder: (context, index) {
            return LogItemWidget(
              item: items[index],
              index: index,
              isLast: index == items.length - 1,
            );
          }),
    );
  }
}

class LogItemWidget extends StatefulWidget {
  final LogBean item;
  final int index;
  final bool isLast;

  LogItemWidget(
      {Key key,
      @required this.item,
      @required this.index,
      @required this.isLast})
      : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return _LogItemWidgetState();
  }
}

class _LogItemWidgetState extends State<LogItemWidget> {
  static final String KEY_SHOW_LOG_EXPAND_TIPS = 'key_show_log_expand_tips';

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
        onLongPress: () {
          Clipboard.setData(ClipboardData(text: widget.item.msg));
          Scaffold.of(context).showSnackBar(SnackBar(
            duration: Duration(milliseconds: 500),
            content: Text('已拷贝至剪贴板'),
          ));
        },
        onTap: () {
          setState(() {
            widget.item.expand = !widget.item.expand;
            SharedPreferences.getInstance().then((prefs) => {
                  if (!prefs.containsKey(KEY_SHOW_LOG_EXPAND_TIPS))
                    {
                      prefs.setBool(KEY_SHOW_LOG_EXPAND_TIPS, true),
                      Scaffold.of(context).showSnackBar(SnackBar(
                        duration: Duration(milliseconds: 2000),
                        content: Text('日志超过7行时，点击可展开日志详情'),
                      ))
                    }
                });
          });
        },
        child: Container(
          padding: EdgeInsets.only(left: 16, right: 16),
          decoration: BoxDecoration(
              color: widget.item.expand ? Colors.black : Colors.white,
              border: Border(
                  bottom: BorderSide(width: 0.5, color: Color(0xffeeeeee)))),
          child: Container(
              margin: EdgeInsets.only(top: 10, bottom: 10),
              child: RichText(
                maxLines: widget.item.expand ? 9999 : 7,
                overflow: TextOverflow.ellipsis,
                text: TextSpan(children: [
                  TextSpan(
                      text:
                          '[${TimeUtils.toTimeString(widget.item.timestamp)}] ',
                      style: TextStyle(
                          color: widget.item.type == LogBean.TYPE_ERROR
                              ? Colors.red
                              : (widget.item.expand
                                  ? Colors.white
                                  : Color(0xff333333)),
                          height: 1.4,
                          fontSize: 10)),
                  TextSpan(
                      text: widget.item.msg,
                      style: TextStyle(
                          color: widget.item.type == LogBean.TYPE_ERROR
                              ? Colors.red
                              : (widget.item.expand
                                  ? Colors.white
                                  : Color(0xff333333)),
                          height: 1.4,
                          fontSize: 10))
                ]),
              )),
        ));
  }
}
