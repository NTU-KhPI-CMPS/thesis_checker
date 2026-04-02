class HighlightError {
  final int start;
  final int end;

  const HighlightError({
    required this.start,
    required this.end,
  });

  factory HighlightError.fromJson(Map<String, dynamic> json) {
    return HighlightError(
      start: (json['start'] as num?)?.toInt() ?? 0,
      end: (json['end'] as num?)?.toInt() ?? 0,
    );
  }
}
