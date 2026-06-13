import 'package:thesis_checker/data/models/format_error_api.dart';

class ErrorsByCategory {
  final String category;
  final int count;
  final List<FormatErrorApi> errors;

  const ErrorsByCategory({required this.category, required this.errors, int? count})
      : count = count ?? errors.length;
}
