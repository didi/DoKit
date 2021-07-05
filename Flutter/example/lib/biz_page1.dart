// Copyright© Dokit for Flutter. All rights reserved.
//
// biz_page1.dart
// Flutter
//
// Created by linusflow on 2021/7/5
// Modified by linusflow on 2021/7/5 下午4:45
//
import 'package:dokit/kit/biz/biz.dart';
import 'package:flutter/material.dart';

import 'biz_page2.dart';

class TestBizPage1 extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return TestBizPageState1();
  }
}

class TestBizPageState1 extends State<TestBizPage1> {
  int index = 0;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('BizPage1'),
        leading: GestureDetector(
          onTap: () => Navigator.of(context).pop(),
//          onTap: () => BizKitManager.instance.hide(),
          child: Container(
            padding: EdgeInsets.only(right: 16),
            child: Column(
              children: [
                Image.asset('images/dk_nav_back.png', height: 32, width: 32),
                Text('隐藏'),
              ],
            ),
          ),
        ),
        actions: [
          GestureDetector(
            onTap: () => BizKitManager.instance.close(),
            child: Container(
              padding: EdgeInsets.only(right: 16),
              child: Column(
                children: [
                  Image.asset('images/dk_nav_close.png', height: 32, width: 32),
                  Text('关闭'),
                ],
              ),
            ),
          ),
        ],
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(
              'BizPage1:',
            ),
            Text(
              '0',
              style: Theme.of(context).textTheme.headline4,
            ),
            Container(
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.all(Radius.circular(4)),
                  color: Color(0xffcccccc)),
              margin: EdgeInsets.only(bottom: 30),
              child: FlatButton(
                child: Text('Go to bizPage2',
                    style: TextStyle(
                      color: Color(0xff000000),
                      fontSize: 18,
                    )),
                onPressed: () {
                  Navigator.of(context).push(MaterialPageRoute(
                    builder: (context) => TestBizPage2(),
                  ));
                },
              ),
            )
          ],
        ),
      ),
    );
  }
}
