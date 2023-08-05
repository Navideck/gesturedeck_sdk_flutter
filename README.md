# Gesturedeck Flutter

This is a Flutter plugin for native `Gesturedeck Android` and `Gesturedeck IOS`. It provides a simple way to integrate Gesturedeck into your Flutter app.

## Key Features

- Intuitive touch gestures for seamless device control.
- Customizable gesture actions for enhanced user interactions.
- Integrated GesturedeckMedia for media app controls with overlay UI support.
- Support for volume button actions with GesturedeckMedia.
- Sensitivity settings for fine-tuning gesture responsiveness.
- Does not require internet connectivity

## Getting Started

### Setup Gesturedeck

To use Gesturedeck, you need to initialize it with an activation key and set up actions for different gestures. Here's an example:

```dart
// Initialize Gesturedeck
var gesturedeck =  Gesturedeck(
    activationKey: "",
    autoStart: true,
    tapAction: () {},
    swipeLeftAction:  () {},
    swipeRightAction:  () {},
    panAction: () {},
);

// To start manually, if autoStart = false
gesturedeck.start()

// To stop
gesturedeck.stop()
```

### Setup GesturedeckMedia  

```dart
// To get icon from assets
var icon = await rootBundle.load("assets/icon.png");
Uint8List iconBytes = testIcon.buffer.asUint8List();

// Initialize GesturedeckMedia
var gesturedeckMedia =  Gesturedeck(
    activationKey: "",
    autoStart: true,
    reverseHorizontalSwipes: reverseHorizontalSwipes,
    tapAction: () {},
    swipeLeftAction:  () {},
    swipeRightAction:  () {},
    panAction: () {},
    gesturedeckMediaOverlay: GesturedeckMediaOverlay(
        tintColor: Colors.green,
        topIcon: icon, 
        iconSwipeLeft: ..,
        iconSwipeRight: ..,
        iconTap: ..,
        iconTapToggled: ..,
    ),
);

// To start manually, if autoStart = false
gesturedeckMedia.start()

// To stop
gesturedeckMedia.stop()


// To reverse horizontal swipes
gesturedeckMedia.reverseHorizontalSwipes(true)
```

## Generate docs

```dart
dart doc --output docs
```

## Free to Use
Gesturedeck SDK is free to use, providing you with the full functionality of the SDK without any time limitations. You are welcome to integrate it into both personal and commercial projects. When using Gesturedeck SDK for free, a watermark will be presented during runtime. It is strictly prohibited  to hide, remove, or alter in any way the watermark from the free version of Gesturedeck SDK.

### Activation Key and Watermark Removal
For those who wish to remove the watermark from their app, we offer an activation key that allows you to use the SDK without any watermarks. However, please be aware that the watermark-free version is not available for free and requires a purchase.

To inquire about purchasing an activation key or if you have any other questions related to licensing and usage, please contact us at team@navideck.com. We will be happy to assist you with the process and provide you with the necessary information.

## Contact

For questions or support, please contact us at team@navideck.com. Thank you for choosing Volumedeck SDK!