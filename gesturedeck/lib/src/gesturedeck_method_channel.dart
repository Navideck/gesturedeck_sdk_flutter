import 'package:flutter/services.dart';

/// An implementation of [GesturedeckPlatform] that uses method channels.
class MethodChannelGesturedeck {
  static const EventChannel _gestureEventChannel =
      EventChannel('com.navideck.gesturedeck');

  static Stream get touchEventStream =>
      _gestureEventChannel.receiveBroadcastStream({'name': 'touchEvent'});
}
