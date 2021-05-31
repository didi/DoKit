import 'dart:async';

import 'package:dokit/dokit.dart';
import 'package:dokit/kit/apm/apm.dart';
import 'package:dokit/kit/kit.dart';
import 'package:dokit/util/time_util.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/scheduler.dart';
import 'package:flutter/services.dart';
import 'package:shared_preferences/shared_preferences.dart';

class LogKit extends ApmKit {
  FlutterExceptionHandler? originOnError;

  final CommonStorage _error = CommonStorage();

  CommonStorage get error => _error;

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
  bool save(IInfo? info) {
    if ((info as LogBean).type == LogBean.TYPE_ERROR) {
      _error.save(info);
    }
    return super.save(info);
  }

  @override
  void start() {
    resetOnErrorInstance();
  }

  // dart vm会通过widget_inspector.dart structuredErrors服务替换掉FlutterError.onError，不清楚具体执行时机是什么时候,会重复执行。这里启动定时器每1秒将其替换回来。
  // 需要保留widget_inspector注入的onError调用，否则会影响android studio的输出
  void resetOnErrorInstance() {
    Timer.periodic(const Duration(seconds: 1), (Timer timer) {
      if (FlutterError.onError != _doKitOnError) {
        originOnError = FlutterError.onError;
        FlutterError.onError = _doKitOnError;
      }
    });
  }

  void _doKitOnError(FlutterErrorDetails details) {
    // 委托给runZone内的onError
    var stack = details.stack;
    if (stack != null) {
      Zone.current.handleUncaughtError(details.exception, stack);
    }
    if (originOnError != null) {
      originOnError?.call(details);
    }
  }

  @override
  void stop() {}
}

class LogManager {
  LogManager._privateConstructor();

  static final LogManager _instance = LogManager._privateConstructor();

  Function? listener;

  void registerListener(Function listener) {
    this.listener = listener;
  }

  void unregisterListener() {
    listener = null;
  }

  static LogManager get instance {
    return _instance;
  }

  List<IInfo>? getLogs() {
    return ApmKitManager.instance
        .getKit(ApmKitName.KIT_LOG)
        ?.getStorage()
        .getAll();
  }

  List<IInfo>? getErrors() {
    return ApmKitManager.instance
        .getKit<LogKit>(ApmKitName.KIT_LOG)
        ?.error
        .getAll();
  }

  void addLog(int type, String msg) {
    if (ApmKitManager.instance.getKit(ApmKitName.KIT_LOG) != null) {
      final LogBean log = LogBean(type, msg);
      final LogKit? kit = ApmKitManager.instance.getKit(ApmKitName.KIT_LOG);
      kit?.save(log);
      if (type != LogBean.TYPE_ERROR || LogPageState._showError) {
        listener?.call(log);
      }
    }
  }

  void addException(String exception) {
    addLog(LogBean.TYPE_ERROR, exception);
  }
}

class LogBean implements IInfo {
  LogBean(this.type, this.msg) {
    timestamp = DateTime.now().millisecondsSinceEpoch;
    expand = false;
  }

  static const int TYPE_LOG = 1;
  static const int TYPE_DEBUG = 2;
  static const int TYPE_INFO = 3;
  static const int TYPE_WARN = 4;
  static const int TYPE_ERROR = 5;

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
  final ScrollController _offsetController =
      ScrollController(); //定义ListView的controller
  static bool _showError = false;

  Future<void> _listener(LogBean logBean) async {
    if (!mounted) {
      return Future<void>.value();
    }
    // if there's a current frame,
    if (SchedulerBinding.instance?.schedulerPhase != SchedulerPhase.idle) {
      // wait for the end of that frame.
      await SchedulerBinding.instance?.endOfFrame;
      if (!mounted) {
        return Future<void>.value();
      }
    }
    setState(() {
      // 如果正在查看，就不自动滑动到底部
      if (_offsetController.offset < 10) {
        _offsetController.jumpTo(0);
      }
    });

    return Future<void>.value();
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
    final List<IInfo> items = (LogPageState._showError
            ? LogManager.instance.getErrors()?.reversed.toList()
            : LogManager.instance.getLogs()?.reversed.toList()) ??
        [];
    return Column(
      children: <Widget>[
        Row(
          mainAxisAlignment: MainAxisAlignment.start,
          children: <Widget>[
            GestureDetector(
              behavior: HitTestBehavior.opaque,
              onTap: () {
                setState(() {
                  _showError = !_showError;
                });
              },
              child: Container(
                height: 44,
                width: 44,
                padding: const EdgeInsets.only(left: 16),
                child: Image.asset(
                    _showError
                        ? 'images/dk_channel_check_h.png'
                        : 'images/dk_channel_check_n.png',
                    package: DK_PACKAGE_NAME,
                    height: 13,
                    width: 13),
              ),
            ),
            GestureDetector(
                behavior: HitTestBehavior.opaque,
                onTap: () {
                  setState(() {
                    _showError = !_showError;
                  });
                },
                child: Text('只显示异常',
                    style: TextStyle(
                        color: _showError
                            ? const Color(0xff337cc4)
                            : const Color(0xff333333),
                        fontSize: 12))),
            Container(
              decoration: BoxDecoration(
                border: Border.all(color: const Color(0xff337cc4), width: 1),
                borderRadius: BorderRadius.circular(2), // 也可控件一边圆角大小
              ),
              margin: const EdgeInsets.only(left: 16, top: 8, bottom: 8),
              padding: const EdgeInsets.all(2),
              alignment: Alignment.centerLeft,
              child: GestureDetector(
                  behavior: HitTestBehavior.opaque,
                  onTap: () {
                    setState(() {
                      ApmKitManager.instance
                          .getKit<LogKit>(ApmKitName.KIT_LOG)
                          ?.getStorage()
                          .clear();
                      ApmKitManager.instance
                          .getKit<LogKit>(ApmKitName.KIT_LOG)
                          ?.error
                          .clear();
                    });
                  },
                  child: const Text('清除本页数据',
                      style:
                          TextStyle(color: Color(0xff333333), fontSize: 12))),
            ),
            Container(
              decoration: BoxDecoration(
                border: Border.all(color: const Color(0xff337cc4), width: 1),
                borderRadius: BorderRadius.circular(2), // 也可控件一边圆角大小
              ),
              margin: const EdgeInsets.only(left: 10, top: 8, bottom: 8),
              padding: const EdgeInsets.all(2),
              alignment: Alignment.centerLeft,
              child: GestureDetector(
                  behavior: HitTestBehavior.opaque,
                  onTap: () {
                    _offsetController.jumpTo(0);
                  },
                  child: const Text('滑动到底部',
                      style:
                          TextStyle(color: Color(0xff333333), fontSize: 12))),
            ),
          ],
        ),
        Expanded(
          child: Container(
            alignment: Alignment.topLeft,
            child: ListView.builder(
                controller: _offsetController,
                itemCount: items.length,
                reverse: true,
                shrinkWrap: true,
                padding:
                    const EdgeInsets.only(left: 0, right: 0, bottom: 0, top: 0),
                itemBuilder: (BuildContext context, int index) {
                  return LogItemWidget(
                    item: items[index] as LogBean,
                    index: index,
                    isLast: index == items.length - 1,
                  );
                }),
          ),
        ),
      ],
    );
  }
}

class LogItemWidget extends StatefulWidget {
  const LogItemWidget(
      {Key? key, required this.item, required this.index, required this.isLast})
      : super(key: key);

  final LogBean item;
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
                  text: '[${toTimeString(widget.item.timestamp)}] ',
                  style: TextStyle(
                      color: widget.item.type == LogBean.TYPE_ERROR
                          ? Colors.red
                          : (widget.item.expand
                              ? Colors.white
                              : const Color(0xff333333)),
                      height: 1.4,
                      fontSize: 10)),
              TextSpan(
                  text: widget.item.msg,
                  style: TextStyle(
                      color: widget.item.type == LogBean.TYPE_ERROR
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
