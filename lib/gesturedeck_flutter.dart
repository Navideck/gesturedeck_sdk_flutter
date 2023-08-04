import 'dart:async';
import 'package:flutter/services.dart';

import 'overlay_config.dart';

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
    bool autoStart = true,
    bool reverseHorizontalSwipes = false,
    bool enableGesturedeckMedia = false,
    OverlayConfig? overlayConfig,
  }) async {
    if (_isInitialized) throw "Gesturedeck already initialized";
    await _methodChannel.invokeMethod("initialize", {
      "activationKey": activationKey,
      "autoStart": autoStart,
      "reverseHorizontalSwipes": reverseHorizontalSwipes,
      "enableGesturedeckMedia": enableGesturedeckMedia,
      "overlayConfig": overlayConfig?.toJson() ?? {},
    });
    _isInitialized = true;
  }

  static Future<void> dispose() => _methodChannel.invokeMethod('dispose');

  /// [reverseHorizontalSwipes] will reverse the icons on overlay, this will work only if [enableGesturedeckMedia] is true
  static Future<void> reverseHorizontalSwipes(bool value) async {
    _ensureInitialized();
    await _methodChannel
        .invokeMethod("reverseHorizontalSwipes", {"value": value});
  }

  /// call [start] to start receiving gesturedeck updates
  static Future start() async {
    _ensureInitialized();
    await _methodChannel.invokeMethod('start');
  }

  /// call [stop] to stop gesturedeck
  static Future stop() async {
    _ensureInitialized();
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

  static void _ensureInitialized() {
    if (!_isInitialized) throw "Please initialize gesturedeck";
  }
}
