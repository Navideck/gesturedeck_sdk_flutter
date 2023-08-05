# Gesturedeck Flutter

This is a Flutter plugin for native `Gesturedeck Android` and `Gesturedeck IOS`. It provides a simple way to integrate Gesturedeck into your Flutter app.

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