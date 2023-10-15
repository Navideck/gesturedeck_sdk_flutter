import 'dart:typed_data';
import 'dart:ui';

/// A model representing a media overlay in the Gesturedeck SDK.
class GesturedeckMediaOverlay {
  /// The color used to tint the overlay elements. Has effect only when an activation key is set.
  Color? tintColor;

  /// The background color of the overlay.
  Color? backgroundColor;

  /// The icon displayed at the top of the media overlay.
  Uint8List? topIcon;

  /// The icon displayed for the swipe left gesture.
  Uint8List? iconSwipeLeft;

  /// The icon displayed for the swipe right gesture.
  Uint8List? iconSwipeRight;

  /// The icon displayed for the tap gesture.
  Uint8List? iconTap;

  /// The icon displayed for the toggled tap gesture.
  Uint8List? iconTapToggled;

  /// Creates a new instance of [GesturedeckMediaOverlay].
  ///
  /// [tintColor] specifies the color used to tint the overlay elements.
  /// [backgroundColor] specifies the background color of the overlay.
  /// [topIcon] specifies the icon displayed at the top of the media overlay.
  /// [iconSwipeLeft] specifies the icon displayed for the swipe left gesture.
  /// [iconSwipeRight] specifies the icon displayed for the swipe right gesture.
  /// [iconTap] specifies the icon displayed for the tap gesture.
  /// [iconTapToggled] specifies the icon displayed for the toggled tap gesture.
  GesturedeckMediaOverlay({
    this.tintColor,
    this.backgroundColor,
    this.topIcon,
    this.iconSwipeLeft,
    this.iconSwipeRight,
    this.iconTap,
    this.iconTapToggled,
  });
}
