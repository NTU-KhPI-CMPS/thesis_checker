import 'package:thesis_checker/data/models/error_by_category.dart';

class AnalysisResult {
  final String fileName;
  final DateTime analyzedAt;
  final List<ErrorsByCategory> errorsByCategory;

  const AnalysisResult({
    required this.fileName,
    required this.analyzedAt,
    required this.errorsByCategory,
  });

  int get totalErrors => errorsByCategory.fold(0, (sum, item) => sum + item.count);
}
