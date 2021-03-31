import 'package:dokit/kit/apm/apm.dart';
import 'package:dokit/kit/apm/vm/vm_helper.dart';
import 'package:dokit/widget/source_code/source_code_view.dart';
import 'package:flutter/material.dart';
import 'package:dokit/kit/kit.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:dokit/dokit.dart';
import 'dart:convert';

class SourceCodeKit extends ApmKit {
  @override
  Widget createDisplayPage() {
    return SourceCodePage();
  }

  @override
  IStorage createStorage() {
    return CommonStorage(maxCount: 120);
  }

  @override
  String getIcon() {
    return 'images/dk_source_code.png';
  }

  @override
  String getKitName() {
    return ApmKitName.KIT_SOURCE_CODE;
  }

  @override
  void start() {}

  @override
  void stop() {}
}

class SourceCodePage extends StatefulWidget {
  @override
  _SourceCodePageState createState() => _SourceCodePageState();
}

class _SourceCodePageState extends State<SourceCodePage> {
  String sourceCode;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((timeStamp) {
      var renderObject = findTopRenderObject();
      WidgetInspectorService.instance.selection.current = renderObject;
      final String nodeDesc =
          WidgetInspectorService.instance.getSelectedSummaryWidget(null, null);
      if (nodeDesc != null) {
        final Map<String, dynamic> map =
            json.decode(nodeDesc) as Map<String, dynamic>;
        if (map != null) {
          final Map<String, dynamic> location =
              map['creationLocation'] as Map<String, dynamic>;
          if (location != null) {
            final fileLocation = location['file'] as String;
            final fileName = fileLocation.split("/").last;
            getScriptList(fileName).then((value) => {
                  setState(() {
                    sourceCode = value.source;
                  })
                });
          }
        }
      }
    });
  }

  RenderObject findTopRenderObject() {
    Element topElement;
    final ModalRoute<dynamic> rootRoute =
        ModalRoute.of(DoKitApp.appKey.currentContext);
    void listTopView(Element element) {
      if (element.widget is! PositionedDirectional) {
        if (element is RenderObjectElement &&
            element.renderObject is RenderBox) {
          final ModalRoute<dynamic> route = ModalRoute.of(element);
          if (route != null && route != rootRoute) {
            topElement = element;
          }
        }
        element.visitChildren(listTopView);
      }
    }

    DoKitApp.appKey.currentContext.visitChildElements(listTopView);
    if (topElement != null) {
      return topElement.renderObject;
    }
    return null;
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      constraints: BoxConstraints.expand(),
      child: sourceCode == null
          ? Center(
              child: Text("加载中"),
            )
          : Padding(
              padding: const EdgeInsets.all(8.0),
              child: SourceCodeView(sourceCode: sourceCode),
            ),
    );
  }
}
