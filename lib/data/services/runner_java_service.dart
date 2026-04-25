import 'dart:convert';
import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:thesis_checker/data/models/report_api.dart';
import 'package:path_provider/path_provider.dart';
import 'package:thesis_checker/data/services/thesis_checker_service.dart';

class RunnerJavaService {
  RunnerJavaService._internal();
  static final RunnerJavaService _instance = RunnerJavaService._internal();
  factory RunnerJavaService() => _instance;

  final checkerService = ThesisCheckerService();

  Future<ReportApi> checkFile(String filePath) async {
    try {
      final directory = await getApplicationSupportDirectory();
      final resultsRoot = Directory('${directory.path}/results');

      final returnCode = await checkerService.runThesisChecks(
        filePath: filePath,
        resultDirectory: resultsRoot.path,
      );

      if (returnCode != 0) {
        throw Exception('Аналіз завершився з помилкою');
      }

      final file = File('${resultsRoot.path}/result.json');
      final rawJson = await file.readAsString();
      final decodedReport = jsonDecode(rawJson) as Map<String, dynamic>;
      
      return ReportApi.fromJson(decodedReport);
    } catch (e) {
      debugPrint('Failed to execute analysis flow: $e');
      throw Exception('Не вдалося виконати аналіз. Спробуйте ще раз.');
    }
  }
}
