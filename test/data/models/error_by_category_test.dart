import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_app/core/enums/check.dart';
import 'package:flutter_app/data/models/error_by_category.dart';

void main() {
  group('ErrorsByCategory.fromJson', () {
    test('parses category errors and defaults count to errors length', () {
      final category = ErrorsByCategory.fromJson({
        'category': 'Шрифт',
        'errors': [
          {
            'id': 'e-1',
            'category': Check.fontName.name,
            'expected': 'Times New Roman',
            'found': 'Arial',
            'severity': 'error',
            'title': 'Невірні шрифти у параграфі',
          },
          {
            'id': 'e-2',
            'category': Check.fontSize.name,
            'expected': '14',
            'found': '12',
            'severity': 'error',
            'title': 'Невірний розмір шрифту',
          },
        ],
      });

      expect(category.category, 'Шрифт');
      expect(category.count, 2);
      expect(category.errors.length, 2);
      expect(category.errors.first.category, Check.fontName.name);
      expect(category.errors.last.category, Check.fontSize.name);
    });

    test('uses provided count when present in payload', () {
      final category = ErrorsByCategory.fromJson({
        'category': 'Шрифт',
        'count': 7,
        'errors': [
          {
            'id': 'e-1',
            'category': Check.fontName.name,
            'expected': 'Times New Roman',
            'found': 'Arial',
            'severity': 'error',
            'title': 'Невірні шрифти у параграфі',
          }
        ],
      });

      expect(category.count, 7);
      expect(category.errors.length, 1);
    });
  });
}
