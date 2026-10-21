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
}
