import 'package:dokit/dokit.dart';
import 'package:dokit/kit/apm/apm.dart';
import 'package:dokit/ui/dokit_app.dart';
import 'package:dokit/ui/resident_page.dart';
import 'package:flutter/material.dart';

// DoKitBtn 点击事件回调
// 参数说明：
// true : dokit面板展开
// false: dokit面板收起
typedef DoKitBtnClickedCallback = void Function(bool);

// 入口btn
// ignore: must_be_immutable
class DoKitBtn extends StatefulWidget {
  DoKitBtn() : super(key: doKitBtnKey);

  static GlobalKey<DoKitBtnState> doKitBtnKey = GlobalKey<DoKitBtnState>();
  OverlayEntry? overlayEntry;
  DoKitBtnClickedCallback? btnClickCallback;

  @override
  DoKitBtnState createState() => DoKitBtnState(overlayEntry!);

  void addToOverlay() {
    assert(overlayEntry == null);
    overlayEntry = OverlayEntry(builder: (BuildContext context) {
      return this;
    });
    final OverlayState? rootOverlay = doKitOverlayKey.currentState;
    assert(rootOverlay != null);
    rootOverlay?.insert(overlayEntry!);
    ApmKitManager.instance.startUp();
  }
}

class DoKitBtnState extends State<DoKitBtn> {
  DoKitBtnState(this.owner);

  Offset? offsetA; //按钮的初始位置
  final OverlayEntry owner;
  OverlayEntry? debugPage;
  bool showDebugPage = false;

  @override
  Widget build(BuildContext context) {
    return Positioned(
        left: offsetA?.dx,
        top: offsetA?.dy,
        right: offsetA == null ? 20 : null,
        bottom: offsetA == null ? 120 : null,
        child: Draggable(
            child: Container(
              width: 70,
              height: 70,
              alignment: Alignment.center,
              child: TextButton(
                style: ButtonStyle(
                  padding: MaterialStateProperty.all(EdgeInsets.all(0)),
                ),
                child: Image.asset('images/dokit_flutter_btn.png',
                    package: DK_PACKAGE_NAME, height: 70, width: 70),
                onPressed: openDebugPage,
              ),
            ),
            feedback: Container(
              width: 70,
              height: 70,
              alignment: Alignment.center,
              child: TextButton(
                style: ButtonStyle(
                  padding: MaterialStateProperty.all(EdgeInsets.all(0)),
                ),
                child: Image.asset('images/dokit_flutter_btn.png',
                    package: DK_PACKAGE_NAME, height: 70, width: 70),
                onPressed: openDebugPage,
              ),
            ),
            childWhenDragging: Container(),
            onDragEnd: (DraggableDetails detail) {
              final Offset offset = detail.offset;
              setState(() {
                final Size size = MediaQuery.of(context).size;
                final double width = size.width;
                final double height = size.height;
                double x = offset.dx;
                double y = offset.dy;
                if (x < 0) {
                  x = 0;
                }
                if (x > width - 80) {
                  x = width - 80;
                }
                if (y < 0) {
                  y = 0;
                }
                if (y > height - 26) {
                  y = height - 26;
                }
                offsetA = Offset(x, y);
              });
            },
            onDraggableCanceled: (Velocity velocity, Offset offset) {}));
  }

  void openDebugPage() {
    debugPage ??= OverlayEntry(builder: (BuildContext context) {
      return ResidentPage();
    });
    if (showDebugPage) {
      closeDebugPage();
    } else {
      doKitOverlayKey.currentState?.insert(debugPage!, below: owner);
      showDebugPage = true;
    }

    widget.btnClickCallback!(showDebugPage);
  }

  void closeDebugPage() {
    if (showDebugPage && debugPage != null) {
      debugPage!.remove();
      showDebugPage = false;
    }
  }
}
