import 'package:equatable/equatable.dart';
import 'package:thesis_checker/core/enums/check.dart';

/// Enum representing different categories of checks.
enum CheckCategory { font, spacing, alignment, formula, list, other }

/// UI model for cards describing available check types.
class CheckTypeInfo extends Equatable {
  final CheckCategory category;
  final List<Check> checks;
  final String iconPath;

  const CheckTypeInfo({
    required this.category,
    required this.checks,
    required this.iconPath,
  });

  @override
  List<Object?> get props => [category, checks, iconPath];
}
