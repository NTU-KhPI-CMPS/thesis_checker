import 'package:thesis_checker/data/models/error_by_category.dart';
import 'package:thesis_checker/models/check_type_info.dart';

class AnalysisResult {
  final String filePath;
  final String fileName;
  final DateTime analyzedAt;
  final List<ErrorsByCategory> errorsByCategory;
  final List<CheckTypeInfo> selectedChecks;

  const AnalysisResult({
    required this.filePath,
    required this.fileName,
    required this.analyzedAt,
    required this.errorsByCategory,
    required this.selectedChecks,
  });

  int get totalErrors => errorsByCategory.fold(0, (sum, item) => sum + item.count);
}
