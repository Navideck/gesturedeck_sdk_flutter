import 'package:pigeon/pigeon.dart';

/// Run:  dart run pigeon --input pigeons/gesturedeck.dart

@ConfigurePigeon(
  PigeonOptions(
    dartPackageName: "gesturedeck_flutter",
    dartOut: 'lib/src/generated/gesturedeck_generated.g.dart',
    dartOptions: DartOptions(),
    kotlinOut:
        'android/src/main/kotlin/com/navideck/gesturedeck_flutter/GesturedeckGenerated.g.kt',
    swiftOut: 'ios/Classes/GesturedeckGenerated.g.swift',
    kotlinOptions: KotlinOptions(package: 'com.navideck.gesturedeck_flutter'),
    swiftOptions: SwiftOptions(),
  ),
)

/// Gesturedeck
@HostApi()
abstract class GesturedeckChannel {
  void initialize(
    String? androidActivationKey,
    String? iOSActivationKey,
    bool autoStart,
    GestureActionConfig gestureActionConfig,
  );

  void start();

  void stop();

  void updateActionConfig(GestureActionConfig gestureActionConfig);
}

/// GesturedeckMedia
@HostApi()
abstract class GesturedeckMediaChannel {
  void initialize(
    String? androidActivationKey,
    String? iOSActivationKey,
    bool autoStart,
    bool reverseHorizontalSwipes,
    int? panSensitivity,
    GestureActionConfig gestureActionConfig,
    OverlayConfig? overlayConfig,
  );

  void start();

  void stop();

  void dispose();

  void reverseHorizontalSwipes(bool value);

  void setGesturedeckMediaOverlay(OverlayConfig? overlayConfig);

  void updateActionConfig(GestureActionConfig gestureActionConfig);
}

@FlutterApi()
abstract class GesturedeckCallback {
  void onTap();
  void onSwipeLeft();
  void onSwipeRight();
  void onPan();
  void onLongPress();
}

@FlutterApi()
abstract class GesturedeckMediaCallback {
  void onTap();
  void onSwipeLeft();
  void onSwipeRight();
  void onPan();
  void onLongPress();
}

class OverlayConfig {
  String? tintColor;
  String? backgroundColor;
  Uint8List? topIcon;
  Uint8List? iconSwipeLeft;
  Uint8List? iconSwipeRight;
  Uint8List? iconTap;
  Uint8List? iconTapToggled;
}

class GestureActionConfig {
  bool? enableTapAction;
  bool? enableSwipeLeftAction;
  bool? enableSwipeRightAction;
  bool? enablePanAction;
  bool? enableLongPressAction;
}
