import 'package:flutter/material.dart';

/// Clickable option card with hover state and an anchored checkbox.
class CheckboxContainer extends StatefulWidget {
  final List<Widget> children;
  final Widget rightWidget;
  final VoidCallback onTap;
  final bool isSelected;
  final double borderWidth;
  final Color? bottomStripeColor;
  final double indicatorHeight;

  const CheckboxContainer({
    super.key, 
    required this.children, 
    required this.onTap,
    required this.rightWidget,
    this.isSelected = false,
    this.borderWidth = 1.0,
    this.bottomStripeColor,
    this.indicatorHeight = 4.0,
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
            border: Border.all(
              color: isHovered || isActive ? hoverBorderColor : borderColor!, 
              width: widget.borderWidth
            ),
            borderRadius: BorderRadius.circular(12.0),
          ),
          child: Stack(
            children: [
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: widget.children,
              ),
              if (widget.bottomStripeColor != null)
                Positioned(
                  left: 0,
                  right: 0,
                  bottom: 0,
                  child: Container(
                    height: widget.indicatorHeight,
                    decoration: BoxDecoration(
                      color: widget.bottomStripeColor,
                      borderRadius: BorderRadius.all(Radius.circular(8.0)),
                    ),
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
