import 'package:flutter_test/flutter_test.dart';
import 'package:thesis_checker/core/constants/available_check_types.dart';
import 'package:thesis_checker/core/enums/check.dart';
import 'package:thesis_checker/data/models/format_error_api.dart';
import 'package:thesis_checker/data/repositories/analysis_repository.dart';

FormatErrorApi makeError(String category) {
  return FormatErrorApi(
    id: 'id-$category',
    category: category,
    expected: 'expected',
    found: 'found',
    severity: 'error',
    title: 'title',
  );
}

void main() {
  group('AnalysisRepository parsing', () {
    test('parseCheckFromCode returns null for null input', () {
      final parsed = AnalysisRepository.parseCheckFromCode(null);

      expect(parsed, isNull);
    });

    test('parseCheckFromCode parses known code case-insensitively', () {
      final parsed = AnalysisRepository.parseCheckFromCode('  font_name  ');

      expect(parsed, isNotNull);
      expect(parsed!.name, 'FONT_NAME');
    });

    test('parseCheckFromCode returns null for unknown code', () {
      final parsed = AnalysisRepository.parseCheckFromCode('UNKNOWN_CHECK');

      expect(parsed, isNull);
    });
  });

  group('AnalysisRepository aggregation', () {
    test('resolveTypeByError maps known checks to font group', () {
      final fontNameType = AnalysisRepository.resolveTypeByError(
        makeError(Check.fontName.name),
      );
      final fontSizeType = AnalysisRepository.resolveTypeByError(
        makeError(Check.fontSize.name),
      );

      expect(fontNameType.title, 'Шрифт');
      expect(fontSizeType.title, 'Шрифт');
    });

    test('resolveTypeByError maps unknown checks to other group', () {
      final type = AnalysisRepository.resolveTypeByError(
        makeError('SOMETHING_ELSE'),
      );

      expect(type.title, 'Інші');
    });

    test('countErrorsByType returns full map with counts', () {
      final counts = AnalysisRepository.countErrorsByType([
        makeError(Check.fontName.name),
        makeError(Check.fontSize.name),
        makeError('SOMETHING_ELSE'),
      ]);

      expect(counts['Шрифт'], 2);
      expect(counts['Інші'], 1);
      expect(
        counts.keys.toSet(),
        AvailableCheckTypes.checkTypes.map((type) => type.title).toSet(),
      );
    });

    test('countErrorsByType returns zeros for empty list', () {
      final counts = AnalysisRepository.countErrorsByType(const []);

      expect(counts['Шрифт'], 0);
      expect(counts['Інші'], 0);
    });

    test('filterErrorsByType returns only errors for selected group', () {
      final errors = [
        makeError(Check.fontName.name),
        makeError('SOMETHING_ELSE'),
        makeError(Check.fontSize.name),
      ];
      final fontType = AvailableCheckTypes.checkTypes.firstWhere(
        (type) => type.title == 'Шрифт',
      );

      final filtered = AnalysisRepository.filterErrorsByType(errors, fontType);

      expect(filtered.length, 2);
      expect(filtered.every((error) => error.category.startsWith('FONT_')), true);
    });

    test('filterErrorsByType returns unknown checks for other group', () {
      final errors = [
        makeError(Check.fontName.name),
        makeError('SOMETHING_ELSE'),
      ];
      final otherType = AvailableCheckTypes.checkTypes.firstWhere(
        (type) => type.title == 'Інші',
      );

      final filtered = AnalysisRepository.filterErrorsByType(errors, otherType);

      expect(filtered.length, 1);
      expect(filtered.first.category, 'SOMETHING_ELSE');
    });
  });
}