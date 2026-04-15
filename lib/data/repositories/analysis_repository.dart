import 'dart:io';

import 'package:flutter_app/core/constants/available_check_types.dart';
import 'package:flutter_app/core/utils/check_type_grouping.dart';
import 'package:flutter_app/data/services/runner_java_service.dart';
import 'package:flutter_app/models/analysis_result.dart';
import 'package:flutter_app/data/models/error_by_category.dart';
import 'package:flutter_app/data/models/found_error.dart';

class AnalysisRepository {
  AnalysisRepository._internal();
  static final AnalysisRepository _instance = AnalysisRepository._internal();
  factory AnalysisRepository() => _instance;

  final RunnerJavaService _runnerJavaService = RunnerJavaService();

  Future<AnalysisResult> checkFile(String filePath) async {
    final report = await _runnerJavaService.checkFile(filePath);
    final foundErrors = report.errors;

    final groupedErrorsByType = <String, List<FoundError>> {
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
      fileName: filePath.split(Platform.pathSeparator).last,
      analyzedAt: DateTime.now(),
      errorsByCategory: errorsByCategory,
    );
  }
}
