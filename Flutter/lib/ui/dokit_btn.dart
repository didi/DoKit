import 'package:dokit/kit/apm/apm.dart';
import 'package:dokit/util/screen_util.dart';
import 'package:flutter/material.dart';

import '../dokit.dart';
import 'dokit_app.dart';
import 'resident_page.dart';

final ValueNotifier<Offset> _dokitBtnPositionNotifier =
    ValueNotifier<Offset>(null);

void setDoKitBtnPosition(Offset newPosition) {
  if (newPosition == null) {
    _dokitBtnPositionNotifier.value == null;
    return;
  }

  double dx = newPosition.dx;
  double dy = newPosition.dy;

  if (dx < 0) {
    dx = 0;
  }
  if (dx > ScreenUtil.instance.screenWidth - 80) {
    dx = ScreenUtil.instance.screenWidth - 80;
  }
  if (dy < 0) {
    dy = 0;
  }
  if (dy > ScreenUtil.instance.screenHeight - 26) {
    dy = ScreenUtil.instance.screenHeight - 26;
  }

  _dokitBtnPositionNotifier.value = Offset(dx, dy);
}

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
    return ValueListenableBuilder<Offset>(
        valueListenable: _dokitBtnPositionNotifier,
        builder: (BuildContext context, Offset value, Widget child) {
          offsetA = value;

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
                    setDoKitBtnPosition(offset);
                  },
                  onDraggableCanceled: (Velocity velocity, Offset offset) {}));
        });
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
