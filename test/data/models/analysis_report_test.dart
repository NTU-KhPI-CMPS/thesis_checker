import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_app/core/enums/check.dart';
import 'package:flutter_app/data/models/analysis_report.dart';

void main() {
  group('AnalysisReport.fromJson', () {
    test('parses flat errors array from API payload', () {
      final report = AnalysisReport.fromJson({
        'errors': [
          {
            'category': Check.fontName.name,
            'expected': 'Times New Roman',
            'found': ['Cambria Math'],
            'id': 'err_font',
            'paragraphText': 'Sample paragraph 1',
            'severity': 'error',
            'title': 'Невірні шрифти у параграфі',
          },
          {
            'category': Check.fontName.name,
            'expected': 'Times New Roman',
            'found': ['Cambria Math'],
            'id': 'err_font',
            'paragraphText': 'Sample paragraph 2',
            'severity': 'error',
            'title': 'Невірні шрифти у параграфі',
          },
          {
            'category': Check.fontName.name,
            'expected': 'Times New Roman',
            'found': ['Cambria Math'],
            'id': 'err_font',
            'paragraphText': 'Sample paragraph 3',
            'severity': 'error',
            'title': 'Невірні шрифти у параграфі',
          },
        ],
      });

      expect(report.errors.length, 3);
      expect(report.errors.every((error) => error.category == Check.fontName.name), true);
      expect(report.errors.every((error) => error.found == 'Cambria Math'), true);
      expect(report.errors.every((error) => error.id == 'err_font'), true);
    });

    test('returns empty list when errors are missing', () {
      final report = AnalysisReport.fromJson({});

      expect(report.errors, isEmpty);
    });
  });
}
