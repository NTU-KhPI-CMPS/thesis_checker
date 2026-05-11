import 'package:flutter_test/flutter_test.dart';
import 'package:thesis_checker/core/enums/check.dart';
import 'package:thesis_checker/data/models/error_by_category.dart';
import 'package:thesis_checker/data/models/format_error_api.dart';

FormatErrorApi _makeError(String id, String category) {
  return FormatErrorApi(
    id: id,
    category: category,
    expected: 'expected',
    found: 'found',
    severity: 'error',
    title: 'title',
  );
}

void main() {
  group('ErrorsByCategory', () {
    test('defaults count to errors length when count is not provided', () {
      final category = ErrorsByCategory(
        category: 'Шрифт',
        errors: [
          _makeError('e-1', Check.fontName.name),
          _makeError('e-2', Check.fontSize.name),
        ],
      );

      expect(category.category, 'Шрифт');
      expect(category.count, 2);
      expect(category.errors.length, 2);
      expect(category.errors.first.category, Check.fontName.name);
      expect(category.errors.last.category, Check.fontSize.name);
    });

    test('uses provided count when explicitly set', () {
      final category = ErrorsByCategory(
        category: 'Шрифт',
        count: 7,
        errors: [_makeError('e-1', Check.fontName.name)],
      );

      expect(category.count, 7);
      expect(category.errors.length, 1);
    });
  });
}
