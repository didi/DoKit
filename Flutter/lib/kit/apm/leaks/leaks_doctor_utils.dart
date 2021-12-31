import 'dart:math';
import 'dart:ui';

class RandomColor {
  static Color randomColor([int value = 256]) {
    var r = Random().nextInt(value) % 256;
    var g = Random().nextInt(value) % 256;
    var b = Random().nextInt(value) % 256;
    return Color.fromRGBO(r, g, b, 1);
  }
}
