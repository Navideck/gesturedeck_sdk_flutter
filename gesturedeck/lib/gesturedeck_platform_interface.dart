import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'gesturedeck_method_channel.dart';

abstract class GesturedeckPlatform extends PlatformInterface {
  /// Constructs a GesturedeckPlatform.
  GesturedeckPlatform() : super(token: _token);

  static final Object _token = Object();

  static GesturedeckPlatform _instance = MethodChannelGesturedeck();

  /// The default instance of [GesturedeckPlatform] to use.
  ///
  /// Defaults to [MethodChannelGesturedeck].
  static GesturedeckPlatform get instance => _instance;
  
  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [GesturedeckPlatform] when
  /// they register themselves.
  static set instance(GesturedeckPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
