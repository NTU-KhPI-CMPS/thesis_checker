import 'package:auto_size_text/auto_size_text.dart';
import 'package:flutter/material.dart';
import 'package:thesis_checker/core/constants/available_check_types.dart';
import 'package:thesis_checker/core/constants/app_colors.dart';
import 'package:thesis_checker/core/extensions/check_type_localization_x.dart';
import 'package:thesis_checker/core/extensions/error_localization_x.dart';
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
import 'package:thesis_checker/l10n/l10n.dart';
import 'package:thesis_checker/models/analysis_result.dart';

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
    final l10n = AppLocalizations.of(context)!;

    final accentColor = Theme.of(context).primaryColor;
    final isLightTheme = Theme.of(context).brightness == Brightness.light;
    final fileNameTextColor = Theme.of(context).textTheme.bodyLarge?.color;
    final subTextColor = Theme.of(context).textTheme.bodyMedium?.color;
    final activeTextColor = isLightTheme ? AppColors.errorDark : AppColors.errorLight;
    final warnColor = AppColors.warn;

    return BlocBuilder<ResultCubit, ResultState>(
      builder: (context, state) {
        if (state is! ResultLoaded) {
          return HintText(text: l10n.loadingNoFile);
        }

        final result = state.result;
        final checkTypes = AvailableCheckTypes.checkTypes;
        final selectedChecks = result.selectedChecks;
        final visibleCheckTypes = selectedChecks.isEmpty
            ? checkTypes
            : checkTypes.where(selectedChecks.contains).toList();
        if (visibleCheckTypes.isEmpty) {
          return HintText(text: l10n.resultNoSelectedCategories);
        }
        final categoriesByTitle = {
          for (final item in result.errorsByCategory) item.category: item,
        };

        final safeIndex = activeCategoryIndex < visibleCheckTypes.length
            ? activeCategoryIndex
            : 0;
        final selectedType = visibleCheckTypes[safeIndex];
        final selectedCategory = categoriesByTitle[selectedType.category];

        final selectedCategoryTitle = selectedType.localizedTitle(context);
        final filteredErrors = selectedCategory?.errors ?? const [];
        final totalWarnings = result.errorsByCategory.fold<int>(0, (sum, item) =>
            sum + item.errors.where((e) => e.severity.toLowerCase() == 'warning').length);

        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            LayoutBuilder(
              builder: (BuildContext context, BoxConstraints constraints) {
                if (constraints.maxWidth > 400) {
                  return _buildFullSizeInfoCard(
                    result,
                    fileNameTextColor,
                    totalWarnings,
                    l10n,
                    accentColor,
                    context
                  );
                } else {
                  return _buildSmallSizeInfoCard(
                    result,
                    fileNameTextColor,
                    totalWarnings,
                    l10n,
                    accentColor,
                    context
                  );
                }
              },
            ),
            const SizedBox(height: 24.0),
            InfoText(text: l10n.resultCategories),
            const SizedBox(height: 12.0),
            LayoutBuilder(
              builder: (context, constraints) {
                final crossAxisCount = constraints.maxWidth < 700 ? 1 : 2;
                final mainAxisExtent = crossAxisCount == 2 ? 135.0 : 175.0;
                final textMaxLines = mainAxisExtent == 135.0 ? 4 : 6;

                return GridView.builder(
                  itemCount: visibleCheckTypes.length,
                  shrinkWrap: true,
                  physics: const NeverScrollableScrollPhysics(),
                  gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                    crossAxisCount: crossAxisCount,
                    crossAxisSpacing: 16.0,
                    mainAxisSpacing: 16.0,
                    mainAxisExtent: mainAxisExtent
                  ),
                  itemBuilder: (context, index) {
                    final type = visibleCheckTypes[index];
                    final category = categoriesByTitle[type.category];
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
                        AutoSizeText(
                          type.localizedTitle(context),
                          style: TextStyle(
                            fontSize: 14.0,
                            fontWeight: FontWeight.w600,
                            fontFamily: 'FunnelSans',
                            color: safeIndex == index
                                ? activeTextColor
                                : fileNameTextColor,
                          ),
                        ),
                        AutoSizeText(
                          type.localizedDescription(context),
                          style: TextStyle(
                            fontSize: 14.0,
                            fontFamily: 'FunnelSans',
                            fontWeight: FontWeight.w600,
                            color: subTextColor,
                          ),
                          maxLines: textMaxLines,
                          overflow: TextOverflow.ellipsis,
                        )
                      ],
                    );
                  },
                );
              },
            ),
            const SizedBox(height: 24.0),
            InfoText(text: l10n.resultDetails),
            const SizedBox(height: 12.0),
            if (filteredErrors.isEmpty)
              Text(
                l10n.resultNoErrorsForCategory(selectedCategoryTitle),
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

                  final errorTitle = error.getLocalizedMessage(context);
                  final errorFoundValue = error.getLocalizedFound(context);
                  final errorExpectedValue = error.getLocalizedExpected(context);
                  final paragraphText = error.paragraphText?.trim();

                  return ErrorDetailExpandableCard(
                    title: errorTitle,
                    quote: (paragraphText == null || paragraphText.isEmpty)
                        ? l10n.errorFragmentMissing
                        : paragraphText,
                    severity: error.severity,
                    foundValue: errorFoundValue,
                    expectedValue: errorExpectedValue,
                  );
                },
              ),
          ]
        );
      },
    );
  }

  Widget _buildStatusIcon(AnalysisResult result) {
    return Image.asset(
      result.totalErrors > 0
          ? 'assets/images/found_errors.png'
          : 'assets/images/no_errors.png',
      width: 24.0,
      height: 24.0,
    );
  }

  Widget _buildFileInfo(
      AnalysisResult result,
      Color? fileNameTextColor,
      int totalWarnings, {
        bool centered = false,
    }) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      crossAxisAlignment:
      centered ? CrossAxisAlignment.center : CrossAxisAlignment.start,
      children: [
        Text(
          result.fileName,
          textAlign: centered ? TextAlign.center : TextAlign.start,
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
        FittedBox(
          fit: BoxFit.scaleDown,
          alignment: centered ? Alignment.center : Alignment.centerLeft,
          child: ErrorCountBadge(
            errorCount: result.totalErrors,
            warnCount: totalWarnings,
          ),
        ),
      ],
    );
  }

  Widget _buildRetryButton(
      AnalysisResult result,
      AppLocalizations l10n,
      Color accentColor,
      BuildContext context,
    ) {
    return CustomAnimatedButton(
      text: l10n.retryAnalysis,
      buttonIsHovered: buttonIsHovered,
      accentColor: accentColor,
      onTap: () => _showAnalysisOptions(context, result),
      onHover: (isHovered) => setState(() => buttonIsHovered = isHovered),
    );
  }

  void _showAnalysisOptions(BuildContext context, AnalysisResult result) {
    showDialog(
      context: context,
      builder: (context) => AnalysisOptionsDialog(
        filePath: result.filePath,
        fileName: result.fileName,
      ),
    );
  }

  InfoCard _buildSmallSizeInfoCard(
      AnalysisResult result,
      Color? fileNameTextColor,
      int totalWarnings,
      AppLocalizations l10n,
      Color accentColor,
      BuildContext context,
    ) {
    return InfoCard(
      children: [
        Expanded(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Row(
                mainAxisSize: MainAxisSize.min,
                mainAxisAlignment: MainAxisAlignment.center,
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  _buildStatusIcon(result),
                  const SizedBox(width: 12.0),
                  Flexible(
                    child: _buildFileInfo(
                      result,
                      fileNameTextColor,
                      totalWarnings,
                      centered: true,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 10.0),
              SizedBox(
                width: double.infinity,
                child: _buildRetryButton(result, l10n, accentColor, context),
              ),
            ],
          ),
        ),
      ],
    );
  }

  InfoCard _buildFullSizeInfoCard(
      AnalysisResult result,
      Color? fileNameTextColor,
      int totalWarnings,
      AppLocalizations l10n,
      Color accentColor,
      BuildContext context,
    ) {
    return InfoCard(
      padding: const EdgeInsets.all(24.0),
      borderRadius: BorderRadius.circular(16.0),
      children: [
        _buildStatusIcon(result),
        const SizedBox(width: 12.0),
        Expanded(
          child: _buildFileInfo(result, fileNameTextColor, totalWarnings),
        ),
        const SizedBox(width: 12.0),
        _buildRetryButton(result, l10n, accentColor, context),
      ],
    );
  }
}
