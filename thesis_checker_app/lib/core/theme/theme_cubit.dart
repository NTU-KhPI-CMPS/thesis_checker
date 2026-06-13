import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';

part 'theme_state.dart';

/// Cubit that manages application theme state (light or dark).
class ThemeCubit extends Cubit<ThemeState> {
  ThemeCubit() : super(ThemeLight());

  void toggleTheme() {
    if (state is ThemeLight) {
      emit(ThemeDark());
    } else {
      emit(ThemeLight());
    }
  }
}
