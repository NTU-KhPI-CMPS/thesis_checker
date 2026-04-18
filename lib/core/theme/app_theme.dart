import 'package:flutter/material.dart';
import 'package:thesis_checker/core/constants/app_colors.dart';

/// Application theme definitions for light and dark modes.
class AppTheme {
  static ThemeData get lightTheme {
    return ThemeData(
      brightness: Brightness.light,
      primaryColor: AppColors.accent,
      scaffoldBackgroundColor: AppColors.surface,
      canvasColor: AppColors.bg,
      dividerColor: AppColors.border,
      colorScheme: ColorScheme.light(
        primary: AppColors.accent,
        secondary: AppColors.accent2,
        surface: AppColors.surface2,
        error: AppColors.error,
        errorContainer: AppColors.errorLight,
        onErrorContainer: AppColors.error,
      ),
      appBarTheme: AppBarTheme(
        backgroundColor: AppColors.surface,
        foregroundColor: AppColors.text,
        elevation: 0,
      ),
      textTheme: TextTheme(
        bodyLarge: TextStyle(color: AppColors.text),
        bodyMedium: TextStyle(color: AppColors.text2),
        bodySmall: TextStyle(color: AppColors.text2),
      ),
      inputDecorationTheme: InputDecorationTheme(
        fillColor: AppColors.surface2,
        filled: true,
        border: OutlineInputBorder(
          borderSide: BorderSide(color: AppColors.border),
        ),
        enabledBorder: OutlineInputBorder(
          borderSide: BorderSide(color: AppColors.border),
        ),
      ),
    );
  }

  static ThemeData get darkTheme {
    return ThemeData(
      brightness: Brightness.dark,
      primaryColor: AppColors.accentDark,
      scaffoldBackgroundColor: AppColors.surfaceDark,
      canvasColor: AppColors.bgDark,
      dividerColor: AppColors.borderDark,
      colorScheme: ColorScheme.dark(
        primary: AppColors.accentDark,
        secondary: AppColors.accent2Dark,
        surface: AppColors.surface2Dark,
        error: AppColors.error,
        errorContainer: AppColors.errorDark,
        onErrorContainer: AppColors.textDark,
      ),
      appBarTheme: AppBarTheme(
        backgroundColor: AppColors.surfaceDark,
        foregroundColor: AppColors.textDark,
        elevation: 0,
      ),
      textTheme: TextTheme(
        bodyLarge: TextStyle(color: AppColors.textDark),
        bodyMedium: TextStyle(color: AppColors.text2Dark),
        bodySmall: TextStyle(color: AppColors.text2Dark),
      ),
      inputDecorationTheme: InputDecorationTheme(
        fillColor: AppColors.surface2Dark,
        filled: true,
        border: OutlineInputBorder(
          borderSide: BorderSide(color: AppColors.borderDark),
        ),
        enabledBorder: OutlineInputBorder(
          borderSide: BorderSide(color: AppColors.borderDark),
        ),
      ),
    );
  }
}
