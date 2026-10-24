import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';
import 'package:thesis_checker/data/repositories/application_preferences_repository.dart';
import 'package:thesis_checker/models/application_localization.dart';

part 'app_localization_event.dart';

part 'app_localization_state.dart';

/// Bloc that manages the app's localization state.
class AppLocalizationBloc extends Bloc<AppLocalizationEvent, AppLocalizationState> {
  final ApplicationPreferencesRepository _appPreferences;

  AppLocalizationBloc({required this._appPreferences})
    : super(AppLocalizationState(localization: _appPreferences.getApplicationLocalization())) {
    on<ChangeAppLocalizationEvent>(_onChangeAppLocalization);
    on<GetAppLocalizationEvent>(_onGetAppLocalization);
  }

  Future<void> _onChangeAppLocalization(
    ChangeAppLocalizationEvent event,
    Emitter<AppLocalizationState> emit,
  ) async {
    emit(state.copyWith(selectedLocalization: event.selectedLocalization));
    await _appPreferences.saveApplicationLocalization(
      event.selectedLocalization,
    );
  }

  void _onGetAppLocalization(
    GetAppLocalizationEvent event,
    Emitter<AppLocalizationState> emit,
  ) {
    ApplicationLocalization localization = _appPreferences
        .getApplicationLocalization();
    emit(state.copyWith(selectedLocalization: localization));
  }
}
