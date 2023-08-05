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
abstract class GesturedeckFlutter {
  void initialize(
    String? activationKey,
    bool autoStart,
  );

  void start();

  void stop();
}

/// GesturedeckMedia
@HostApi()
abstract class GesturedeckMediaFlutter {
  void initialize(
    String? activationKey,
    bool autoStart,
    bool reverseHorizontalSwipes,
    OverlayConfig? overlayConfig,
  );

  void start();

  void stop();

  void dispose();

  void reverseHorizontalSwipes(bool value);
}

@FlutterApi()
abstract class GesturedeckCallback {
  void onTap();
  void onSwipeLeft();
  void onSwipeRight();
  void onPan();
}

@FlutterApi()
abstract class GesturedeckMediaCallback {
  void onTap();
  void onSwipeLeft();
  void onSwipeRight();
  void onPan();
}

class OverlayConfig {
  String? tintColor;
  Uint8List? topIcon;
  Uint8List? iconSwipeLeft;
  Uint8List? iconSwipeRight;
  Uint8List? iconTap;
  Uint8List? iconTapToggled;
}