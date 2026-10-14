import 'dart:ui';

/// Enumeration representing the supported localizations in the application.
enum ApplicationLocalization {
  ukrainian(
    Locale('uk'),
    'assets/images/flag_ua.png',
    'Українська'
  );

  final Locale locale;
  final String iconPath;
  final String text;

  const ApplicationLocalization(this.locale, this.iconPath, this.text);
}
