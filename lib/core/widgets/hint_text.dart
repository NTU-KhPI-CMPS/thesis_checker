import 'package:flutter/material.dart';

class HintText extends StatelessWidget {
  final String text;
  final Color? textColor;

  const HintText({super.key, required this.text, this.textColor});

  @override
  Widget build(BuildContext context) {
    final themeTextColor = Theme.of(context).textTheme.bodyLarge?.color;

    return Text(
      text,
      style: TextStyle(
        color: textColor ?? themeTextColor,
        fontFamily: 'FunnelSans',
        fontSize: 20.0,
        fontWeight: FontWeight.w600,
      ),
    );
  }
}