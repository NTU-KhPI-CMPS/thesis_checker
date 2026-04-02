import 'package:flutter/material.dart';
import 'package:flutter_app/core/constants/app_colors.dart';
import 'package:flutter_app/core/utils/ukrainian_plural.dart';
import 'package:flutter_app/core/widgets/checkbox_container.dart';
import 'package:flutter_app/core/widgets/hint_text.dart';
import 'package:flutter_app/features/result/widgets/error_count_badge.dart';
import 'package:flutter_app/features/result/widgets/error_detail_expandable_card.dart';
import 'package:flutter_app/core/widgets/info_card.dart';
import 'package:flutter_app/core/widgets/info_text.dart';
import 'package:flutter_app/features/loading_analysis/bloc/analysis_bloc.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

class ResultView extends StatefulWidget {
  const ResultView({super.key});

  @override
  State<ResultView> createState() => _ResultViewState();
}

class _ResultViewState extends State<ResultView> {
  int activeCategoryIndex = 0;

  @override
  Widget build(BuildContext context) {
    final isLightTheme = Theme.of(context).brightness == Brightness.light;
    final fileNameTextColor = Theme.of(context).textTheme.bodyLarge?.color;
    final subTextColor = Theme.of(context).textTheme.bodyMedium?.color;
    final activeTextColor = isLightTheme ? AppColors.errorDark : AppColors.errorLight;

    return BlocBuilder<AnalysisBloc, AnalysisState>(
      builder: (context, state) {
        if (state is AnalysisSuccessState) {
          final hasCategories = state.result.errorsByCategory.isNotEmpty;
          final safeActiveCategoryIndex = hasCategories
              ? (activeCategoryIndex < state.result.errorsByCategory.length
                  ? activeCategoryIndex
                  : 0)
              : 0;
          final selectedCategory = hasCategories
              ? state.result.errorsByCategory[safeActiveCategoryIndex].category
              : '';
          final filteredErrors = state.result.foundErrors
              .where((error) => error.category == selectedCategory)
              .toList();

          return Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              InfoCard(
                padding: EdgeInsets.all(24.0),
                borderRadius: BorderRadius.circular(16.0),
                children: [
                  Image.asset(
                    state.result.foundErrors.isNotEmpty
                      ? 'assets/images/found_errors.png'
                      : 'assets/images/no_errors.png',
                    width: 24.0,
                    height: 24.0,
                  ),
                  const SizedBox(width: 12.0),
                  Column(
                    mainAxisSize: MainAxisSize.min,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        state.result.fileName,
                        style: TextStyle(
                          fontSize: 16.0,
                          fontWeight: FontWeight.w600,
                          fontFamily: 'FunnelSans',
                          color: fileNameTextColor
                        ),
                      ),
                      Text(
                        'Знайдено: ${UkrainePlural.formatErrorCount(state.result.totalErrors)}',
                        style: TextStyle(
                          fontSize: 12.0,
                          fontWeight: FontWeight.w600,
                          fontFamily: 'FunnelSans',
                          color: subTextColor
                        ),
                      )
                    ],
                  )
                ],
              ),
              const SizedBox(height: 24.0),
              InfoText(text: 'Категорії',),
              const SizedBox(height: 12.0),
              GridView.builder(
                itemCount: state.result.errorsByCategory.length,
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                  crossAxisCount: 2,
                  crossAxisSpacing: 16.0,
                  mainAxisSpacing: 16.0,
                  mainAxisExtent: 76.0,
                ),
                itemBuilder: (context, index) => CheckboxContainer(
                  isSelected: safeActiveCategoryIndex == index,
                  onTap: () => setState(() => activeCategoryIndex = index),
                  rightWidget: ErrorCountBadge(
                    count: state.result.errorsByCategory[index].count,
                  ),
                  children: [
                    Text(
                      state.result.errorsByCategory[index].category,
                      style: TextStyle(
                        fontSize: 14.0,
                        fontWeight: FontWeight.w600,
                        fontFamily: 'FunnelSans',
                        color: activeCategoryIndex == index ? activeTextColor : fileNameTextColor,
                      ),
                    ),
                  ],
                )
              ),
              const SizedBox(height: 24.0),
              InfoText(text: 'Деталі помилок',),
              const SizedBox(height: 12.0),
              if (!hasCategories)
                Text(
                  'Категорії відсутні.',
                  style: TextStyle(
                    fontSize: 14.0,
                    fontWeight: FontWeight.w600,
                    fontFamily: 'FunnelSans',
                    color: subTextColor,
                  ),
                )
              else if (filteredErrors.isEmpty)
                Text(
                  'Для категорії "$selectedCategory" помилки відсутні.',
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
                      subtitle: error.paragraphIndex == null
                          ? 'Параграф не вказано'
                          : 'Параграф ${error.paragraphIndex}',
                      quote: (paragraphText == null || paragraphText.isEmpty)
                          ? 'Фрагмент тексту відсутній.'
                          : paragraphText,
                      highlightStart: error.highlightError?.start,
                      highlightEnd: error.highlightError?.end,
                      foundValue: error.found,
                      expectedValue: error.expected,
                      tags: [error.category, ...error.suggestions],
                    );
                  },
                ),
            ]
          );
        } else if (state is AnalysisFailureState) {
          return HintText(text: 'Сталася помилка: ${state.error}');
        } else {
          return const HintText(text: 'Завантажте, будь ласка, документ для аналізу');
        }
      },
    );
  }
}
