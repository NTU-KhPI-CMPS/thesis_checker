import 'package:flutter/material.dart';

/// A compact checkbox used inside option cards.
class CustomCheckbox extends StatelessWidget {
  final bool value;
  final ValueChanged<bool>? onChanged;
  final VoidCallback? onTap;

  const CustomCheckbox({
    super.key,
    required this.value,
    this.onChanged,
    this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final backgroundColor = Theme.of(context).scaffoldBackgroundColor;
    final checkedBackgroundColor = Theme.of(context).colorScheme.primary;
    final borderColor = Theme.of(context).inputDecorationTheme.border?.borderSide.color;

    return GestureDetector(
      onTap: () {
        onChanged?.call(!value);
        onTap?.call();
      },
      child: AnimatedContainer(
        width: 20.0,
        height: 20.0,
        duration: const Duration(milliseconds: 150),
        decoration: BoxDecoration(
          border: Border.all(
            color: value? Colors.transparent : borderColor!,
            width: 2.0,
          ),
          color: value ? checkedBackgroundColor : backgroundColor,
          borderRadius: BorderRadius.circular(6.0),
        ),
        child: Center(
          child: Text(
            '✓',
            style: TextStyle(
              fontSize: 12.0,
              color: value ? Colors.white : Colors.transparent,
              fontWeight: FontWeight.bold,
            ),
          ),
        ),
      ),
    );
  }
}
