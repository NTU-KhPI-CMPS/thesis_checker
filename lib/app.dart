import 'package:flutter/material.dart';
import 'package:flutter_app/app_view.dart';
import 'package:flutter_app/core/theme/theme_cubit.dart';
import 'package:flutter_app/features/home/bloc/file_bloc.dart';
import 'package:flutter_app/features/loading_analysis/bloc/analysis_bloc.dart';
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
          create: (_) => AnalysisBloc()
        ),
      ],
      child: const AppView(),
    );
  }
}
