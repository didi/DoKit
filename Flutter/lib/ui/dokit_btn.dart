import 'package:dokit/kit/apm/apm.dart';
import 'package:flutter/material.dart';

import '../dokit.dart';
import 'dokit_app.dart';
import 'resident_page.dart';

// 入口btn
// ignore: must_be_immutable
class DoKitBtn extends StatefulWidget {
  DoKitBtn() : super(key: doKitBtnKey);

  static GlobalKey<DoKitBtnState> doKitBtnKey = GlobalKey<DoKitBtnState>();
  OverlayEntry overlayEntry;

  @override
  DoKitBtnState createState() => DoKitBtnState(overlayEntry);

  void addToOverlay() {
    assert(overlayEntry == null);
    overlayEntry = OverlayEntry(builder: (BuildContext context) {
      return this;
    });
    final OverlayState rootOverlay = doKitOverlayKey.currentState;
    assert(rootOverlay != null);
    rootOverlay.insert(overlayEntry);
    ApmKitManager.instance.startUp();
  }
}

class DoKitBtnState extends State<DoKitBtn> {
  DoKitBtnState(this.owner);

  Offset offsetA; //按钮的初始位置
  OverlayEntry owner;
  OverlayEntry debugPage;
  bool showDebugPage = false;

  @override
  Widget build(BuildContext context) {
    return Positioned(
        left: offsetA?.dx,
        top: offsetA?.dy,
        right: offsetA == null ? 20 : null,
        bottom: offsetA == null ? 120 : null,
        child: Draggable<dynamic>(
            child: Container(
              width: 70,
              height: 70,
              alignment: Alignment.center,
              child: FlatButton(
                padding: const EdgeInsets.all(0),
                child: Image.asset('images/dokit_flutter_btn.png',
                    package: DK_PACKAGE_NAME, height: 70, width: 70),
                onPressed: openDebugPage,
              ),
            ),
            feedback: Container(
              width: 70,
              height: 70,
              alignment: Alignment.center,
              child: FlatButton(
                padding: const EdgeInsets.all(0),
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
      doKitOverlayKey.currentState.insert(debugPage, below: owner);
      showDebugPage = true;
    }
  }

  void closeDebugPage() {
    if (showDebugPage && debugPage != null) {
      debugPage.remove();
      showDebugPage = false;
    }
  }
}

void hideDebugPage() {
  if (DoKitBtn.doKitBtnKey.currentState == null) {
    return;
  }
  if (DoKitBtn.doKitBtnKey.currentState.showDebugPage) {
    DoKitBtn.doKitBtnKey.currentState.closeDebugPage();
  }
}
