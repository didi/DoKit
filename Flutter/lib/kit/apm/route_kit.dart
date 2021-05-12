import 'package:dokit/dokit.dart';
import 'package:dokit/kit/apm/apm.dart';
import 'package:dokit/kit/kit.dart';
import 'package:dokit/ui/dokit_app.dart';
import 'package:flutter/material.dart';

class RouteKit extends ApmKit {
  @override
  Widget createDisplayPage() {
    return RouteInfoPage();
  }

  @override
  IStorage createStorage() {
    return CommonStorage(maxCount: 120);
  }

  @override
  String getKitName() {
    return ApmKitName.KIT_ROUTE;
  }

  @override
  String getIcon() {
    return 'images/dk_view_route.png';
  }

  @override
  void start() {}

  @override
  void stop() {}
}

class RouteInfoPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return RouteInfoPageState();
  }
}

class RouteInfoPageState extends State<RouteInfoPage> {
  RouteInfo? route;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance?.addPostFrameCallback((Duration timeStamp) {
      final RouteInfo? route = findRoute();
      if (route != null && (route.current != null)) {
        setState(() {
          this.route = route;
        });
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      child: Container(
          margin: const EdgeInsets.all(16),
          child: Column(
            children: <Widget>[
              Container(
                  alignment: Alignment.topLeft,
                  width: MediaQuery.of(context).size.width,
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: <Widget>[
                      const Text('当前页面路由树',
                          style: TextStyle(
                              color: Color(0xff333333),
                              fontSize: 16,
                              fontWeight: FontWeight.bold)),
                      Container(
                          alignment: Alignment.center,
                          margin: const EdgeInsets.only(top: 3),
                          child: const Text(
                              'Navigator存在嵌套情况，打印当前页面在每层Navigator内的路由信息',
                              style: TextStyle(
                                  color: Color(0xff999999), fontSize: 12))),
                      Container(
                          margin: const EdgeInsets.only(top: 10),
                          alignment: Alignment.topLeft,
                          child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: buildRouteInfoWidget()))
                    ],
                  )),
            ],
          )),
    );
  }

  List<Widget> buildRouteInfoWidget() {
    final List<Widget> widgets = <Widget>[];
    RouteInfo? route = this.route;
    if (route == null) {
      return widgets;
    }
    do {
      if (route?.current != null) {
        widgets.add(
          Container(
            padding: const EdgeInsets.all(12),
            width: MediaQuery.of(context).size.width,
            decoration: const BoxDecoration(
                color: Color(0xfff5f6f7),
                borderRadius: BorderRadius.all(Radius.circular(4.0))),
            alignment: Alignment.topLeft,
            child: RichText(
              text: TextSpan(
                children: <TextSpan>[
                  const TextSpan(
                      text: '路由名称: ',
                      style: TextStyle(
                          fontSize: 10,
                          color: Color(0xff333333),
                          height: 1.5,
                          fontWeight: FontWeight.bold)),
                  TextSpan(
                      text: route?.current?.settings.name,
                      style: const TextStyle(
                          fontSize: 10, height: 1.5, color: Color(0xff666666))),
                  const TextSpan(
                      text: '\n路由参数: ',
                      style: TextStyle(
                          height: 1.5,
                          fontSize: 10,
                          color: Color(0xff333333),
                          fontWeight: FontWeight.bold)),
                  TextSpan(
                      text: '${route?.current?.settings.arguments}',
                      style: const TextStyle(
                          fontSize: 10, height: 1.5, color: Color(0xff666666))),
                  const TextSpan(
                      text: '\n所在Navigator: ',
                      style: TextStyle(
                          fontSize: 10,
                          height: 1.5,
                          color: Color(0xff333333),
                          fontWeight: FontWeight.bold)),
                  TextSpan(
                      text: route?.parentNavigator != null
                          ? route?.parentNavigator.toString()
                          : '未知',
                      style: const TextStyle(
                          fontSize: 10, height: 1.5, color: Color(0xff666666))),
                  const TextSpan(
                      text: '\n所有信息: ',
                      style: TextStyle(
                          fontSize: 10,
                          height: 1.5,
                          color: Color(0xff333333),
                          fontWeight: FontWeight.bold)),
                  TextSpan(
                      text: route?.current.toString(),
                      style: const TextStyle(
                          fontSize: 10, height: 1.5, color: Color(0xff666666))),
                ],
              ),
            ),
          ),
        );
      }
      route = route?.parent;
      if (route != null && route.parent != null) {
        widgets.add(
          Container(
            margin: const EdgeInsets.only(top: 10, bottom: 10),
            alignment: Alignment.center,
            child: Image.asset('images/dk_route_arrow.png',
                package: DK_PACKAGE_NAME, height: 13, width: 12),
          ),
        );
      }
      // 过滤掉dokit自带的navigator
    } while (route != null);
    return widgets;
  }

  RouteInfo? findRoute() {
    Element? topElement;
    var context = DoKitApp.appKey.currentContext;
    if (context == null) return null;
    final ModalRoute<dynamic>? rootRoute = ModalRoute.of(context);
    void listTopView(Element element) {
      if (element.widget is! PositionedDirectional) {
        if (element is RenderObjectElement &&
            element.renderObject is RenderBox) {
          final ModalRoute<dynamic>? route = ModalRoute.of(element);
          if (route != null && route != rootRoute) {
            topElement = element;
          }
        }
        element.visitChildren(listTopView);
      }
    }

    context.visitChildElements(listTopView);
    if (topElement != null) {
      final RouteInfo routeInfo = RouteInfo();
      routeInfo.current = ModalRoute.of(topElement!);
      buildNavigatorTree(topElement!, routeInfo);
      return routeInfo;
    }
    return null;
  }

  /// 反向遍历生成路由树
  void buildNavigatorTree(Element element, RouteInfo routeInfo) {
    final NavigatorState? navigatorState =
        element.findAncestorStateOfType<NavigatorState>();

    if (navigatorState != null) {
      final RouteInfo parent = RouteInfo();
      parent.current = ModalRoute.of(navigatorState.context);
      routeInfo.parent = parent;
      routeInfo.parentNavigator = navigatorState.widget;
      return buildNavigatorTree(navigatorState.context as Element, parent);
    }
  }
}

class RouteInfo extends IInfo {
  ModalRoute<dynamic>? current;
  Widget? parentNavigator;
  RouteInfo? parent;

  @override
  int getValue() {
    return 0;
  }
}
