// ignore_for_file: avoid_print

import 'package:flutter/services.dart';
import 'package:gesturedeck_flutter/src/models/gesturedeck_media_overlay.dart';
import 'generated/gesturedeck_generated.g.dart';

/// A subclass of [Gesturedeck] that provides media-specific functionality such as volume control and media playback actions.
///
/// Gesturedeck also includes support for media overlays, which can be used to display additional information or controls on top of the app's content.
class GesturedeckMedia {
  static final _gesturedeckMediaChannel = GesturedeckMediaChannel();
  static bool _isInitialized = false;

  /// Initializes GesturedeckMedia with the provided parameters.
  ///
  /// [tapAction] - The action to perform when tapping with two fingers.
  ///
  /// [swipeLeftAction] - The action to perform when swiping left with two fingers.
  ///
  /// [swipeRightAction] - The action to perform when swiping right with two fingers.
  ///
  /// [panAction] - The action to perform when panning vertically with two fingers or double tapping and sliding with a single finger.
  ///
  /// [panSensitivity] - The sensitivity of Gesturedeck when panning.
  ///
  /// [longPressAction] - The action to perform when long pressing with two fingers.
  ///
  /// [reverseHorizontalSwipes] - Whether to reverse the direction of horizontal swipes.
  ///
  /// [activationKey] - An optional activation key to remove watermarks. If not provided, Gesturedeck will present a watermark.
  ///
  /// [autoStart] - Determines whether Gesturedeck should automatically start detecting gestures when it is initialized. The default value is `true`.
  ///
  /// [gesturedeckMediaOverlay] - The overlay to display on top of Gesturedeck.
  ///
  ///  Throws an exception if GesturedeckMedia is already initialized.
  static Future<void> initialize({
    VoidCallback? tapAction,
    VoidCallback? swipeLeftAction,
    VoidCallback? swipeRightAction,
    VoidCallback? panAction,
    PanSensitivity? panSensitivity,
    VoidCallback? longPressAction,
    bool reverseHorizontalSwipes = false,
    String? activationKey,
    bool autoStart = true,
    GesturedeckMediaOverlay? gesturedeckMediaOverlay,
  }) async {
    if (_isInitialized) throw Exception("Gesturedeck is already initialized");
    GesturedeckMediaCallback.setup(_GesturedeckMediaCallbackHandler(
      tapAction: tapAction,
      swipeLeftAction: swipeLeftAction,
      swipeRightAction: swipeRightAction,
      panAction: panAction,
      longPressAction: longPressAction,
    ));
    await _gesturedeckMediaChannel.initialize(
      activationKey,
      autoStart,
      reverseHorizontalSwipes,
      panSensitivity?.value,
      gesturedeckMediaOverlay?.toOverlayConfig() ?? OverlayConfig(),
    );
    _isInitialized = true;
  }

  static Future<void> start() async {
    _ensureInitialized();
    await _gesturedeckMediaChannel.start();
  }

  static Future<void> stop() async {
    _ensureInitialized();
    await _gesturedeckMediaChannel.stop();
  }

  static set gesturedeckMediaOverlay(GesturedeckMediaOverlay overlay) {
    _ensureInitialized();
    _gesturedeckMediaChannel.setGesturedeckMediaOverlay(
      overlay.toOverlayConfig(),
    );
  }

  static set reverseHorizontalSwipes(bool reverse) {
    _ensureInitialized();
    _gesturedeckMediaChannel.reverseHorizontalSwipes(reverse);
  }

  static void _ensureInitialized() {
    if (!_isInitialized) throw Exception("Gesturedeck is not initialized");
  }
}

/// An enum representing the sensitivity of Gesturedeck when performing a panning gesture.
enum PanSensitivity {
  low(0),
  medium(1),
  high(2);

  const PanSensitivity(this.value);
  final int value;
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

extension _OverlayConfigExtension on GesturedeckMediaOverlay {
  OverlayConfig toOverlayConfig() {
    return OverlayConfig(
      tintColor: tintColor?.value.toRadixString(16),
      overlayBackgroundColor: overlayBackgroundColor?.value.toRadixString(16),
      topIcon: topIcon,
      iconSwipeLeft: iconSwipeLeft,
      iconSwipeRight: iconSwipeRight,
      iconTap: iconTap,
      iconTapToggled: iconTapToggled,
    );
  }
}
