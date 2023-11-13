/// An enum representing the sensitivity of Gesturedeck when performing a panning gesture.
enum PanSensitivity {
  low(0),
  medium(1),
  high(2);

  const PanSensitivity(this.value);
  final int value;
}
