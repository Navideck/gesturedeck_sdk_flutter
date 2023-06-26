import 'dart:typed_data';
import 'dart:ui';

class OverlayConfig {
  Color? tintColor;
  Uint8List? topIcon;
  Uint8List? iconSwipeLeft;
  Uint8List? iconSwipeRight;
  Uint8List? iconTap;
  Uint8List? iconTapToggled;

  OverlayConfig({
    this.tintColor,
    this.topIcon,
    this.iconSwipeLeft,
    this.iconSwipeRight,
    this.iconTap,
    this.iconTapToggled,
  });

  Map<String, dynamic> toJson() {
    return {
      'tintColor': tintColor?.value.toRadixString(16),
      'topIcon': topIcon,
      'iconSwipeLeft': iconSwipeLeft,
      'iconSwipeRight': iconSwipeRight,
      'iconTap': iconTap,
      'iconTapToggled': iconTapToggled,
    };
  }
}
