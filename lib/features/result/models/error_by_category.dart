class ErrorsByCategory {
  final String category;
  final int count;

  const ErrorsByCategory({
    required this.category,
    required this.count,
  });

  factory ErrorsByCategory.fromJson(Map<String, dynamic> json) {
    return ErrorsByCategory(
      category: json['category']?.toString() ?? '',
      count: (json['count'] as num?)?.toInt() ?? 0,
    );
  }
}
