import 'dart:async';
import 'package:flutter/services.dart';

enum GestureType { tap, swipedLeft, swipedRight }

class Gesturedeck {
  static bool _isInitialized = false;
  static const MethodChannel _methodChannel =
      MethodChannel("com.navideck.gesturedeck.method");
  static const EventChannel _gestureEventChannel =
      EventChannel('com.navideck.gesturedeck');

  /// call [initialize] once with activation key
  static Future<void> initialize({
    String? activationKey,
  }) async {
    if (_isInitialized) throw "Gesturedeck already initialized";
    await _methodChannel.invokeMethod("initialize", {
      "activationKey": activationKey,
    });
    _isInitialized = true;
  }

  /// call [start] to start receiving gesturedeck updates
  static Future start() async {
    if (!_isInitialized) throw "Please initialize gesturedeck";
    await _methodChannel.invokeMethod('start');
  }

  /// call [stop] to stop gesturedeck
  static Future stop() async {
    if (!_isInitialized) throw "Please initialize gesturedeck";
    await _methodChannel.invokeMethod('stop');
  }

  /// Subscribe to [gesturesStream] for Gesturedeck events after calling [start].
  static Stream<GestureType> get gesturesStream {
    return _gestureEventChannel
        .receiveBroadcastStream({'name': 'touchEvent'})
        .where((element) => _getGestureType(element) != null)
        .map((event) => _getGestureType(event)!);
  }

  static GestureType? _getGestureType(String data) {
    try {
      return GestureType.values.firstWhere((values) => values.name == data);
    } catch (_) {
      return null;
    }
  }
}
