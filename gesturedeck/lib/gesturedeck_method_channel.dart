import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'gesturedeck_platform_interface.dart';

/// An implementation of [GesturedeckPlatform] that uses method channels.
class MethodChannelGesturedeck extends GesturedeckPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('gesturedeck');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
