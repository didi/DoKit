import 'dart:io';
import 'dart:ui';

import 'package:dokit/dokit.dart';
import 'package:dokit/engine/dokit_http.dart';
import 'package:dokit/kit/apm/apm.dart';
import 'package:dokit/kit/kit.dart';
import 'package:dokit/util/byte_util.dart';
import 'package:dokit/util/time_util.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/scheduler.dart';
import 'package:flutter/services.dart';

class HttpInfo implements IInfo {
  HttpInfo(this.uri, this.method)
      : startTimestamp = DateTime.now().millisecondsSinceEpoch;

  factory HttpInfo.error(String error) {
    return HttpInfo(null, '')
      ..error = error
      ..response.update(-1, '', '', 0);
  }

  final Uri? uri;

  final String method;

  final int startTimestamp;

  String? error;
  HttpRequest request = HttpRequest();
  HttpResponse response = HttpResponse();

  bool expand = false;

  @override
  String getValue() {
    if (error != null) {
      return 'Error:$error';
    }
    return 'Uri:$uri\nMethod:$method\nParameters:${request.parameters}\nResponse:$response';
  }
}

class HttpRequest {
  List<String> parameters = <String>[];
  String? header;

  void add(String parameter) {
    parameters.add(parameter);
    final HttpKit? kit = ApmKitManager.instance.getKit(ApmKitName.KIT_HTTP);
    kit?.listener?.call();
  }
}

class HttpResponse {
  String? _result;

  String? get result => _result;
  int _code = 0;

  int get code => _code;

  String? _header;

  String? get header => _header;
  int endTimestamp = 0;
  int size = 0;

  void update(int code, String result, String header, int size) {
    _code = code;
    _result = result;
    _header = header;
    this.size = size;
    endTimestamp = DateTime.now().millisecondsSinceEpoch;
    final HttpKit? kit = ApmKitManager.instance.getKit(ApmKitName.KIT_HTTP);
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

  Function? listener;

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
    final HttpOverrides? origin = HttpOverrides.current;
    HttpOverrides.global = DoKitHttpOverrides(origin);
  }

  @override
  void stop() {}

  void registerListener(Function listener) {
    this.listener = listener;
  }

  void unregisterListener() {
    listener = null;
  }
}

class HttpPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return HttpPageState();
  }
}

class HttpPageState extends State<HttpPage> {
  final ScrollController _offsetController =
      ScrollController(); //定义ListView的controller
  static bool showSystemChannel = true;

  Future<void> _listener() async {
    if (!mounted) {
      return;
    }
    if (SchedulerBinding.instance?.schedulerPhase != SchedulerPhase.idle) {
      await SchedulerBinding.instance?.endOfFrame;
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
        .getKit<HttpKit>(ApmKitName.KIT_HTTP)
        ?.registerListener(_listener);
  }

  @override
  void dispose() {
    super.dispose();
    ApmKitManager.instance
        .getKit<HttpKit>(ApmKitName.KIT_HTTP)
        ?.unregisterListener();
  }

  @override
  Widget build(BuildContext context) {
    final List<IInfo> items = ApmKitManager.instance
            .getKit<HttpKit>(ApmKitName.KIT_HTTP)
            ?.getStorage()
            .getAll()
            .reversed
            .toList() ??
        [];
    return Column(
      children: <Widget>[
        Row(
          mainAxisAlignment: MainAxisAlignment.start,
          children: <Widget>[
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
                          .getKit<HttpKit>(ApmKitName.KIT_HTTP)
                          ?.getStorage()
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
                child: const Text(
                  '滑动到底部',
                  style: TextStyle(color: Color(0xff333333), fontSize: 12),
                ),
              ),
            ),
          ],
        ),
        Expanded(
          child: Container(
              alignment: Alignment.topCenter,
              color: const Color(0xfff5f6f7),
              child: ListView.builder(
                  controller: _offsetController,
                  itemCount: items.length,
                  reverse: true,
                  padding: const EdgeInsets.only(
                      left: 4, right: 4, bottom: 0, top: 8),
                  shrinkWrap: true,
                  itemBuilder: (BuildContext context, int index) {
                    return HttpItemWidget(
                      item: items[index] as HttpInfo,
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
  const HttpItemWidget(
      {Key? key, required this.item, required this.index, required this.isLast})
      : super(key: key);

  final HttpInfo item;
  final int index;
  final bool isLast;

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
          ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
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
                text: TextSpan(
                  children: <InlineSpan>[
                    TextSpan(
                        text: '[${toTimeString(widget.item.startTimestamp)}]',
                        style: const TextStyle(
                            fontSize: 9,
                            color: Color(0xff333333),
                            height: 1.2)),
                    WidgetSpan(
                        child: Container(
                            child: Text(getCode(),
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
                                color: (widget.item.response.code == 200 ||
                                        widget.item.response.code == 0)
                                    ? const Color(0xff337cc4)
                                    : const Color(0xffd0607e)))),
                    TextSpan(
                        text: '  ${widget.item.method}'
                            '  Cost:${widget.item.response.endTimestamp > 0 ? ((widget.item.response.endTimestamp - widget.item.startTimestamp).toString() + 'ms') : '-'} '
                            '  Size:${widget.item.response.size > 0 ? (toByteString(widget.item.response.size)) : '-'}',
                        style: const TextStyle(
                          fontSize: 9,
                          color: Color(0xff666666),
                          height: 1.5,
                        )),
                    const TextSpan(
                        text: '\nUri: ',
                        style: TextStyle(
                            fontSize: 10,
                            color: Color(0xff333333),
                            height: 1.5,
                            fontWeight: FontWeight.bold)),
                    TextSpan(
                        text: widget.item.uri.toString(),
                        style: const TextStyle(
                            fontSize: 10,
                            height: 1.5,
                            color: Color(0xff666666))),
                    const TextSpan(
                        text: '\nRequestHeader: ',
                        style: TextStyle(
                            height: 1.5,
                            fontSize: 10,
                            color: Color(0xff333333),
                            fontWeight: FontWeight.bold)),
                    TextSpan(
                        text: widget.item.request.header,
                        style: const TextStyle(
                            fontSize: 10,
                            height: 1.5,
                            color: Color(0xff666666))),
                    const TextSpan(
                        text: '\nResponseHeader: ',
                        style: TextStyle(
                            height: 1.5,
                            fontSize: 10,
                            color: Color(0xff333333),
                            fontWeight: FontWeight.bold)),
                    TextSpan(
                        text: widget.item.response.header,
                        style: const TextStyle(
                            fontSize: 10,
                            height: 1.5,
                            color: Color(0xff666666))),
                    const TextSpan(
                        text: '\nParameters: ',
                        style: TextStyle(
                            fontSize: 10,
                            height: 1.5,
                            color: Color(0xff333333),
                            fontWeight: FontWeight.bold)),
                    TextSpan(
                        text: widget.item.request.parameters.toString(),
                        style: const TextStyle(
                            fontSize: 10,
                            height: 1.5,
                            color: Color(0xff666666))),
                    const TextSpan(
                        text: '\nResponse: ',
                        style: TextStyle(
                            fontSize: 10,
                            height: 1.5,
                            color: Color(0xff333333),
                            fontWeight: FontWeight.bold)),
                    TextSpan(
                        text: widget.item.response.result,
                        style: const TextStyle(
                            fontSize: 10,
                            height: 1.5,
                            color: Color(0xff666666))),
                  ],
                ),
              ),
            ),
            Image.asset(
                widget.item.expand
                    ? 'images/dk_channel_expand_h.png'
                    : 'images/dk_channel_expand_n.png',
                package: DK_PACKAGE_NAME,
                height: 14,
                width: 9),
          ],
        ),
      ),
    );
  }
}
