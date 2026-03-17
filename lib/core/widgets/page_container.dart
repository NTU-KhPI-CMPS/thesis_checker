import 'package:flutter/material.dart';

/// Reusable layout wrapper with a full-page background and centered content.
class PageContainer extends StatelessWidget {
  final Widget child;
  const PageContainer({super.key, required this.child});

  @override
  Widget build(BuildContext context) {
    final bgColor = Theme.of(context).canvasColor;
    final borderColor = Theme.of(context).dividerColor;

    return Container(
      width: double.infinity,
      height: double.infinity,
      decoration: BoxDecoration(
        color: bgColor,
        border: Border(
          top: BorderSide(
            color: borderColor,
            width: 1.0,
          ),
        ),
      ),
      child: LayoutBuilder(
        builder: (context, constraints) {
          final minHeight = constraints.maxHeight;
          return SingleChildScrollView(
            child: ConstrainedBox(
              constraints: BoxConstraints(
                minHeight: minHeight,
              ),
              child: Padding(
                padding: const EdgeInsets.all(36.0),
                child: Align(
                  alignment: Alignment.topCenter,
                  child: ConstrainedBox(
                    constraints: BoxConstraints(
                      maxWidth: 820.0,
                    ),
                    child: child,
                  ),
                ),
              ),
            ),
          );
        },
      ),
    );
  }
}
