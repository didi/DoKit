import 'dart:ui';

import 'package:dokit/util/util.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/scheduler.dart';
import 'package:flutter/services.dart';

import '../../dokit.dart';
import 'apm.dart';

class ChannelInfo implements IInfo {
  ChannelInfo(this.channelName, this.method, this.arguments, this.type)
      : startTimestamp = DateTime.now().millisecondsSinceEpoch;

  factory ChannelInfo.error(String channelName, int type) {
    return ChannelInfo(channelName, '', null, type);
  }

  static const int TYPE_USER_SEND = 0;
  static const int TYPE_USER_RECEIVE = 1;
  static const int TYPE_SYSTEM_SEND = 2;
  static const int TYPE_SYSTEM_RECEIVE = 3;
  final String channelName;

  final String method;

  final dynamic arguments;
  final int startTimestamp;
  int endTimestamp = 0;

  final int type;
  dynamic results;
  bool expand = false;
  MethodCodec methodCodec;
  MessageCodec<dynamic> messageCodec;

  @override
  String getValue() {
    return (type == TYPE_USER_SEND
            ? 'dart端调用方法\n'
            : type == TYPE_USER_RECEIVE
                ? 'native端调用方法\n'
                : type == TYPE_SYSTEM_SEND
                    ? 'dart端调用方法[系统]\n'
                    : 'native端调用方法[系统]\n') +
        'channelName:$channelName\n' +
        'method:$method\n' +
        'arguments:$arguments\n' +
        'results:$results';
  }
}

class MethodChannelKit extends ApmKit {
  Function listener;

  @override
  Widget createDisplayPage() {
    return ChannelPage();
  }

  @override
  String getIcon() {
    return 'images/dk_method_channel.png';
  }

  @override
  IStorage createStorage() {
    return CommonStorage(maxCount: 240);
  }

  @override
  String getKitName() {
    return ApmKitName.KIT_CHANNEL;
  }

  @override
  bool save(IInfo info) {
    if (!ChannelPageState.showSystemChannel &&
        ((info as ChannelInfo).type == ChannelInfo.TYPE_SYSTEM_RECEIVE ||
            (info as ChannelInfo).type == ChannelInfo.TYPE_SYSTEM_SEND)) {
      super.save(info);
      return false;
    }
    final bool result = super.save(info);
    ApmKitManager.instance
        .getKit<MethodChannelKit>(ApmKitName.KIT_CHANNEL)
        ?.listener
        ?.call();
    return result;
  }

  @override
  void start() {}

  @override
  void stop() {}

  void registerListener(Function listener) {
    this.listener = listener;
  }

  void unregisterListener() {
    listener = null;
  }
}

class ChannelPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return ChannelPageState();
  }
}

class ChannelPageState extends State<ChannelPage> {
  final ScrollController _offsetController =
      ScrollController(); //定义ListView的controller
  static bool showSystemChannel = false;

  Future<void> _listener() async {
    if (!mounted) {
      return;
    }
    if (SchedulerBinding.instance.schedulerPhase != SchedulerPhase.idle) {
      await SchedulerBinding.instance.endOfFrame;
      if (!mounted) {
        return;
      }
    }
    setState(() {
      // 如果正在查看，就不自动滑动到底部
      if (_offsetController.offset < 10) {
        _offsetController.jumpTo(0);
      }
    });
  }

  @override
  void initState() {
    super.initState();
    ApmKitManager.instance
        .getKit<MethodChannelKit>(ApmKitName.KIT_CHANNEL)
        .registerListener(_listener);
  }

  @override
  void dispose() {
    super.dispose();
    ApmKitManager.instance
        .getKit<MethodChannelKit>(ApmKitName.KIT_CHANNEL)
        .unregisterListener();
  }

  @override
  Widget build(BuildContext context) {
    final List<IInfo> items = ApmKitManager.instance
        .getKit<MethodChannelKit>(ApmKitName.KIT_CHANNEL)
        .getStorage()
        .getAll()
        .reversed
        .where((IInfo element) {
      if (showSystemChannel) {
        return true;
      }

      return (element as ChannelInfo).type == ChannelInfo.TYPE_USER_SEND ||
          (element as ChannelInfo).type == ChannelInfo.TYPE_USER_RECEIVE;
    }).toList();

    return Column(
      children: <Widget>[
        Container(
            color: const Color(0xfff5f6f7),
            alignment: Alignment.topLeft,
            child: Row(
              mainAxisAlignment: MainAxisAlignment.start,
              children: <Widget>[
                GestureDetector(
                  behavior: HitTestBehavior.opaque,
                  onTap: () {
                    setState(() {
                      showSystemChannel = !showSystemChannel;
                    });
                  },
                  child: Container(
                    height: 44,
                    width: 44,
                    padding: const EdgeInsets.only(left: 16),
                    child: Image.asset(
                        showSystemChannel
                            ? 'images/dk_channel_check_h.png'
                            : 'images/dk_channel_check_n.png',
                        package: DoKit.PACKAGE_NAME,
                        height: 13,
                        width: 13),
                  ),
                ),
                GestureDetector(
                    behavior: HitTestBehavior.opaque,
                    onTap: () {
                      setState(() {
                        showSystemChannel = !showSystemChannel;
                      });
                    },
                    child: Text('显示系统Channel',
                        style: TextStyle(
                            color: showSystemChannel
                                ? const Color(0xff337cc4)
                                : const Color(0xff333333),
                            fontSize: 12))),
                GestureDetector(
                    behavior: HitTestBehavior.opaque,
                    onTap: () {
                      setState(() {
                        ApmKitManager.instance
                            .getKit<MethodChannelKit>(ApmKitName.KIT_CHANNEL)
                            .getStorage()
                            .clear();
                      });
                    },
                    child: Container(
                      decoration: BoxDecoration(
                        border: Border.all(
                            color: const Color(0xff337cc4), width: 1),
                        borderRadius: BorderRadius.circular(2), // 也可控件一边圆角大小
                      ),
                      margin: const EdgeInsets.only(left: 10),
                      padding: const EdgeInsets.all(2),
                      child: Text('清除本页数据',
                          style: TextStyle(
                              color: showSystemChannel
                                  ? const Color(0xff337cc4)
                                  : const Color(0xff333333),
                              fontSize: 12)),
                    )),
                GestureDetector(
                    behavior: HitTestBehavior.opaque,
                    onTap: () {
                      _offsetController.jumpTo(0);
                    },
                    child: Container(
                      decoration: BoxDecoration(
                        border: Border.all(
                            color: const Color(0xff337cc4), width: 1),
                        borderRadius: BorderRadius.circular(2), // 也可控件一边圆角大小
                      ),
                      margin: const EdgeInsets.only(left: 10),
                      padding: const EdgeInsets.all(2),
                      child: Text('滑动到底部',
                          style: TextStyle(
                              color: showSystemChannel
                                  ? const Color(0xff337cc4)
                                  : const Color(0xff333333),
                              fontSize: 12)),
                    )),
              ],
            )),
        Expanded(
          child: Container(
              alignment: Alignment.topCenter,
              color: const Color(0xfff5f6f7),
              child: ListView.builder(
                  controller: _offsetController,
                  itemCount: items.length,
                  padding: const EdgeInsets.only(
                      left: 4, right: 4, bottom: 0, top: 0),
                  reverse: true,
                  shrinkWrap: true,
                  itemBuilder: (BuildContext context, int index) {
                    return ChannelItemWidget(
                      item: items[index] as ChannelInfo,
                      index: index,
                      isLast: index == items.length - 1,
                    );
                  })),
        )
      ],
    );
  }
}

class ChannelItemWidget extends StatefulWidget {
  const ChannelItemWidget(
      {Key key,
      @required this.item,
      @required this.index,
      @required this.isLast})
      : super(key: key);

  final ChannelInfo item;
  final int index;
  final bool isLast;

  @override
  State<StatefulWidget> createState() {
    return _ChannelItemWidgetState();
  }
}

class _ChannelItemWidgetState extends State<ChannelItemWidget> {
  String getChannelType() {
    switch (widget.item.type) {
      case ChannelInfo.TYPE_USER_SEND:
        return 'Flutter > 终端';
      case ChannelInfo.TYPE_USER_RECEIVE:
        return '终端 > Flutter';
      case ChannelInfo.TYPE_SYSTEM_SEND:
        return 'Flutter > 终端 [系统调用]';
      case ChannelInfo.TYPE_SYSTEM_RECEIVE:
        return '终端 > Flutter [系统调用]';
      default:
        return 'Flutter > 终端';
    }
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () {
        setState(() {
          widget.item.expand = !widget.item.expand;
        });
      },
      child: Card(
        color: Colors.white,
        child: Row(
          mainAxisAlignment: MainAxisAlignment.start,
          children: <Widget>[
            Container(
                width: MediaQuery.of(context).size.width - 80,
                margin: const EdgeInsets.only(
                    left: 16, right: 16, top: 12, bottom: 12),
                child: RichText(
                    maxLines: widget.item.expand ? 9999 : 7,
                    overflow: TextOverflow.ellipsis,
                    text: TextSpan(children: <InlineSpan>[
                      TextSpan(
                          text:
                              '[${TimeUtils.toTimeString(widget.item.startTimestamp)}]',
                          style: const TextStyle(
                              fontSize: 9,
                              color: Color(0xff333333),
                              height: 1.2)),
                      WidgetSpan(
                        child: Container(
                          child: Text(getChannelType(),
                              style: const TextStyle(
                                  fontSize: 8,
                                  color: Color(0xffffffff),
                                  height: 1.2)),
                          height: 11,
                          margin: const EdgeInsets.only(left: 4),
                          padding: const EdgeInsets.only(left: 6, right: 6),
                          decoration: BoxDecoration(
                              borderRadius:
                                  const BorderRadius.all(Radius.circular(2)),
                              color: widget.item.type % 2 != 0
                                  ? const Color(0xffd0607e)
                                  : const Color(0xff337cc4)),
                        ),
                      ),
                      TextSpan(
                          text:
                              '  Cost:${widget.item.endTimestamp > 0 ? ((widget.item.endTimestamp - widget.item.startTimestamp).toString() + 'ms') : '-'} ',
                          style: const TextStyle(
                            fontSize: 9,
                            color: Color(0xff666666),
                            height: 1.5,
                          )),
                      const TextSpan(
                          text: '\nChannelName: ',
                          style: TextStyle(
                              fontSize: 10,
                              color: Color(0xff333333),
                              height: 1.5,
                              fontWeight: FontWeight.bold)),
                      TextSpan(
                          text: widget.item.channelName,
                          style: const TextStyle(
                              fontSize: 10,
                              height: 1.5,
                              color: Color(0xff666666))),
                      const TextSpan(
                          text: '\nMethod: ',
                          style: TextStyle(
                              height: 1.5,
                              fontSize: 10,
                              color: Color(0xff333333),
                              fontWeight: FontWeight.bold)),
                      TextSpan(
                          text: widget.item.method,
                          style: const TextStyle(
                              fontSize: 10,
                              height: 1.5,
                              color: Color(0xff666666))),
                      const TextSpan(
                          text: '\nArguments: ',
                          style: TextStyle(
                              fontSize: 10,
                              height: 1.5,
                              color: Color(0xff333333),
                              fontWeight: FontWeight.bold)),
                      TextSpan(
                          text: '${widget.item.arguments}',
                          style: const TextStyle(
                              fontSize: 10,
                              height: 1.5,
                              color: Color(0xff666666))),
                      const TextSpan(
                          text: '\nResult: ',
                          style: TextStyle(
                              fontSize: 10,
                              height: 1.5,
                              color: Color(0xff333333),
                              fontWeight: FontWeight.bold)),
                      TextSpan(
                          text: '${widget.item.results}',
                          style: const TextStyle(
                              fontSize: 10,
                              height: 1.5,
                              color: Color(0xff666666))),
                    ]))),
            Image.asset(
                widget.item.expand
                    ? 'images/dk_channel_expand_h.png'
                    : 'images/dk_channel_expand_n.png',
                package: DoKit.PACKAGE_NAME,
                height: 14,
                width: 9)
          ],
        ),
      ),
    );
  }
}
