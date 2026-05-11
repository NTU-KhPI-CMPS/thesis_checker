class FormatErrorApi {
  final String id;
  final String category;
  final String expected;
  final String found;
  final String? paragraphText;
  final String severity;
  final String title;

  const FormatErrorApi({
    required this.id,
    required this.category,
    required this.expected,
    required this.found,
    this.paragraphText,
    required this.severity,
    required this.title,
  });

  factory FormatErrorApi.fromJson(Map<String, dynamic> json) {
    final rawFound = json['found'];
    final foundValue = rawFound is List<dynamic>
        ? rawFound.map((value) => value.toString()).join(', ')
        : rawFound?.toString() ?? '';

    return FormatErrorApi(
      id: json['id']?.toString() ?? '',
      category: json['category']?.toString() ?? '',
      expected: json['expected']?.toString() ?? '',
      found: foundValue,
      paragraphText: json['paragraphText']?.toString(),
      severity: json['severity']?.toString() ?? '',
      title: json['title']?.toString() ?? '',
    );
  }
}
