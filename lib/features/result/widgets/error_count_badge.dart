import 'package:flutter/material.dart';
import 'package:thesis_checker/core/utils/ukrainian_plural.dart';

/// Compact badge that displays error count with theme-aware error container color.
class ErrorCountBadge extends StatelessWidget {
  final int count;

  const ErrorCountBadge({
    super.key,
    required this.count,
  });

  @override
  Widget build(BuildContext context) {
    final errorContainerColor = Theme.of(context).colorScheme.errorContainer;
    final textColor = Theme.of(context).colorScheme.error;

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8.0, vertical: 4.0),
      decoration: BoxDecoration(
        color: errorContainerColor,
        borderRadius: BorderRadius.circular(8.0),
      ),
      child: Text(
        UkrainePlural.formatErrorCount(count),
        style: TextStyle(
          fontSize: 11.0,
          fontWeight: FontWeight.w700,
          fontFamily: 'FunnelSans',
          color: textColor,
        ),
      ),
    );
  }
}
