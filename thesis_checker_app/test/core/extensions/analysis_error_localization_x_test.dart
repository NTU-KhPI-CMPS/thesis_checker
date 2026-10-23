import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:parameterized_test/parameterized_test.dart';
import 'package:thesis_checker/core/errors/analysis_exception.dart';
import 'package:thesis_checker/core/extensions/analysis_error_localization_x.dart';
import 'package:thesis_checker/l10n/l10n.dart';

Widget _buildApp({
  required AnalysisErrorType errorType,
  Locale locale = const Locale('en'),
}) {
  return MaterialApp(
    locale: locale,
    localizationsDelegates: AppLocalizations.localizationsDelegates,
    supportedLocales: AppLocalizations.supportedLocales,
    home: Scaffold(
      body: Builder(
        builder: (context) => Text(errorType.localizedErrorText(context)),
      ),
    ),
  );
}

void main() {
  group('AnalysisErrorLocalizationX.localizedErrorText', () {
    group('given localizations in the tree', () {
      parameterizedGroup(
        'when built with a locale and an error type',
        [
          [
            const Locale('en'),
            AnalysisErrorType.finishedWithError,
            'Analysis finished with an error',
          ],
          [
            const Locale('en'),
            AnalysisErrorType.couldNotComplete,
            'Could not complete the analysis. Please try again.',
          ],
          [
            const Locale('uk'),
            AnalysisErrorType.finishedWithError,
            'Аналіз завершився з помилкою',
          ],
          [
            const Locale('uk'),
            AnalysisErrorType.couldNotComplete,
            'Не вдалося виконати аналіз. Спробуйте ще раз.',
          ],
        ],
        (Locale locale, AnalysisErrorType errorType, String text) {
          testWidgets('then shows the matching translation', (
            WidgetTester tester,
          ) async {
            // Given
            final app = _buildApp(errorType: errorType, locale: locale);

            // When
            await tester.pumpWidget(app);
            await tester.pumpAndSettle();

            // Then
            expect(find.text(text), findsOneWidget);
          });
        },
      );

      group('when locale changes dynamically', () {
        testWidgets('then updates the text to the new language', (
          WidgetTester tester,
        ) async {
          // Given
          await tester.pumpWidget(
            _buildApp(
              errorType: AnalysisErrorType.finishedWithError,
              locale: const Locale('en'),
            ),
          );
          await tester.pumpAndSettle();
          expect(find.text('Analysis finished with an error'), findsOneWidget);

          // When
          await tester.pumpWidget(
            _buildApp(
              errorType: AnalysisErrorType.finishedWithError,
              locale: const Locale('uk'),
            ),
          );
          await tester.pumpAndSettle();

          // Then
          expect(find.text('Аналіз завершився з помилкою'), findsOneWidget);
          expect(find.text('Analysis finished with an error'), findsNothing);
        });
      });
    });
  });
}
