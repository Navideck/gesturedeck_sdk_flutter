import '../../../gesturedeck_flutter.dart';

// convert String from Native platform to GestureType enum
GestureType? getGestureType(String data) {
  try {
    return GestureType.values.firstWhere((values) => values.name == data);
  } catch (e) {
    return null;
  }
}
