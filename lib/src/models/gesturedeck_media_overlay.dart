import 'dart:typed_data';
import 'dart:ui';

class GesturedeckMediaOverlay {
  Color? tintColor;
  Uint8List? topIcon;
  Uint8List? iconSwipeLeft;
  Uint8List? iconSwipeRight;
  Uint8List? iconTap;
  Uint8List? iconTapToggled;

  GesturedeckMediaOverlay({
    this.tintColor,
    this.topIcon,
    this.iconSwipeLeft,
    this.iconSwipeRight,
    this.iconTap,
    this.iconTapToggled,
  });
}
