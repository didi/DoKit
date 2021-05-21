import 'package:dokit/kit/apm/apm.dart';
import 'package:dokit/kit/apm/launch/model.dart';
import 'package:dokit/kit/apm/launch/route_observer.dart';
import 'package:dokit/kit/kit.dart';
import 'package:dokit/ui/dokit_app.dart';
import 'package:dokit/util/screen_util.dart';
import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';

class PageLaunchKit extends ApmKit {
  static bool _open = false;

  static OverlayEntry _overlayEntry = OverlayEntry(builder: (context) {
    return TimeCounter(notifier.value);
  });

  static VoidCallback? callback;

  static double left = ScreenUtil.instance.screenWidth / 2;
  static double top = ScreenUtil.instance.screenHeight / 2;

  static void closeCounter() {
    enabled = false;
    _open = false;
    if (callback != null) {
      notifier.removeListener(PageLaunchKit.callback!);
    }
    _overlayEntry.remove();
  }

  @override
  Widget createDisplayPage() {
    return PageLaunchPage();
  }

  @override
  IStorage createStorage() {
    return CommonStorage(maxCount: 120);
  }

  @override
  String getIcon() {
    return 'images/dk_time_counter.png';
  }

  @override
  String getKitName() {
    return ApmKitName.KIT_PAGE_LAUNCH;
  }

  @override
  void start() {}

  @override
  void stop() {}
}

class PageLaunchPage extends StatefulWidget {
  @override
  _PageLaunchPageState createState() => _PageLaunchPageState();
}

class _PageLaunchPageState extends State<PageLaunchPage> {
  @override
  Widget build(BuildContext context) {
    return Container(
      constraints: BoxConstraints.expand(),
      child: Padding(
        padding: const EdgeInsets.all(10.0),
        child: Column(
          children: [
            Text('请在Profile模式下检测性能'),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text('页面启动耗时'),
                Switch(
                    value: PageLaunchKit._open,
                    onChanged: (newValue) {
                      setState(() {
                        PageLaunchKit._open = newValue;
                        if (PageLaunchKit._open) {
                          openCounter();
                        } else {
                          PageLaunchKit.closeCounter();
                        }
                      });
                    })
              ],
            )
          ],
        ),
      ),
    );
  }

  void openCounter() {
    enabled = true;
    PageLaunchKit.callback = () {
      PageLaunchKit._overlayEntry.markNeedsBuild();
    };
    notifier.addListener(PageLaunchKit.callback!);
    doKitOverlayKey.currentState?.insert(PageLaunchKit._overlayEntry);
  }
}

class TimeCounter extends StatefulWidget {
  final LaunchInfo info;

  TimeCounter(this.info);

  @override
  _TimeCounterState createState() => _TimeCounterState();
}

class _TimeCounterState extends State<TimeCounter> {
  @override
  Widget build(BuildContext context) {
    return Positioned(
      left: PageLaunchKit.left,
      top: PageLaunchKit.top,
      child: Draggable(
        childWhenDragging: Container(),
        onDragEnd: (details) {
          setState(() {
            PageLaunchKit.left = details.offset.dx;
            PageLaunchKit.top = details.offset.dy;
          });
        },
        feedback: buildRealCounter(),
        child: buildRealCounter(),
      ),
    );
  }

  Container buildRealCounter() {
    return Container(
      decoration: BoxDecoration(
        color: Color(0xffcccccc),
        borderRadius: BorderRadius.all(Radius.circular(4)),
      ),
      child: Padding(
        padding: const EdgeInsets.all(8.0),
        child: Stack(
          children: [
            Padding(
              padding: const EdgeInsets.only(right: 18.0),
              child: Column(
                children: [
                  Text(
                    '${widget.info.previousPage} -> ${widget.info.newPage}',
                    style: TextStyle(color: Color(0xff333333)),
                  ),
                  Text(
                    'Cost Time: ${widget.info.costTime} ms',
                    style: TextStyle(color: Color(0xff333333)),
                  )
                ],
              ),
            ),
            Positioned(
              right: 0,
              child: GestureDetector(
                child: Image.asset(
                  'images/dokit_ic_close.png',
                  package: 'dokit',
                  height: 22,
                  width: 22,
                ),
                onTap: () {
                  PageLaunchKit.closeCounter();
                },
              ),
            )
          ],
        ),
      ),
    );
  }
}
