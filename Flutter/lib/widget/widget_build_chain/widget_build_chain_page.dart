// Copyright© Dokit for Flutter.
//
// widget_build_chain_page.dart
// Flutter
//
// Created by linusflow on 2021/4/30
// Modified by linusflow on 2021/5/11 下午4:50
//

import 'dart:math';

import 'package:dokit/ui/dokit_app.dart';
import 'package:dokit/widget/widget_build_chain/widget_details_page.dart';
import 'package:flutter/material.dart';

class WidgetBuildChainController {
  OverlayEntry? _overlayEntry;
  final Element element;

  WidgetBuildChainController(this.element);

  void show() {
    if (_overlayEntry != null) {
      return;
    }
    _overlayEntry = OverlayEntry(builder: (BuildContext context) {
      return WidgetBuildChainPage(element: element, controller: this);
    });
    final rootOverlay = doKitOverlayKey.currentState;
    assert(rootOverlay != null);
    rootOverlay!.insert(_overlayEntry!);
  }

  void remove() {
    _overlayEntry?.remove();
    _overlayEntry = null;
  }
}

class WidgetBuildChainPage extends StatefulWidget {
  final Element element;
  final WidgetBuildChainController controller;

  WidgetBuildChainPage(
      {Key? key, required this.element, required this.controller})
      : super(key: key);

  @override
  State<StatefulWidget> createState() => _WidgetBuildChainPageState();
}

class _WidgetBuildChainPageState extends State<WidgetBuildChainPage> {
  late FocusNode _focusNode;
  late TextEditingController _queryTextController;
  List<Element>? _buildChainWidgets;
  List<Element> get buildChainWidgets {
    _buildChainWidgets ??= widget.element.debugGetDiagnosticChain();
    return _buildChainWidgets!;
  }

  late List<CellBean> cells;
  String? queryString;
  List<CellBean> get filterCells => cells
      .where((e) =>
          e.title.toLowerCase().contains(queryString?.toLowerCase() ?? ''))
      .toList();

  final cacheExtent = 44.0;

  final cellColors = const [
    Colors.blue,
    Colors.red,
    Colors.amber,
    Colors.brown,
    Colors.cyanAccent,
    Colors.green,
    Colors.purple,
    Colors.yellow,
  ];
  Color? _lastCellLeadingColor;
  Color get cellLeadingColor {
    Color diffColor;
    do {
      final index = Random().nextInt(cellColors.length - 1);
      diffColor = cellColors[index];
    } while (diffColor == _lastCellLeadingColor);
    _lastCellLeadingColor = diffColor;

    return diffColor;
  }

  @override
  void initState() {
    super.initState();

    _focusNode = FocusNode();
    _queryTextController = TextEditingController();
    cells = buildChainWidgets
        .map((e) =>
            CellBean(cellLeadingColor, e.toStringShort().split('-').first, e))
        .toList();
  }

  @override
  void dispose() {
    _focusNode.dispose();
    _queryTextController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: GestureDetector(
        onTap: () => _focusNode.unfocus(),
        child: Scaffold(
          appBar: AppBar(
            title: RichText(
              text: TextSpan(children: [
                TextSpan(
                    text: 'Widget Build Chain',
                    style:
                        TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                WidgetSpan(child: SizedBox(width: 10)),
                TextSpan(
                    text: 'depth:${widget.element.depth}',
                    style:
                        TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
              ]),
            ),
            leading: GestureDetector(
              onTap: () => _onBack(context),
              child: Icon(
                Icons.chevron_left,
                size: 28,
              ),
            ),
          ),
          body: Container(
            padding: EdgeInsets.all(12),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                TextField(
                  textInputAction: TextInputAction.done,
                  keyboardType: TextInputType.text,
                  controller: _queryTextController,
                  focusNode: _focusNode,
                  decoration: InputDecoration(
                    hintText: '输入需要查看的Widget名称',
                    hintStyle: TextStyle(color: Colors.grey, fontSize: 14),
                    prefixIcon: Icon(
                      Icons.search,
                      size: 24,
                    ),
                  ),
                  style: TextStyle(
                    textBaseline: TextBaseline.alphabetic,
                    fontSize: 14,
                    color: Colors.black,
                  ),
                  onSubmitted: (_) {},
                  onChanged: _onTextChange,
                ),
                SizedBox(height: 8),
                Text(
                  '共${filterCells.length}条数据',
                  style: TextStyle(
                    fontSize: 10,
                    color: Colors.grey,
                  ),
                ),
                SizedBox(height: 2),
                Expanded(
                  child: ListView.builder(
                    itemCount: filterCells.length,
                    cacheExtent: cacheExtent,
                    itemBuilder: (BuildContext context, int index) {
                      return GestureDetector(
                        onTap: () =>
                            _onCellTap(filterCells[index].element, context),
                        child: Container(
                          height: cacheExtent,
                          child: Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            children: [
                              Container(
                                height: cacheExtent * 0.4,
                                width: cacheExtent * 0.4,
                                decoration: BoxDecoration(
                                  color: filterCells[index].leadingColor,
                                  borderRadius: BorderRadius.all(
                                    Radius.circular(cacheExtent * 0.2),
                                  ),
                                ),
                              ),
                              SizedBox(width: 18),
                              Expanded(
                                child: Text(
                                  '${filterCells[index].title}',
                                  maxLines: 1,
                                  overflow: TextOverflow.ellipsis,
                                ),
                              ),
                            ],
                          ),
                          decoration: BoxDecoration(
                            border: Border(
                              bottom:
                                  BorderSide(width: 0.5, color: Colors.black12),
                            ),
                          ),
                        ),
                      );
                    },
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  void _onCellTap(Element e, BuildContext context) {
    Navigator.of(context, rootNavigator: false).push<void>(
      MaterialPageRoute(
        builder: (context) {
          return WidgetDetailsPage(element: e);
        },
      ),
    );
  }

  void _onTextChange(String query) {
    setState(() {
      queryString = query;
    });
  }

  void _onBack(BuildContext context) {
    widget.controller.remove();
  }
}

class CellBean {
  final Color leadingColor;
  final String title;
  final Element element;

  CellBean(this.leadingColor, this.title, this.element);
}
