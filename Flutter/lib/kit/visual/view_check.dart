// Copyright© Dokit for Flutter.
//
// view_check.dart
// Flutter
//
// Created by linjizong on 2021/3/05
// Modified by linusflow on 2021/5/12 下午2:51
//

import 'dart:convert';
import 'dart:math';

import 'package:dokit/dokit.dart';
import 'package:dokit/kit/visual/visual.dart';
import 'package:dokit/ui/dokit_app.dart';
import 'package:dokit/ui/dokit_btn.dart';
import 'package:dokit/util/screen_util.dart';
import 'package:dokit/widget/dash_decoration.dart';
import 'package:dokit/widget/widget_build_chain/widget_build_chain_page.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';

class ViewCheckerKit extends VisualKit {
  ViewCheckerKit._privateConstructor() {
    final ViewCheckerWidget widget = ViewCheckerWidget();

    focusEntry = OverlayEntry(builder: (BuildContext context) {
      return widget;
    });

    rectEntry = OverlayEntry(builder: (BuildContext context) {
      return Positioned(
        left: area.left,
        top: area.top,
        child: RectWidget(),
      );
    });
    infoEntry = OverlayEntry(builder: (BuildContext context) {
      return InfoWidget();
    });
    alignEntry = OverlayEntry(builder: (BuildContext context) {
      return const AlignRulerWidget();
    });
    isShown = false;
  }

  late bool isShown;
  late OverlayEntry focusEntry;
  late OverlayEntry rectEntry;
  late OverlayEntry infoEntry;
  late OverlayEntry alignEntry;

  Rect area = const Rect.fromLTWH(0, 0, 0, 0);
  String info = '移动屏幕中心焦点聚焦控件，查看控件信息';
  RenderObjectElement? selectedElement;

  final ValueNotifier<Offset> viewCheckerWidgetCenterNotifier =
      ValueNotifier<Offset>(ScreenUtil.instance.screenCenter);

  static final ViewCheckerKit _instance = ViewCheckerKit._privateConstructor();

  static ViewCheckerKit get instance => _instance;

  static void show(BuildContext? context, OverlayEntry? entrance) {
    _instance._show(context, entrance);
  }

  void _show(BuildContext? context, OverlayEntry? entrance) {
    if (isShown) {
      return;
    }
    isShown = true;
    doKitOverlayKey.currentState?.insert(focusEntry, below: entrance);
    doKitOverlayKey.currentState?.insert(infoEntry, below: focusEntry);
    doKitOverlayKey.currentState?.insert(rectEntry, below: infoEntry);
    doKitOverlayKey.currentState?.insert(alignEntry, below: rectEntry);
  }

  static bool hide(BuildContext context) {
    return _instance._hide(context);
  }

  bool _hide(BuildContext context) {
    if (!isShown) {
      return false;
    }
    isShown = false;
    focusEntry.remove();
    rectEntry.remove();
    infoEntry.remove();
    alignEntry.remove();
    viewCheckerWidgetCenterNotifier.value = ScreenUtil.instance.screenCenter;
    area = const Rect.fromLTWH(0, 0, 0, 0);
    info = '移动屏幕中心焦点聚焦控件，查看控件信息';
    return true;
  }

  @override
  String getIcon() {
    return 'images/dk_view_check.png';
  }

  @override
  String getKitName() {
    return VisualKitName.KIT_VIEW_CHECK;
  }

  @override
  void tabAction() {
    final DoKitBtnState? state = DoKitBtn.doKitBtnKey.currentState;
    state?.closeDebugPage();
    show(DoKitBtn.doKitBtnKey.currentContext, state?.owner);
  }
}

class RectWidget extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return _RectWidgetState();
  }
}

class _RectWidgetState extends State<RectWidget> {
  @override
  Widget build(BuildContext context) {
    return Container(
        width: ViewCheckerKit._instance.area.width,
        height: ViewCheckerKit._instance.area.height,
        decoration: const DashedDecoration(
            dashedColor: Color(0xffCC3000),
            color: Color(0x183ca0e6),
            borderRadius: BorderRadius.all(Radius.circular(1))));
  }
}

class InfoWidget extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return _InfoWidgetState();
  }
}

class _InfoWidgetState extends State<InfoWidget> {
  late double top;
  late WidgetBuildChainController controller;

  @override
  Widget build(BuildContext context) {
    top = MediaQuery.of(context).size.height - 250;
    return Positioned(
      left: 20,
      top: top,
      child: Draggable(
          child: _buildInfoView(),
          feedback: _buildInfoView(),
          childWhenDragging: Container(),
          onDragEnd: (DraggableDetails detail) {
            final Offset offset = detail.offset;
            setState(() {
              top = offset.dy;
              if (top < 0) {
                top = 0;
              }
            });
          },
          onDraggableCanceled: (Velocity velocity, Offset offset) {}),
    );
  }

  Widget _buildInfoView() {
    final Size size = MediaQuery.of(context).size;
    return Container(
        width: size.width - 40,
        padding: const EdgeInsets.only(left: 10, right: 16, top: 9, bottom: 24),
        decoration: BoxDecoration(
            border: Border.all(color: const Color(0xffeeeeee), width: 0.5),
            borderRadius: const BorderRadius.all(Radius.circular(4)),
            color: Colors.white),
        alignment: Alignment.centerLeft,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: <Widget>[
            Container(
                alignment: Alignment.centerRight,
                width: size.width - 40,
                child: GestureDetector(
                  child: Image.asset('images/dokit_ic_close.png',
                      package: DK_PACKAGE_NAME, height: 22, width: 22),
                  onTap: () {
                    ViewCheckerKit.hide(context);
                  },
                )),
            Text(
              ViewCheckerKit._instance.info,
              style: const TextStyle(
                  color: Color(0xff333333),
                  fontFamily: 'PingFang SC',
                  fontWeight: FontWeight.normal,
                  decoration: TextDecoration.none,
                  fontSize: 12),
            ),
            Offstage(
              offstage: ViewCheckerKit._instance.selectedElement == null,
              child: GestureDetector(
                onTap: _openWidgetBuildChainPage,
                child: Padding(
                  padding: EdgeInsets.only(top: 6),
                  child: Text(
                    '查看控件的build链',
                    style: TextStyle(
                      color: Colors.blue,
                      fontSize: 10,
                      decoration: TextDecoration.underline,
                    ),
                  ),
                ),
              ),
            )
          ],
        ));
  }

  void _openWidgetBuildChainPage() {
    if (ViewCheckerKit._instance.selectedElement == null) {
      return;
    }
    controller =
        WidgetBuildChainController(ViewCheckerKit._instance.selectedElement!);
    controller.show();
  }
}

// ignore: must_be_immutable
class ViewCheckerWidget extends StatefulWidget {
  ViewCheckerWidget({Key? key, this.diameter = 40}) : super(key: key);

  late OverlayEntry owner;
  late OverlayEntry debugPage;
  bool showDebugPage = false;

  final double diameter;

  @override
  _ViewCheckerWidgetState createState() => _ViewCheckerWidgetState();
}

class _ViewCheckerWidgetState extends State<ViewCheckerWidget> {
  Offset? buttonOffset; // 按钮的初始位置
  Offset? touchOffset; // 手指和屏幕接触的初始位置
  Offset get deltaOffset {
    if (touchOffset == null || buttonOffset == null) {
      return Offset.zero;
    }
    return touchOffset! - buttonOffset!;
  }

  static const String _dokitViewCheckGroup = 'dokit_view_check-group';

  @override
  void dispose() {
    // ignore: invalid_use_of_protected_member
    WidgetInspectorService.instance.disposeGroup(_dokitViewCheckGroup);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final Size size = MediaQuery.of(context).size;
    final double width = size.width;
    final double height = size.height;
    buttonOffset ??= ScreenUtil.instance.screenCenter -
        Offset(widget.diameter / 2, widget.diameter / 2);
    return Positioned(
      left: buttonOffset!.dx,
      top: buttonOffset!.dy,
      child: Listener(
        behavior: HitTestBehavior.opaque,
        onPointerDown: (PointerDownEvent event) {
          touchOffset = event.position;
        },
        onPointerMove: (PointerMoveEvent event) {
          ViewCheckerKit._instance.viewCheckerWidgetCenterNotifier.value =
              event.position -
                  deltaOffset +
                  Offset(widget.diameter / 2, widget.diameter / 2);
        },
        child: Draggable(
            child: FocusWidget(widget.diameter),
            feedback: FocusWidget(widget.diameter),
            childWhenDragging: Container(),
            onDragEnd: (DraggableDetails detail) {
              final Offset offset = detail.offset;
              bool needCorrect = false;
              setState(() {
                double x = offset.dx;
                double y = offset.dy;
                // 避免在iOS中，按钮移动到statusBar所在区域出现无法再拖动按钮的问题
                final double statusBarHeight =
                    ScreenUtil.instance.statusBarHeight;
                if (x < -widget.diameter / 2) {
                  x = -widget.diameter / 2;
                  needCorrect = true;
                }
                if (x > width - widget.diameter / 2) {
                  x = width - widget.diameter / 2;
                  needCorrect = true;
                }
                if (y < statusBarHeight - widget.diameter / 2) {
                  y = statusBarHeight - widget.diameter / 2;
                  needCorrect = true;
                }
                if (y > height - widget.diameter / 2) {
                  y = height - widget.diameter / 2;
                  needCorrect = true;
                }
                buttonOffset = Offset(x, y);
                if (needCorrect) {
                  ViewCheckerKit
                          ._instance.viewCheckerWidgetCenterNotifier.value =
                      buttonOffset! +
                          Offset(widget.diameter / 2, widget.diameter / 2);
                }
                findFocusView();
              });
            },
            onDraggableCanceled: (Velocity velocity, Offset offset) {}),
      ),
    );
  }

  // 找到当前聚焦控件的方式：
  // 1.从根节点开始向下遍历，找到当前处于顶部的控件，记录这个控件的路由信息，作为当前页面路由
  // 2.遍历过程中，记录所有和焦点悬浮窗有交集控件。
  // 3.遍历上面记录的控件，获取控件所在路由信息和当前页面路由一致的控件，再计算(焦点悬浮窗和在控件重叠区)/(组件面积)，占比更高者作为当前聚焦组件
  // 4.debug模式下，将聚焦控件设置为选中控件，可以获取到源码信息
  void findFocusView() {
    final RenderObjectElement? element = resolveTree();
    ViewCheckerKit._instance.selectedElement = element;

    if (element != null && element.size != null) {
      final Offset offset =
          (element.renderObject as RenderBox).localToGlobal(Offset.zero);
      ViewCheckerKit._instance.area = Rect.fromLTWH(
          offset.dx, offset.dy, element.size!.width, element.size!.height);
      ViewCheckerKit._instance.info = toInfoString(element);
    } else {
      ViewCheckerKit._instance.area = const Rect.fromLTWH(0, 0, 0, 0);
    }
  }

  String toInfoString(RenderObjectElement element) {
    String? fileLocation;
    int? line;
    int? column;
    if (kDebugMode) {
      WidgetInspectorService.instance.selection.current = element.renderObject;
      final id = WidgetInspectorService.instance
          // ignore: invalid_use_of_protected_member
          .toId(element.renderObject.toDiagnosticsNode(), _dokitViewCheckGroup);
      if (id == null) {
        return '';
      }
      final String? nodeDesc = WidgetInspectorService.instance
          .getSelectedSummaryWidget(id, _dokitViewCheckGroup);

      if (nodeDesc != null) {
        final Map<String, dynamic>? map =
            json.decode(nodeDesc) as Map<String, dynamic>;
        if (map != null) {
          final Map<String, dynamic>? location =
              map['creationLocation'] as Map<String, dynamic>;
          if (location != null) {
            fileLocation = location['file'] as String;
            line = location['line'] as int;
            column = location['column'] as int;
          }
        }
      }
    }

    final Offset offset =
        (element.renderObject as RenderBox).localToGlobal(Offset.zero);
    String info = '控件名称: ${element.widget.toString()}\n'
        '控件位置: 左${offset.dx} 上${offset.dy} 宽${element.size!.width} 高${element.size!.height}';
    if (fileLocation != null) {
      info += '\n源码位置: $fileLocation' '【行 ${line!} 列 ${column!}】';
    }
    return info;
  }

  RenderObjectElement? resolveTree() {
    Element? currentPage;
    final List<RenderObjectElement> inBounds = <RenderObjectElement>[];
    final Rect focus = Rect.fromLTWH(
        buttonOffset!.dx, buttonOffset!.dy, widget.diameter, widget.diameter);
    // 记录根路由，用以过滤overlay
    final ModalRoute<dynamic>? rootRoute =
        ModalRoute.of<dynamic>(DoKitApp.appKey.currentContext!);

    void filter(Element element) {
      // 兼容IOS，IOS的MaterialApp会在Navigator后再插入一个PositionedDirectional控件，用以处理右滑关闭手势，遍历的时候跳过该控件
      // ignore:
      if (element.widget is! PositionedDirectional) {
        if (element is RenderObjectElement &&
            element.renderObject is RenderBox) {
          final ModalRoute<dynamic>? route = ModalRoute.of<dynamic>(element);
          // overlay不包含route信息，通过ModalRoute.of会获取到当前所在materialapp在其父节点内的route,此处对overlay做过滤。只能过滤掉直接添加在根MaterialApp的overlay,
          // 并且该overlay的子widget不能包含materialApp或navigator
          if (route != null && route != rootRoute) {
            currentPage = element;
            final RenderBox renderBox = element.renderObject as RenderBox;
            if (renderBox.hasSize && renderBox.attached) {
              final Offset offset = renderBox.localToGlobal(Offset.zero);
              if (isOverlap(
                  focus,
                  Rect.fromLTWH(offset.dx, offset.dy, element.size!.width,
                      element.size!.height))) {
                inBounds.add(element);
              }
            }
          }
        }
        element.visitChildren(filter);
      }
    }

    DoKitApp.appKey.currentContext?.visitChildElements(filter);
    RenderObjectElement? topElement;
    final ModalRoute<dynamic>? route =
        currentPage != null ? ModalRoute.of<dynamic>(currentPage!) : null;
    for (final RenderObjectElement element in inBounds) {
      if ((route == null || ModalRoute.of(element) == route) &&
          checkSelected(topElement, element)) {
        topElement = element;
      }
    }
    return topElement;
  }

  bool checkSelected(RenderObjectElement? last, RenderObjectElement current) {
    if (last == null) {
      return true;
    } else {
      return getOverlayPercent(current) > getOverlayPercent(last) &&
          current.depth >= last.depth;
    }
  }

  double getOverlayPercent(RenderObjectElement element) {
    if (element.size == null) {
      return 0;
    }
    final double size = element.size!.width * element.size!.height;
    final Offset offset =
        (element.renderObject as RenderBox).localToGlobal(Offset.zero);
    final Rect rect = Rect.fromLTWH(
        offset.dx, offset.dy, element.size!.width, element.size!.height);
    final double xc1 = max(rect.left, buttonOffset!.dx);
    final double yc1 = max(rect.top, buttonOffset!.dy);
    final double xc2 = min(rect.right, buttonOffset!.dx + widget.diameter);
    final double yc2 = min(rect.bottom, buttonOffset!.dy + widget.diameter);
    return ((xc2 - xc1) * (yc2 - yc1)) / size;
  }

  bool isOverlap(Rect rc1, Rect rc2) {
    return rc1.left + rc1.width > rc2.left &&
        rc2.left + rc2.width > rc1.left &&
        rc1.top + rc1.height > rc2.top &&
        rc2.top + rc2.height > rc1.top;
  }
}

class FocusWidget extends StatelessWidget {
  const FocusWidget(this.diameter, {Key? key}) : super(key: key);

  final double diameter;

  @override
  Widget build(BuildContext context) {
    return Container(
      width: diameter,
      height: diameter,
      decoration: BoxDecoration(
        shape: BoxShape.circle,
        border: Border.all(color: const Color(0xff337CC4), width: 1),
        color: const Color(0x00000000),
      ),
      alignment: Alignment.center,
      child: Container(
        width: diameter * 1 / 3,
        height: diameter * 1 / 3,
        decoration: const BoxDecoration(
          shape: BoxShape.circle,
          color: Color(0x30CC3A4B),
        ),
        alignment: Alignment.center,
        child: Container(
          width: diameter * 1 / 6,
          height: diameter * 1 / 6,
          decoration: const BoxDecoration(
            shape: BoxShape.circle,
            color: Color(0xffCC3A4B),
          ),
        ),
      ),
    );
  }
}

class AlignRulerWidget extends StatelessWidget {
  const AlignRulerWidget({
    Key? key,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final width = MediaQuery.of(context).size.width;
    final height = MediaQuery.of(context).size.height;

    return ValueListenableBuilder<Offset>(
      builder: (BuildContext context, Offset value, Widget? child) {
        return Container(
          height: height,
          width: width,
          child: CustomPaint(
            painter: AlignRulerPainter(value),
          ),
        );
      },
      valueListenable: ViewCheckerKit._instance.viewCheckerWidgetCenterNotifier,
    );
  }
}

class AlignRulerPainter extends CustomPainter {
  AlignRulerPainter(this.center);

  final Offset center;

  @override
  void paint(Canvas canvas, Size size) {
    _paintVertical(canvas, size);
    _paintHorizontal(canvas, size);
  }

  void _paintVertical(Canvas canvas, Size size) {
    final double sh = size.height;

    final Paint paint = Paint()
      ..color = Colors.red
      ..style = PaintingStyle.stroke
      ..strokeWidth = 1;

    final Path path = Path()
      ..moveTo(center.dx, 0)
      ..lineTo(center.dx, sh);

    canvas.drawPath(path, paint);
    path.close();

    final double dx = center.dx;
    final double dy = center.dy;
    final double top = dy;
    final double bottom = sh - dy;

    TextPainter(
      text: TextSpan(
        text: top.toStringAsFixed(1),
        style: const TextStyle(fontSize: 12, color: Colors.red),
      ),
      textAlign: TextAlign.right,
      textDirection: TextDirection.ltr,
      textWidthBasis: TextWidthBasis.longestLine,
    )
      ..layout(minWidth: 0)
      ..paint(canvas, Offset(dx, dy / 2));

    TextPainter(
      text: TextSpan(
        text: bottom.toStringAsFixed(1),
        style: const TextStyle(fontSize: 12, color: Colors.red),
      ),
      textAlign: TextAlign.right,
      textDirection: TextDirection.ltr,
      textWidthBasis: TextWidthBasis.longestLine,
    )
      ..layout(minWidth: 0)
      ..paint(canvas, Offset(dx, sh - bottom / 2));
  }

  void _paintHorizontal(Canvas canvas, Size size) {
    final double sw = size.width;

    final Paint paint = Paint()
      ..color = Colors.red
      ..style = PaintingStyle.stroke
      ..strokeWidth = 1;

    final Path path = Path()
      ..moveTo(0, center.dy)
      ..lineTo(sw, center.dy);

    canvas.drawPath(path, paint);
    path.close();

    final double dx = center.dx;
    final double dy = center.dy;
    final double right = sw - dx;
    final double left = dx;

    TextPainter(
      text: TextSpan(
        text: right.toStringAsFixed(1),
        style: const TextStyle(fontSize: 12, color: Colors.red),
      ),
      textAlign: TextAlign.right,
      textDirection: TextDirection.ltr,
      textWidthBasis: TextWidthBasis.longestLine,
    )
      ..layout(minWidth: 0)
      ..paint(canvas, Offset(sw - right / 2, dy));

    TextPainter(
      text: TextSpan(
        text: left.toStringAsFixed(1),
        style: const TextStyle(fontSize: 12, color: Colors.red),
      ),
      textAlign: TextAlign.right,
      textDirection: TextDirection.ltr,
      textWidthBasis: TextWidthBasis.longestLine,
    )
      ..layout(minWidth: 0)
      ..paint(canvas, Offset(dx / 2, dy));
  }

  @override
  bool shouldRepaint(AlignRulerPainter oldDelegate) => true;

  @override
  bool hitTest(Offset position) => false;
}
