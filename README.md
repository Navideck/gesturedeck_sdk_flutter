<p align="center">
  <img src="https://navideck.com/sites/navideck.com/files/2023-10/Gesturedeck%20SDK%20icon.png" height=150 />
</p>

# Gesturedeck Flutter
[![gesturedeck_flutter version](https://img.shields.io/pub/v/gesturedeck_flutter?label=gesturedeck_flutter)](https://pub.dev/packages/gesturedeck_flutter)


## Overview

Revolutionize your app's user experience with the extraordinary power of Gesturedeck! Seamlessly integrated into your application, Gesturedeck empowers users to effortlessly control their devices through intuitive touch gestures, without even needing to look at the screen.

<p align="center">
  <img src="https://navideck.com/sites/navideck.com/files/2023-10/Gesturedeck%20touch%20gestures%20list.png" height=300 /> &nbsp;
  <img src="https://navideck.com/sites/navideck.com/files/2023-10/Gesturedeck%20volume%20gesture.png" height=300 /> &nbsp;
  <img src="https://navideck.com/sites/navideck.com/files/2023-10/Gesturedeck%20play%20pause%20gesture.png" height=300 />
</p>

Imagine enhancing your app with the ability to adjust volume, skip tracks, and perform various actions effortlessly, making interactions smoother and more natural than ever before. Whether users are driving, biking, or engaged in any activity that demands their full attention, Gesturedeck ensures a seamless experience that enhances productivity and safety.

The Gesturedeck Flutter plugin is compatible with both Android and iOS.

## Key Features

- Intuitive touch gestures for seamless device control.
- Customizable gesture actions for enhanced user interactions.
- Integrated GesturedeckMedia for media app controls with overlay UI support.
- Support for volume button actions with GesturedeckMedia.
- Sensitivity settings for fine-tuning gesture responsiveness.
- Does not require internet connectivity.

## Getting Started

### Setup Gesturedeck

Integrate Gesturedeck into your Flutter app with just a few steps:

1. Initialize Gesturedeck:
```dart
await Gesturedeck.initialize(
    tapAction: () {},
    swipeLeftAction: () {},
    swipeRightAction: () {},
    panAction: () {},
);

// Or set actions after initialization
Gesturedeck.tapAction = (){}
```

2. Start and stop Gesturedeck detection:
```dart
Gesturedeck.start();
Gesturedeck.stop();
```

To disable a gesture action, set its corresponding parameter to null when initializing Gesturedeck, like this:

```dart
await Gesturedeck.initialize(
    tapAction: null,
);
```

Alternatively, you can disable a gesture action by setting its corresponding property to null after Gesturedeck has been initialized, like this:

```dart
Gesturedeck.tapAction = null
```

### Setup GesturedeckMedia  

Enhance media app controls using GesturedeckMedia:

1. Initialize GesturedeckMedia with overlay UI customization:
```dart
await GesturedeckMedia.initialize(
    tapAction: () {},
    swipeLeftAction: () {},
    swipeRightAction: () {},
    panAction: () {},
    gesturedeckMediaOverlay: GesturedeckMediaOverlay(
        topIcon: icon,
        iconSwipeLeft: ...,
        iconSwipeRight: ...,
        iconTap: ...,
        iconTapToggled: ...,
    ),
);
```

2. Start and stop GesturedeckMedia detection:
```dart
GesturedeckMedia.start();
GesturedeckMedia.stop();
```

3. Customize reverse horizontal swipes:
```dart
GesturedeckMedia.reverseHorizontalSwipes = true;
```

To display GesturedeckMedia UI when pressing volume buttons in Android, replace `class MainActivity : FlutterActivity()` with `class MainActivity : GesturedeckFlutterActivity()` in native Android.

#### iOS only
When using the default gesture actions you need to add the `NSAppleMusicUsageDescription` key in your project's `Info` tab with a value explaining why you need this permission (e.g. `"Control music playback"`).

## API reference

You can find the API reference [here](https://pub.dev/documentation/gesturedeck_flutter/latest/).

## Free to Use

Gesturedeck SDK is freely available for use in both personal and commercial projects, offering full functionality without time limitations. However, when using the free version, a watermark will be displayed during runtime. It is strictly prohibited to hide, remove, or alter the watermark from the free version of Gesturedeck SDK.

### Activation Key and Watermark Removal

To remove the watermark from your app, an activation key is available for purchase. The watermark-free version of Gesturedeck SDK can be obtained through this activation key.

You need to set a different activation key for each platform.

To inquire about purchasing an activation key or for any other questions related to licensing and usage, please reach out to us at team@navideck.com. We are here to assist you with the process and provide the necessary information.

## Contact

For any inquiries, questions, or support, please don't hesitate to contact our team at team@navideck.com. Thank you for choosing Gesturedeck Flutter Plugin!