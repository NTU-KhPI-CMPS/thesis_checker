import 'dart:convert';
import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter_app/features/result/models/analysis_result.dart';
import 'package:flutter_app/features/result/models/error_by_category.dart';
import 'package:flutter_app/features/result/models/found_error.dart';
import 'package:flutter_app/features/result/models/highlight_error.dart';
import 'package:flutter/services.dart';
import 'package:path_provider/path_provider.dart';

class RunnerJavaService {
  RunnerJavaService._internal();
  static final RunnerJavaService _instance = RunnerJavaService._internal();
  factory RunnerJavaService() => _instance;

  final _javaThesisChecker = 'java-thesis-checker';

  Future<AnalysisResult> checkFile(String filePath) async {
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

      // Temporary mock result until real process JSON output parsing is added.
      return _buildMockResult(filePath);

    } catch (e) {
      print('Failed to start process: $e');
      rethrow;
    }
  }

  AnalysisResult _buildMockResult(String filePath) {
    final fileName = filePath.split(Platform.pathSeparator).last;

    return AnalysisResult(
      fileName: fileName,
      analyzedAt: DateTime.now(),
      errorsByCategory: [
        ErrorsByCategory(category: 'Шрифт', count: 2),
        ErrorsByCategory(category: 'Абзац', count: 1),
      ],
      foundErrors: const [
        FoundError(
          id: 'font-1',
          category: 'Шрифт',
          title: 'Некоректний розмір шрифту',
          paragraphIndex: 2,
          paragraphText: 'Це приклад абзацу з неправильним форматуванням.',
          highlightError: HighlightError(start: 10, end: 24),
          found: '11pt',
          expected: '14pt',
          suggestions: [],
        ),
        FoundError(
          id: 'font-2',
          category: 'Шрифт',
          title: 'Некоректний розмір шрифту',
          paragraphIndex: 3,
          paragraphText: 'Це приклад абзацу з неправильним форматуванням.',
          highlightError: HighlightError(start: 10, end: 32),
          found: '11pt',
          expected: '14pt',
          suggestions: [],
        ),
        FoundError(
          id: 'paragraph-1',
          category: 'Абзац',
          title: 'Некоректний міжрядковий інтервал',
          paragraphIndex: 5,
          paragraphText: 'Ще один абзац із проблемою інтервалу.',
          highlightError: null,
          found: '1.0',
          expected: '1.5',
          suggestions: [],
        ),
      ],
    );
  }
}
