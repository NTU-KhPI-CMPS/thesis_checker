import 'dart:math';

import 'package:flutter/material.dart';
import 'package:flutter_app/core/constants/app_colors.dart';

class DualRingIndicator extends StatefulWidget {
  const DualRingIndicator({
    super.key,
    this.size = 80.0,
    this.strokeWidth = 3.0,
    this.gap = 12.0,
  });

  final double size;
  final double strokeWidth;
  final double gap;

  @override
  State<DualRingIndicator> createState() => _DualRingIndicatorState();
}

class _DualRingIndicatorState extends State<DualRingIndicator>
    with SingleTickerProviderStateMixin {
  late final AnimationController _controller;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1400),
    )..repeat();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _controller,
      builder: (context, _) {
        return CustomPaint(
          size: Size.square(widget.size),
          painter: _DualRingPainter(
            progress: _controller.value,
            strokeWidth: widget.strokeWidth,
            gap: widget.gap,
          ),
        );
      },
    );
  }
}

class _DualRingPainter extends CustomPainter {
  const _DualRingPainter({
    required this.progress,
    required this.strokeWidth,
    required this.gap,
  });

  final double progress;
  final double strokeWidth;
  final double gap;

  static const double _arcSpan = pi / 2;

  @override
  void paint(Canvas canvas, Size size) {
    final center = Offset(size.width / 2, size.height / 2);

    final outerRadius = size.width / 2 - strokeWidth / 2;
    final innerRadius = outerRadius - gap - strokeWidth;

    final trackPaint = Paint()
      ..color = AppColors.border
      ..style = PaintingStyle.stroke
      ..strokeWidth = strokeWidth
      ..strokeCap = StrokeCap.round;

    final outerArcPaint = Paint()
      ..color = AppColors.accent
      ..style = PaintingStyle.stroke
      ..strokeWidth = strokeWidth
      ..strokeCap = StrokeCap.round;

    final innerArcPaint = Paint()
      ..color = AppColors.accent2
      ..style = PaintingStyle.stroke
      ..strokeWidth = strokeWidth
      ..strokeCap = StrokeCap.round;

    canvas.drawCircle(center, outerRadius, trackPaint);
    canvas.drawCircle(center, innerRadius, trackPaint);

    final outerStart = 2 * pi * progress - pi / 2;
    canvas.drawArc(
      Rect.fromCircle(center: center, radius: outerRadius),
      outerStart,
      _arcSpan,
      false,
      outerArcPaint,
    );
    
    final innerStart = -2 * pi * progress - pi / 2;
    canvas.drawArc(
      Rect.fromCircle(center: center, radius: innerRadius),
      innerStart,
      -_arcSpan,
      false,
      innerArcPaint,
    );
  }

  @override
  bool shouldRepaint(_DualRingPainter old) => old.progress != progress;
}
