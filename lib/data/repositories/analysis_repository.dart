import 'dart:io';

import 'package:thesis_checker/core/constants/available_check_types.dart';
import 'package:thesis_checker/core/enums/check.dart';
import 'package:thesis_checker/data/services/runner_java_service.dart';
import 'package:thesis_checker/models/check_type_info.dart';
import 'package:thesis_checker/models/analysis_result.dart';
import 'package:thesis_checker/data/models/error_by_category.dart';
import 'package:thesis_checker/data/models/format_error_api.dart';

class AnalysisRepository {
  AnalysisRepository._internal();
  static final AnalysisRepository _instance = AnalysisRepository._internal();
  factory AnalysisRepository() => _instance;

  final RunnerJavaService _runnerJavaService = RunnerJavaService();

  static Check? parseCheckFromCode(String? code) {
    if (code == null) {
      return null;
    }

    final normalizedCode = code.trim().toUpperCase();

    for (final check in Check.values) {
      if (check.name == normalizedCode) {
        return check;
      }
    }

    return null;
  }

  static Check? parseCheckFromError(FormatErrorApi error) {
    return parseCheckFromCode(error.category);
  }

  static CheckTypeInfo resolveTypeByError(FormatErrorApi error) {
    final parsedCheck = parseCheckFromError(error);

    if (parsedCheck != null) {
      for (final type in AvailableCheckTypes.checkTypes) {
        if (type.checks.contains(parsedCheck)) {
          return type;
        }
      }
    }

    for (final type in AvailableCheckTypes.checkTypes) {
      if (type.checks.isEmpty) {
        return type;
      }
    }

    return AvailableCheckTypes.checkTypes.first;
  }

  static Map<String, int> countErrorsByType(List<FormatErrorApi> errors) {
    final result = <String, int>{
      for (final type in AvailableCheckTypes.checkTypes) type.title: 0,
    };

    for (final error in errors) {
      final type = resolveTypeByError(error);
      result[type.title] = (result[type.title] ?? 0) + 1;
    }

    return result;
  }

  static List<FormatErrorApi> filterErrorsByType(List<FormatErrorApi> errors, CheckTypeInfo selectedType) {
    return errors
        .where((error) => resolveTypeByError(error).title == selectedType.title)
        .toList();
  }

  Future<AnalysisResult> checkFile(String filePath) async {
    final report = await _runnerJavaService.checkFile(filePath);
    final foundErrors = report.errors;

    final groupedErrorsByType = <String, List<FormatErrorApi>> {
      for (final type in AvailableCheckTypes.checkTypes) type.title: <FormatErrorApi>[],
    };

    for (final error in foundErrors) {
      final type = resolveTypeByError(error);
      groupedErrorsByType[type.title]?.add(error);
    }

    final errorsByCategory = AvailableCheckTypes.checkTypes
        .map(
          (type) => ErrorsByCategory(
            category: type.title,
            errors: groupedErrorsByType[type.title] ?? const <FormatErrorApi>[],
          ),
        ).toList();

    return AnalysisResult(
      fileName: filePath.split(Platform.pathSeparator).last,
      analyzedAt: DateTime.now(),
      errorsByCategory: errorsByCategory,
    );
  }
}
