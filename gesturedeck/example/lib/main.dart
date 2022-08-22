import 'package:flutter/material.dart';
import 'package:gesturedeck/gesturedeck.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
    Gesturedeck.subscribeGestures().listen((event) {
      if (event == GestureType.tap) {
        print('tapped');
      } else if (event == GestureType.swipe) {
        print('swiped');
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: const Center(
          child: Text('Gesturedeck'),
        ),
      ),
    );
  }
}
