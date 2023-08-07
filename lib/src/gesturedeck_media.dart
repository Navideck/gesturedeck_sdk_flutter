// ignore_for_file: avoid_print

import 'package:flutter/services.dart';
import 'package:gesturedeck_flutter/src/models/gesturedeck_media_overlay.dart';
import 'generated/gesturedeck_generated.g.dart';

class GesturedeckMedia {
  static final _gesturedeckMediaFlutter = GesturedeckMediaFlutter();
  static bool _isInitialized = false;

  static Future<void> initialize({
    String? activationKey,
    bool autoStart = true,
    bool reverseHorizontalSwipes = false,
    GesturedeckMediaOverlay? gesturedeckMediaOverlay,
    VoidCallback? tapAction,
    VoidCallback? swipeLeftAction,
    VoidCallback? swipeRightAction,
    VoidCallback? panAction,
    VoidCallback? longPressAction,
  }) async {
    if (_isInitialized) throw Exception("Gesturedeck is already initialized");
    GesturedeckMediaCallback.setup(_GesturedeckMediaCallbackHandler(
      tapAction: tapAction,
      swipeLeftAction: swipeLeftAction,
      swipeRightAction: swipeRightAction,
      panAction: panAction,
      longPressAction: longPressAction,
    ));
    await _gesturedeckMediaFlutter.initialize(
      activationKey,
      autoStart,
      reverseHorizontalSwipes,
      OverlayConfig(
        tintColor: gesturedeckMediaOverlay?.tintColor?.value.toRadixString(16),
        topIcon: gesturedeckMediaOverlay?.topIcon,
        iconSwipeLeft: gesturedeckMediaOverlay?.iconSwipeLeft,
        iconSwipeRight: gesturedeckMediaOverlay?.iconSwipeRight,
        iconTap: gesturedeckMediaOverlay?.iconTap,
        iconTapToggled: gesturedeckMediaOverlay?.iconTapToggled,
      ),
    );
    _isInitialized = true;
  }

  static Future<void> start() async {
    _ensureInitialized();
    await _gesturedeckMediaFlutter.start();
  }

  static Future<void> stop() async {
    _ensureInitialized();
    await _gesturedeckMediaFlutter.stop();
  }

  static set reverseHorizontalSwipes(bool reverse) {
    _ensureInitialized();
    _gesturedeckMediaFlutter.reverseHorizontalSwipes(reverse);
  }

  static void _ensureInitialized() {
    if (!_isInitialized) throw Exception("Gesturedeck is not initialized");
  }
}

class _GesturedeckMediaCallbackHandler extends GesturedeckMediaCallback {
  VoidCallback? tapAction;
  VoidCallback? swipeLeftAction;
  VoidCallback? swipeRightAction;
  VoidCallback? panAction;
  VoidCallback? longPressAction;

  _GesturedeckMediaCallbackHandler({
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
