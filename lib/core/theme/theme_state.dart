part of 'theme_cubit.dart';

sealed class ThemeState extends Equatable {
  const ThemeState();

  @override
  List<Object> get props => [];
}


final class ThemeLight extends ThemeState {}

final class ThemeDark extends ThemeState {}
