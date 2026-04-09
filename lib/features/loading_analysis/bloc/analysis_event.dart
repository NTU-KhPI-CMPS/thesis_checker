part of 'analysis_bloc.dart';

/// Base class for all analysis-related events.
sealed class AnalysisEvent extends Equatable {
  const AnalysisEvent();

  @override
  List<Object> get props => [];
}

/// Event to start the analysis process with the given file path.
final class StartAnalysisEvent extends AnalysisEvent {
  final String filePath;
  final List<String> checkedOptions;

  const StartAnalysisEvent({required this.filePath, required this.checkedOptions});

  @override
  List<Object> get props => [filePath, checkedOptions];
}

/// Event to reset the analysis state to initial.
final class ResetAnalysisEvent extends AnalysisEvent {}
