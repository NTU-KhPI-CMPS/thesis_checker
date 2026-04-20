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

  factory AnalysisResult.fromJson(Map<String, dynamic> json) {
    return AnalysisResult(
      fileName: json['fileName']?.toString() ?? '',
      analyzedAt: DateTime.tryParse(json['analyzedAt']?.toString() ?? '') ??
          DateTime.fromMillisecondsSinceEpoch(0),
      errorsByCategory: (json['errorsByCategory'] as List<dynamic>? ?? const [])
          .map(
            (item) => ErrorsByCategory.fromJson(
              Map<String, dynamic>.from(item as Map),
            ),
          ).toList(),
    );
  }
}
