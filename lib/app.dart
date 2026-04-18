import 'package:flutter/material.dart';
import 'package:thesis_checker/app_view.dart';
import 'package:thesis_checker/core/theme/theme_cubit.dart';
import 'package:thesis_checker/features/home/bloc/file_bloc.dart';
import 'package:thesis_checker/features/loading_analysis/bloc/analysis_bloc.dart';
import 'package:thesis_checker/data/repositories/analysis_repository.dart';
import 'package:thesis_checker/features/result/cubit/result_cubit.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

/// Top-level app widget that injects global dependencies.
class App extends StatelessWidget {
  const App({super.key});

  @override
  Widget build(BuildContext context) {
    return MultiBlocProvider(
      providers: [
        BlocProvider(
          create:  (_) => ThemeCubit()
        ),
        BlocProvider(
          create: (_) => FileBloc(),
        ),
        BlocProvider(
          create: (_) => ResultCubit(),
        ),
        BlocProvider(
          create: (context) => AnalysisBloc(
            analysisRepository: AnalysisRepository(),
            resultCubit: context.read<ResultCubit>(),
          )
        ),
      ],
      child: const AppView(),
    );
  }
}
