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
  final List<String>? selectedCategories;

  const StartAnalysisEvent.onlyPath(this.filePath)
      : checkedOptions = const <String>[],
        selectedCategories = null;

  const StartAnalysisEvent.withOptions({
    required this.filePath,
    required this.checkedOptions,
    this.selectedCategories,
  });

  @override
  List<Object> get props => [filePath, checkedOptions, selectedCategories ?? []];
}
