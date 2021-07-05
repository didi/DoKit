// Copyright© Dokit for Flutter. All rights reserved.
//
// biz_page2.dart
// Flutter
//
// Created by linusflow on 2021/7/1
// Modified by linusflow on 2021/7/1 下午9:30
//

import 'package:flutter/material.dart';

class TestBizPage2 extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return TestBizPageState2();
  }
}

class TestBizPageState2 extends State<TestBizPage2> {
  int index = 0;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("BizPage2"),
        leading: GestureDetector(
          onTap: () => Navigator.of(context).pop(),
          child: Container(
            padding: EdgeInsets.only(right: 16, top: 12),
            child: Column(
              children: [
                Image.asset('images/dk_nav_back.png', height: 32, width: 32),
              ],
            ),
          ),
        ),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(
              'BizPage2:',
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
