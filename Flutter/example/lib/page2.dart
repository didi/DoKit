import 'package:flutter/material.dart';

class TestPage2 extends StatefulWidget {
  @override
  StatefulElement createElement() {
    StatefulElement element = super.createElement();

    // Future.delayed(Duration(hours: 1), () {
    //   print(element);
    // });
    return element;
    // return super.createElement();
  }

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
