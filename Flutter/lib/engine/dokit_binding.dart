import 'dart:async';
import 'dart:convert';
import 'dart:ui';

import 'package:dokit/kit/apm/apm.dart';
import 'package:dokit/kit/apm/method_channel_kit.dart';
import 'package:dokit/kit/kit.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';

List<String> blackList = <String>[];

class DoKitWidgetsFlutterBinding extends WidgetsFlutterBinding
    with DoKitServicesBinding {
  static WidgetsBinding? ensureInitialized() {
    if (WidgetsBinding.instance == null) {
      DoKitWidgetsFlutterBinding();
    }
    return WidgetsBinding.instance;
  }
}

mixin DoKitServicesBinding on BindingBase, ServicesBinding {
  @override
  BinaryMessenger createBinaryMessenger() {
    return DoKitBinaryMessenger(super.createBinaryMessenger());
  }
}

// 扩展1.20版本新增的两个方法，解决低版本编译问题
// 扩展的方法直接调用this.同名方法，在高版本上会调用原方法，低版本上虽然会造成递归调用，但实际不会用到这个方法
extension _BinaryMessengerExt on BinaryMessenger {
  // ignore: unused_element
  bool checkMessageHandler(String channel, MessageHandler handler) {
    return this.checkMessageHandler(channel, handler);
  }

  // ignore: unused_element
  bool checkMockMessageHandler(String channel, MessageHandler handler) {
    return this.checkMockMessageHandler(channel, handler);
  }
}

class DoKitBinaryMessenger extends BinaryMessenger {
  DoKitBinaryMessenger(this.origin);

  final MethodCodec codec = const StandardMethodCodec();
  final BinaryMessenger origin;

  @override
  Future<void> handlePlatformMessage(String channel, ByteData? data, callback) {
    final ChannelInfo? info = saveMessage(channel, data, false);
    if (info == null) {
      return origin.handlePlatformMessage(channel, data, callback);
    }
    final PlatformMessageResponseCallback wrapper = (ByteData? data) {
      resolveResult(info, data);
      callback?.call(data);
    };
    return origin.handlePlatformMessage(channel, data, wrapper);
  }

  @override
  Future<ByteData?>? send(String channel, ByteData? message) async {
    final ChannelInfo? info = saveMessage(channel, message, true);
    if (info == null) {
      return origin.send(channel, message);
    }
    final ByteData? result = await origin.send(channel, message);
    resolveResult(info, result);
    return result;
  }

  void resolveResult(ChannelInfo? info, ByteData? result) {
    try {
      if (info != null && result != null) {
        if (info.methodCodec != null) {
          info.results = info.methodCodec?.decodeEnvelope(result);
          info.endTimestamp = DateTime.now().millisecondsSinceEpoch;
        } else if (info.messageCodec != null) {
          info.results = info.messageCodec?.decodeMessage(result);
          info.endTimestamp = DateTime.now().millisecondsSinceEpoch;
        } else {
          info.endTimestamp = DateTime.now().millisecondsSinceEpoch;
        }
      }
    } catch (e) {
      print(e);
    }
    info?.methodCodec = null;
    info?.messageCodec = null;
  }

  ChannelInfo? saveMessage(String name, ByteData? data, bool send) {
    final MethodChannelKit? kit =
        ApmKitManager.instance.getKit<MethodChannelKit>(ApmKitName.KIT_CHANNEL);
    if (kit == null) {
      return null;
    }
    if (blackList.contains(name)) {
      return null;
    }
    ChannelInfo? info;
    try {
      info = filterSystemChannel(name, data, send) as ChannelInfo?;
      if (info == null) {
        final MethodCall call = codec.decodeMethodCall(data);
        info = ChannelInfo(name, call.method, call.arguments,
            send ? ChannelInfo.TYPE_USER_SEND : ChannelInfo.TYPE_USER_RECEIVE);
        info.methodCodec = codec;
      }
    } catch (e) {
      info = ChannelInfo.error(name,
          send ? ChannelInfo.TYPE_USER_SEND : ChannelInfo.TYPE_USER_RECEIVE);
    }
    kit.save(info);
    return info;
  }

  @override
  void setMessageHandler(String channel, handler) {
    origin.setMessageHandler(channel, handler);
  }

  @override
  void setMockMessageHandler(String channel, handler) {
    // origin.setMockMessageHandler(channel, handler);
  }

  @override
  bool checkMessageHandler(String channel, handler) {
    return origin.checkMessageHandler(channel, handler);
  }

  @override
  bool checkMockMessageHandler(String channel, MessageHandler? handler) {
    return origin.checkMockMessageHandler(channel, handler!);
  }

  IInfo? filterSystemChannel(String name, ByteData? data, bool send) {
    if (name == SystemChannels.lifecycle.name) {
      return decodeMessage(name, data, SystemChannels.lifecycle.codec, send);
    }
    if (name == SystemChannels.accessibility.name) {
      return decodeMessage(
          name, data, SystemChannels.accessibility.codec, send);
    }
    if (name == SystemChannels.keyEvent.name) {
      return decodeMessage(name, data, SystemChannels.keyEvent.codec, send);
    }
    if (name == SystemChannels.navigation.name) {
      return decodeMethod(name, data, SystemChannels.navigation.codec, send);
    }
    if (name == SystemChannels.platform.name) {
      return decodeMethod(name, data, SystemChannels.platform.codec, send);
    }
    if (name == SystemChannels.platform_views.name) {
      return decodeMethod(
          name, data, SystemChannels.platform_views.codec, send);
    }
    if (name == SystemChannels.skia.name) {
      return decodeMethod(name, data, SystemChannels.skia.codec, send);
    }
    if (name == SystemChannels.system.name) {
      return decodeMessage(name, data, SystemChannels.system.codec, send);
    }
    if (name == SystemChannels.textInput.name) {
      return decodeMethod(name, data, SystemChannels.textInput.codec, send);
    }
    if (name == 'flutter/assets') {
      return decodeByteMessage(name, data, send);
    }
    return null;
  }

  IInfo? decodeByteMessage(String name, ByteData? data, bool send) {
    var arguments =
        data == null ? null : utf8.decode(data.buffer.asUint8List());
    return ChannelInfo(name, null, arguments,
        send ? ChannelInfo.TYPE_SYSTEM_SEND : ChannelInfo.TYPE_SYSTEM_RECEIVE);
  }

  IInfo decodeMessage(
      String name, ByteData? data, MessageCodec<dynamic> codec, bool send) {
    final dynamic call = codec.decodeMessage(data);
    return ChannelInfo(name, call.toString(), null,
        send ? ChannelInfo.TYPE_SYSTEM_SEND : ChannelInfo.TYPE_SYSTEM_RECEIVE)
      ..messageCodec = codec;
  }

  IInfo decodeMethod(
      String name, ByteData? data, MethodCodec codec, bool send) {
    final MethodCall call = codec.decodeMethodCall(data);
    return ChannelInfo(name, call.method, call.arguments,
        send ? ChannelInfo.TYPE_SYSTEM_SEND : ChannelInfo.TYPE_SYSTEM_RECEIVE)
      ..methodCodec = codec;
  }
}
