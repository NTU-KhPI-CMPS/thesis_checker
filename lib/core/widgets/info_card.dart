import 'package:flutter/material.dart';

/// Small bordered card used to display a compact row of info widgets.
class InfoCard extends StatelessWidget {
  final List<Widget> children;
  final EdgeInsets padding;
  final BorderRadius borderRadius;

  const InfoCard({
    super.key, 
    required this.children, 
    this.padding = const EdgeInsets.symmetric(vertical: 14.0, horizontal: 16.0),
    this.borderRadius = const BorderRadius.all(Radius.circular(12.0))
  });

  @override
  Widget build(BuildContext context) {
    final surfaceColor = Theme.of(context).scaffoldBackgroundColor;
    final borderColor = Theme.of(context).dividerColor;

    return Container(
      padding: padding,
      decoration: BoxDecoration(
        color: surfaceColor,
        border: Border.all(
          color: borderColor,
          width: 1.0,
        ),
        borderRadius: borderRadius,
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.center,
        children: children,
      ),
    );
  }
}
