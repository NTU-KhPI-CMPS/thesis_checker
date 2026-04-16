import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_app/core/enums/check.dart';
import 'package:flutter_app/data/models/found_error.dart';

void main() {
  group('FoundError.fromJson', () {
    test('casts scalar found value to string', () {
      final error = FoundError.fromJson({
        'found': 12,
      });

      expect(error.found, '12');
    });

    test('joins list values in found field from API payload', () {
      final error = FoundError.fromJson({
        'id': 'err_font',
        'category': Check.fontName.name,
        'expected': 'Times New Roman',
        'found': ['Cambria Math'],
        'paragraphText': 'Sample paragraph',
        'severity': 'error',
        'title': 'Невірні шрифти у параграфі',
      });

      expect(error.id, 'err_font');
      expect(error.category, Check.fontName.name);
      expect(error.expected, 'Times New Roman');
      expect(error.found, 'Cambria Math');
      expect(error.paragraphText, 'Sample paragraph');
      expect(error.severity, 'error');
      expect(error.title, 'Невірні шрифти у параграфі');
    });

    test('uses defaults when fields are missing', () {
      final error = FoundError.fromJson({
        'found': null,
      });

      expect(error.id, '');
      expect(error.category, '');
      expect(error.expected, '');
      expect(error.found, '');
      expect(error.paragraphText, isNull);
      expect(error.severity, '');
      expect(error.title, '');
    });
  });
}
