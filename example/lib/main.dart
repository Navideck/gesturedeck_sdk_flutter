// ignore_for_file: avoid_print, unused_local_variable

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
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
  late GesturedeckMedia gesturedeckMedia;

  bool reverseHorizontalSwipes = false;
  String gesturedeckMediaAction = "";
  String gesturedeckAction = "";

  void initializeGesturedeck() {
    Gesturedeck(
      activationKey: "",
      autoStart: true,
      tapAction: () => setState(() => gesturedeckAction = "tap"),
      swipeLeftAction: () => setState(() => gesturedeckAction = "swipeLeft"),
      swipeRightAction: () => setState(() => gesturedeckAction = "swipeRight"),
      panAction: () => setState(() => gesturedeckAction = "pan"),
    );
  }

  void initializeGesturedeckMedia() async {
    var testIcon = await rootBundle.load("assets/test_icon.png");
    Uint8List testIconBytes = testIcon.buffer.asUint8List();
    var gesturedeckMediaOverlay = GesturedeckMediaOverlay(
      tintColor: Colors.green,
      topIcon: testIconBytes,
      iconSwipeLeft: testIconBytes,
      iconSwipeRight: testIconBytes,
      iconTap: testIconBytes,
      iconTapToggled: testIconBytes,
    );

    gesturedeckMedia = GesturedeckMedia(
      activationKey: "",
      autoStart: true,
      reverseHorizontalSwipes: reverseHorizontalSwipes,
      tapAction: () => setState(() => gesturedeckMediaAction = "tap"),
      swipeLeftAction: () =>
          setState(() => gesturedeckMediaAction = "swipeLeft"),
      swipeRightAction: () =>
          setState(() => gesturedeckMediaAction = "swipeRight"),
      panAction: () => setState(() => gesturedeckMediaAction = "pan"),
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
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceAround,
            children: [
              ElevatedButton(
                  onPressed: () => gesturedeckMedia.start(),
                  child: const Text("start")),
              ElevatedButton(
                  onPressed: () async {
                    await gesturedeckMedia
                        .reverseHorizontalSwipes(!reverseHorizontalSwipes);
                    setState(() {
                      reverseHorizontalSwipes = !reverseHorizontalSwipes;
                    });
                  },
                  child: const Text("Reverse")),
              ElevatedButton(
                onPressed: () => gesturedeckMedia.stop(),
                child: const Text("stop"),
              ),
            ],
          ),
          const Divider(),
          Text(
              'Swipe ${reverseHorizontalSwipes ? 'left' : 'right'} to skip next'),
          const Divider(),
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
                              'Recognized GesturedeckMedia : $gesturedeckMediaAction'),
                    ),
                    const Divider(),
                    gesturedeckAction.isEmpty
                        ? const SizedBox()
                        : Padding(
                            padding: const EdgeInsets.all(8.0),
                            child: Text(
                                'Recognized Gesturedeck : $gesturedeckAction'),
                          ),
                  ],
                )),
          ),
        ],
      ),
    );
  }
}
