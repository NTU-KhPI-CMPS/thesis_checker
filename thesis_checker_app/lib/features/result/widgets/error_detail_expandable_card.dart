import 'package:flutter/material.dart';
import 'package:thesis_checker/core/constants/app_colors.dart';
import 'package:thesis_checker/l10n/l10n.dart';

/// Expandable card that displays a summary row and detailed error information.
class ErrorDetailExpandableCard extends StatefulWidget {
  final String title;
  final String quote;
  final String severity;
  final String foundValue;
  final String expectedValue;
  final String iconAsset;

  const ErrorDetailExpandableCard({
    super.key,
    required this.title,
    required this.quote,
    required this.severity,
    required this.foundValue,
    required this.expectedValue,
    this.iconAsset = 'assets/images/abc.png', // replace with some universal image
  });

  @override
  State<ErrorDetailExpandableCard> createState() => _ErrorDetailExpandableCardState();
}

class _ErrorDetailExpandableCardState extends State<ErrorDetailExpandableCard> {

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context)!;

    final theme = Theme.of(context);
    final isLightTheme = theme.brightness == Brightness.light;

    final sectionBackground = theme.scaffoldBackgroundColor;
    final borderColor = theme.dividerColor;
    final titleColor = theme.textTheme.bodyLarge?.color;
    final subtitleColor = theme.textTheme.bodyMedium?.color;
    final quoteColor = theme.textTheme.bodyLarge?.color;
    final chipTextColor = isLightTheme ? AppColors.error : AppColors.errorLight;
    final expectedChipTextColor = AppColors.ok;
    final highlightBackgroundErrorColor = theme.colorScheme.errorContainer;
    final highlightBackgroundWarningColor = isLightTheme ? AppColors.warn.withAlpha(75) :
                                            AppColors.warn.withAlpha(95);

    return AnimatedContainer(
      duration: const Duration(milliseconds: 200),
      curve: Curves.easeInOut,
      decoration: BoxDecoration(
        color: sectionBackground,
        border: Border.all(color: borderColor),
        borderRadius: BorderRadius.circular(12.0),
      ),
      child: ClipRRect(
        borderRadius: BorderRadius.circular(12.0),
        child: Column(
          children: [
            Container(
              padding: const EdgeInsets.all(12.0),
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  Container(
                    width: 36.0,
                    height: 36.0,
                    decoration: BoxDecoration(
                      color: widget.severity == 'error' ? highlightBackgroundErrorColor : highlightBackgroundWarningColor,
                      borderRadius: BorderRadius.circular(10.0),
                    ),
                    child: Padding(
                      padding: const EdgeInsets.all(8.0),
                      child: Image.asset(widget.iconAsset),
                    ),
                  ),
                  const SizedBox(width: 12.0),
                  Expanded(
                    child: Text(
                      widget.title,
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                      style: TextStyle(
                        fontFamily: 'FunnelSans',
                        fontWeight: FontWeight.w700,
                        fontSize: 16.0,
                        color: titleColor,
                      ),
                    ),
                  ),
                ],
              ),
            ),
            Container(
              decoration: BoxDecoration(
                border: Border(
                  bottom: BorderSide(
                  color: borderColor,
                  width: 1.0,
                )),
              ),
            ),
            Container(
              padding: const EdgeInsets.fromLTRB(12.0, 0.0, 12.0, 12.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  AnimatedContainer(
                    duration: const Duration(milliseconds: 200),
                    curve: Curves.easeInOut,
                    width: double.infinity,
                    padding: const EdgeInsets.all(12.0),
                    decoration: BoxDecoration(
                      color: sectionBackground,
                      borderRadius: BorderRadius.circular(10.0),
                    ),
                    child: SelectableText(
                      widget.quote,
                      style: TextStyle(
                        fontFamily: 'FunnelSans',
                        fontSize: 14.0,
                        height: 1.4,
                        fontWeight: FontWeight.w600,
                        color: quoteColor,
                      ),
                    ),
                  ),
                  const SizedBox(height: 10.0),
                  Wrap(
                    spacing: 8.0,
                    runSpacing: 8.0,
                    crossAxisAlignment: WrapCrossAlignment.center,
                    children: [
                      _pill(
                        context: context,
                        text: widget.foundValue,
                        background: theme.colorScheme.errorContainer,
                        textColor: chipTextColor,
                      ),
                      Text(
                        l10n.errorArrowShouldBe,
                        style: TextStyle(
                          fontFamily: 'FunnelSans',
                          fontWeight: FontWeight.w600,
                          fontSize: 14.0,
                          color: subtitleColor,
                        ),
                      ),
                      _pill(
                        context: context,
                        text: widget.expectedValue,
                        background: AppColors.ok.withAlpha(isLightTheme ? 40 : 55),
                        textColor: expectedChipTextColor,
                      ),
                    ],
                  ),
                ],
              ),
            )
          ],
        ),
      ),
    );
  }

  Widget _pill({
    required BuildContext context,
    required String text,
    required Color background,
    required Color? textColor,
  }) {
    return AnimatedContainer(
      duration: const Duration(milliseconds: 200),
      curve: Curves.easeInOut,
      padding: const EdgeInsets.symmetric(horizontal: 10.0, vertical: 4.0),
      decoration: BoxDecoration(
        color: background,
        borderRadius: BorderRadius.circular(10.0),
      ),
      child: Text(
        text,
        style: TextStyle(
          fontFamily: 'FunnelSans',
          fontWeight: FontWeight.w700,
          fontSize: 13.0,
          color: textColor,
        ),
      ),
    );
  }
}
