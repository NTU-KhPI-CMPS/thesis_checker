import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

/// Manages application settings
class ApplicationPreferencesRepository {
  final SharedPreferences _prefs;
  final String _themeKey = 'theme';

  const ApplicationPreferencesRepository(this._prefs);

  Future<void> saveApplicationTheme(ThemeMode theme) async {
    String themeStringValue = _mapThemeModeToString(theme);
    await _prefs.setString(_themeKey, themeStringValue);
  }

  ThemeMode getApplicationTheme() {
    return _mapStringToThemeMode(_prefs.getString(_themeKey));
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
