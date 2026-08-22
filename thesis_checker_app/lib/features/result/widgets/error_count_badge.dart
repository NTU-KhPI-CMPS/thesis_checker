import 'package:flutter/material.dart';
import 'package:thesis_checker/core/constants/app_colors.dart';
import 'package:thesis_checker/core/utils/ukrainian_plural.dart';

/// Badge that displays error and warning counts with appropriate colors.
class ErrorCountBadge extends StatelessWidget {
  final int errorCount;
  final int warnCount;

  const ErrorCountBadge({
    super.key,
    required this.errorCount,
    this.warnCount = 0,
  });

  @override
  Widget build(BuildContext context) {
    final isLightTheme = Theme.of(context).brightness == Brightness.light;

    // Error visuals
    final errorContainerColor = Theme.of(context).colorScheme.errorContainer;
    final errorTextColor = Theme.of(context).colorScheme.error;
    final noErrorContainerColor = isLightTheme ? AppColors.okLight : AppColors.okDark;
    final noErrorTextColor = AppColors.ok;
    final hasErrors = errorCount > 0;

    // Warning visuals
    final warnTextColor = AppColors.warn;
    final hasWarnings = warnCount > 0;
    final warnAlpha = isLightTheme ? 31 : 56;
    final warnBg = AppColors.warn.withAlpha(warnAlpha);

    final errorBadge = AnimatedContainer(
      duration: const Duration(milliseconds: 200),
      curve: Curves.easeInOut,
      padding: const EdgeInsets.symmetric(horizontal: 8.0, vertical: 4.0),
      decoration: BoxDecoration(
        color: hasErrors ? errorContainerColor : noErrorContainerColor,
        borderRadius: BorderRadius.circular(8.0),
      ),
      child: Text(
        UkrainePlural.formatErrorCount(errorCount),
        style: TextStyle(
          fontSize: 11.0,
          fontWeight: FontWeight.w700,
          fontFamily: 'FunnelSans',
          color: hasErrors ? errorTextColor : noErrorTextColor,
        ),
      ),
    );

    final warnText = UkrainePlural.formatWarningCount(warnCount);
    final warnBadge = AnimatedContainer(
      duration: const Duration(milliseconds: 200),
      curve: Curves.easeInOut,
      padding: const EdgeInsets.symmetric(horizontal: 8.0, vertical: 4.0),
      decoration: BoxDecoration(
        color: hasWarnings ? warnBg : Colors.transparent,
        borderRadius: BorderRadius.circular(8.0),
        border: hasWarnings ? Border.all(color: warnTextColor.withAlpha(warnAlpha)) : null,
      ),
      child: Text(
        warnText,
        style: TextStyle(
          fontSize: 11.0,
          fontWeight: FontWeight.w700,
          fontFamily: 'FunnelSans',
          color: hasWarnings ? warnTextColor : Colors.transparent,
        ),
      ),
    );

    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        errorBadge,
        if (hasWarnings) const SizedBox(width: 8.0),
        if (hasWarnings) warnBadge,
      ],
    );
  }
}
