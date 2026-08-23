import 'package:flutter/material.dart';
import 'package:thesis_checker/features/analysis_options_dialog/widgets/option_checkbox.dart';

/// Clickable option card with hover state and an anchored checkbox.
class ClickableOptionCard extends StatefulWidget {
  final List<Widget> children;
  final ValueChanged<bool> onChanged;
  final bool checked;

  const ClickableOptionCard({
    super.key,
    required this.children,
    required this.onChanged,
    this.checked = false,
  });
  @override
  State<ClickableOptionCard> createState() => _ClickableOptionCardState();
}

class _ClickableOptionCardState extends State<ClickableOptionCard> {
  bool isHovered = false;

  @override
  Widget build(BuildContext context) {
    final backgroundColor = Theme.of(context).scaffoldBackgroundColor;
    final hoverBackgroundColor = Theme.of(context).canvasColor;
    final borderColor = Theme.of(context).inputDecorationTheme.border?.borderSide.color;
    final hoverBorderColor = Theme.of(context).primaryColor;

    return MouseRegion(
      onEnter: (event) => setState(() => isHovered = true),
      onExit: (event) => setState(() => isHovered = false),
      cursor: SystemMouseCursors.click,
      child: GestureDetector(
        onTap: () {
          widget.onChanged(!widget.checked);
        },
        child: AnimatedContainer(
          width: double.infinity,
          duration: const Duration(milliseconds: 180),
          padding: const EdgeInsets.all(12.0),
          decoration: BoxDecoration(
            color: isHovered ? hoverBackgroundColor : backgroundColor,
            border: Border.all(color: isHovered ? hoverBorderColor : borderColor!, width: 2.0),
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
                child: OptionCheckbox(
                  value: widget.checked,
                  onChanged: (v) => widget.onChanged(v),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

