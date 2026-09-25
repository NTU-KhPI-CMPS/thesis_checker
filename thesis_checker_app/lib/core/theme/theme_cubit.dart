import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';
import 'package:thesis_checker/data/repositories/application_preferences_repository.dart';

part 'theme_state.dart';

/// Cubit that manages application theme state (light or dark).
class ThemeCubit extends Cubit<ThemeState> {
  final ApplicationPreferencesRepository _appPreferences;

  ThemeCubit(this._appPreferences) : super(_mapThemeModeToThemeState(
      _appPreferences.getApplicationTheme()));

  static ThemeState _mapThemeModeToThemeState(ThemeMode theme) {
    if (theme == ThemeMode.light) {
      return ThemeLight();
    } else if (theme == ThemeMode.dark) {
      return ThemeDark();
    } else {
      return ThemeSystem();
    }
  }

  void toggleTheme() {
    ThemeMode selectedTheme;

    if (state is ThemeLight) {
      selectedTheme = ThemeMode.dark;
      emit(ThemeDark());
    } else if (state is ThemeDark) {
      selectedTheme = ThemeMode.system;
      emit(ThemeSystem());
    } else {
      selectedTheme = ThemeMode.light;
      emit(ThemeLight());
    }

    _appPreferences.saveApplicationTheme(selectedTheme);
  }
}
