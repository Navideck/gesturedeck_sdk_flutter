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

    Uint8List testIconBytes = testIcon.buffer.asUint8List();
    var gesturedeckMediaOverlay = GesturedeckMediaOverlay(
      tintColor: Colors.green,
      topIcon: testIconBytes,
      iconSwipeLeft: testIconBytes,
      iconSwipeRight: testIconBytes,
      iconTap: testIconBytes,
      iconTapToggled: testIconBytes,
    );

    var androidActivationKey =
        "HYeVdkywQiF1LJ-UN9obGZ-UtL71p2wdasCRnmZTVNBIykpYeuYEXOYlbwcy_gj7TJjdCgbg09DpD8VIUiaX8FxJNlx1GvjR3rCC70C1YSrUxoFNCr0lLCTMJiLloJA4QuAAi-m0AZCkmr8dpjGOTOt15Q4JKe2ZvXkbsvdCNcjyZ0TPAewx8-xIJ4k9llv52zENg3MtQLufvH7INzoWu5ZbsWiQnAx6RbASUtjhcIwp9ZN2-FrKw-NBi-jvOnVGVEP2CcyOLwrvj3AJDQ-sMP6MyC_fPaXycX7vwT_8WsZnP972mNSCrgFcF2IwRkTfjTmA7wDhiPpv5c9IuxgNklowECxKPmn-ZYMlkob7VQ84TovzHGaIjLCKumaeHXoUSZ_IvvXMJLtY1tI8rKoQM0c9lqr1q6SuswFYGFhZBvDRuZU3HYYHIB0rNiGkUi_ZLEgQnhSBtH0EeON-kpPAwTCiULVFs9CPvJ9r86ysju5O1BpU2cHI1E9jxz3ybFL8mkJYbitgCg2e3KBHsTSHjIuFjRZQoy_qh5P3dVcZS49SZDrqRVmEneJvesjTxoemPNlrCrqSad19xImZi4BeMQTD4mUWOBbNEUyX9austnx5gfoUoW5QNKFpuIg21LP1_oGsbW39AgcngWU8T7PhWNMKt6vz82WVwK4uFN8LEko";
    var iOSActivationKey =
        "UcId71-0_2lW6IxWUmOr477c0nghOWSyEJB4VZdwPcUVdWPeMCxWF-FzkLGsACYR1a7IQgwXsFfGpk6_YhKcRh2B4t2FNEBQyG5Af-YQwgWlcfZfjEYBVcblIFb1S3BYYn7PSiNIGwWqpP_F-0PNRpzO-nfoGnbm8j4-vBXR1Pa-WMVIx2N2xokM_fbMOq8hXVg-E-eA0AdT26y1YYp-VVF4ot8inxDb4OMwnHnaJMJcUQQTtpaqHNEPytxdDbDyiGymIf1mckkH8VttgdCJtAviXPDUUYiDO_u764vp9D_-fMs6R4QhhskPZYnc6I5YKqjJ8hGjnDL80k3p_196FzhJKFtVdl6CPG6T4kGecUsPKcKyC_wEYVZXvSUlbeDrFlgB0VgoVJZZsEwTfjCvO3k2Tc13qSdqIDxATxlHSPVDnslhQ6fOu2M7-vQCsAAP4sVVxcCNsUgnl_zDaGkPQnIxfjI2kyDiOR5w4-9Sq8nwGPI33t7z0d1zYLpcmpbxTlVJL1L8vBS73KApzvctjHctX3nYFVArFemCHhpi097g08_foUPvbVt7Jvd6DAowdWftX7knbrLVZ_UBJ68BDOA2OvPfXzCcUUDTPFEmSBptdhaseKikQhncqosxOkmuH9dpoAi450bkrURizKXgnZrXv0VVjK599JkKdzB5S2A";

    await GesturedeckMedia.initialize(
      androidActivationKey: androidActivationKey,
      iOSActivationKey: iOSActivationKey,
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
