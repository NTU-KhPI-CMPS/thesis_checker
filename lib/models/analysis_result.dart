import 'package:thesis_checker/data/models/error_by_category.dart';

class AnalysisResult {
  final String filePath;
  final String fileName;
  final DateTime analyzedAt;
  final List<ErrorsByCategory> errorsByCategory;
  final List<String>? selectedCategories;

  const AnalysisResult({
    required this.filePath,
    required this.fileName,
    required this.analyzedAt,
    required this.errorsByCategory,
    this.selectedCategories,
  });

  int get totalErrors => errorsByCategory.fold(0, (sum, item) => sum + item.count);
}
