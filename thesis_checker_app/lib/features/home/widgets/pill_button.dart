import 'package:flutter/material.dart';

class PillButton extends StatefulWidget {
  final String icon;
  final String label;
  final VoidCallback onTap;

  const PillButton({
    super.key,
    required this.icon,
    required this.label,
    required this.onTap
  });

  @override
  State<PillButton> createState() => _PillButtonState();
}

class _PillButtonState extends State<PillButton> {
  bool _isHovered = false;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final accentColor = theme.primaryColor;
    final borderColor = theme.dividerColor;
    final surface2Color = theme.colorScheme.surface;
    final textColor2 = theme.textTheme.bodyMedium?.color;

    return MouseRegion(
      onEnter: (event) => setState(() => _isHovered = true),
      onExit: (event) => setState(() => _isHovered = false),
      child: GestureDetector(
        onTap: widget.onTap,
        child: AnimatedContainer(
          duration: const Duration(milliseconds: 200),
          padding: const EdgeInsets.symmetric(
            vertical: 6.0,
            horizontal: 14.0,
          ),
          decoration: BoxDecoration(
            border: Border.all(
              color: _isHovered ? accentColor : borderColor,
            ),
            borderRadius: const BorderRadius.all(Radius.circular(20.0)),
            color: surface2Color,
          ),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              Image.asset(
                widget.icon,
                width: 20.0,
                height: 20.0,
              ),
              const SizedBox(width: 5),
              Text(
                widget.label,
                style: TextStyle(
                  color: _isHovered
                      ? accentColor
                      : textColor2,
                  fontSize: 13.0,
                  fontFamily: 'FunnelSans',
                  fontWeight: FontWeight.w600,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
