// This is a basic Flutter widget test.
//
// To perform an interaction with a widget in your test, use the WidgetTester
// utility that Flutter provides. For example, you can send tap and scroll
// gestures. You can also use WidgetTester to find child widgets in the widget
// tree, read text, and verify that the values of widget properties are correct.

import 'dart:async';
import 'dart:convert';
import 'dart:io';
import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:dokit/dokit.dart';
import 'package:flutter/services.dart';

void main() {
  DoKit.runApp(app: DoKitApp(MyApp()), useInRelease: true);
  // runApp(MyApp());
}

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'DoKit Test',
      theme: ThemeData(
        // This is the theme of your application.
        //
        // Try running your application with "flutter run". You'll see the
        // application has a blue toolbar. Then, without quitting the app, try
        // changing the primarySwatch below to Colors.green and then invoke
        // "hot reload" (press "r" in the console where you ran "flutter run",
        // or simply save your changes to "hot reload" in a Flutter IDE).
        // Notice that the counter didn't reset back to zero; the application
        // is not restarted.
        primarySwatch: Colors.blue,
        // This makes the kit.visual density adapt to the platform that you run
        // the app on. For desktop platforms, the controls will be smaller and
        // closer together (more dense) than on mobile platforms.
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      home: DoKitTestPage(),
    );
  }
}

class DoKitTestPage extends StatefulWidget {
  // This widget is the home page of your application. It is stateful, meaning
  // that it has a State object (defined below) that contains fields that affect
  // how it looks.

  // This class is the configuration for the state. It holds the values (in this
  // case the title) provided by the parent (in this case the App widget) and
  // used by the build method of the State. Fields in a Widget subclass are
  // always marked "final".

  @override
  _DoKitTestPageState createState() => _DoKitTestPageState();
}

class _DoKitTestPageState extends State<DoKitTestPage> {
  @override
  void initState() {
    super.initState();
  }

  @override
  void dispose() {
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      alignment: Alignment.center,
      color: Colors.white,
      child: SingleChildScrollView(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: <Widget>[
            Container(
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.all(Radius.circular(4)),
                  color: Color(0xffcccccc)),
              margin: EdgeInsets.only(bottom: 30),
              child: FlatButton(
                child: Text('Mock Http Post',
                    style: TextStyle(
                      color: Color(0xff000000),
                      fontSize: 18,
                    )),
                onPressed: mockHttpPost,
              ),
            ),
            Container(
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.all(Radius.circular(4)),
                  color: Color(0xffcccccc)),
              margin: EdgeInsets.only(bottom: 30),
              child: FlatButton(
                child: Text('Mock Http Get',
                    style: TextStyle(
                      color: Color(0xff000000),
                      fontSize: 18,
                    )),
                onPressed: mockHttpGet,
              ),
            ),
            Container(
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.all(Radius.circular(4)),
                  color: Color(0xffcccccc)),
              margin: EdgeInsets.only(bottom: 30),
              child: FlatButton(
                child: Text('Test Method Channel',
                    style: TextStyle(
                      color: Color(0xff000000),
                      fontSize: 18,
                    )),
                onPressed: () {
                  testMethodChannel();
                },
              ),
            ),
            Container(
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.all(Radius.circular(4)),
                  color: Color(0xffcccccc)),
              margin: EdgeInsets.only(bottom: 30),
              child: FlatButton(
                child: Text('Open Route Page',
                    style: TextStyle(
                      color: Color(0xff000000),
                      fontSize: 18,
                    )),
                onPressed: () {
                  Navigator.of(context, rootNavigator: false).push(
                      new MaterialPageRoute(
                          builder: (context) {
                            //指定跳转的页面
                            return new TestPage2();
                          },
                          settings: new RouteSettings(
                              name: 'page1', arguments: ['test', '111'])));
                },
              ),
            ),
            Container(
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.all(Radius.circular(4)),
                  color: Color(0xffcccccc)),
              margin: EdgeInsets.only(bottom: 30),
              child: FlatButton(
                child: Text('Stop Timer',
                    style: TextStyle(
                      color: Color(0xff000000),
                      fontSize: 18,
                    )),
                onPressed: stopAll,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Timer timer;

  // downloadFile(String url, {String filename}) async {
  //   var httpClient = http.Client();
  //   var request = new http.Request('GET', Uri.parse(url));
  //   var response = httpClient.send(request);
  //   String dir = '/test/';
  //
  //   List<List<int>> chunks = new List();
  //   int downloaded = 0;
  //
  //   response.asStream().listen((http.StreamedResponse r) {
  //
  //     r.stream.listen((List<int> chunk) {
  //       // Display percentage of completion
  //       debugPrint('downloadPercentage: ${downloaded / r.contentLength * 100}');
  //
  //       chunks.add(chunk);
  //       downloaded += chunk.length;
  //     }, onDone: () async {
  //       // Display percentage of completion
  //       debugPrint('downloadPercentage: ${downloaded / r.contentLength * 100}');
  //
  //       // Save the file
  //       // File file = new File('$dir/$filename');
  //       // final Uint8List bytes = Uint8List(r.contentLength);
  //       // int offset = 0;
  //       // for (List<int> chunk in chunks) {
  //       //   bytes.setRange(offset, offset + chunk.length, chunk);
  //       //   offset += chunk.length;
  //       // }
  //       // await file.writeAsBytes(bytes);
  //       return;
  //     });
  //   });
  // }

  void testMethodChannel() {
    timer?.cancel();
    timer = new Timer.periodic(new Duration(seconds: 2), (timer) async {
      const MethodChannel _kChannel =
          MethodChannel('plugins.flutter.io/package_info');
      final Map<String, dynamic> map =
          await _kChannel.invokeMapMethod<String, dynamic>('getAll');
    });
  }

  void stopAll() {
    timer?.cancel();
    timer = null;
  }

  void mockHttpPost() async {
    timer?.cancel();
    timer = new Timer.periodic(new Duration(seconds: 2), (timer) async {
      HttpClient client = new HttpClient();
      String url = 'https://pinzhi.didichuxing.com/kop_stable/gateway?api=hhh';
      HttpClientRequest request = await client.postUrl(Uri.parse(url));
      Map<String, String> map1 = new Map();
      map1["v"] = "1.0";
      map1["month"] = "7";
      map1["day"] = "25";
      map1["key"] = "bd6e35a2691ae5bb8425c8631e475c2a";
      request.add(utf8.encode(json.encode(map1)));
      request.add(utf8.encode(json.encode(map1)));
      HttpClientResponse response = await request.close();
      String responseBody = await response.transform(utf8.decoder).join();
    });
  }

  void mockHttpGet() async {
    timer?.cancel();
    timer = new Timer.periodic(new Duration(seconds: 2), (timer) async {
      HttpClient client = new HttpClient();
      String url = 'https://www.baidu.com';
      HttpClientRequest request = await client.postUrl(Uri.parse(url));
      HttpClientResponse response = await request.close();
      String responseBody = await response.transform(utf8.decoder).join();
    });
  }
}

class TestPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return TestPageState();
  }
}

class TestPageState extends State<TestPage> {
  int index = 0;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(),
      body: Center(
        // Center is a layout widget. It takes a single child and positions it
        // in the middle of the parent.
        child: Column(
          // Column is also a layout widget. It takes a list of children and
          // arranges them vertically. By default, it sizes itself to fit its
          // children horizontally, and tries to be as tall as its parent.
          //
          // Invoke "debug painting" (press "p" in the console, choose the
          // "Toggle Debug Paint" action from the Flutter Inspector in Android
          // Studio, or the "Toggle Debug Paint" command in Visual Studio Code)
          // to see the wireframe for each widget.
          //
          // Column has various properties to control how it sizes itself and
          // how it positions its children. Here we use mainAxisAlignment to
          // center the children vertically; the main axis here is the vertical
          // axis because Columns are vertical (the cross axis would be
          // horizontal).
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            GestureDetector(
              onTap: () => {
                Navigator.of(context, rootNavigator: false).push(
                    new MaterialPageRoute(
                        builder: (context) {
                          //指定跳转的页面
                          return new TestPage2();
                        },
                        settings: new RouteSettings(
                            name: 'page1', arguments: ['test', '111'])))
              },
              child: Text(
                'page1:',
              ),
            ),
            Text(
              '0',
              style: Theme.of(context).textTheme.headline4,
            ),
            Container(height: 100, width: 300)
          ],
        ),
      ),
    );
  }
}

class TestPage2 extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return TestPageState2();
  }
}

class TestPageState2 extends State<TestPage2> {
  int index = 0;

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              Text(
                'page2:',
              ),
              Text(
                '0',
                style: Theme.of(context).textTheme.headline4,
              )
            ],
          ),
        ),
      ),
    );
  }
}

class TestPage3 extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return TestPageState3();
  }
}

class TestPageState3 extends State<TestPage3> {
  int index = 0;

  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return Scaffold(
      appBar: AppBar(),
      body: Center(
        // Center is a layout widget. It takes a single child and positions it
        // in the middle of the parent.
        child: Column(
          // Column is also a layout widget. It takes a list of children and
          // arranges them vertically. By default, it sizes itself to fit its
          // children horizontally, and tries to be as tall as its parent.
          //
          // Invoke "debug painting" (press "p" in the console, choose the
          // "Toggle Debug Paint" action from the Flutter Inspector in Android
          // Studio, or the "Toggle Debug Paint" command in Visual Studio Code)
          // to see the wireframe for each widget.
          //
          // Column has various properties to control how it sizes itself and
          // how it positions its children. Here we use mainAxisAlignment to
          // center the children vertically; the main axis here is the vertical
          // axis because Columns are vertical (the cross axis would be
          // horizontal).
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            GestureDetector(
              onTap: () => {
                Navigator.of(context, rootNavigator: false)
                    .push(new MaterialPageRoute(
                        builder: (context) {
                          //指定跳转的页面
                          return new MyApp();
                        },
                        settings: new RouteSettings(name: 'page3')))
              },
              child: Text(
                'page3:',
              ),
            ),
            Text(
              '0',
              style: Theme.of(context).textTheme.headline4,
            )
          ],
        ),
      ),
    );
  }
}
