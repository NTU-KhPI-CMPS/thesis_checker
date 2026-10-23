import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:parameterized_test/parameterized_test.dart';
import 'package:thesis_checker/core/extensions/error_localization_x.dart';
import 'package:thesis_checker/data/models/format_error_api.dart';
import 'package:thesis_checker/l10n/l10n.dart';

FormatErrorApi _error(String id) => FormatErrorApi(
  id: id,
  category: '',
  expected: '',
  found: '',
  severity: '',
  title: '',
);

Widget _buildApp({
  required FormatErrorApi error,
  Locale locale = const Locale('en'),
  bool withLocalizations = true,
}) {
  return MaterialApp(
    locale: locale,
    localizationsDelegates: withLocalizations
        ? AppLocalizations.localizationsDelegates
        : null,
    supportedLocales: AppLocalizations.supportedLocales,
    home: Scaffold(
      body: Builder(
        builder: (context) => Text(error.getLocalizedMessage(context)),
      ),
    ),
  );
}

Widget _buildValueApp({
  required String Function(BuildContext context) resolve,
  Locale locale = const Locale('en'),
  bool withLocalizations = true,
}) {
  return MaterialApp(
    locale: locale,
    localizationsDelegates: withLocalizations
        ? AppLocalizations.localizationsDelegates
        : null,
    supportedLocales: AppLocalizations.supportedLocales,
    home: Scaffold(
      body: Builder(builder: (context) => Text(resolve(context))),
    ),
  );
}

FormatErrorApi _valueError({String found = '', String expected = ''}) =>
  FormatErrorApi(
    id: '',
    category: '',
    expected: expected,
    found: found,
    severity: '',
    title: '',
  );

void main() {
  group('ErrorLocalizationX.getLocalizedMessage', () {
    group('given localizations in the tree', () {
      parameterizedGroup(
        'when built with a locale',
        [
          [
            const Locale('en'),
            'err_font_header',
            'Incorrect fonts in header'
          ],
          [
            const Locale('en'),
            'err_font_footer',
            'Incorrect fonts in footer'
          ],
          [
            const Locale('en'),
            'err_list_format',
            'Inconsistent numbering format on one list level',
          ],
          [
            const Locale('uk'),
            'err_font_header',
            'Неправильні шрифти в верхньому колонтитулі',
          ],
          [
            const Locale('uk'),
            'err_font_footer',
            'Неправильні шрифти в нижньому колонтитулі',
          ],
          [
            const Locale('uk'),
            'err_list_format',
            'Невідповідний формат нумерації на одному рівні списку',
          ],
        ],
        (Locale locale, String id, String text) {
          testWidgets('then shows the matching translation', (
            WidgetTester tester,
          ) async {
            // Given
            final error = _error(id);

            // When
            await tester.pumpWidget(_buildApp(error: error, locale: locale));
            await tester.pumpAndSettle();

            // Then
            expect(find.text(text), findsOneWidget);
          });
        },
      );

      group('when locale changes dynamically', () {
        testWidgets('then updates the message to the new language', (
          WidgetTester tester,
        ) async {
          // Given
          final error = _error('err_font_header');
          await tester.pumpWidget(
            _buildApp(error: error, locale: const Locale('en')),
          );
          await tester.pumpAndSettle();
          expect(find.text('Incorrect fonts in header'), findsOneWidget);

          // When
          await tester.pumpWidget(
            _buildApp(error: error, locale: const Locale('uk')),
          );
          await tester.pumpAndSettle();

          // Then
          expect(
            find.text('Неправильні шрифти в верхньому колонтитулі'),
            findsOneWidget,
          );
          expect(find.text('Incorrect fonts in header'), findsNothing);
        });
      });
    });

    group('given no localizations in the tree', () {
      parameterizedGroup(
        'when built with an error id',
        ['err_font_header', 'err_unknown_xyz'],
        (String id) {
          testWidgets('then falls back to showing the id itself', (
            WidgetTester tester,
          ) async {
            // Given
            final error = _error(id);

            // When
            await tester.pumpWidget(
              _buildApp(error: error, withLocalizations: false),
            );
            await tester.pumpAndSettle();

            // Then
            expect(find.text(id), findsOneWidget);
          });
        },
      );
    });
  });

  group('ErrorLocalizationX.getLocalizedFound', () {
    group('given localizations in the tree', () {
      parameterizedGroup(
        'when built with a locale',
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
            'По лівому краю',
            'Left',
          ],
          [
            const Locale('en'),
            'BOTH, RIGHT',
            'Justified, Right',
          ],
          [
            const Locale('en'),
            'LEFT, Custom',
            'Left, Custom',
          ],
          [
            const Locale('en'),
            '1.3 см',
            '1.3 cm',
          ],
          [
            const Locale('uk'),
            '1.3 см',
            '1.3 см',
          ],
          [
            const Locale('en'),
            'ВСТУП, ВИСНОВКИ',
            'INTRODUCTION (ВСТУП), CONCLUSIONS (ВИСНОВКИ)',
          ],
          [
            const Locale('en'),
            'немає пустого рядка перед',
            'no blank line before',
          ],
          [
            const Locale('en'),
            'Times New Roman',
            'Times New Roman',
          ],
        ],
        (Locale locale, String found, String text) {
          testWidgets('then shows the matching translation', (
            WidgetTester tester,
          ) async {
            // Given
            final error = _valueError(found: found);

            // When
            await tester.pumpWidget(
              _buildValueApp(resolve: error.getLocalizedFound, locale: locale),
            );
            await tester.pumpAndSettle();

            // Then
            expect(find.text(text), findsOneWidget);
          });
        },
      );

      group('when locale changes dynamically', () {
        testWidgets('then updates the found value to the new language', (
          WidgetTester tester,
        ) async {
          // Given
          final error = _valueError(found: 'LEFT');
          await tester.pumpWidget(
            _buildValueApp(
              resolve: error.getLocalizedFound,
              locale: const Locale('en'),
            ),
          );
          await tester.pumpAndSettle();
          expect(find.text('Left'), findsOneWidget);

          // When
          await tester.pumpWidget(
            _buildValueApp(
              resolve: error.getLocalizedFound,
              locale: const Locale('uk'),
            ),
          );
          await tester.pumpAndSettle();

          // Then
          expect(find.text('По лівому краю'), findsOneWidget);
          expect(find.text('Left'), findsNothing);
        });
      });
    });

    group('given no localizations in the tree', () {
      parameterizedGroup(
        'when built with a found value',
        ['LEFT', 'Times New Roman'],
        (String found) {
          testWidgets('then falls back to showing the raw value', (
            WidgetTester tester,
          ) async {
            // Given
            final error = _valueError(found: found);

            // When
            await tester.pumpWidget(
              _buildValueApp(
                resolve: error.getLocalizedFound,
                withLocalizations: false,
              ),
            );
            await tester.pumpAndSettle();

            // Then
            expect(find.text(found), findsOneWidget);
          });
        },
      );
    });
  });

  group('ErrorLocalizationX.getLocalizedExpected', () {
    group('given localizations in the tree', () {
      parameterizedGroup(
        'when built with a locale',
        [
          [
            const Locale('en'),
            'consistent',
            'Consistent format',
          ],
          [
            const Locale('uk'),
            'consistent',
            'Однаковий формат',
          ],
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
            '«Рисунок <номер> - <назва>»',
            '«Figure <number> - <title>»',
          ],
          [
            const Locale('uk'),
            '«Рисунок <номер> - <назва>»',
            '«Рисунок <номер> - <назва>»',
          ],
          [
            const Locale('en'),
            'Порожній рядок перед формулою',
            'Blank line before the formula',
          ],
          [
            const Locale('en'),
            '14.0pt',
            '14.0pt',
          ],
        ],
        (Locale locale, String expected, String text) {
          testWidgets('then shows the matching translation', (
            WidgetTester tester,
          ) async {
            // Given
            final error = _valueError(expected: expected);

            // When
            await tester.pumpWidget(
              _buildValueApp(
                resolve: error.getLocalizedExpected,
                locale: locale,
              ),
            );
            await tester.pumpAndSettle();

            // Then
            expect(find.text(text), findsOneWidget);
          });
        },
      );

      group('when locale changes dynamically', () {
        testWidgets('then updates the expected value to the new language', (
          WidgetTester tester,
        ) async {
          // Given
          final error = _valueError(expected: 'consistent');
          await tester.pumpWidget(
            _buildValueApp(
              resolve: error.getLocalizedExpected,
              locale: const Locale('en'),
            ),
          );
          await tester.pumpAndSettle();
          expect(find.text('Consistent format'), findsOneWidget);

          // When
          await tester.pumpWidget(
            _buildValueApp(
              resolve: error.getLocalizedExpected,
              locale: const Locale('uk'),
            ),
          );
          await tester.pumpAndSettle();

          // Then
          expect(find.text('Однаковий формат'), findsOneWidget);
          expect(find.text('Consistent format'), findsNothing);
        });
      });
    });

    group('given no localizations in the tree', () {
      parameterizedGroup(
        'when built with an expected value',
        ['consistent', '14.0pt'],
        (String expected) {
          testWidgets('then falls back to showing the raw value', (
            WidgetTester tester,
          ) async {
            // Given
            final error = _valueError(expected: expected);

            // When
            await tester.pumpWidget(
              _buildValueApp(
                resolve: error.getLocalizedExpected,
                withLocalizations: false,
              ),
            );
            await tester.pumpAndSettle();

            // Then
            expect(find.text(expected), findsOneWidget);
          });
        },
      );
    });
  });
}
