import 'package:flutter/material.dart';
import 'package:flutter_app/app_view.dart';
import 'package:flutter_app/features/home/bloc/file_bloc.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

class App extends StatelessWidget {
  const App({super.key});

  @override
  Widget build(BuildContext context) {
    return MultiBlocProvider(
      providers: [
        BlocProvider(
          create: (_) => FileBloc(),
        ),
      ],
      child: const AppView(),
    );
  }
}
