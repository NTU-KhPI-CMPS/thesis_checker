import 'package:flutter/material.dart';
import 'package:thesis_checker/core/constants/available_check_types.dart';
import 'package:thesis_checker/core/constants/app_colors.dart';
import 'package:thesis_checker/core/widgets/checkbox_container.dart';
import 'package:thesis_checker/core/widgets/hint_text.dart';
import 'package:thesis_checker/features/home/widgets/custom_animated_button.dart';
import 'package:thesis_checker/features/result/widgets/error_count_badge.dart';
import 'package:thesis_checker/features/result/widgets/error_detail_expandable_card.dart';
import 'package:thesis_checker/core/widgets/info_card.dart';
import 'package:thesis_checker/core/widgets/info_text.dart';
import 'package:thesis_checker/features/result/cubit/result_cubit.dart';
import 'package:thesis_checker/features/result/cubit/result_state.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:thesis_checker/features/analysis_options_dialog/analysis_options_dialog.dart';

class ResultView extends StatefulWidget {
  const ResultView({super.key});

  @override
  State<ResultView> createState() => _ResultViewState();
}

class _ResultViewState extends State<ResultView> {
  int activeCategoryIndex = 0;
  bool buttonIsHovered = false;

  @override
  Widget build(BuildContext context) {
    final accentColor = Theme.of(context).primaryColor;
    final isLightTheme = Theme.of(context).brightness == Brightness.light;
    final fileNameTextColor = Theme.of(context).textTheme.bodyLarge?.color;
    final subTextColor = Theme.of(context).textTheme.bodyMedium?.color;
    final activeTextColor = isLightTheme ? AppColors.errorDark : AppColors.errorLight;
    final warnColor = AppColors.warn;

    return BlocBuilder<ResultCubit, ResultState>(
      builder: (context, state) {
        if (state is! ResultLoaded) {
          return const HintText(text: 'Завантажте, будь ласка, документ для аналізу');
        }

        final result = state.result;
        final checkTypes = AvailableCheckTypes.checkTypes;
        final selectedChecks = result.selectedChecks;
        final visibleCheckTypes = selectedChecks.isEmpty
            ? checkTypes
            : checkTypes.where(selectedChecks.contains).toList();
        if (visibleCheckTypes.isEmpty) {
          return const HintText(text: 'Немає обраних категорій для відображення');
        }
        final categoriesByTitle = {
          for (final item in result.errorsByCategory) item.category: item,
        };

        final safeIndex = activeCategoryIndex < visibleCheckTypes.length
            ? activeCategoryIndex
            : 0;
        final selectedType = visibleCheckTypes[safeIndex];
        final selectedCategory = categoriesByTitle[selectedType.title];

        final selectedCategoryTitle = selectedType.title;
        final filteredErrors = selectedCategory?.errors ?? const [];
        final totalWarnings = result.errorsByCategory.fold<int>(0, (sum, item) =>
            sum + item.errors.where((e) => e.severity.toLowerCase() == 'warning').length);

        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            InfoCard(
              padding: EdgeInsets.all(24.0),
              borderRadius: BorderRadius.circular(16.0),
              children: [
                Image.asset(
                  result.totalErrors > 0
                      ? 'assets/images/found_errors.png'
                      : 'assets/images/no_errors.png',
                  width: 24.0,
                  height: 24.0,
                ),
                const SizedBox(width: 12.0),
                Expanded(
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        result.fileName,
                        style: TextStyle(
                          fontSize: 16.0,
                          fontWeight: FontWeight.w600,
                          fontFamily: 'FunnelSans',
                          color: fileNameTextColor,
                        ),
                        overflow: TextOverflow.ellipsis,
                        maxLines: 3,
                      ),
                      const SizedBox(height: 6.0),
                      Row(
                        children: [
                          ErrorCountBadge(
                            errorCount: result.totalErrors,
                            warnCount: totalWarnings,
                          ),
                        ],
                      )
                    ],
                  ),
                ),
                Row(
                  mainAxisAlignment: MainAxisAlignment.end,
                  children: [
                    CustomAnimatedButton(
                      text: 'Заново',
                      buttonIsHovered: buttonIsHovered,
                      accentColor: accentColor,
                      onTap: () => showDialog(
                          context: context,
                          builder: (context) => AnalysisOptionsDialog(
                               filePath: result.filePath,
                               fileName: result.fileName
                           )
                       ),
                      onHover: (isHovered) => setState(() => buttonIsHovered = isHovered),
                    ),
                  ],
                )
              ],
            ),
            const SizedBox(height: 24.0),
            InfoText(text: 'Категорії',),
            const SizedBox(height: 12.0),
            LayoutBuilder(
              builder: (context, constraints) {
                final crossAxisCount = constraints.maxWidth < 500 ? 1 : 2;

                return GridView.builder(
                  itemCount: visibleCheckTypes.length,
                  shrinkWrap: true,
                  physics: const NeverScrollableScrollPhysics(),
                  gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                    crossAxisCount: crossAxisCount,
                    crossAxisSpacing: 16.0,
                    mainAxisSpacing: 16.0,
                    mainAxisExtent: 76.0,
                  ),
                  itemBuilder: (context, index) {
                    final type = visibleCheckTypes[index];
                    final category = categoriesByTitle[type.title];
                    final warnCount = category?.errors.where((e) => e.severity.toLowerCase() == 'warning').length ?? 0;
                    final errorCount = category?.errors.where((e) => e.severity.toLowerCase() != 'warning').length ?? 0;

                    return CheckboxContainer(
                      isSelected: (activeCategoryIndex < visibleCheckTypes.length
                              ? activeCategoryIndex
                              : 0) == index,
                      onTap: () => setState(() => activeCategoryIndex = index),
                      bottomStripeColor: (errorCount > 0 && warnCount == 0)
                          ? AppColors.error
                          : (errorCount == 0 && warnCount > 0)
                              ? warnColor
                              : (errorCount == 0 && warnCount == 0 ? AppColors.ok : null),
                      bottomStripeGradient: (errorCount > 0 && warnCount > 0)
                          ? LinearGradient(colors: [AppColors.error, warnColor])
                          : null,
                      rightWidget: ErrorCountBadge(
                        errorCount: errorCount,
                        warnCount: warnCount,
                      ),
                      children: [
                        Text(
                          type.title,
                          style: TextStyle(
                            fontSize: 14.0,
                            fontWeight: FontWeight.w600,
                            fontFamily: 'FunnelSans',
                            color: safeIndex == index
                                ? activeTextColor
                                : fileNameTextColor,
                          ),
                        ),
                        Text(
                          type.description,
                          style: TextStyle(
                            fontSize: 14.0,
                            fontFamily: 'FunnelSans',
                            fontWeight: FontWeight.w600,
                            color: subTextColor,
                          ),
                          maxLines: 1,
                          overflow: TextOverflow.ellipsis,
                        )
                      ],
                    );
                  },
                );
              },
            ),
            const SizedBox(height: 24.0),
            InfoText(text: 'Деталі помилок',),
            const SizedBox(height: 12.0),
            if (filteredErrors.isEmpty)
              Text(
                'Для категорії "$selectedCategoryTitle" помилки відсутні.',
                style: TextStyle(
                  fontSize: 14.0,
                  fontWeight: FontWeight.w600,
                  fontFamily: 'FunnelSans',
                  color: subTextColor,
                ),
              )
            else
              ListView.separated(
                itemCount: filteredErrors.length,
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                separatorBuilder: (_, __) => const SizedBox(height: 12.0),
                itemBuilder: (context, index) {
                  final error = filteredErrors[index];
                  final paragraphText = error.paragraphText?.trim();

                  return ErrorDetailExpandableCard(
                    title: error.title,
                    quote: (paragraphText == null || paragraphText.isEmpty)
                        ? 'Фрагмент тексту відсутній.'
                        : paragraphText,
                    severity: error.severity,
                    foundValue: error.found,
                    expectedValue: error.expected,
                  );
                },
              ),
          ]
        );
      },
    );
  }
}
