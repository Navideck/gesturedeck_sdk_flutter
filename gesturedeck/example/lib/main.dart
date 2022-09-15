// ignore_for_file: avoid_print

import 'package:flutter/material.dart';
import 'package:gesturedeck/gesturedeck.dart';

void main() {
  runApp(
    const MaterialApp(
      debugShowCheckedModeBanner: false,
      home: MyApp(),
    ),
  );
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  GestureType? gestureType;

  void _initGesturedeck() {
    Gesturedeck.subscribeGestures().listen((GestureType type) {
      setState(() {
        gestureType = type;
      });
      print(type.name.toString());
    });
  }

  @override
  void initState() {
    _initGesturedeck();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Gesturedeck'),
        centerTitle: true,
      ),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Text('Recognized Gesture : ${gestureType?.name.toString()}'),
          ),
          const Divider(),
          Expanded(
            child: Container(
              color: Colors.grey.shade400,
              child: const Center(
                child: Text('Swipe or Tap on Screen'),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
