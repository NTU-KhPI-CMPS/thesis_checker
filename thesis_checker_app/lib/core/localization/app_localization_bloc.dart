import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';
import 'package:thesis_checker/models/application_localization.dart';

part 'app_localization_event.dart';
part 'app_localization_state.dart';

/// Bloc that manages the app's localization state.
class AppLocalizationBloc extends Bloc<AppLocalizationEvent, AppLocalizationState> {
  AppLocalizationBloc() : super(const AppLocalizationState()) {
    on<ChangeAppLocalizationEvent>(_onChangeAppLocalization);
  }

  void _onChangeAppLocalization(ChangeAppLocalizationEvent event, Emitter<AppLocalizationState> emit) {
    emit(state.copyWith(selectedLocalization: event.selectedLocalization));
  }
}
