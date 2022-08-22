import 'dart:async';

import 'package:flutter/services.dart';

class Gesturedeck {
  static const MethodChannel _channel =
      MethodChannel('com.navideck.gesturedeck');

  static const EventChannel _gestureEventChannel =
      EventChannel('com.navideck.gesturedeck');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  /// Subscribes to [SwipeGestures] events.
  ///
  /// Throws a [PlatformException] if this fails
  /// Throws a [MissingPluginException] if the method is not implemented on
  /// the native platforms.
  static Stream<GestureType> subscribeGestures() {
    return _gestureEventChannel.receiveBroadcastStream().map((event) =>
        GestureType.values.firstWhere((element) =>
            element.toString().split('.').last == event.toString()));
  }
}

enum GestureType { swipe, tap }
