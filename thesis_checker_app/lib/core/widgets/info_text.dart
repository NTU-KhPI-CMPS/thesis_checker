import 'package:flutter/material.dart';

class InfoText extends StatelessWidget {
  final String text;

  const InfoText({super.key, required this.text});

  @override
  Widget build(BuildContext context) {
    final textColor2 = Theme.of(context).textTheme.bodyMedium?.color;

    return Text(
      text.toUpperCase(),
      style: TextStyle(
        fontFamily: 'FunnelSans',
        fontSize: 13.0,
        fontWeight: FontWeight.w600,
        color: textColor2,
        letterSpacing: 0.5,
      ),
    );
  }
}
