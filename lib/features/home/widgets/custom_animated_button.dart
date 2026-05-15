import 'package:flutter/material.dart';

class CustomAnimatedButton extends StatelessWidget {
  final String text;
  final bool buttonIsHovered;
  final Color accentColor;
  final VoidCallback? onTap;
  final ValueChanged<bool>? onHover;

  const CustomAnimatedButton({
    super.key,
    required this.text,
    required this.buttonIsHovered,
    required this.accentColor,
    this.onTap,
    this.onHover,
  });

  @override
  Widget build(BuildContext context) {
    return MouseRegion(
      onEnter: (_) => onHover?.call(true),
      onExit: (_) => onHover?.call(false),
      child: GestureDetector(
        onTap: onTap,
        child: TweenAnimationBuilder<double>(
          tween: Tween(begin: 0.0, end: buttonIsHovered ? -2.0 : 0.0),
          duration: const Duration(milliseconds: 200),
          curve: Curves.easeInOut,
          builder: (context, offset, _) {
            return Transform.translate(
              offset: Offset(0, offset),
              child: AnimatedContainer(
                duration: Duration(milliseconds: 200),
                padding: EdgeInsets.symmetric(vertical: 8.0, horizontal: 24.0),
                decoration: BoxDecoration(
                  color: buttonIsHovered ? accentColor.withAlpha(200) : accentColor,
                  borderRadius: BorderRadius.circular(8.0),
                ),
                child: Text(
                  text,
                  style: TextStyle(
                    color: buttonIsHovered ? Colors.grey[100] : Colors.white,
                    fontSize: 16.0,
                    fontFamily: 'FunnelSans',
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
            );
          },
        ),
      ),
    );
  }
}
