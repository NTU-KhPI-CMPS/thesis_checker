import 'package:flutter/material.dart';
import 'package:flutter_app/core/constants/available_check_types.dart';
import 'package:flutter_app/core/constants/app_colors.dart';
import 'package:flutter_app/core/utils/ukrainian_plural.dart';
import 'package:flutter_app/core/widgets/checkbox_container.dart';
import 'package:flutter_app/core/widgets/hint_text.dart';
import 'package:flutter_app/features/result/widgets/error_count_badge.dart';
import 'package:flutter_app/features/result/widgets/error_detail_expandable_card.dart';
import 'package:flutter_app/core/widgets/info_card.dart';
import 'package:flutter_app/core/widgets/info_text.dart';
import 'package:flutter_app/features/result/cubit/result_cubit.dart';
import 'package:flutter_app/features/result/cubit/result_state.dart';
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

    return BlocBuilder<ResultCubit, ResultState>(
      builder: (context, state) {
        if (state is! ResultLoaded) {
          return const HintText(text: 'Завантажте, будь ласка, документ для аналізу');
        }

        final result = state.result;
        final checkTypes = AvailableCheckTypes.checkTypes;
        final categoriesByTitle = {
          for (final item in result.errorsByCategory) item.category: item,
        };

        final selectedType = checkTypes[activeCategoryIndex];
        final selectedCategory = categoriesByTitle[selectedType.title];

        final selectedCategoryTitle = selectedType.title;
        final filteredErrors = selectedCategory?.errors ?? const [];

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
                Column(
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
                    ),
                    Text(
                      'Знайдено: ${UkrainePlural.formatErrorCount(result.totalErrors)}',
                      style: TextStyle(
                        fontSize: 12.0,
                        fontWeight: FontWeight.w600,
                        fontFamily: 'FunnelSans',
                        color: subTextColor,
                      ),
                    )
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
                  itemCount: checkTypes.length,
                  shrinkWrap: true,
                  physics: const NeverScrollableScrollPhysics(),
                  gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                    crossAxisCount: crossAxisCount,
                    crossAxisSpacing: 16.0,
                    mainAxisSpacing: 16.0,
                    mainAxisExtent: 76.0,
                  ),
                  itemBuilder: (context, index) {
                    final type = checkTypes[index];
                    final count = categoriesByTitle[type.title]?.count ?? 0;

                    return CheckboxContainer(
                      isSelected: activeCategoryIndex == index,
                      onTap: () => setState(() => activeCategoryIndex = index),
                      bottomStripeColor: count > 0 ? AppColors.error : AppColors.ok,
                      rightWidget: ErrorCountBadge(
                        count: count,
                      ),
                      children: [
                        Text(
                          type.title,
                          style: TextStyle(
                            fontSize: 14.0,
                            fontWeight: FontWeight.w600,
                            fontFamily: 'FunnelSans',
                            color: activeCategoryIndex == index
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
