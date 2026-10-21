import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:parameterized_test/parameterized_test.dart';
import 'package:thesis_checker/l10n/error_message_resolvers.dart';
import 'package:thesis_checker/l10n/l10n.dart';

void main() {
  group('localizeByErrorId', () {
    group('given a known error id', () {
      parameterizedTest(
        'when localized, then returns the matching translation',
        [
          [
            'err_figure_alignment',
            (AppLocalizations l) => l.errFigureAlignment,
          ],
          [
            'err_list_format',
            (AppLocalizations l) => l.errListFormat
          ],
          [
            'err_font_header',
            (AppLocalizations l) => l.errFontHeader
          ],
          [
            'err_font_footer',
            (AppLocalizations l) => l.errFontFooter
          ],
          [
            'err_formula_sequence',
            (AppLocalizations l) => l.errFormulaSequence,
          ],
        ],
        (String id, String Function(AppLocalizations) expected) {
          // Given
          final l10n = lookupAppLocalizations(const Locale('en'));

          // When
          final result = localizeByErrorId(l10n, id);

          // Then
          expect(result, expected(l10n));
        },
      );
    });

    group('given an unknown error id', () {
      parameterizedTest(
        'when localized, then returns the id itself',
        ['err_unknown_xyz', '', 'ERR_FONT'],
        (String id) {
          // Given
          final l10n = lookupAppLocalizations(const Locale('en'));

          // When
          final result = localizeByErrorId(l10n, id);

          // Then
          expect(result, id);
        },
      );
    });
  });
}
