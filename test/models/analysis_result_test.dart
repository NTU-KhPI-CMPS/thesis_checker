import 'package:flutter_test/flutter_test.dart';
import 'package:thesis_checker/core/enums/check.dart';
import 'package:thesis_checker/data/models/error_by_category.dart';
import 'package:thesis_checker/data/models/format_error_api.dart';
import 'package:thesis_checker/models/analysis_result.dart';

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
  group('AnalysisResult', () {
    test('computes totalErrors based on category counts', () {
      final result = AnalysisResult(
        fileName: 'thesis.docx',
        analyzedAt: DateTime.parse('2026-04-16T12:00:00.000Z'),
        errorsByCategory: [
          ErrorsByCategory(
            category: 'Шрифт',
            errors: [
              _makeError('e-1', Check.fontName.name),
              _makeError('e-2', Check.fontSize.name),
            ],
          ),
          ErrorsByCategory(category: 'Інші', errors: const []),
        ],
      );

      expect(result.fileName, 'thesis.docx');
      expect(result.analyzedAt.toUtc().toIso8601String(), '2026-04-16T12:00:00.000Z');
      expect(result.errorsByCategory.length, 2);
      expect(result.errorsByCategory.first.errors.length, 2);
      expect(result.totalErrors, 2);
      expect(result.errorsByCategory.first.errors.first.category, Check.fontName.name);
    });

    test('returns zero totalErrors when no categories are present', () {
      final result = AnalysisResult(
        fileName: '',
        analyzedAt: DateTime.fromMillisecondsSinceEpoch(0),
        errorsByCategory: const [],
      );

      expect(result.fileName, '');
      expect(result.analyzedAt, DateTime.fromMillisecondsSinceEpoch(0));
      expect(result.errorsByCategory, isEmpty);
      expect(result.totalErrors, 0);
    });
  });
}
