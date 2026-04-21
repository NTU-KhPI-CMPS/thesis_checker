import 'package:flutter_test/flutter_test.dart';
import 'package:thesis_checker/core/enums/check.dart';
import 'package:thesis_checker/data/models/format_error_api.dart';

void main() {
  group('FormatErrorApi.fromJson', () {
    test('casts scalar found value to string', () {
      final error = FormatErrorApi.fromJson({
        'found': 12,
      });

      expect(error.found, '12');
    });

    test('joins list values in found field from API payload', () {
      final error = FormatErrorApi.fromJson({
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
      final error = FormatErrorApi.fromJson({
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
