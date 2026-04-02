import 'package:flutter_app/features/result/models/highlight_error.dart';

class FoundError {
  final String id;
  final String category;
  final String title;
  final int? paragraphIndex;
  final String? paragraphText;
  final HighlightError? highlightError;
  final String found;
  final String expected;
  final List<String> suggestions;

  const FoundError({
    required this.id,
    required this.category,
    required this.title,
    this.paragraphIndex,
    this.paragraphText,
    this.highlightError,
    required this.found,
    required this.expected,
    required this.suggestions,
  });

  factory FoundError.fromJson(Map<String, dynamic> json) {
    return FoundError(
      id: json['id']?.toString() ?? '',
      category: json['category']?.toString() ?? '',
      title: json['title']?.toString() ?? '',
      paragraphIndex: (json['paragraphIndex'] as num?)?.toInt(),
      paragraphText: json['paragraphText'] as String?,
      highlightError: json['highlightError'] == null
          ? null
          : HighlightError.fromJson(
              Map<String, dynamic>.from(json['highlightError'] as Map),
            ),
      found: json['found']?.toString() ?? '',
      expected: json['expected']?.toString() ?? '',
      suggestions: (json['suggestions'] as List<dynamic>? ?? const [])
          .map((item) => item.toString())
          .toList(),
    );
  }
}
