class FoundError {
  final String id;
  final String category;
  final String expected;
  final String found;
  final String? paragraphText;
  final String severity;
  final String title;

  const FoundError({
    required this.id,
    required this.category,
    required this.expected,
    required this.found,
    this.paragraphText,
    required this.severity,
    required this.title,
  });

  factory FoundError.fromJson(Map<String, dynamic> json) {
    return FoundError(
      id: json['id']?.toString() ?? '',
      category: json['category']?.toString() ?? '',
      expected: json['expected']?.toString() ?? '',
      found: json['found']?.toString() ?? '',
      paragraphText: json['paragraphText'] as String?,
      severity: json['severity']?.toString() ?? '',
      title: json['title']?.toString() ?? '',
    );
  }
}
