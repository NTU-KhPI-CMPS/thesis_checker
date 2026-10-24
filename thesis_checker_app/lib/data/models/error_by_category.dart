import 'package:thesis_checker/data/models/format_error_api.dart';
import 'package:thesis_checker/models/check_type_info.dart';

class ErrorsByCategory {
  final CheckCategory category;
  final int count;
  final List<FormatErrorApi> errors;

  const ErrorsByCategory({required this.category, required this.errors, int? count})
      : count = count ?? errors.length;
}
