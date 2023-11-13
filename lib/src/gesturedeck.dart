import 'package:flutter/services.dart';
import 'generated/gesturedeck_generated.g.dart';

/// This is the low-level API that allows you to build custom functionalities on top of Gesturedeck, with seamless callbacks for Gesturedeck's gestures.
///
/// The library provides callbacks for each gesture, allowing developers to perform custom actions in response to user input.
///
/// It allows developers to easily add support for multi-finger and single-finger gestures such as taps, swipes, pans, and long presses to their apps, that are active on top of the entire UI and do not interfere with the apps' UI elements.
///
/// To use Gesturedeck, call `initialize()` and pass in the required parameters. When a gesture is detected, the corresponding callback will be invoked.
///
/// Gesturedeck requires an activation key to remove watermarks. The activation key can be obtained by contacting Navideck, the creators of Gesturedeck. The watermarked mode is available for free for developers who want to use it without setting an activation key.
class Gesturedeck {
  static final _gesturedeckFlutter = GesturedeckChannel();
  static bool _isInitialized = false;
  static VoidCallback? _tapAction;
  static VoidCallback? _swipeLeftAction;
  static VoidCallback? _swipeRightAction;
  static VoidCallback? _panAction;
  static VoidCallback? _longPressAction;

  /// Initializes Gesturedeck with the specified parameters.
  ///
  /// [tapAction] - The action to perform when tapping with two fingers.
  ///
  /// [swipeLeftAction] - The action to perform when swiping left with two fingers.
  ///
  /// [swipeRightAction] - The action to perform when swiping right with two fingers.
  ///
  /// [panAction] - The action to perform when panning with two fingers.
  ///
  /// [longPressAction] - The action to perform when long pressing with two fingers.
  ///
  /// [androidActivationKey] - The activation key required to remove watermarks from Gesturedeck on Android. The activation key can be obtained by contacting Navideck, the creators of Gesturedeck. The watermarked mode is available for free for developers who want to use it without setting an activation key.
  ///
  /// [iOSActivationKey] - The activation key required to remove watermarks from Gesturedeck on iOS. The activation key can be obtained by contacting Navideck, the creators of Gesturedeck. The watermarked mode is available for free for developers who want to use it without setting an activation key.
  ///
  /// [autoStart] - Determines whether Gesturedeck should automatically start detecting gestures when it is initialized. The default value is `true`.
  ///
  /// Throws an exception if Gesturedeck is already initialized.
  static Future<void> initialize({
    VoidCallback? tapAction = _defaultAction,
    VoidCallback? swipeLeftAction = _defaultAction,
    VoidCallback? swipeRightAction = _defaultAction,
    VoidCallback? panAction = _defaultAction,
    VoidCallback? longPressAction = _defaultAction,
    String? androidActivationKey,
    String? iOSActivationKey,
    bool autoStart = true,
  }) async {
    if (_isInitialized) throw Exception("Gesturedeck is already initialized");
    _tapAction = tapAction;
    _swipeLeftAction = swipeLeftAction;
    _swipeRightAction = swipeRightAction;
    _panAction = panAction;
    _longPressAction = longPressAction;
    _setupGesturedeckActionListener();
    await _gesturedeckFlutter.initialize(
      androidActivationKey,
      iOSActivationKey,
      autoStart,
      GestureActionConfig(
        enableTapAction: tapAction != null,
        enableSwipeLeftAction: swipeLeftAction != null,
        enableSwipeRightAction: swipeRightAction != null,
        enablePanAction: panAction != null,
        enableLongPressAction: longPressAction != null,
      ),
    );
    _isInitialized = true;
  }

  static set tapAction(VoidCallback? callback) {
    _ensureInitialized();
    _tapAction = callback;
    _gesturedeckFlutter.updateActionConfig(GestureActionConfig(
      enableTapAction: _tapAction != null,
    ));
  }

  static set swipeLeftAction(VoidCallback? callback) {
    _ensureInitialized();
    _swipeLeftAction = callback;
    _gesturedeckFlutter.updateActionConfig(GestureActionConfig(
      enableSwipeLeftAction: _swipeLeftAction != null,
    ));
  }

  static set swipeRightAction(VoidCallback? callback) {
    _ensureInitialized();
    _swipeRightAction = callback;
    _gesturedeckFlutter.updateActionConfig(GestureActionConfig(
      enableSwipeRightAction: _swipeRightAction != null,
    ));
  }

  static set panAction(VoidCallback? callback) {
    _ensureInitialized();
    _panAction = callback;
    _gesturedeckFlutter.updateActionConfig(GestureActionConfig(
      enablePanAction: _panAction != null,
    ));
  }

  static set longPressAction(VoidCallback? callback) {
    _ensureInitialized();
    _longPressAction = callback;
    _gesturedeckFlutter.updateActionConfig(GestureActionConfig(
      enableLongPressAction: _longPressAction != null,
    ));
  }

  static Future<void> start() async {
    _ensureInitialized();
    await _gesturedeckFlutter.start();
  }

  static Future<void> stop() async {
    _ensureInitialized();
    await _gesturedeckFlutter.stop();
  }

  static void _setupGesturedeckActionListener() async {
    GesturedeckCallback.setup(_GesturedeckCallbackHandler(
      tapAction: () => _tapAction?.call(),
      swipeLeftAction: () => _swipeLeftAction?.call(),
      swipeRightAction: () => _swipeRightAction?.call(),
      panAction: () => _panAction?.call(),
      longPressAction: () => _longPressAction?.call(),
    ));
  }

  static void _ensureInitialized() {
    if (!_isInitialized) throw Exception("Gesturedeck is not initialized");
  }

  static void _defaultAction() {}
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

  @override
  void onLongPress() {
    longPressAction?.call();
  }

  @override
  void onPan() {
    panAction?.call();
  }
}
