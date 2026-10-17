import 'package:flutter/material.dart';
import 'package:thesis_checker/core/localization/app_localization_bloc.dart';
import 'package:thesis_checker/core/theme/app_theme.dart';
import 'package:thesis_checker/core/theme/theme_cubit.dart';
import 'package:thesis_checker/features/home/view/home_view.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:thesis_checker/l10n/l10n.dart';

/// Root view that configures the app-level [MaterialApp].
class AppView extends StatelessWidget {
  const AppView({super.key});

  @override
  Widget build(BuildContext context) {
    final themeState = context.watch<ThemeCubit>().state;
    final localizationState = context.watch<AppLocalizationBloc>().state;

    return MaterialApp(
      locale: localizationState.selectedLocalization.locale,
      theme: AppTheme.lightTheme,
      darkTheme: AppTheme.darkTheme,
      themeMode: themeState,
      supportedLocales: AppLocalizations.supportedLocales,
      localizationsDelegates: AppLocalizations.localizationsDelegates,
      debugShowCheckedModeBanner: false,
      home: const HomeView(),
    );
  }
}
