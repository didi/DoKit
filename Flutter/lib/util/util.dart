import 'package:date_format/date_format.dart';

class TimeUtils {
  static String toTimeString(int time) {
    return formatDate(DateTime.fromMillisecondsSinceEpoch(time),
        <String>[HH, ':', nn, ':', ss, '.', nn]);
  }
}

class ByteUtil {
  static String toByteString(int bytes) {
    if (bytes <= (1 << 10)) {
      return '${bytes}B';
    } else if (bytes <= (1 << 20)) {
      return '${(bytes >> 10).toStringAsFixed(2)}K';
    } else {
      return '${(bytes >> 20).toStringAsFixed(2)}M';
    }
  }
}
