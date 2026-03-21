import 'dart:ffi';
import 'dart:io';
import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:path_provider/path_provider.dart';

Future<void> checkFile(String filePath) async {

  final javaThesisChecker = 'java-thesis-checker.jar';

  try {
    // 1. Find a safe directory on the user's computer to store the file
    final directory = await getApplicationSupportDirectory();
    print("Tmp directory to put jar: ${directory.path}");
    final jarFile = File('${directory.path}/$javaThesisChecker');

    // 2. Extract the .jar from assets ONLY if it hasn't been extracted yet
    // if (!await jarFile.exists() || kDebugMode) {
      print('Extracting .jar file for the first time...');
      final byteData = await rootBundle.load('assets/$javaThesisChecker');

      // Write the bytes to the physical file
      await jarFile.writeAsBytes(byteData.buffer.asUint8List(
          byteData.offsetInBytes,
          byteData.lengthInBytes
      ));
    // }

    printJavaVersion(); // this is just for debug, because we need the same java version as we use to build jar

    print('Execute java process to ckech file.');
    final process = await Process.start('java', ['-jar', jarFile.path, '-filePath', filePath, '-checks', '["FONT", "PARAGRAPH"]']);

    // Listen to standard output in real-time
    process.stdout.transform(utf8.decoder).listen((data) {
      print('STDOUT: $data');
    });

    // Listen to standard error in real-time
    process.stderr.transform(utf8.decoder).listen((data) {
      print('STDERR: $data');
    });

    // Check when it exits
    final exitCode = await process.exitCode;
    print('Java process exited with code $exitCode');

  } catch (e) {
    print('Failed to start Java process: $e');
  }
}

Future<void> printJavaVersion() async {
  final process = await Process.start('java', ['--version']);

  // Listen to standard output in real-time
  process.stdout.transform(utf8.decoder).listen((data) {
    print('STDOUT: $data');
  });
}