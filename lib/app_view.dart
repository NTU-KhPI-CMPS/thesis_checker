import 'package:flutter/material.dart';
import 'package:flutter_app/core/theme/app_theme.dart';
import 'package:flutter_app/core/theme/theme_cubit.dart';
import 'package:flutter_app/features/home/view/home_view.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

/// Root view that configures the app-level [MaterialApp].
class AppView extends StatelessWidget {
  const AppView({super.key});

  @override
  Widget build(BuildContext context) {
    return BlocBuilder<ThemeCubit, ThemeState>(
      builder: (context, state) {
        return MaterialApp(
          theme: AppTheme.lightTheme,
          darkTheme: AppTheme.darkTheme,
          themeMode: state is ThemeLight ? ThemeMode.light : ThemeMode.dark,
          debugShowCheckedModeBanner: false,
          home: const HomeView(),
        );
      },
    );
  }
}
