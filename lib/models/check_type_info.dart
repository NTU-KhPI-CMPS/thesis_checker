import 'package:thesis_checker/core/enums/check.dart';

/// UI model for cards describing available check types.
class CheckTypeInfo {
  final String title;
  final String description;
  final List<Check> checks;
  final String iconPath;

  const CheckTypeInfo({
    required this.title,
    required this.description,
    required this.checks,
    required this.iconPath,
  });
}
