// ignore_for_file: avoid_print

import 'dart:io';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:gesturedeck_flutter/gesturedeck_flutter.dart';
import 'package:gesturedeck_flutter_example/main.dart';

class Home extends StatefulWidget {
  const Home({Key? key}) : super(key: key);

  @override
  State<Home> createState() => _HomeState();
}

class _HomeState extends State<Home> {
  bool isGesturedeckRunning = true;

  void initializeGesturedeck() async {
    await GesturedeckMedia.initialize();

    GesturedeckMedia.tapAction = () {
      print("Tap Action");
    };
  }

  @override
  void initState() {
    initializeGesturedeck();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Gesturedeck Example'),
      ),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.symmetric(vertical: 10.0, horizontal: 2),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                AdaptiveButton(
                  isEnabled: !isGesturedeckRunning,
                  text: "Start",
                  onPressed: () {
                    GesturedeckMedia.start();
                    setState(() {
                      isGesturedeckRunning = true;
                    });
                  },
                ),
                AdaptiveButton(
                  isEnabled: isGesturedeckRunning,
                  text: "Stop",
                  onPressed: () {
                    GesturedeckMedia.stop();
                    setState(() {
                      isGesturedeckRunning = false;
                    });
                  },
                ),
              ],
            ),
          ),
          const Divider(color: Colors.grey),
          const Expanded(
            child: Center(
              child: Text(
                'Perform Gestures',
                style: TextStyle(
                  fontSize: 18,
                  fontWeight: FontWeight.w400,
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class AdaptiveButton extends StatelessWidget {
  final String text;
  final VoidCallback? onPressed;
  final bool isEnabled;
  const AdaptiveButton({
    super.key,
    required this.onPressed,
    required this.text,
    this.isEnabled = true,
  });

  @override
  Widget build(BuildContext context) {
    return Platform.isIOS
        ? CupertinoButton(
            onPressed: !isEnabled ? null : onPressed,
            color: primaryColor,
            disabledColor: Colors.grey.withOpacity(0.5),
            padding: const EdgeInsets.symmetric(horizontal: 40),
            child: Text(text),
          )
        : ElevatedButton(
            onPressed: !isEnabled ? null : onPressed,
            child: Text(text),
          );
  }
}
