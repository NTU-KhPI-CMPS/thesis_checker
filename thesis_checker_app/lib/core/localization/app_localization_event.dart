part of 'app_localization_bloc.dart';

/// Event classes for the [AppLocalizationBloc].
sealed class AppLocalizationEvent extends Equatable {
  const AppLocalizationEvent();
}

class ChangeAppLocalizationEvent extends AppLocalizationEvent {
  final ApplicationLocalization selectedLocalization;

  const ChangeAppLocalizationEvent({required this.selectedLocalization});

  @override
  List<Object?> get props => [selectedLocalization];
}

class GetAppLocalizationEvent extends AppLocalizationEvent {
  @override
  List<Object?> get props => [];
}
