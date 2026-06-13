class UkrainePlural {
  static String ukrainianPlural(int count, String one, String few, String many) {
    final normalized = count.abs() % 100;
    final lastDigit = normalized % 10;

    if (normalized > 10 && normalized < 20) {
      return many;
    }

    if (lastDigit > 1 && lastDigit < 5) {
      return few;
    }

    if (lastDigit == 1) {
      return one;
    }

    return many;
  }

  static String formatErrorCount(int count) {
    final word = ukrainianPlural(count, 'помилка', 'помилки', 'помилок');
    return '$count $word';
  }
}
