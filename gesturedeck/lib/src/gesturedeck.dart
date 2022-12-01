import 'dart:async';
import 'package:gesturedeck/src/gesturedeck_method_channel.dart';
import 'package:gesturedeck/src/helper/helper.dart';

class Gesturedeck {
  /// Subscribes to [SwipeGestures] events.
  static Stream<GestureType> subscribeGestures() {
    return MethodChannelGesturedeck.touchEventStream
        .where((element) => getGestureType(element) != null)
        .map((event) => getGestureType(event)!);
  }
}

enum GestureType { tap, swipedLeft, swipedRight }
