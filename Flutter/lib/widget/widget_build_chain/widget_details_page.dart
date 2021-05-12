// Copyright© Dokit for Flutter.
//
// widget_details_page.dart
// Flutter
//
// Created by linusflow on 2021/4/30
// Modified by linusflow on 2021/5/12 下午3:09
//

import 'package:flutter/material.dart';

class WidgetDetailsPage extends StatelessWidget {
  final Element element;

  const WidgetDetailsPage({Key? key, required this.element}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: RichText(
          text: TextSpan(children: [
            TextSpan(
                text: 'Widget Details',
                style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
            WidgetSpan(child: SizedBox(width: 10)),
            TextSpan(
                text: 'depth:${element.depth}',
                style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
          ]),
        ),
        leading: GestureDetector(
          onTap: () => _onBack(context),
          child: Icon(
            Icons.chevron_left,
            size: 28,
          ),
        ),
      ),
      body: Scrollbar(
        child: SingleChildScrollView(
          child: SingleChildScrollView(
            scrollDirection: Axis.horizontal,
            child: Container(
              padding: EdgeInsets.all(12),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'Widget Overview',
                    style: TextStyle(
                      fontSize: 20,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  SizedBox(height: 8),
                  Divider(
                    height: 0.5,
                  ),
                  SizedBox(height: 10),
                  Text(
                    '${element.widget.toStringDeep()}',
                    style: TextStyle(
                      fontSize: 12,
                    ),
                  ),
                  SizedBox(height: 16),
                  Text(
                    'RenderObject Full Details',
                    style: TextStyle(
                      fontSize: 20,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  SizedBox(height: 8),
                  Divider(
                    height: 0.5,
                  ),
                  SizedBox(height: 16),
                  Text(
                    '${element.renderObject?.toStringDeep() ?? '-'}',
                    style: TextStyle(
                      fontSize: 12,
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }

  void _onBack(BuildContext context) {
    Navigator.of(context).pop();
  }
}
