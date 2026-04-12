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
      debugPrint("Tmp directory to put jar: ${directory.path}");
      final jarFile = File('${directory.path}/$_javaThesisChecker');

      if (!await jarFile.exists() || kDebugMode) {
        debugPrint('Extracting .jar file for the first time...');
        final byteData = await rootBundle.load('assets/$_javaThesisChecker');

        // Write the bytes to the physical file
        await jarFile.writeAsBytes(byteData.buffer.asUint8List(
            byteData.offsetInBytes,
            byteData.lengthInBytes
        ));
      }

      debugPrint('Execute process to check file.');
      // Mark java-thesis-checker binary as executable
      await Process.start('chmod', ['+x', jarFile.path]);
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
      final resultFileName = filePath.split(Platform.pathSeparator).last;

      final rawJson = await file.readAsString();
        final decodedReport = jsonDecode(rawJson) as Map<String, dynamic>;
        final rawErrors = decodedReport['errors'] as List<dynamic>;

      final foundErrors = rawErrors
          .map(
            (item) => FoundError.fromJson(
              Map<String, dynamic>.from(item as Map)
              )
          ).toList();

      final groupedErrorsByType = <String, List<FoundError>>{
        for (final type in AvailableCheckTypes.checkTypes) type.title: <FoundError>[],
      };

      for (final error in foundErrors) {
        final type = CheckTypeGrouping.resolveTypeByError(error);
        groupedErrorsByType[type.title]?.add(error);
      }

      final errorsByCategory = AvailableCheckTypes.checkTypes
          .map(
            (type) => ErrorsByCategory(
              category: type.title,
              errors: groupedErrorsByType[type.title] ?? const <FoundError>[],
            ),
          ).toList();

      return AnalysisResult(
        fileName: resultFileName,
        analyzedAt: DateTime.now(),
        errorsByCategory: errorsByCategory,
        foundErrors: foundErrors,
      );
    } catch (e) {
      debugPrint('Failed to execute analysis flow: $e');
      throw Exception('Не вдалося виконати аналіз. Спробуйте ще раз.');
    }
  }
}
