import 'package:flutter/material.dart';
import 'package:flutter_app/app.dart';
import 'dart:io';
import 'package:jni/jni.dart';
// import 'package:path/path.dart' as p;

void main() {
  WidgetsFlutterBinding.ensureInitialized();

  if (!Platform.isAndroid) {
    // Manually spawn the JVM on Desktop
    final jarPath = './../java-checker/target/java-checker-1.0-SNAPSHOT.jar';

    Jni.spawn(
      // classPath: [jarPath],
      // Optional: add dylibDir if you have custom native libs
    );
  }

  runApp(const App());
}
