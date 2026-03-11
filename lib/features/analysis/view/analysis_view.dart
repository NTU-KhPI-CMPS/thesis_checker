import 'package:flutter/material.dart';
import 'package:flutter_app/core/constants/app_colors.dart';
import 'package:flutter_app/features/analysis/widgets/dual_ring_indicator.dart';
import 'package:flutter_app/features/home/bloc/file_bloc.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

class AnalysisView extends StatelessWidget {
  const AnalysisView({super.key});

  @override
  Widget build(BuildContext context) {
    return BlocBuilder<FileBloc, FileState>(
      builder: (context, state) {
        final fileName = state is FileLoadedState ? state.fileName : 'назва_документу.docx';
        return Center(
          child: SizedBox(
            width: 260.0,
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              mainAxisSize: MainAxisSize.min,
              children: [
                DualRingIndicator(),
                SizedBox(height: 24.0),
                Text(
                  'Аналізую документ...',
                  style: TextStyle(
                    color: AppColors.text,
                    fontFamily: 'FunnelSans',
                    fontSize: 20.0,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                SizedBox(height: 8.0),
                Text(
                  '$fileName · n сторінок',
                  style: TextStyle(
                    fontFamily: 'FunnelSans',
                    fontSize: 14.0,
                    fontWeight: FontWeight.w600,
                    color: AppColors.text2,
                  ),
                ),
                SizedBox(height: 16.0),
                LinearProgressIndicator(
                  borderRadius: BorderRadius.circular(4.0),
                  value: 0.8,
                  backgroundColor: AppColors.border,
                  valueColor: AlwaysStoppedAnimation<Color>(AppColors.accent),
                ),
              ],
            ),
          ),
        );
      },
    );
  }
}
