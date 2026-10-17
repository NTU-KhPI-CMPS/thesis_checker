part of 'app_localization_bloc.dart';

/// State class for the [AppLocalizationBloc].
class AppLocalizationState extends Equatable {
  final ApplicationLocalization selectedLocalization;

  const AppLocalizationState({ApplicationLocalization? localization})
      : selectedLocalization = localization ?? ApplicationLocalization.ukrainian;

  @override
  List<Object?> get props => [selectedLocalization];

  AppLocalizationState copyWith({ApplicationLocalization? selectedLocalization}) {
    return AppLocalizationState(
      localization: selectedLocalization ?? this.selectedLocalization
    );
  }
}
