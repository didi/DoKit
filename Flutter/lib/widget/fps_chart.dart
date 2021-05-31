import 'dart:math';

import 'package:dokit/kit/kit.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class BarChartPainter extends CustomPainter {
  BarChartPainter({required this.datas});

  List<IInfo> datas;

  @override
  bool shouldRepaint(BarChartPainter oldDelegate) => true;

  @override
  bool shouldRebuildSemantics(BarChartPainter oldDelegate) => false;

  void _drawAxis(Canvas canvas, Size size) {
    final double sw = size.width;
    final double sh = size.height;

    // 使用 Paint 定义路径的样式
    final Paint paint = Paint()
      ..color = const Color(0xffdddddd)
      ..style = PaintingStyle.stroke
      ..strokeWidth = 0.5;

    // 使用 Path 定义绘制的路径，从画布的左上角到左下角在到右下角
    final Path path = Path()
      ..moveTo(0, 0)
      ..lineTo(0, sh)
      ..lineTo(sw, sh);

    // 使用 drawPath 方法绘制路径
    canvas.drawPath(path, paint);
  }

  void _drawLabels(Canvas canvas, Size size) {
    const double labelFontSize = 10;
    final double sh = size.height;
    final List<double> yAxisLabels = <double>[];

    yAxisLabels.add(16);
    yAxisLabels.add(32);
    yAxisLabels.add(50);
    yAxisLabels.add(100);

    yAxisLabels.asMap().forEach(
      (int index, double label) {
        // 标识的高度为画布高度减去标识的值
        final double top = sh - label * 2.5;
        // ignore: unused_local_variable
        final Rect rect = Rect.fromLTWH(0, top, 4, 1);
        final Offset textOffset = Offset(
          0 - (label.toInt().toString().length == 3 ? 24 : 20).toDouble(),
          top - labelFontSize / 2,
        );

        // 绘制文字需要用 `TextPainter`，最后调用 paint 方法绘制文字
        TextPainter(
          text: TextSpan(
            text: label.toStringAsFixed(0),
            style: const TextStyle(
                fontSize: labelFontSize, color: Color(0xff4a4b5b)),
          ),
          textAlign: TextAlign.right,
          textDirection: TextDirection.ltr,
          textWidthBasis: TextWidthBasis.longestLine,
        )
          ..layout(minWidth: 0, maxWidth: 24)
          ..paint(canvas, textOffset);
      },
    );
  }

  void _drawBars(Canvas canvas, Size size) {
    final double sh = size.height;
    final Paint paint = Paint()..style = PaintingStyle.fill;
    const double marginLeft = 7.5;
    const double _barWidth = 2.5;
    final double maxVisibleSize = (size.width - marginLeft) / 2.5;
    if (datas.length > maxVisibleSize.toInt()) {
      datas = datas.sublist(datas.length - maxVisibleSize.toInt());
    }
    const double _barGap = 0;
    for (int i = 0; i < datas.length; i++) {
      int value = datas[i].getValue() as int;
      value = min(value, 110);
      paint.color = value <= 16
          ? const Color(0xff55a8fd)
          : value < 50
              ? const Color(0xfffad337)
              : const Color(0xffd0607e);
      // 矩形的上边缘为画布高度减去数据值
      final double top = sh - value * 2.5;
      // 矩形的左边缘为当前索引值乘以矩形宽度加上矩形之间的间距
      final double left = marginLeft + i * _barWidth + (i * _barGap) + _barGap;

      // 使用 Rect.fromLTWH 方法创建要绘制的矩形
      final Rect rect =
          Rect.fromLTWH(left, top, _barWidth, value * 2.5.toDouble());
      // 使用 drawRect 方法绘制矩形
      canvas.drawRect(rect, paint);
    }
  }

  @override
  void paint(Canvas canvas, Size size) {
    _drawAxis(canvas, size);
    _drawLabels(canvas, size);
    _drawBars(canvas, size);
  }
}

class FpsBarChart extends StatefulWidget {
  const FpsBarChart({
    required this.data,
  });

  final List<IInfo> data;

  @override
  _FpsBarChartState createState() => _FpsBarChartState();
}

class _FpsBarChartState extends State<FpsBarChart>
    with TickerProviderStateMixin {
  @override
  Widget build(BuildContext context) {
    final double width =
        MediaQuery.of(context).orientation == Orientation.portrait
            ? MediaQuery.of(context).size.width - 56
            : MediaQuery.of(context).size.width - 30;
    final double height =
        MediaQuery.of(context).orientation == Orientation.portrait
            ? MediaQuery.of(context).size.height - 200 - 140
            : MediaQuery.of(context).size.height - 200;
    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: <Widget>[
        Container(
          margin: const EdgeInsets.only(top: 40, left: 24),
          child: CustomPaint(
            painter: BarChartPainter(datas: widget.data),
            child: Container(
              width: width,
              height: height,
            ),
          ),
        )
      ],
    );
  }
}
