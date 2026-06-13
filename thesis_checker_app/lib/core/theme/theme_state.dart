part of 'theme_cubit.dart';

/// Base class for all theme-related states.
sealed class ThemeState extends Equatable {
  const ThemeState();

  @override
  List<Object> get props => [];
}


/// State representing light theme mode.
final class ThemeLight extends ThemeState {}

/// State representing dark theme mode.
final class ThemeDark extends ThemeState {}
