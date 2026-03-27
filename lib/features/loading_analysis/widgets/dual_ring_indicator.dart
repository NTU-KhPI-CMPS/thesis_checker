import 'dart:math';
import 'package:flutter/material.dart';

/// Animated dual-ring loading indicator with rotating arcs.
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

/// Internal state for [DualRingIndicator].
class _DualRingIndicatorState extends State<DualRingIndicator> with SingleTickerProviderStateMixin {
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
    final trackColor = Theme.of(context).dividerColor;
    final outerColor = Theme.of(context).colorScheme.primary;
    final innerColor = Theme.of(context).colorScheme.secondary;

    return AnimatedBuilder(
      animation: _controller,
      builder: (context, _) {
        return CustomPaint(
          size: Size.square(widget.size),
          painter: _DualRingPainter(
            progress: _controller.value,
            strokeWidth: widget.strokeWidth,
            gap: widget.gap,
            trackColor: trackColor,
            outerColor: outerColor,
            innerColor: innerColor,
          ),
        );
      },
    );
  }
}

/// Custom painter for rendering the dual-ring animation.
class _DualRingPainter extends CustomPainter {
  const _DualRingPainter({
    required this.progress,
    required this.strokeWidth,
    required this.gap,
    required this.trackColor,
    required this.outerColor,
    required this.innerColor,
  });

  final double progress;
  final double strokeWidth;
  final double gap;
  final Color trackColor;
  final Color outerColor;
  final Color innerColor;

  static const double _arcSpan = pi / 2;

  @override
  void paint(Canvas canvas, Size size) {
    final center = Offset(size.width / 2, size.height / 2);

    final outerRadius = size.width / 2 - strokeWidth / 2;
    final innerRadius = outerRadius - gap - strokeWidth;

    final trackPaint = Paint()
      ..color = trackColor
      ..style = PaintingStyle.stroke
      ..strokeWidth = strokeWidth
      ..strokeCap = StrokeCap.round;

    final outerArcPaint = Paint()
      ..color = outerColor
      ..style = PaintingStyle.stroke
      ..strokeWidth = strokeWidth
      ..strokeCap = StrokeCap.round;

    final innerArcPaint = Paint()
      ..color = innerColor
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
