import 'package:flutter/material.dart';
import 'leaks_doctor.dart';
import 'leaks_doctor_data.dart';

import 'leaks_doctor_utils.dart';

// 泄漏结果显示 首页
class LeaksDoctorPage extends StatefulWidget {
  LeaksDoctorPage({Key? key, required this.title}) : super(key: key);

  final String title;

  @override
  _LeaksDoctorPageState createState() => _LeaksDoctorPageState();
}

class _LeaksDoctorPageState extends State<LeaksDoctorPage> {
  List<LeaksMsgInfo> get dataSource => LeaksDoctor.getAll();

  String? textTitle(int index) {
    var leaksMsgInfo = dataSource[index];
    return leaksMsgInfo.leaksClsName;
  }

  String textSubTitle(int index) {
    var leaksMsgInfo = dataSource[index];
    return '实例个数${leaksMsgInfo.leaksInstanceCounts}';
  }

  @override
  void initState() {
    super.initState();
  }

  @override
  void dispose() {
    super.dispose();
    // LeaksDoctor.clear();
  }

  void onTap(int index, BuildContext context) {
    var leaksMsgInfo = dataSource[index];
    Navigator.push(
      context,
      MaterialPageRoute(
          builder: (context) => LeaksDoctorDetailPage(
                leaksMsgInfo: leaksMsgInfo,
              )),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: Text(widget.title),
        ),
        body: buildListView(context));
  }

  Widget buildListView(BuildContext context) {
    return ListView.builder(
        itemCount: dataSource.length,
        itemBuilder: (context, index) {
          return Card(
            color: RandomColor.randomColor(),
            child: ListTile(
              leading: Icon(Icons.bug_report),
              title: Text(textTitle(index) ?? ''),
              subtitle: Text(textSubTitle(index)),
              trailing: Icon(Icons.keyboard_arrow_right),
              onTap: () {
                onTap(index, context);
              },
            ),
          );
        });
  }
}

const _strokeWidth = 2.3;

// 泄漏详情页
class LeaksDoctorDetailPage extends StatefulWidget {
  final LeaksMsgInfo leaksMsgInfo;

  const LeaksDoctorDetailPage({Key? key, required this.leaksMsgInfo})
      : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return _LeaksDoctorDetailPageState();
  }
}

class _LeaksDoctorDetailPageState extends State<LeaksDoctorDetailPage> {
  final ScrollController _scrollController = ScrollController();

  @override
  void initState() {
    if (_scrollController.hasClients) _scrollController.jumpTo(50.0);
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    final retainingPath = widget.leaksMsgInfo.retainingPathList;
    final gcRootType = widget.leaksMsgInfo.gcRootType;
    final leaksInstanceCounts = widget.leaksMsgInfo.leaksInstanceCounts;
    final count = retainingPath!.length;

    return Scaffold(
        appBar: AppBar(
          title: Text('详情'),
        ),
        body: Container(
          height: MediaQuery.of(context).size.height * 6 / 7,
          child: Column(
            children: [
              Container(
                height: 40,
                color: Color(0xFF3E5E87),
                padding: EdgeInsets.symmetric(horizontal: 15),
                alignment: Alignment.centerLeft,
                child: Row(
                  children: [
                    Expanded(
                      child: Text(
                        'GcRoot type : ${gcRootType ?? ''}',
                        overflow: TextOverflow.ellipsis,
                        style: TextStyle(
                          color: Color(0xFFFFFFFF),
                          fontSize: 15,
                          fontWeight: FontWeight.normal,
                        ),
                      ),
                    ),
                    Text(
                      '共有$leaksInstanceCounts个对象',
                      style: TextStyle(
                        color: Colors.white70,
                        fontSize: 12,
                        fontWeight: FontWeight.normal,
                      ),
                    ),
                  ],
                ),
              ),
              Expanded(
                child: Scrollbar(
                  child: ListView.builder(
                    physics: BouncingScrollPhysics(),
                    padding: EdgeInsets.all(0),
                    controller: _scrollController,
                    itemBuilder: (BuildContext context, int index) {
                      return _retainingPathNode(
                        retainingPath[index],
                        index == 0,
                        index == count - 1,
                        RandomColor.randomColor(),
                      );
                    },
                    itemCount: count,
                  ),
                ),
              )
            ],
          ),
        ));
  }

  Widget _retainingPathNode(
      LeaksMsgNode node, bool isFirst, bool isLast, Color lineColor) {
    final hasSourceCodeLocation = node.codeLocation != null;
    final height = node.closureNode != null ? 72.0 : 64.0;
    return Column(
      mainAxisSize: MainAxisSize.min,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // 主要头部信息
        Container(
          height: height,
          color: Colors.black45,
          padding: EdgeInsets.symmetric(horizontal: 20),
          child: Row(
            children: [
              SizedBox(
                height: height,
                width: 30,
                child: CustomPaint(
                  painter: NodeRouteSign(isFirst, isLast, lineColor),
                ),
              ),
              SizedBox(width: 10),
              if (node.closureNode == null)
                Expanded(
                  child: RichText(
                    text: TextSpan(
                      text: node.name,
                      children: [
                        if (node.leakedNodeType != null &&
                            node.leakedNodeType != LeakedNodeType.unknown)
                          TextSpan(
                            text:
                                ' (${_getNodeTypeString(node.leakedNodeType!)})',
                            style: TextStyle(
                              color: Color(0xffebcf81),
                              fontSize: 14,
                              fontWeight: FontWeight.normal,
                            ),
                          ),
                      ],
                      style: TextStyle(
                        color: isFirst ? Color(0xFFFFFFFF) : Color(0xFFF5F5F5),
                        fontSize: 18,
                        fontWeight: FontWeight.normal,
                      ),
                    ),
                    softWrap: true,
                    overflow: TextOverflow.visible,
                  ),
                ),
              if (node.closureNode != null)
                Expanded(
                  child: RichText(
                    text: TextSpan(
                      text: node.closureNode!.closureOwner ?? '',
                      children: [
                        TextSpan(
                          text: ' (Closure#${node.closureNode!.ownerClass})',
                          style: TextStyle(
                            color: Color(0xffebcf81),
                            fontSize: 14,
                            fontWeight: FontWeight.normal,
                          ),
                        ),
                      ],
                      style: TextStyle(
                        color: isFirst ? Color(0xFFFFFFFF) : Color(0xFFF5F5F5),
                        fontSize: 18,
                        fontWeight: FontWeight.normal,
                      ),
                    ),
                    softWrap: true,
                    overflow: TextOverflow.visible,
                  ),
                ),
            ],
          ),
        ),
        // 代码信息相关 uri
        if (hasSourceCodeLocation)
          Container(
            padding: EdgeInsets.symmetric(horizontal: 20),
            child: Stack(
              children: [
                Positioned(
                  top: 0,
                  bottom: 0,
                  left: 0,
                  child: SizedBox(
                    width: 30,
                    child: Center(
                      child: Container(
                        color: lineColor,
                        width: _strokeWidth,
                      ),
                    ),
                  ),
                ),
                Container(
                  margin: EdgeInsets.symmetric(vertical: 8),
                  alignment: Alignment.centerLeft,
                  child: Container(
                    decoration: BoxDecoration(
                      border: Border.all(color: Color(0xFFFAFAFA), width: 1),
                      borderRadius: BorderRadius.all(Radius.circular(4)),
                      color: Colors.blue,
                    ),
                    padding: EdgeInsets.symmetric(horizontal: 20, vertical: 20),
                    child: RichText(
                      text: TextSpan(
                        text:
                            'uri : \n${node.codeLocation?.uri ?? ''}#${node.codeLocation?.lineNum}:${node.codeLocation?.columnNum}',
                        children: [
                          if (node.closureNode != null)
                            TextSpan(
                              text:
                                  '\n\nClosure#${node.closureNode!.ownerClass} - uri:',
                              style: TextStyle(
                                color: Color(0xFF7BB2DF),
                                fontSize: 17,
                                fontWeight: FontWeight.normal,
                              ),
                            ),
                          if (node.closureNode != null)
                            TextSpan(
                              text:
                                  '\n\n${node.closureNode?.libraries ?? ''}#${node.closureNode?.funLine}:${node.closureNode?.funColumn}',
                              style: TextStyle(
                                color: Color(0xFF7BB2DF),
                                fontSize: 17,
                                fontWeight: FontWeight.normal,
                              ),
                            ),
                          if (node.fieldOwnerNode != null)
                            TextSpan(
                              text:
                                  '\n\n#${node.fieldOwnerNode?.ownerName ?? ''}',
                              style: TextStyle(
                                color: Color(0xFF7BB2DF),
                                fontSize: 17,
                                fontWeight: FontWeight.normal,
                              ),
                            ),
                        ],
                        style: TextStyle(
                          color: Color(0xFFFFB74D),
                          fontSize: 18,
                          fontWeight: FontWeight.normal,
                        ),
                      ),
                      softWrap: true,
                      overflow: TextOverflow.visible,
                    ),
                  ),
                ),
              ],
            ),
          ),
      ],
    );
  }

  String _getNodeTypeString(LeakedNodeType leakedNodeType) {
    switch (leakedNodeType) {
      case LeakedNodeType.unknown:
        return 'unknown';
      case LeakedNodeType.widget:
        return 'Widget';
      case LeakedNodeType.element:
        return 'Element';
    }
  }
}

// 节点路标 向下的箭头
class NodeRouteSign extends CustomPainter {
  final bool isLast;
  final bool isFirst;
  final Color color;

  final Paint _paint = Paint()
    ..color = Color(0xff1e7ce4)
    ..style = PaintingStyle.fill
    ..strokeWidth = _strokeWidth;

  NodeRouteSign(this.isFirst, this.isLast, this.color) {
    _paint.color = color;
  }

  @override
  void paint(Canvas canvas, Size size) {
    if (!isFirst) {
      canvas.drawLine(Offset(size.width / 2, 0),
          Offset(size.width / 2, size.height / 2), _paint);
      drawArrow(canvas, size);
    } else {
      canvas.drawCircle(Offset(size.width / 2, size.height / 2), 6, _paint);
    }

    canvas.drawLine(Offset(size.width / 2, size.height / 2),
        Offset(size.width / 2, size.height), _paint);

    if (isLast) {
      canvas.drawCircle(Offset(size.width / 2, size.height / 2 + 5), 6, _paint);
    }
  }

  @override
  bool shouldRepaint(covariant NodeRouteSign oldDelegate) {
    return isLast != oldDelegate.isLast;
  }

  void drawArrow(Canvas canvas, Size size) {
    canvas.drawLine(Offset(size.width / 2, size.height / 2),
        Offset(size.width / 2 + 10, size.height / 2 - 10), _paint);
    canvas.drawLine(Offset(size.width / 2, size.height / 2),
        Offset(size.width / 2 - 10, size.height / 2 - 10), _paint);
  }
}
