import 'dart:convert';
import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter_app/data/models/analysis_report.dart';
import 'package:flutter/services.dart';
import 'package:path_provider/path_provider.dart';

class RunnerJavaService {
  RunnerJavaService._internal();
  static final RunnerJavaService _instance = RunnerJavaService._internal();
  factory RunnerJavaService() => _instance;

  static const _javaThesisCheckerBaseName = 'java-thesis-checker';

  bool get _isWindows => Platform.isWindows;

  String get _javaThesisCheckerFileName {
    return _isWindows ? 
        '$_javaThesisCheckerBaseName.exe': 
        _javaThesisCheckerBaseName;
  }

  Future<AnalysisReport> checkFile(String filePath) async {
    try {
      // 1. Find a safe directory on the user's computer to store the file
      final directory = await getApplicationSupportDirectory();
      debugPrint("Tmp directory to put jar: ${directory.path}");
      final jarFile = File('${directory.path}/$_javaThesisCheckerFileName');

      if (!await jarFile.exists() || kDebugMode) {
        debugPrint('Extracting .jar file for the first time...');
        final byteData = await rootBundle.load('assets/$_javaThesisCheckerFileName');

        // Write the bytes to the physical file
        await jarFile.writeAsBytes(byteData.buffer.asUint8List(
            byteData.offsetInBytes,
            byteData.lengthInBytes
        ));
      }

      debugPrint('Execute process to check file.');
      // Mark binary as executable only on Unix-like platforms.
      if (!_isWindows) {
        await Process.start('chmod', ['+x', jarFile.path]);
      }
      // Execute binary with args
      final process = await Process.start(jarFile.path, ['-filePath', filePath, '-checks', '["FONT", "PARAGRAPH"]']);
      final stderrBuffer = StringBuffer();

      // Listen to standard output in real-time
      process.stdout.transform(utf8.decoder).listen((data) {
        debugPrint('STDOUT: $data');
      });

      // Listen to standard error in real-time
      process.stderr.transform(utf8.decoder).listen((data) {
        // debugPrint('STDERR: $data');
        stderrBuffer.write(data);
      });

      // Check when it exits
      final exitCode = await process.exitCode;
      debugPrint('Process exited with code $exitCode');

      if (exitCode != 0) {
        final stderrOutput = stderrBuffer.toString().trim();
        throw Exception(
          stderrOutput.isNotEmpty
              ? 'Аналіз завершився з помилкою: $stderrOutput'
              : 'Аналіз завершився з кодом $exitCode.',
        );
      }

      final file = File('./reports/result.json');

      final rawJson = await file.readAsString();
      final decodedReport = jsonDecode(rawJson) as Map<String, dynamic>;
      return AnalysisReport.fromJson(decodedReport);
    } catch (e) {
      debugPrint('Failed to execute analysis flow: $e');
      throw Exception('Не вдалося виконати аналіз. Спробуйте ще раз.');
    }
  }
}
