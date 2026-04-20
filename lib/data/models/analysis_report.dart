import 'package:thesis_checker/data/models/found_error.dart';

class AnalysisReport {
  const AnalysisReport({
    required this.errors,
  });

  final List<FoundError> errors;

  factory AnalysisReport.fromJson(Map<String, dynamic> json) {
    return AnalysisReport(
      errors: (json['errors'] as List<dynamic>? ?? const [])
          .map(
            (item) => FoundError.fromJson(
              Map<String, dynamic>.from(item as Map),
            ),
          ).toList(),
    );
  }
}
