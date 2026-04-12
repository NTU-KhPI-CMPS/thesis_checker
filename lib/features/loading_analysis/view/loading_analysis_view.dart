import 'package:flutter/material.dart';
import 'package:flutter_app/features/home/bloc/file_bloc.dart';
import 'package:flutter_app/features/loading_analysis/bloc/analysis_bloc.dart';
import 'package:flutter_app/features/loading_analysis/widgets/dual_ring_indicator.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

/// Displays the analysis progress with animated loading indicator and progress bar.
class LoadingAnalysisView extends StatefulWidget {
  const LoadingAnalysisView({
    super.key,
    this.onAnalysisComplete,
    this.onAnalysisFailed,
  });

  final VoidCallback? onAnalysisComplete;
  final ValueChanged<String>? onAnalysisFailed;

  @override
  State<LoadingAnalysisView> createState() => _LoadingAnalysisViewState();
}

class _LoadingAnalysisViewState extends State<LoadingAnalysisView> with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _animation;

  @override
  void initState() {
    super.initState();

    _controller = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 3),
    );

    _animation = Tween<double>(
      begin: 0.0,
      end: 0.8,
    ).animate(CurvedAnimation(parent: _controller, curve: Curves.easeInOut));

    _controller.forward();
  }

  Future<void> _completeAnimation() async {
    _animation = Tween<double>(
      begin: _animation.value,
      end: 1.0,
    ).animate(CurvedAnimation(parent: _controller, curve: Curves.easeOut));

    _controller
      ..duration = const Duration(milliseconds: 500)
      ..reset();

    await _controller.forward();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final textColor = Theme.of(context).textTheme.bodyLarge?.color;
    final progressBarBgColor = Theme.of(context).dividerColor;
    final prograssBarValueColor = Theme.of(context).colorScheme.primary;
    final additiontalTextColor = Theme.of(context).textTheme.bodyMedium?.color;

    return BlocListener<AnalysisBloc, AnalysisState>(
      listener: (context, state) async {
        if (state is AnalysisDoneState) {
          await _completeAnimation();
          widget.onAnalysisComplete?.call();
        }

        if (state is AnalysisFailureState) {
          await _completeAnimation();
          widget.onAnalysisFailed?.call(state.error);
        }
      },
      child: BlocBuilder<FileBloc, FileState>(
        builder: (context, state) {
          if (state is! FileUploadedState) {
            return Center(
              child: Text(
                'Завантажте, будь ласка, документ для аналізу',
                style: TextStyle(
                  color: textColor,
                  fontFamily: 'FunnelSans',
                  fontSize: 20.0,
                  fontWeight: FontWeight.w600,
                ),
              ),
            );
          }

          return Center(
            child: SizedBox(
              width: 260.0,
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  DualRingIndicator(),
                  SizedBox(height: 24.0),
                  Text(
                    'Аналізую документ...',
                    style: TextStyle(
                      color: textColor,
                      fontFamily: 'FunnelSans',
                      fontSize: 20.0,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                  SizedBox(height: 8.0),
                  Text(
                    state.fileName,
                    style: TextStyle(
                      fontFamily: 'FunnelSans',
                      fontSize: 14.0,
                      fontWeight: FontWeight.w600,
                      color: additiontalTextColor,
                    ),
                  ),
                  SizedBox(height: 16.0),
                  AnimatedBuilder(
                    animation: _animation,
                    builder: (context, child) {
                      return LinearProgressIndicator(
                        borderRadius: BorderRadius.circular(4.0),
                        value: _animation.value,
                        backgroundColor: progressBarBgColor,
                        valueColor: AlwaysStoppedAnimation<Color>(
                          prograssBarValueColor,
                        ),
                      );
                    },
                  ),
                ],
              ),
            ),
          );
        },
      ),
    );
  }
}
