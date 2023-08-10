# Gesturedeck Flutter

This Flutter plugin allows seamless integration of Gesturedeck into your Flutter app, providing intuitive touch gestures for enhanced user interactions. The plugin is compatible with both Android and iOS platforms.

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
var gesturedeck = Gesturedeck.initialize(
    tapAction: () {},
    swipeLeftAction: () {},
    swipeRightAction: () {},
    panAction: () {},
);
```

2. Start and stop Gesturedeck detection:
```dart
gesturedeck.start();
gesturedeck.stop();
```

### Setup GesturedeckMedia  

Enhance media app controls using GesturedeckMedia:

1. Initialize GesturedeckMedia with overlay UI customization:
```dart
var gesturedeckMedia = GesturedeckMedia.initialize(
    tapAction: () {},
    swipeLeftAction: () {},
    swipeRightAction: () {},
    panAction: () {},
    gesturedeckMediaOverlay: GesturedeckMediaOverlay(
        tintColor: Colors.green,
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
gesturedeckMedia.start();
gesturedeckMedia.stop();
```

3. Customize reverse horizontal swipes:
```dart
gesturedeckMedia.reverseHorizontalSwipes = true;
```

## Generate Documentation

Generate documentation for your Gesturedeck Flutter integration using the following command:
```bash
dart doc --output docs
```

## Free to Use

Gesturedeck SDK is freely available for use in both personal and commercial projects, offering full functionality without time limitations. However, when using the free version, a watermark will be displayed during runtime. It is strictly prohibited to hide, remove, or alter the watermark from the free version of Gesturedeck SDK.

### Activation Key and Watermark Removal

To remove the watermark from your app, an activation key is available for purchase. The watermark-free version of Gesturedeck SDK can be obtained through this activation key.

To inquire about purchasing an activation key or for any other questions related to licensing and usage, please reach out to us at team@navideck.com. We are here to assist you with the process and provide the necessary information.

## Contact

For any inquiries, questions, or support, please don't hesitate to contact our team at team@navideck.com. Thank you for choosing Gesturedeck Flutter Plugin!