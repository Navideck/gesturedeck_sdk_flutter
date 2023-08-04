// ignore_for_file: avoid_print

import 'package:flutter/services.dart';
import 'package:gesturedeck_flutter/src/models/gesturedeck_media_overlay.dart';
import 'generated/gesturedeck_generated.g.dart';

class GesturedeckMedia extends GesturedeckMediaCallback {
  final _gesturedeckMediaFlutter = GesturedeckMediaFlutter();
  VoidCallback? tapAction;
  VoidCallback? swipeLeftAction;
  VoidCallback? swipeRightAction;
  VoidCallback? panAction;

  GesturedeckMedia({
    String? activationKey,
    bool autoStart = true,
    bool reverseHorizontalSwipes = false,
    GesturedeckMediaOverlay? gesturedeckMediaOverlay,
    this.tapAction,
    this.swipeLeftAction,
    this.swipeRightAction,
    this.panAction,
  }) {
    GesturedeckMediaCallback.setup(this);
    _gesturedeckMediaFlutter.initialize(
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
  }

  Future<void> start() => _gesturedeckMediaFlutter.start();

  Future<void> stop() => _gesturedeckMediaFlutter.stop();

  Future<void> reverseHorizontalSwipes(bool reverse) =>
      _gesturedeckMediaFlutter.reverseHorizontalSwipes(reverse);

  @override
  void onPan() => panAction?.call();

  @override
  void onSwipeLeft() => swipeLeftAction?.call();

  @override
  void onSwipeRight() => swipeRightAction?.call();

  @override
  void onTap() => tapAction?.call();
}
