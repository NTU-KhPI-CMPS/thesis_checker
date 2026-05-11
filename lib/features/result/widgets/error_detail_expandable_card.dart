import 'package:flutter/material.dart';
import 'package:thesis_checker/core/constants/app_colors.dart';

/// Expandable card that displays a summary row and detailed error information.
class ErrorDetailExpandableCard extends StatefulWidget {
  final String title;
  final String quote;
  final String foundValue;
  final String expectedValue;
  final String iconAsset;

  const ErrorDetailExpandableCard({
    super.key,
    required this.title,
    required this.quote,
    required this.foundValue,
    required this.expectedValue,
    this.iconAsset = 'assets/images/abc.png', // replace with some universal image
  });

  @override
  State<ErrorDetailExpandableCard> createState() => _ErrorDetailExpandableCardState();
}

class _ErrorDetailExpandableCardState extends State<ErrorDetailExpandableCard> {
  bool _isExpanded = false;
  bool _isHovered = false;

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isLightTheme = theme.brightness == Brightness.light;

    final sectionBackground = theme.scaffoldBackgroundColor;
    final borderColor = theme.dividerColor;
    final borderColorHovered = theme.primaryColor;
    final titleColor = theme.textTheme.bodyLarge?.color;
    final subtitleColor = theme.textTheme.bodyMedium?.color;
    final quoteColor = theme.textTheme.bodyLarge?.color;
    final chipTextColor = isLightTheme ? AppColors.error : AppColors.errorLight;
    final expectedChipTextColor = AppColors.ok;
    final highlightBackgroundColor = theme.colorScheme.errorContainer;

    return MouseRegion(
      onEnter: (_) => setState(() => _isHovered = true),
      onExit: (_) => setState(() => _isHovered = false),
      cursor: SystemMouseCursors.click,
      child: GestureDetector(
        onTap: () => setState(() => _isExpanded = !_isExpanded),
        behavior: HitTestBehavior.opaque,
        child: AnimatedContainer(
          duration: const Duration(milliseconds: 200),
          curve: Curves.easeInOut,
          decoration: BoxDecoration(
            color: sectionBackground,
            border: Border.all(color: _isHovered ? borderColorHovered : borderColor),
            borderRadius: BorderRadius.circular(12.0),
          ),
          child: ClipRRect(
            borderRadius: BorderRadius.circular(12.0),
            child: Column(
              children: [
                Padding(
                  padding: const EdgeInsets.all(12.0),
                  child: Row(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      Container(
                        width: 36.0,
                        height: 36.0,
                        decoration: BoxDecoration(
                          color: highlightBackgroundColor,
                          borderRadius: BorderRadius.circular(10.0),
                        ),
                        child: Padding(
                          padding: const EdgeInsets.all(8.0),
                          child: Image.asset(widget.iconAsset),
                        ),
                      ),
                      const SizedBox(width: 12.0),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              widget.title,
                              maxLines: 1,
                              overflow: TextOverflow.ellipsis,
                              style: TextStyle(
                                fontFamily: 'FunnelSans',
                                fontWeight: FontWeight.w700,
                                fontSize: 16.0,
                                color: titleColor,
                              ),
                            ),
                          ],
                        ),
                      ),
                      AnimatedRotation(
                        turns: _isExpanded ? 0.5 : 0.0,
                        duration: const Duration(milliseconds: 220),
                        curve: Curves.easeInOut,
                        child: Text(
                          '▼',
                          style: TextStyle(
                            fontFamily: 'FunnelSans',
                            fontWeight: FontWeight.w700,
                            fontSize: 12.0,
                            color: subtitleColor,
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
                if (_isExpanded)
                  Container(
                    height: 1.0,
                    color: borderColor,
                  ),
                AnimatedSize(
                  duration: const Duration(milliseconds: 220),
                  curve: Curves.easeInOut,
                  child: _isExpanded
                      ? Padding(
                          padding: const EdgeInsets.fromLTRB(12.0, 0.0, 12.0, 12.0),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Container(
                                width: double.infinity,
                                padding: const EdgeInsets.all(12.0),
                                decoration: BoxDecoration(
                                  color: sectionBackground,
                                  borderRadius: BorderRadius.circular(10.0),
                                ),
                                child: Text(
                                  widget.quote,
                                  style: TextStyle(
                                    fontFamily: 'FunnelSans',
                                    fontSize: 14.0,
                                    height: 1.4,
                                    fontWeight: FontWeight.w600,
                                    color: quoteColor,
                                  ),
                                ),
                              ),
                              const SizedBox(height: 10.0),
                              Wrap(
                                spacing: 8.0,
                                runSpacing: 8.0,
                                crossAxisAlignment: WrapCrossAlignment.center,
                                children: [
                                  _pill(
                                    context: context,
                                    text: widget.foundValue,
                                    background: theme.colorScheme.errorContainer,
                                    textColor: chipTextColor,
                                  ),
                                  Text(
                                    '→ має бути',
                                    style: TextStyle(
                                      fontFamily: 'FunnelSans',
                                      fontWeight: FontWeight.w600,
                                      fontSize: 14.0,
                                      color: subtitleColor,
                                    ),
                                  ),
                                  _pill(
                                    context: context,
                                    text: widget.expectedValue,
                                    background: AppColors.ok.withAlpha(isLightTheme ? 40 : 55),
                                    textColor: expectedChipTextColor,
                                  ),
                                ],
                              ),
                            ],
                          ),
                        )
                      : const SizedBox.shrink(),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _pill({
    required BuildContext context,
    required String text,
    required Color background,
    required Color? textColor,
  }) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10.0, vertical: 4.0),
      decoration: BoxDecoration(
        color: background,
        borderRadius: BorderRadius.circular(10.0),
      ),
      child: Text(
        text,
        style: TextStyle(
          fontFamily: 'FunnelSans',
          fontWeight: FontWeight.w700,
          fontSize: 13.0,
          color: textColor,
        ),
      ),
    );
  }
}
