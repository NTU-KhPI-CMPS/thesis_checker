import 'dart:convert';
import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:path_provider/path_provider.dart';

class RunnerJavaService {
  RunnerJavaService._internal();
  static final RunnerJavaService _instance = RunnerJavaService._internal();
  factory RunnerJavaService() => _instance;

  final _javaThesisChecker = 'java-thesis-checker';

  Future<void> checkFile(String filePath) async {
    try {
      // 1. Find a safe directory on the user's computer to store the file
      final directory = await getApplicationSupportDirectory();
      print("Tmp directory to put jar: ${directory.path}");
      final jarFile = File('${directory.path}/$_javaThesisChecker');

      if (!await jarFile.exists() || kDebugMode) {
        print('Extracting .jar file for the first time...');
        final byteData = await rootBundle.load('assets/$_javaThesisChecker');

        // Write the bytes to the physical file
        await jarFile.writeAsBytes(byteData.buffer.asUint8List(
            byteData.offsetInBytes,
            byteData.lengthInBytes
        ));
      }

      print('Execute process to check file.');
      // Mark java-thesis-checker binary as executable
      await Process.start('chmod', ['+x', jarFile.path]);
      // Execute binary with args
      final process = await Process.start(jarFile.path, ['-filePath', filePath, '-checks', '["FONT", "PARAGRAPH"]']);

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
      print('Process exited with code $exitCode');

    } catch (e) {
      print('Failed to start process: $e');
    }
  }
}