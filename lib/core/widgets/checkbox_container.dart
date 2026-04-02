import 'package:flutter/material.dart';

/// Clickable option card with hover state and an anchored checkbox.
class CheckboxContainer extends StatefulWidget {
  final List<Widget> children;
  final Widget rightWidget;
  final VoidCallback onTap;
  final bool isSelected;
  final double borderWidth;

  const CheckboxContainer({
    super.key, 
    required this.children, 
    required this.onTap,
    required this.rightWidget,
    this.isSelected = false,
    this.borderWidth = 1.0,
  });

  @override
  State<CheckboxContainer> createState() => _CheckboxContainerState();
}

class _CheckboxContainerState extends State<CheckboxContainer> {
  bool isHovered = false;

  @override
  Widget build(BuildContext context) {
    final backgroundColor = Theme.of(context).scaffoldBackgroundColor;
    final activeBackgroundColor = Theme.of(context).colorScheme.surface;
    final borderColor = Theme.of(context).inputDecorationTheme.border?.borderSide.color;
    final hoverBorderColor = Theme.of(context).primaryColor;
    final isActive = widget.isSelected;

    return MouseRegion(
      onEnter: (event) => setState(() => isHovered = true),
      onExit: (event) => setState(() => isHovered = false),
      cursor: SystemMouseCursors.click,
      child: GestureDetector(
        onTap: widget.onTap,
        child: AnimatedContainer(
          width: double.infinity,
          duration: const Duration(milliseconds: 180),
          padding: const EdgeInsets.all(12.0),
          decoration: BoxDecoration(
            color: isActive ? activeBackgroundColor : backgroundColor,
            border: Border.all(color: isHovered || isActive ? hoverBorderColor : borderColor!, width: widget.borderWidth),
            borderRadius: BorderRadius.circular(12.0),
          ),
          child: Stack(
            children: [
              Padding(
                padding: const EdgeInsets.only(right: 30.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: widget.children,
                ),
              ),
              Positioned(
                top: 0,
                right: 0,
                child: widget.rightWidget,
              ),
            ],
          ),
        ),
      ),
    );
  }
}
