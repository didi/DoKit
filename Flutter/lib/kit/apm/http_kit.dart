import 'dart:io';
import 'dart:ui';

import 'package:dokit/engine/dokit_http.dart';
import 'package:dokit/util/util.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/scheduler.dart';
import 'package:flutter/services.dart';
import 'package:flutter/src/widgets/framework.dart';

import '../../dokit.dart';
import '../kit.dart';
import 'apm.dart';

class HttpInfo implements IInfo {
  final Uri uri;

  final String method;

  final int startTimestamp;

  String error;
  HttpRequest request = new HttpRequest();
  HttpResponse response = new HttpResponse();

  bool expand = false;

  HttpInfo(this.uri, this.method)
      : this.startTimestamp = new DateTime.now().millisecondsSinceEpoch;

  factory HttpInfo.error(String error) {
    return HttpInfo(null, '')
      ..error = error
      ..response.update(-1, '', '', 0);
  }

  @override
  String getValue() {
    if (error != null) {
      return 'Error:$error';
    }
    return 'Uri:$uri\nMethod:$method\nParameters:${request.parameters}\nResponse:$response';
  }
}

class HttpRequest {
  List<String> parameters = [];
  String header;

  void add(String parameter) {
    parameters.add(parameter);
    HttpKit kit = ApmKitManager.instance.getKit(ApmKitName.KIT_HTTP);
    kit?.listener?.call();
  }
}

class HttpResponse {
  String _result;

  String get result => _result;
  int _code = 0;

  int get code => _code;

  String _header;

  String get header => _header;
  int endTimestamp = 0;
  int size = 0;

  void update(int code, String result, String header, int size) {
    _code = code;
    _result = result;
    _header = header;
    this.size = size;
    endTimestamp = new DateTime.now().millisecondsSinceEpoch;
    HttpKit kit = ApmKitManager.instance.getKit(ApmKitName.KIT_HTTP);
    kit?.listener?.call();
  }

  @override
  String toString() {
    return _code > 0 ? 'code $_code,result $_result' : '';
  }
}

class HttpKit extends ApmKit {
  @override
  Widget createDisplayPage() {
    return HttpPage();
  }

  Function listener;

  @override
  String getIcon() {
    return 'images/dk_net_monitor.png';
  }

  @override
  IStorage createStorage() {
    return CommonStorage(maxCount: 60);
  }

  @override
  String getKitName() {
    return ApmKitName.KIT_HTTP;
  }

  @override
  void start() {
    HttpOverrides origin = HttpOverrides.current;
    HttpOverrides.global = new DoKitHttpOverrides(origin);
  }

  @override
  void stop() {}

  void registerListener(Function listener) {
    this.listener = listener;
  }

  void unregisterListener() {
    this.listener = null;
  }
}

class HttpPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return HttpPageState();
  }
}

class HttpPageState extends State<HttpPage> {
  ScrollController _offsetController =
      ScrollController(); //定义ListView的controller
  static bool showSystemChannel = true;

  Future<void> _listener() async {
    if (!mounted) return;
    if (SchedulerBinding.instance.schedulerPhase != SchedulerPhase.idle) {
      await SchedulerBinding.instance.endOfFrame;
      if (!mounted) return;
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
        .getKit<HttpKit>(ApmKitName.KIT_HTTP)
        .registerListener(_listener);
  }

  @override
  void dispose() {
    super.dispose();
    ApmKitManager.instance
        .getKit<HttpKit>(ApmKitName.KIT_HTTP)
        .unregisterListener();
  }

  @override
  Widget build(BuildContext context) {
    List<IInfo> items = ApmKitManager.instance
        .getKit<HttpKit>(ApmKitName.KIT_HTTP)
        .getStorage()
        .getAll()
        .reversed
        .toList();
    return Column(
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            Container(
              decoration: new BoxDecoration(
                border: new Border.all(color: Color(0xff337cc4), width: 1),
                borderRadius: new BorderRadius.circular(2), // 也可控件一边圆角大小
              ),
              margin: EdgeInsets.only(left: 16, top: 8, bottom: 8),
              padding: EdgeInsets.all(2),
              alignment: Alignment.centerLeft,
              child: GestureDetector(
                  behavior: HitTestBehavior.opaque,
                  onTap: () {
                    this.setState(() {
                      ApmKitManager.instance
                          .getKit<HttpKit>(ApmKitName.KIT_HTTP)
                          .getStorage()
                          .clear();
                    });
                  },
                  child: Text('清除本页数据',
                      style:
                          TextStyle(color: Color(0xff333333), fontSize: 12))),
            ),
            Container(
              decoration: new BoxDecoration(
                border: new Border.all(color: Color(0xff337cc4), width: 1),
                borderRadius: new BorderRadius.circular(2), // 也可控件一边圆角大小
              ),
              margin: EdgeInsets.only(left: 10, top: 8, bottom: 8),
              padding: EdgeInsets.all(2),
              alignment: Alignment.centerLeft,
              child: GestureDetector(
                  behavior: HitTestBehavior.opaque,
                  onTap: () {
                    _offsetController.jumpTo(0);
                  },
                  child: Text('滑动到底部',
                      style:
                          TextStyle(color: Color(0xff333333), fontSize: 12))),
            ),
          ],
        ),
        Expanded(
          child: Container(
              alignment: Alignment.topCenter,
              color: Color(0xfff5f6f7),
              child: ListView.builder(
                  controller: _offsetController,
                  itemCount: items.length,
                  reverse: true,
                  padding:
                      EdgeInsets.only(left: 4, right: 4, bottom: 0, top: 8),
                  shrinkWrap: true,
                  itemBuilder: (context, index) {
                    return HttpItemWidget(
                      item: items[index],
                      index: index,
                      isLast: index == items.length - 1,
                    );
                  })),
        ),
      ],
    );
  }
}

class HttpItemWidget extends StatefulWidget {
  final HttpInfo item;
  final int index;
  final bool isLast;

  HttpItemWidget(
      {Key key,
      @required this.item,
      @required this.index,
      @required this.isLast})
      : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return _HttpItemWidgetState();
  }
}

class _HttpItemWidgetState extends State<HttpItemWidget> {
  String getCode() {
    return widget.item.response.code > 0 ? '${widget.item.response.code}' : '-';
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
        onLongPress: () {
          if (widget.item.response.result != null) {
            Clipboard.setData(ClipboardData(text: widget.item.response.result));
            Scaffold.of(context).showSnackBar(SnackBar(
              duration: Duration(milliseconds: 500),
              content: Text('请求返回已拷贝至剪贴板'),
            ));
          }
        },
        onTap: () {
          setState(() {
            widget.item.expand = !widget.item.expand;
          });
        },
        child: Card(
            color: Colors.white,
            child: Row(mainAxisAlignment: MainAxisAlignment.start, children: <
                Widget>[
              Container(
                  width: MediaQuery.of(context).size.width - 80,
                  margin:
                      EdgeInsets.only(left: 16, right: 16, top: 12, bottom: 12),
                  child: RichText(
                      maxLines: widget.item.expand ? 9999 : 7,
                      overflow: TextOverflow.ellipsis,
                      text: TextSpan(children: [
                        TextSpan(
                            text:
                                '[${TimeUtils.toTimeString(widget.item.startTimestamp)}]',
                            style: TextStyle(
                                fontSize: 9,
                                color: Color(0xff333333),
                                height: 1.2)),
                        WidgetSpan(
                            child: Container(
                                child: Text('${getCode()}',
                                    style: TextStyle(
                                        fontSize: 8,
                                        color: Color(0xffffffff),
                                        height: 1.2)),
                                height: 11,
                                margin: EdgeInsets.only(left: 4),
                                padding: EdgeInsets.only(left: 6, right: 6),
                                decoration: BoxDecoration(
                                    borderRadius:
                                        BorderRadius.all(Radius.circular(2)),
                                    color: (widget.item.response.code == 200 ||
                                            widget.item.response.code == 0)
                                        ? Color(0xff337cc4)
                                        : Color(0xffd0607e)))),
                        TextSpan(
                            text: '  ${widget.item.method}'
                                '  Cost:${widget.item.response.endTimestamp > 0 ? ((widget.item.response.endTimestamp - widget.item.startTimestamp).toString() + 'ms') : '-'} '
                                '  Size:${widget.item.response.size > 0 ? (ByteUtil.toByteString(widget.item.response.size)) : '-'}',
                            style: TextStyle(
                              fontSize: 9,
                              color: Color(0xff666666),
                              height: 1.5,
                            )),
                        TextSpan(
                            text: '\nUri: ',
                            style: TextStyle(
                                fontSize: 10,
                                color: Color(0xff333333),
                                height: 1.5,
                                fontWeight: FontWeight.bold)),
                        TextSpan(
                            text: '${widget.item.uri}',
                            style: TextStyle(
                                fontSize: 10,
                                height: 1.5,
                                color: Color(0xff666666))),
                        TextSpan(
                            text: '\nRequestHeader: ',
                            style: TextStyle(
                                height: 1.5,
                                fontSize: 10,
                                color: Color(0xff333333),
                                fontWeight: FontWeight.bold)),
                        TextSpan(
                            text: '${widget.item.request.header}',
                            style: TextStyle(
                                fontSize: 10,
                                height: 1.5,
                                color: Color(0xff666666))),
                        TextSpan(
                            text: '\nResponseHeader: ',
                            style: TextStyle(
                                height: 1.5,
                                fontSize: 10,
                                color: Color(0xff333333),
                                fontWeight: FontWeight.bold)),
                        TextSpan(
                            text: '${widget.item.response.header}',
                            style: TextStyle(
                                fontSize: 10,
                                height: 1.5,
                                color: Color(0xff666666))),
                        TextSpan(
                            text: '\nParameters: ',
                            style: TextStyle(
                                fontSize: 10,
                                height: 1.5,
                                color: Color(0xff333333),
                                fontWeight: FontWeight.bold)),
                        TextSpan(
                            text: '${widget.item.request.parameters}',
                            style: TextStyle(
                                fontSize: 10,
                                height: 1.5,
                                color: Color(0xff666666))),
                        TextSpan(
                            text: '\nResponse: ',
                            style: TextStyle(
                                fontSize: 10,
                                height: 1.5,
                                color: Color(0xff333333),
                                fontWeight: FontWeight.bold)),
                        TextSpan(
                            text: '${widget.item.response.result}',
                            style: TextStyle(
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
            ])));
  }
}
