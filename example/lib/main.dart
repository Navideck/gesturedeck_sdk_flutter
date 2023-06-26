// ignore_for_file: avoid_print

import 'package:flutter/material.dart';
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
  bool reverseHorizontalSwipes = false;

  @override
  void initState() {
    Gesturedeck.initialize(
      activationKey: "",
      reverseHorizontalSwipes: reverseHorizontalSwipes,
      enableGesturedeckMedia: true,
      tintColor: Colors.green,
    );
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
                  onPressed: () => Gesturedeck.start(),
                  child: const Text("start")),
              ElevatedButton(
                  onPressed: () async {
                    await Gesturedeck.reverseHorizontalSwipes(
                        !reverseHorizontalSwipes);
                    setState(() {
                      reverseHorizontalSwipes = !reverseHorizontalSwipes;
                    });
                  },
                  child: const Text("Reverse")),
              ElevatedButton(
                onPressed: () => Gesturedeck.stop(),
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
              child: Center(
                child: Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: StreamBuilder(
                    stream: Gesturedeck.gesturesStream,
                    initialData: null,
                    builder: (BuildContext context, AsyncSnapshot snapshot) {
                      GestureType? gestureType = snapshot.data;
                      return gestureType == null
                          ? const Text('Swipe or Tap on Screen')
                          : Text('Recognized Gesture : ${gestureType.name}');
                    },
                  ),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
