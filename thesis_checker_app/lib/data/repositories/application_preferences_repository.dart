import 'package:flutter/material.dart';
import 'package:thesis_checker/data/services/application_preferences_service.dart';

class ApplicationPreferencesRepository {
  final ApplicationPreferencesService _prefService;

  const ApplicationPreferencesRepository(this._prefService);

  Future<void> saveApplicationTheme(ThemeMode theme) async {
    String themeValue = _mapThemeModeToString(theme);
    await _prefService.saveApplicationTheme(themeValue);
  }

  ThemeMode getApplicationTheme() {
    return _mapStringToThemeMode(_prefService.getApplicationTheme());
  }

  ThemeMode _mapStringToThemeMode(String? theme) {
    if (theme == ThemeMode.light.name) {
      return ThemeMode.light;
    } else if (theme == ThemeMode.dark.name) {
      return ThemeMode.dark;
    } else {
      return ThemeMode.system;
    }
  }

  String _mapThemeModeToString(ThemeMode? theme) {
    if (theme == ThemeMode.light) {
      return ThemeMode.light.name;
    } else if (theme == ThemeMode.dark) {
      return ThemeMode.dark.name;
    } else {
      return ThemeMode.system.name;
    }
  }
}
