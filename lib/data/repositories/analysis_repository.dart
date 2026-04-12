import 'package:flutter_app/data/services/runner_java_service.dart';
import 'package:flutter_app/features/result/models/analysis_result.dart';

class AnalysisRepository{
  AnalysisRepository._internal();
  static final AnalysisRepository _instance = AnalysisRepository._internal();
  factory AnalysisRepository() => _instance;

  final RunnerJavaService _runnerJavaService = RunnerJavaService();

  Future<AnalysisResult> checkFile(String filePath) {
    return _runnerJavaService.checkFile(filePath);
  }
}
