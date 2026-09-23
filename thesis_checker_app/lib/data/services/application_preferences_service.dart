import 'package:shared_preferences/shared_preferences.dart';

class ApplicationPreferencesService {
  final SharedPreferences _prefs;

  final String _themeKey = 'theme';

  const ApplicationPreferencesService(this._prefs);

  Future<void> saveApplicationTheme(String theme) async {
    await _prefs.setString(_themeKey, theme);
  }

  String? getApplicationTheme() {
    return _prefs.getString(_themeKey);
  }
}
