import 'package:equatable/equatable.dart';
import 'package:thesis_checker/core/enums/check.dart';

/// UI model for cards describing available check types.
class CheckTypeInfo extends Equatable{
  final String title;
  final String description;
  final List<Check> checks;
  final String iconPath;

  /// Marks the category that unknown/unmatched check codes fall back to.
  final bool isFallback;

  const CheckTypeInfo({
    required this.title,
    required this.description,
    required this.checks,
    required this.iconPath,
    this.isFallback = false,
  });

  @override
  List<Object?> get props => [title, description, checks, iconPath];
}
