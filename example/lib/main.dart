// ignore_for_file: avoid_print, unused_local_variable

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:gesturedeck_flutter/gesturedeck_flutter.dart';

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
  String gesturedeckMediaAction = "";
  String gesturedeckAction = "";

  void initializeGesturedeck() async {
    await Gesturedeck.initialize(
      tapAction: () => setState(() => gesturedeckAction = "tap"),
      swipeLeftAction: () => setState(() => gesturedeckAction = "swipeLeft"),
      swipeRightAction: () => setState(() => gesturedeckAction = "swipeRight"),
      panAction: () => setState(() => gesturedeckAction = "pan"),
    );
  }

  void initializeGesturedeckMedia() async {
    var testIcon = await rootBundle.load("assets/test_icon.png");
    // Make sure to add .env file in the root of the project
    await dotenv.load(fileName: ".env");

    Uint8List testIconBytes = testIcon.buffer.asUint8List();
    var gesturedeckMediaOverlay = GesturedeckMediaOverlay(
      tintColor: Colors.green,
      topIcon: testIconBytes,
      iconSwipeLeft: testIconBytes,
      iconSwipeRight: testIconBytes,
      iconTap: testIconBytes,
      iconTapToggled: testIconBytes,
    );

    await GesturedeckMedia.initialize(
      androidActivationKey: dotenv.env['ANDROID_ACTIVATION_KEY'],
      iOSActivationKey: dotenv.env['IOS_ACTIVATION_KEY'],
      tapAction: () {
        setState(() => gesturedeckMediaAction = "tap");
      },
      swipeLeftAction: () {
        setState(() => gesturedeckMediaAction = "swipeLeft");
      },
      swipeRightAction: () {
        setState(() => gesturedeckMediaAction = "swipeRight");
      },
      panAction: () {
        setState(() => gesturedeckMediaAction = "pan");
      },
      longPressAction: () {
        setState(() => gesturedeckMediaAction = "longPress");
      },
      // gesturedeckMediaOverlay: gesturedeckMediaOverlay,
    );
  }

  @override
  void initState() {
    // initializeGesturedeck();
    initializeGesturedeckMedia();
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
            padding: const EdgeInsets.all(2.0),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                ElevatedButton(
                    onPressed: () {
                      GesturedeckMedia.start();
                    },
                    child: const Text("Start")),
                ElevatedButton(
                  onPressed: () {
                    GesturedeckMedia.stop();
                  },
                  child: const Text("Stop"),
                ),
              ],
            ),
          ),
          Expanded(
            child: Container(
                color: Colors.grey.shade400,
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Padding(
                      padding: const EdgeInsets.all(8.0),
                      child: gesturedeckMediaAction.isEmpty
                          ? const Text('Swipe or Tap on Screen')
                          : Text(
                              'Recognized GesturedeckMedia : $gesturedeckMediaAction',
                            ),
                    ),
                    const Divider(),
                    gesturedeckAction.isEmpty
                        ? const SizedBox()
                        : Padding(
                            padding: const EdgeInsets.all(8.0),
                            child: Text(
                              'Recognized Gesturedeck : $gesturedeckAction',
                            ),
                          ),
                  ],
                )),
          ),
        ],
      ),
    );
  }
}
