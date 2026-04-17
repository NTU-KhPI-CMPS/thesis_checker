import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_app/core/enums/check.dart';
import 'package:flutter_app/models/analysis_result.dart';

void main() {
  group('AnalysisResult.fromJson', () {
    test('parses categorized errors and computes totalErrors', () {
      final result = AnalysisResult.fromJson({
        'fileName': 'thesis.docx',
        'analyzedAt': '2026-04-16T12:00:00.000Z',
        'errorsByCategory': [
          {
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
          },
          {
            'category': 'Інші',
            'errors': const [],
          },
        ],
      });

      expect(result.fileName, 'thesis.docx');
      expect(result.analyzedAt.toUtc().toIso8601String(), '2026-04-16T12:00:00.000Z');
      expect(result.errorsByCategory.length, 2);
      expect(result.errorsByCategory.first.errors.length, 2);
      expect(result.totalErrors, 2);
      expect(result.errorsByCategory.first.errors.first.category, Check.fontName.name);
    });

    test('uses defaults for missing fields and invalid date', () {
      final result = AnalysisResult.fromJson({
        'analyzedAt': 'invalid-date',
      });

      expect(result.fileName, '');
      expect(result.analyzedAt, DateTime.fromMillisecondsSinceEpoch(0));
      expect(result.errorsByCategory, isEmpty);
      expect(result.totalErrors, 0);
    });
  });
}
