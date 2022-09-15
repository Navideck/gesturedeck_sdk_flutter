import 'dart:async';
import 'package:gesturedeck/gesturedeck_method_channel.dart';

class Gesturedeck {
  /// Subscribes to [SwipeGestures] events.
  static Stream<GestureType> subscribeGestures() {
    return MethodChannelGesturedeck.touchEventStream.map((event) =>
        GestureType.values.firstWhere((element) =>
            element.toString().split('.').last == event.toString()));
  }
}

// TODO: add swipe right, swipe left
enum GestureType { swipe, tap }
