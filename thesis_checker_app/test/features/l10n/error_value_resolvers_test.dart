import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:parameterized_test/parameterized_test.dart';
import 'package:thesis_checker/l10n/error_value_resolvers.dart';
import 'package:thesis_checker/l10n/l10n.dart';

void main() {
  group('localizeErrorValue', () {
    group('given a single recognized value', () {
      parameterizedTest(
        'when localized, then returns the matching translation',
        [
          [
            const Locale('en'),
            'LEFT',
            'Left',
          ],
          [
            const Locale('uk'),
            'LEFT',
            'По лівому краю',
          ],
          [
            const Locale('en'),
            '  LEFT  ',
            'Left',
          ],
        ],
        (Locale locale, String value, String expected) {
          // Given
          final l10n = lookupAppLocalizations(locale);

          // When
          final result = localizeErrorValue(l10n, value);

          // Then
          expect(result, expected);
        },
      );
    });

    group('given a length in centimeters', () {
      parameterizedTest(
        'when localized, then replaces only the unit',
        [
          [
            const Locale('en'),
            '1.3 см',
            '1.3 cm',
          ],
          [
            const Locale('en'),
            '0 см',
            '0 cm',
          ],
          [
            const Locale('en'),
            '-1.5 см',
            '-1.5 cm',
          ],
          [
            const Locale('en'),
            '1,3 см',
            '1,3 cm',
          ],
          [
            const Locale('en'),
            '1.3см',
            '1.3 cm',
          ],
          [
            const Locale('uk'),
            '1.3 см',
            '1.3 см',
          ],
        ],
        (Locale locale, String value, String expected) {
          // Given
          final l10n = lookupAppLocalizations(locale);

          // When
          final result = localizeErrorValue(l10n, value);

          // Then
          expect(result, expected);
        },
      );
    });

    group('given a value made of several parts', () {
      parameterizedTest(
        'when localized, then translates each part and keeps the separators',
        [
          [
            const Locale('en'),
            'BOTH, RIGHT',
            'Justified, Right',
          ],
          [
            const Locale('uk'),
            'BOTH, RIGHT',
            'По ширині, По правому краю',
          ],
          [
            const Locale('en'),
            'ВСТУП -> ВИСНОВКИ',
            'INTRODUCTION (ВСТУП) -> CONCLUSIONS (ВИСНОВКИ)',
          ],
          [
            const Locale('en'),
            'ВСТУП, ЗМІСТ -> ВИСНОВКИ',
            'INTRODUCTION (ВСТУП), TABLE OF CONTENTS (ЗМІСТ) -> '
                'CONCLUSIONS (ВИСНОВКИ)',
          ],
          [
            const Locale('en'),
            '1.3 см, 2 см',
            '1.3 cm, 2 cm',
          ],
          [
            const Locale('en'),
            'LEFT, Custom',
            'Left, Custom',
          ],
        ],
        (Locale locale, String value, String expected) {
          // Given
          final l10n = lookupAppLocalizations(locale);

          // When
          final result = localizeErrorValue(l10n, value);

          // Then
          expect(result, expected);
        },
      );
    });

    group('given an unrecognized value', () {
      parameterizedTest(
        'when localized, then returns the value unchanged',
        ['Times New Roman', '14.0pt', 'left', 'см', '1.3 смx'],
        (String value) {
          // Given
          final l10n = lookupAppLocalizations(const Locale('en'));

          // When
          final result = localizeErrorValue(l10n, value);

          // Then
          expect(result, value);
        },
      );
    });

    group('given an empty or blank value', () {
      parameterizedTest(
        'when localized, then returns the value unchanged',
        ['', '   '],
        (String value) {
          // Given
          final l10n = lookupAppLocalizations(const Locale('en'));

          // When
          final result = localizeErrorValue(l10n, value);

          // Then
          expect(result, value);
        },
      );
    });
  });

  group('errorValueResolvers', () {
    parameterizedTest(
      'when every entry is resolved, then returns a non-empty translation',
      [const Locale('en'), const Locale('uk')],
      (Locale locale) {
        // Given
        final l10n = lookupAppLocalizations(locale);

        // When
        final translations = errorValueResolvers.values.map(
          (resolve) => resolve(l10n),
        );

        // Then
        expect(translations, everyElement(isNotEmpty));
      },
    );
  });
}
