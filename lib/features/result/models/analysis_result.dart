import 'package:flutter_app/features/result/models/error_by_category.dart';
import 'package:flutter_app/features/result/models/found_error.dart';

class AnalysisResult {
  final String fileName;
  final DateTime analyzedAt;
  final List<ErrorsByCategory> errorsByCategory;
  final List<FoundError> foundErrors;

  const AnalysisResult({
    required this.fileName,
    required this.analyzedAt,
    required this.errorsByCategory,
    required this.foundErrors,
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
          )
          .toList(),
      foundErrors: (json['foundErrors'] as List<dynamic>? ?? const [])
          .map(
            (item) => FoundError.fromJson(
              Map<String, dynamic>.from(item as Map),
            ),
          )
          .toList(),
    );
  }
}
