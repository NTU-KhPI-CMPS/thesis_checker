import 'package:flutter/material.dart';

/// Navigation tab button used in the custom app bar.
class AppBarNavButton extends StatelessWidget {
  const AppBarNavButton({
    super.key,
    required this.icon,
    required this.label,
    required this.isActive,
    required this.onTap,
  });

  final String icon;
  final String label;
  final bool isActive;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    final bgColor = Theme.of(context).canvasColor;
    final borderColor = Theme.of(context).dividerColor;
    final accentColor = Theme.of(context).primaryColor;
    final textColor = Theme.of(context).textTheme.bodyLarge?.color ?? Colors.black;

    return GestureDetector(
      onTap: onTap,
      child: Transform.translate(
        offset: isActive ? const Offset(0, 1.0) : Offset.zero,
        child: AnimatedContainer(
          padding: const EdgeInsets.symmetric(
            vertical: 6.0,
            horizontal: 12.0,
          ),
          duration: const Duration(milliseconds: 300),
          curve: Curves.easeInOut,
          decoration: BoxDecoration(
            color: isActive ? bgColor : bgColor.withAlpha(0),
            border: Border(
              top: BorderSide(
                color: isActive ? borderColor : borderColor.withAlpha(0),
                width: 1.0,
              ),
              left: BorderSide(
                color: isActive ? borderColor : borderColor.withAlpha(0),
                width: 1.0,
              ),
              right: BorderSide(
                color: isActive ? borderColor : borderColor.withAlpha(0),
                width: 1.0,
              ),
              bottom: BorderSide.none,
            ),
            borderRadius: const BorderRadius.only(
              topLeft: Radius.circular(8.0),
              topRight: Radius.circular(8.0),
            ),
          ),
          child: Row(
            children: [
              Image.asset(
                icon,
                width: 20.0,
                height: 20.0,
              ),
              const SizedBox(width: 8.0),
              Text(
                label,
                style: TextStyle(
                  color: isActive ? accentColor : textColor,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
