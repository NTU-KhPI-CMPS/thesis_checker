import 'package:flutter_app/data/models/found_error.dart';

class ErrorsByCategory {
  final String category;
  final int count;
  final List<FoundError> errors;

  const ErrorsByCategory({required this.category, required this.errors, int? count})
      : count = count ?? errors.length;

  factory ErrorsByCategory.fromJson(Map<String, dynamic> json) {
    final parsedErrors = (json['errors'] as List<dynamic>? ?? const [])
        .map((item) => FoundError.fromJson(Map<String, dynamic>.from(item as Map)))
        .toList();

    return ErrorsByCategory(
      category: json['category']?.toString() ?? '',
      errors: parsedErrors,
      count: (json['count'] as num?)?.toInt() ?? parsedErrors.length,
    );
  }
}
