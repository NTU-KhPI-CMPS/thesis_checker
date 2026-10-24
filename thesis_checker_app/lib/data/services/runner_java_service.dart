import 'dart:convert';
import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:thesis_checker/core/errors/analysis_exception.dart';
import 'package:thesis_checker/data/models/report_api.dart';
import 'package:path_provider/path_provider.dart';
import 'package:thesis_checker/data/services/thesis_checker_service.dart';
import 'package:thesis_checker/models/check_type_info.dart';

class RunnerJavaService {
  final ThesisCheckerService _checkerService;

  RunnerJavaService({required this._checkerService});

  Future<ReportApi> checkFile(String filePath, {required List<CheckTypeInfo> selectedChecks}) async {
    try {
      final directory = await getApplicationSupportDirectory();
      final resultsRoot = Directory('${directory.path}/results');

      final returnCode = await _checkerService.runThesisChecks(
        files: [filePath],
        resultDirectory: resultsRoot.path,
        selectedChecks: selectedChecks,
      );

      if (returnCode != 0) {
        throw AnalysisException(AnalysisErrorType.finishedWithError);
      }

      final file = File('${resultsRoot.path}/result.json');
      final rawJson = await file.readAsString();
      final decodedReport = jsonDecode(rawJson) as Map<String, dynamic>;
      
      return ReportApi.fromJson(decodedReport);
    } on AnalysisException {
      rethrow;
    } catch (e) {
      debugPrint('Failed to execute analysis flow: $e');
      throw const AnalysisException(AnalysisErrorType.couldNotComplete);
    }
  }
}
