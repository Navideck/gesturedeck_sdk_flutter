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
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Gesturedeck'),
        centerTitle: true,
      ),
      body: Column(
        children: [
          FutureBuilder(
            future: Gesturedeck.initialize(
              activationKey: "",
            ),
            builder: (BuildContext context, AsyncSnapshot snapshot) {
              switch (snapshot.connectionState) {
                case ConnectionState.done:
                  return Row(
                    mainAxisAlignment: MainAxisAlignment.spaceAround,
                    children: [
                      ElevatedButton(
                          onPressed: () => Gesturedeck.start(),
                          child: const Text("start")),
                      ElevatedButton(
                          onPressed: () => Gesturedeck.stop(),
                          child: const Text("stop"))
                    ],
                  );

                default:
                  return const CircularProgressIndicator();
              }
            },
          ),
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: StreamBuilder(
              stream: Gesturedeck.gesturesStream,
              initialData: null,
              builder: (BuildContext context, AsyncSnapshot snapshot) {
                GestureType? gestureType = snapshot.data;
                return Text(
                    'Recognized Gesture : ${gestureType?.name.toString()}');
              },
            ),
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
