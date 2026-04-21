import 'package:flutter_test/flutter_test.dart';
import 'package:thesis_checker/core/constants/available_check_types.dart';
import 'package:thesis_checker/core/enums/check.dart';
import 'package:thesis_checker/core/utils/check_type_grouping.dart';
import 'package:thesis_checker/data/models/format_error_api.dart';

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
  group('CheckTypeGrouping parsing', () {
    test('parseCheckFromCode returns null for null input', () {
      final parsed = CheckTypeGrouping.parseCheckFromCode(null);

      expect(parsed, isNull);
    });

    test('parseCheckFromCode parses known code case-insensitively', () {
      final parsed = CheckTypeGrouping.parseCheckFromCode('  font_name  ');

      expect(parsed, isNotNull);
      expect(parsed!.name, 'FONT_NAME');
    });

    test('parseCheckFromCode returns null for unknown code', () {
      final parsed = CheckTypeGrouping.parseCheckFromCode('UNKNOWN_CHECK');

      expect(parsed, isNull);
    });
  });

  group('CheckTypeGrouping aggregation', () {
    test('resolveTypeByError maps known checks to font group', () {
      final fontNameType = CheckTypeGrouping.resolveTypeByError(
        makeError(Check.fontName.name),
      );
      final fontSizeType = CheckTypeGrouping.resolveTypeByError(
        makeError(Check.fontSize.name),
      );

      expect(fontNameType.title, 'Шрифт');
      expect(fontSizeType.title, 'Шрифт');
    });

    test('resolveTypeByError maps unknown checks to other group', () {
      final type = CheckTypeGrouping.resolveTypeByError(
        makeError('SOMETHING_ELSE'),
      );

      expect(type.title, 'Інші');
    });

    test('countErrorsByType returns full map with counts', () {
      final counts = CheckTypeGrouping.countErrorsByType([
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
      final counts = CheckTypeGrouping.countErrorsByType(const []);

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

      final filtered = CheckTypeGrouping.filterErrorsByType(errors, fontType);

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

      final filtered = CheckTypeGrouping.filterErrorsByType(errors, otherType);

      expect(filtered.length, 1);
      expect(filtered.first.category, 'SOMETHING_ELSE');
    });
  });
}
