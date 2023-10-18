// ignore_for_file: avoid_print, unused_local_variable

import 'dart:io';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:gesturedeck_flutter_example/home.dart';

const primaryColor = Color(0xFFF5977F);

void main() {
  runApp(
    MaterialApp(
      debugShowCheckedModeBanner: false,
      theme: ThemeData.light().copyWith(
        brightness: Brightness.light,
        primaryColor: primaryColor,
        appBarTheme: AppBarTheme(
          backgroundColor: primaryColor,
          surfaceTintColor: Platform.isIOS ? Colors.transparent : null,
          shadowColor:
              Platform.isIOS ? CupertinoColors.darkBackgroundGray : null,
          scrolledUnderElevation: Platform.isIOS ? .1 : null,
        ),
        colorScheme: const ColorScheme.light(primary: primaryColor),
        useMaterial3: true,
      ),
      home: const Home(),
    ),
  );
}
