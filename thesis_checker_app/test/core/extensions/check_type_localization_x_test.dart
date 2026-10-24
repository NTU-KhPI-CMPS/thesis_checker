import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:parameterized_test/parameterized_test.dart';
import 'package:thesis_checker/core/extensions/check_type_localization_x.dart';
import 'package:thesis_checker/l10n/l10n.dart';
import 'package:thesis_checker/models/check_type_info.dart';

CheckTypeInfo _checkType(CheckCategory category) =>
    CheckTypeInfo(category: category, checks: const [], iconPath: '');

Widget _buildApp({
  required String Function(BuildContext context) resolve,
  required Locale locale,
}) {
  return MaterialApp(
    locale: locale,
    localizationsDelegates: AppLocalizations.localizationsDelegates,
    supportedLocales: AppLocalizations.supportedLocales,
    home: Scaffold(
      body: Builder(builder: (context) => Text(resolve(context))),
    ),
  );
}

void main() {
  group('CheckTypeLocalizationX.localizedTitle', () {
    group('given localizations in the tree', () {
      parameterizedGroup(
        'when built with a locale and a category',
        [
          [
            const Locale('en'),
            CheckCategory.font,
            'Font',
          ],
          [
            const Locale('en'),
            CheckCategory.spacing,
            'Spacing',
          ],
          [
            const Locale('en'),
            CheckCategory.alignment,
            'Alignment',
          ],
          [
            const Locale('en'),
            CheckCategory.formula,
            'Formulas',
          ],
          [
            const Locale('en'),
            CheckCategory.list,
            'Lists',
          ],
          [
            const Locale('en'),
            CheckCategory.other,
            'Other',
          ],
          [
            const Locale('uk'),
            CheckCategory.font,
            'Шрифт',
          ],
          [
            const Locale('uk'),
            CheckCategory.spacing,
            'Інтервали',
          ],
          [
            const Locale('uk'),
            CheckCategory.alignment,
            'Вирівнювання',
          ],
          [
            const Locale('uk'),
            CheckCategory.formula,
            'Формули',
          ],
          [
            const Locale('uk'),
            CheckCategory.list,
            'Списки',
          ],
          [
            const Locale('uk'),
            CheckCategory.other,
            'Інші',
          ],
        ],
        (Locale locale, CheckCategory category, String text) {
          testWidgets('then shows the matching title', (
            WidgetTester tester,
          ) async {
            // Given
            final checkType = _checkType(category);

            // When
            await tester.pumpWidget(
              _buildApp(
                resolve: checkType.localizedTitle,
                locale: locale,
              ),
            );
            await tester.pumpAndSettle();

            // Then
            expect(find.text(text), findsOneWidget);
          });
        },
      );
    });
  });

  group('CheckTypeLocalizationX.localizedDescription', () {
    group('given localizations in the tree', () {
      parameterizedGroup(
        'when built with a locale and a category',
        [
          [
            const Locale('en'),
            CheckCategory.font,
            'Font name and size',
          ],
          [
            const Locale('en'),
            CheckCategory.spacing,
            'Line spacing and paragraph spacing',
          ],
          [
            const Locale('en'),
            CheckCategory.alignment,
            'Paragraph and heading alignment',
          ],
          [
            const Locale('en'),
            CheckCategory.formula,
            'Description, numbering, and spacing around formulas',
          ],
          [
            const Locale('en'),
            CheckCategory.list,
            'Nesting, markers, and numbering format at each level',
          ],
          [
            const Locale('en'),
            CheckCategory.other,
            'Other checks that do not fit into categories',
          ],
          [
            const Locale('uk'),
            CheckCategory.font,
            'Назва і розмір шрифту',
          ],
          [
            const Locale('uk'),
            CheckCategory.spacing,
            'Міжрядковий і відступи навколо абзаців',
          ],
          [
            const Locale('uk'),
            CheckCategory.alignment,
            'Вирівнювання абзаців і заголовків',
          ],
          [
            const Locale('uk'),
            CheckCategory.formula,
            'Опис, нумерація, відступи навколо формул',
          ],
          [
            const Locale('uk'),
            CheckCategory.list,
            'Вкладеність, маркери, формат нумарації на відповідному рівні',
          ],
          [
            const Locale('uk'),
            CheckCategory.other,
            'Інші перевірки, які не входять до категорій',
          ],
        ],
        (Locale locale, CheckCategory category, String text) {
          testWidgets('then shows the matching description', (
            WidgetTester tester,
          ) async {
            // Given
            final checkType = _checkType(category);

            // When
            await tester.pumpWidget(
              _buildApp(
                resolve: checkType.localizedDescription,
                locale: locale,
              ),
            );
            await tester.pumpAndSettle();

            // Then
            expect(find.text(text), findsOneWidget);
          });
        },
      );
    });
  });
}
