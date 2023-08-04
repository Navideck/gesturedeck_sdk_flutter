import 'package:flutter/services.dart';
import 'generated/gesturedeck_generated.g.dart';

class Gesturedeck extends GesturedeckCallback {
  final _gesturedeckFlutter = GesturedeckFlutter();
  VoidCallback? tapAction;
  VoidCallback? swipeLeftAction;
  VoidCallback? swipeRightAction;
  VoidCallback? panAction;

  Gesturedeck({
    String? activationKey,
    bool autoStart = true,
    this.tapAction,
    this.swipeLeftAction,
    this.swipeRightAction,
    this.panAction,
  }) {
    GesturedeckCallback.setup(this);
    _gesturedeckFlutter.initialize(activationKey, autoStart);
  }

  Future<void> start() => _gesturedeckFlutter.start();

  Future<void> stop() => _gesturedeckFlutter.stop();

  @override
  void onPan() => panAction?.call();

  @override
  void onSwipeLeft() => swipeLeftAction?.call();

  @override
  void onSwipeRight() => swipeRightAction?.call();

  @override
  void onTap() => tapAction?.call();
}
