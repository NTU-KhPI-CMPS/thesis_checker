import 'dart:convert';
import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter_app/core/constants/available_check_types.dart';
import 'package:flutter_app/core/utils/check_type_grouping.dart';
import 'package:flutter_app/features/result/models/analysis_result.dart';
import 'package:flutter_app/features/result/models/error_by_category.dart';
import 'package:flutter_app/features/result/models/found_error.dart';
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

      return await _buildResultFromTestJsonAsset(filePath);

    } catch (e) {
      print('Failed to start process: $e');
      rethrow;
    }
  }

  Future<AnalysisResult> _buildResultFromTestJsonAsset(String filePath) async {
    final fileName = filePath.split(Platform.pathSeparator).last;
    final rawJson = await rootBundle.loadString('assets/test_json_result/result.json');
    final decoded = jsonDecode(rawJson) as Map<String, dynamic>;
    final rawErrors = (decoded['errors'] as List<dynamic>? ?? const []);

    final foundErrors = rawErrors.map((item) {
      final map = Map<String, dynamic>.from(item as Map);
      final foundRaw = map['found'];

      final foundValue =
        (foundRaw as List<dynamic>).map((value) => value.toString()).join(', ');

      return FoundError(
        id: map['id']?.toString() ?? '',
        category: map['category']?.toString() ?? '',
        expected: map['expected']?.toString() ?? '',
        found: foundValue,
        paragraphText: map['paragraphText']?.toString(),
        severity: map['severity']?.toString() ?? '',
        title: map['title']?.toString() ?? '',
      );
    }).toList();

    final countsByType = CheckTypeGrouping.countErrorsByType(foundErrors);
    final errorsByCategory = AvailableCheckTypes.checkTypes
        .map(
          (type) => ErrorsByCategory(
            category: type.title,
            count: countsByType[type.title] ?? 0,
          ),
        )
        .where((item) => item.count > 0)
        .toList();

    return AnalysisResult(
      fileName: fileName,
      analyzedAt: DateTime.now(),
      errorsByCategory: errorsByCategory,
      foundErrors: foundErrors,
    );
  }
}
