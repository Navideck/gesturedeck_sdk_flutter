// Autogenerated from Pigeon (v10.1.4), do not edit directly.
// See also: https://pub.dev/packages/pigeon
// ignore_for_file: public_member_api_docs, non_constant_identifier_names, avoid_as, unused_import, unnecessary_parenthesis, prefer_null_aware_operators, omit_local_variable_types, unused_shown_name, unnecessary_import

import 'dart:async';
import 'dart:typed_data' show Float64List, Int32List, Int64List, Uint8List;

import 'package:flutter/foundation.dart' show ReadBuffer, WriteBuffer;
import 'package:flutter/services.dart';

class OverlayConfig {
  OverlayConfig({
    this.tintColor,
    this.overlayBackgroundColor,
    this.topIcon,
    this.iconSwipeLeft,
    this.iconSwipeRight,
    this.iconTap,
    this.iconTapToggled,
  });

  String? tintColor;

  String? overlayBackgroundColor;

  Uint8List? topIcon;

  Uint8List? iconSwipeLeft;

  Uint8List? iconSwipeRight;

  Uint8List? iconTap;

  Uint8List? iconTapToggled;

  Object encode() {
    return <Object?>[
      tintColor,
      overlayBackgroundColor,
      topIcon,
      iconSwipeLeft,
      iconSwipeRight,
      iconTap,
      iconTapToggled,
    ];
  }

  static OverlayConfig decode(Object result) {
    result as List<Object?>;
    return OverlayConfig(
      tintColor: result[0] as String?,
      overlayBackgroundColor: result[1] as String?,
      topIcon: result[2] as Uint8List?,
      iconSwipeLeft: result[3] as Uint8List?,
      iconSwipeRight: result[4] as Uint8List?,
      iconTap: result[5] as Uint8List?,
      iconTapToggled: result[6] as Uint8List?,
    );
  }
}

/// Gesturedeck
class GesturedeckChannel {
  /// Constructor for [GesturedeckChannel].  The [binaryMessenger] named argument is
  /// available for dependency injection.  If it is left null, the default
  /// BinaryMessenger will be used which routes to the host platform.
  GesturedeckChannel({BinaryMessenger? binaryMessenger})
      : _binaryMessenger = binaryMessenger;
  final BinaryMessenger? _binaryMessenger;

  static const MessageCodec<Object?> codec = StandardMessageCodec();

  Future<void> initialize(String? arg_androidActivationKey, String? arg_iOSActivationKey, bool arg_autoStart) async {
    final BasicMessageChannel<Object?> channel = BasicMessageChannel<Object?>(
        'dev.flutter.pigeon.gesturedeck_flutter.GesturedeckChannel.initialize', codec,
        binaryMessenger: _binaryMessenger);
    final List<Object?>? replyList =
        await channel.send(<Object?>[arg_androidActivationKey, arg_iOSActivationKey, arg_autoStart]) as List<Object?>?;
    if (replyList == null) {
      throw PlatformException(
        code: 'channel-error',
        message: 'Unable to establish connection on channel.',
      );
    } else if (replyList.length > 1) {
      throw PlatformException(
        code: replyList[0]! as String,
        message: replyList[1] as String?,
        details: replyList[2],
      );
    } else {
      return;
    }
  }

  Future<void> start() async {
    final BasicMessageChannel<Object?> channel = BasicMessageChannel<Object?>(
        'dev.flutter.pigeon.gesturedeck_flutter.GesturedeckChannel.start', codec,
        binaryMessenger: _binaryMessenger);
    final List<Object?>? replyList =
        await channel.send(null) as List<Object?>?;
    if (replyList == null) {
      throw PlatformException(
        code: 'channel-error',
        message: 'Unable to establish connection on channel.',
      );
    } else if (replyList.length > 1) {
      throw PlatformException(
        code: replyList[0]! as String,
        message: replyList[1] as String?,
        details: replyList[2],
      );
    } else {
      return;
    }
  }

  Future<void> stop() async {
    final BasicMessageChannel<Object?> channel = BasicMessageChannel<Object?>(
        'dev.flutter.pigeon.gesturedeck_flutter.GesturedeckChannel.stop', codec,
        binaryMessenger: _binaryMessenger);
    final List<Object?>? replyList =
        await channel.send(null) as List<Object?>?;
    if (replyList == null) {
      throw PlatformException(
        code: 'channel-error',
        message: 'Unable to establish connection on channel.',
      );
    } else if (replyList.length > 1) {
      throw PlatformException(
        code: replyList[0]! as String,
        message: replyList[1] as String?,
        details: replyList[2],
      );
    } else {
      return;
    }
  }
}

class _GesturedeckMediaChannelCodec extends StandardMessageCodec {
  const _GesturedeckMediaChannelCodec();
  @override
  void writeValue(WriteBuffer buffer, Object? value) {
    if (value is OverlayConfig) {
      buffer.putUint8(128);
      writeValue(buffer, value.encode());
    } else {
      super.writeValue(buffer, value);
    }
  }

  @override
  Object? readValueOfType(int type, ReadBuffer buffer) {
    switch (type) {
      case 128: 
        return OverlayConfig.decode(readValue(buffer)!);
      default:
        return super.readValueOfType(type, buffer);
    }
  }
}

/// GesturedeckMedia
class GesturedeckMediaChannel {
  /// Constructor for [GesturedeckMediaChannel].  The [binaryMessenger] named argument is
  /// available for dependency injection.  If it is left null, the default
  /// BinaryMessenger will be used which routes to the host platform.
  GesturedeckMediaChannel({BinaryMessenger? binaryMessenger})
      : _binaryMessenger = binaryMessenger;
  final BinaryMessenger? _binaryMessenger;

  static const MessageCodec<Object?> codec = _GesturedeckMediaChannelCodec();

  Future<void> initialize(String? arg_androidActivationKey, String? arg_iOSActivationKey, bool arg_autoStart, bool arg_reverseHorizontalSwipes, int? arg_panSensitivity, OverlayConfig? arg_overlayConfig) async {
    final BasicMessageChannel<Object?> channel = BasicMessageChannel<Object?>(
        'dev.flutter.pigeon.gesturedeck_flutter.GesturedeckMediaChannel.initialize', codec,
        binaryMessenger: _binaryMessenger);
    final List<Object?>? replyList =
        await channel.send(<Object?>[arg_androidActivationKey, arg_iOSActivationKey, arg_autoStart, arg_reverseHorizontalSwipes, arg_panSensitivity, arg_overlayConfig]) as List<Object?>?;
    if (replyList == null) {
      throw PlatformException(
        code: 'channel-error',
        message: 'Unable to establish connection on channel.',
      );
    } else if (replyList.length > 1) {
      throw PlatformException(
        code: replyList[0]! as String,
        message: replyList[1] as String?,
        details: replyList[2],
      );
    } else {
      return;
    }
  }

  Future<void> start() async {
    final BasicMessageChannel<Object?> channel = BasicMessageChannel<Object?>(
        'dev.flutter.pigeon.gesturedeck_flutter.GesturedeckMediaChannel.start', codec,
        binaryMessenger: _binaryMessenger);
    final List<Object?>? replyList =
        await channel.send(null) as List<Object?>?;
    if (replyList == null) {
      throw PlatformException(
        code: 'channel-error',
        message: 'Unable to establish connection on channel.',
      );
    } else if (replyList.length > 1) {
      throw PlatformException(
        code: replyList[0]! as String,
        message: replyList[1] as String?,
        details: replyList[2],
      );
    } else {
      return;
    }
  }

  Future<void> stop() async {
    final BasicMessageChannel<Object?> channel = BasicMessageChannel<Object?>(
        'dev.flutter.pigeon.gesturedeck_flutter.GesturedeckMediaChannel.stop', codec,
        binaryMessenger: _binaryMessenger);
    final List<Object?>? replyList =
        await channel.send(null) as List<Object?>?;
    if (replyList == null) {
      throw PlatformException(
        code: 'channel-error',
        message: 'Unable to establish connection on channel.',
      );
    } else if (replyList.length > 1) {
      throw PlatformException(
        code: replyList[0]! as String,
        message: replyList[1] as String?,
        details: replyList[2],
      );
    } else {
      return;
    }
  }

  Future<void> dispose() async {
    final BasicMessageChannel<Object?> channel = BasicMessageChannel<Object?>(
        'dev.flutter.pigeon.gesturedeck_flutter.GesturedeckMediaChannel.dispose', codec,
        binaryMessenger: _binaryMessenger);
    final List<Object?>? replyList =
        await channel.send(null) as List<Object?>?;
    if (replyList == null) {
      throw PlatformException(
        code: 'channel-error',
        message: 'Unable to establish connection on channel.',
      );
    } else if (replyList.length > 1) {
      throw PlatformException(
        code: replyList[0]! as String,
        message: replyList[1] as String?,
        details: replyList[2],
      );
    } else {
      return;
    }
  }

  Future<void> reverseHorizontalSwipes(bool arg_value) async {
    final BasicMessageChannel<Object?> channel = BasicMessageChannel<Object?>(
        'dev.flutter.pigeon.gesturedeck_flutter.GesturedeckMediaChannel.reverseHorizontalSwipes', codec,
        binaryMessenger: _binaryMessenger);
    final List<Object?>? replyList =
        await channel.send(<Object?>[arg_value]) as List<Object?>?;
    if (replyList == null) {
      throw PlatformException(
        code: 'channel-error',
        message: 'Unable to establish connection on channel.',
      );
    } else if (replyList.length > 1) {
      throw PlatformException(
        code: replyList[0]! as String,
        message: replyList[1] as String?,
        details: replyList[2],
      );
    } else {
      return;
    }
  }

  Future<void> setGesturedeckMediaOverlay(OverlayConfig? arg_overlayConfig) async {
    final BasicMessageChannel<Object?> channel = BasicMessageChannel<Object?>(
        'dev.flutter.pigeon.gesturedeck_flutter.GesturedeckMediaChannel.setGesturedeckMediaOverlay', codec,
        binaryMessenger: _binaryMessenger);
    final List<Object?>? replyList =
        await channel.send(<Object?>[arg_overlayConfig]) as List<Object?>?;
    if (replyList == null) {
      throw PlatformException(
        code: 'channel-error',
        message: 'Unable to establish connection on channel.',
      );
    } else if (replyList.length > 1) {
      throw PlatformException(
        code: replyList[0]! as String,
        message: replyList[1] as String?,
        details: replyList[2],
      );
    } else {
      return;
    }
  }
}

abstract class GesturedeckCallback {
  static const MessageCodec<Object?> codec = StandardMessageCodec();

  void onTap();

  void onSwipeLeft();

  void onSwipeRight();

  void onPan();

  void onLongPress();

  static void setup(GesturedeckCallback? api, {BinaryMessenger? binaryMessenger}) {
    {
      final BasicMessageChannel<Object?> channel = BasicMessageChannel<Object?>(
          'dev.flutter.pigeon.gesturedeck_flutter.GesturedeckCallback.onTap', codec,
          binaryMessenger: binaryMessenger);
      if (api == null) {
        channel.setMessageHandler(null);
      } else {
        channel.setMessageHandler((Object? message) async {
          // ignore message
          api.onTap();
          return;
        });
      }
    }
    {
      final BasicMessageChannel<Object?> channel = BasicMessageChannel<Object?>(
          'dev.flutter.pigeon.gesturedeck_flutter.GesturedeckCallback.onSwipeLeft', codec,
          binaryMessenger: binaryMessenger);
      if (api == null) {
        channel.setMessageHandler(null);
      } else {
        channel.setMessageHandler((Object? message) async {
          // ignore message
          api.onSwipeLeft();
          return;
        });
      }
    }
    {
      final BasicMessageChannel<Object?> channel = BasicMessageChannel<Object?>(
          'dev.flutter.pigeon.gesturedeck_flutter.GesturedeckCallback.onSwipeRight', codec,
          binaryMessenger: binaryMessenger);
      if (api == null) {
        channel.setMessageHandler(null);
      } else {
        channel.setMessageHandler((Object? message) async {
          // ignore message
          api.onSwipeRight();
          return;
        });
      }
    }
    {
      final BasicMessageChannel<Object?> channel = BasicMessageChannel<Object?>(
          'dev.flutter.pigeon.gesturedeck_flutter.GesturedeckCallback.onPan', codec,
          binaryMessenger: binaryMessenger);
      if (api == null) {
        channel.setMessageHandler(null);
      } else {
        channel.setMessageHandler((Object? message) async {
          // ignore message
          api.onPan();
          return;
        });
      }
    }
    {
      final BasicMessageChannel<Object?> channel = BasicMessageChannel<Object?>(
          'dev.flutter.pigeon.gesturedeck_flutter.GesturedeckCallback.onLongPress', codec,
          binaryMessenger: binaryMessenger);
      if (api == null) {
        channel.setMessageHandler(null);
      } else {
        channel.setMessageHandler((Object? message) async {
          // ignore message
          api.onLongPress();
          return;
        });
      }
    }
  }
}

abstract class GesturedeckMediaCallback {
  static const MessageCodec<Object?> codec = StandardMessageCodec();

  void onTap();

  void onSwipeLeft();

  void onSwipeRight();

  void onPan();

  void onLongPress();

  static void setup(GesturedeckMediaCallback? api, {BinaryMessenger? binaryMessenger}) {
    {
      final BasicMessageChannel<Object?> channel = BasicMessageChannel<Object?>(
          'dev.flutter.pigeon.gesturedeck_flutter.GesturedeckMediaCallback.onTap', codec,
          binaryMessenger: binaryMessenger);
      if (api == null) {
        channel.setMessageHandler(null);
      } else {
        channel.setMessageHandler((Object? message) async {
          // ignore message
          api.onTap();
          return;
        });
      }
    }
    {
      final BasicMessageChannel<Object?> channel = BasicMessageChannel<Object?>(
          'dev.flutter.pigeon.gesturedeck_flutter.GesturedeckMediaCallback.onSwipeLeft', codec,
          binaryMessenger: binaryMessenger);
      if (api == null) {
        channel.setMessageHandler(null);
      } else {
        channel.setMessageHandler((Object? message) async {
          // ignore message
          api.onSwipeLeft();
          return;
        });
      }
    }
    {
      final BasicMessageChannel<Object?> channel = BasicMessageChannel<Object?>(
          'dev.flutter.pigeon.gesturedeck_flutter.GesturedeckMediaCallback.onSwipeRight', codec,
          binaryMessenger: binaryMessenger);
      if (api == null) {
        channel.setMessageHandler(null);
      } else {
        channel.setMessageHandler((Object? message) async {
          // ignore message
          api.onSwipeRight();
          return;
        });
      }
    }
    {
      final BasicMessageChannel<Object?> channel = BasicMessageChannel<Object?>(
          'dev.flutter.pigeon.gesturedeck_flutter.GesturedeckMediaCallback.onPan', codec,
          binaryMessenger: binaryMessenger);
      if (api == null) {
        channel.setMessageHandler(null);
      } else {
        channel.setMessageHandler((Object? message) async {
          // ignore message
          api.onPan();
          return;
        });
      }
    }
    {
      final BasicMessageChannel<Object?> channel = BasicMessageChannel<Object?>(
          'dev.flutter.pigeon.gesturedeck_flutter.GesturedeckMediaCallback.onLongPress', codec,
          binaryMessenger: binaryMessenger);
      if (api == null) {
        channel.setMessageHandler(null);
      } else {
        channel.setMessageHandler((Object? message) async {
          // ignore message
          api.onLongPress();
          return;
        });
      }
    }
  }
}
