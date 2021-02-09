import 'package:date_format/date_format.dart';

class TimeUtils {
  static String toTimeString(int time) {
    return formatDate(DateTime.fromMillisecondsSinceEpoch(time),
        [HH, ":", nn, ":", ss, ".", S]);
  }
}

class ByteUtil {
  static String toByteString(int bytes) {
    if (bytes <= 1024) {
      return '${bytes}B';
    } else if (bytes <= 1024 * 1024) {
      return '${(bytes / (1028)).toStringAsFixed(2)}K';
    } else {
      return '${(bytes / (1028 * 1024)).toStringAsFixed(2)}M';
    }
  }
}
