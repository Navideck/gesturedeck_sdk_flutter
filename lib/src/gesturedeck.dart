import 'package:flutter/services.dart';
import 'generated/gesturedeck_generated.g.dart';

class Gesturedeck {
  static final _gesturedeckFlutter = GesturedeckFlutter();
  static bool _isInitialized = false;

  static Future<void> initialize({
    String? activationKey,
    bool autoStart = true,
    VoidCallback? tapAction,
    VoidCallback? swipeLeftAction,
    VoidCallback? swipeRightAction,
    VoidCallback? panAction,
    VoidCallback? longPressAction,
  }) async {
    if (_isInitialized) throw Exception("Gesturedeck is already initialized");
    var gestureActionHandler = _GesturedeckCallbackHandler(
      tapAction: tapAction,
      swipeLeftAction: swipeLeftAction,
      swipeRightAction: swipeRightAction,
      panAction: panAction,
      longPressAction: longPressAction,
    );
    GesturedeckCallback.setup(gestureActionHandler);
    await _gesturedeckFlutter.initialize(activationKey, autoStart);
    _isInitialized = true;
  }

  static Future<void> start() async {
    _ensureInitialized();
    await _gesturedeckFlutter.start();
  }

  static Future<void> stop() async {
    _ensureInitialized();
    await _gesturedeckFlutter.stop();
  }

  static void _ensureInitialized() {
    if (!_isInitialized) throw Exception("Gesturedeck is not initialized");
  }
}

class _GesturedeckCallbackHandler extends GesturedeckCallback {
  VoidCallback? tapAction;
  VoidCallback? swipeLeftAction;
  VoidCallback? swipeRightAction;
  VoidCallback? panAction;
  VoidCallback? longPressAction;

  _GesturedeckCallbackHandler({
    this.tapAction,
    this.swipeLeftAction,
    this.swipeRightAction,
    this.panAction,
    this.longPressAction,
  });

  @override
  void onLongPress() {
    longPressAction?.call();
  }

  @override
  void onPan() {
    panAction?.call();
  }

  @override
  void onSwipeLeft() {
    swipeLeftAction?.call();
  }

  @override
  void onSwipeRight() {
    swipeRightAction?.call();
  }

  @override
  void onTap() {
    tapAction?.call();
  }
}
