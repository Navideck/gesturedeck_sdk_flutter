// ignore_for_file: avoid_print

import 'package:flutter/services.dart';
import 'package:gesturedeck_flutter/src/models/gesturedeck_media_overlay.dart';
import 'package:gesturedeck_flutter/src/models/pan_sensitivity.dart';
import 'generated/gesturedeck_generated.g.dart';

/// A subclass of [Gesturedeck] that provides media-specific functionality such as volume control and media playback actions.
///
/// GesturedeckMedia also includes support for media overlays, which can be used to display additional information or controls on top of the app's content.
///
/// You can set any action to null to disable it.
///
/// When using an activation key, `gesturedeckMediaOverlay` can be set to null to not appear.
class GesturedeckMedia {
  static final _gesturedeckMediaChannel = GesturedeckMediaChannel();
  static bool _isInitialized = false;
  static VoidCallback? _tapAction;
  static VoidCallback? _swipeLeftAction;
  static VoidCallback? _swipeRightAction;
  static VoidCallback? _panAction;
  static VoidCallback? _longPressAction;

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
  /// [androidActivationKey] - The activation key required to remove watermarks from Gesturedeck on Android. The activation key can be obtained by contacting Navideck, the creators of Gesturedeck. The watermarked mode is available for free for developers who want to use it without setting an activation key.
  ///
  /// [iOSActivationKey] - The activation key required to remove watermarks from Gesturedeck on iOS. The activation key can be obtained by contacting Navideck, the creators of Gesturedeck. The watermarked mode is available for free for developers who want to use it without setting an activation key.
  ///
  /// [autoStart] - Determines whether Gesturedeck should automatically start detecting gestures when it is initialized. The default value is `true`.
  ///
  /// [gesturedeckMediaOverlay] - The overlay to display on top of Gesturedeck.
  ///
  ///  Throws an exception if GesturedeckMedia is already initialized.
  static Future<void> initialize({
    VoidCallback? tapAction = _defaultAction,
    VoidCallback? swipeLeftAction = _defaultAction,
    VoidCallback? swipeRightAction = _defaultAction,
    VoidCallback? panAction = _defaultAction,
    PanSensitivity? panSensitivity,
    VoidCallback? longPressAction = _defaultAction,
    bool reverseHorizontalSwipes = false,
    String? androidActivationKey,
    String? iOSActivationKey,
    bool autoStart = true,
    GesturedeckMediaOverlay? gesturedeckMediaOverlay,
  }) async {
    if (_isInitialized) throw Exception("Gesturedeck is already initialized");
    _tapAction = tapAction;
    _swipeLeftAction = swipeLeftAction;
    _swipeRightAction = swipeRightAction;
    _panAction = panAction;
    _longPressAction = longPressAction;
    _setupGesturedeckActionListener();
    await _gesturedeckMediaChannel.initialize(
      androidActivationKey,
      iOSActivationKey,
      autoStart,
      reverseHorizontalSwipes,
      panSensitivity?.value,
      GestureActionConfig(
        enableTapAction: _tapAction != null,
        enableSwipeLeftAction: _swipeLeftAction != null,
        enableSwipeRightAction: _swipeRightAction != null,
        enablePanAction: _panAction != null,
        enableLongPressAction: _longPressAction != null,
      ),
      // TODO: add support for setting null overlayConfig
      gesturedeckMediaOverlay?.toOverlayConfig() ?? OverlayConfig(),
    );
    _isInitialized = true;
  }

  static set tapAction(VoidCallback? callback) {
    _ensureInitialized();
    _tapAction = callback;
    _gesturedeckMediaChannel.updateActionConfig(GestureActionConfig(
      enableTapAction: _tapAction != null,
    ));
  }

  static set swipeLeftAction(VoidCallback? callback) {
    _ensureInitialized();
    _swipeLeftAction = callback;
    _gesturedeckMediaChannel.updateActionConfig(GestureActionConfig(
      enableSwipeLeftAction: _swipeLeftAction != null,
    ));
  }

  static set swipeRightAction(VoidCallback? callback) {
    _ensureInitialized();
    _swipeRightAction = callback;
    _gesturedeckMediaChannel.updateActionConfig(GestureActionConfig(
      enableSwipeRightAction: _swipeRightAction != null,
    ));
  }

  static set panAction(VoidCallback? callback) {
    _ensureInitialized();
    _panAction = callback;
    _gesturedeckMediaChannel.updateActionConfig(GestureActionConfig(
      enablePanAction: _panAction != null,
    ));
  }

  static set longPressAction(VoidCallback? callback) {
    _ensureInitialized();
    _longPressAction = callback;
    _gesturedeckMediaChannel.updateActionConfig(GestureActionConfig(
      enableLongPressAction: _longPressAction != null,
    ));
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

  static void _setupGesturedeckActionListener() async {
    GesturedeckMediaCallback.setup(
      _GesturedeckMediaCallbackHandler(
        tapAction: () => _tapAction?.call(),
        swipeLeftAction: () => _swipeLeftAction?.call(),
        swipeRightAction: () => _swipeRightAction?.call(),
        panAction: () => _panAction?.call(),
        longPressAction: () => _longPressAction?.call(),
      ),
    );
  }

  /// Default action for all gestures
  static void _defaultAction() {}

  static void _ensureInitialized() {
    if (!_isInitialized) throw Exception("Gesturedeck is not initialized yet");
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

extension _OverlayConfigExtension on GesturedeckMediaOverlay {
  OverlayConfig toOverlayConfig() {
    return OverlayConfig(
      tintColor: tintColor?.value.toRadixString(16),
      backgroundColor: backgroundColor?.value.toRadixString(16),
      topIcon: topIcon,
      iconSwipeLeft: iconSwipeLeft,
      iconSwipeRight: iconSwipeRight,
      iconTap: iconTap,
      iconTapToggled: iconTapToggled,
    );
  }
}
