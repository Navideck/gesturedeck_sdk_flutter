import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:gesturedeck/gesturedeck.dart';

void main() {
  const MethodChannel channel = MethodChannel('gesturedeck');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await Gesturedeck.platformVersion, '42');
  });
}
