import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:thesis_checker/data/repositories/application_preferences_repository.dart';

/// Cubit that manages application theme state (light or dark).
class ThemeCubit extends Cubit<ThemeMode> {
  final ApplicationPreferencesRepository _appPreferences;

  ThemeCubit(this._appPreferences) : super(
      _appPreferences.getApplicationTheme());

  void toggleTheme() {
    final themeMode = switch (state) {
      ThemeMode.light => ThemeMode.dark,
      ThemeMode.dark => ThemeMode.system,
      ThemeMode.system => ThemeMode.light
    };

    emit(themeMode);

    _appPreferences.saveApplicationTheme(themeMode);
  }
}
