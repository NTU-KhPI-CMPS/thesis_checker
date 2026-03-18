part of 'analysis_bloc.dart';

/// Base class for all analysis-related states.
sealed class AnalysisState extends Equatable {
  const AnalysisState();
  
  @override
  List<Object> get props => [];
}

/// Initial state before analysis starts.
final class AnalysisInitial extends AnalysisState {}

/// State emitted when analysis is currently processing.
final class AnalysisInProgressState extends AnalysisState {}

/// State emitted when analysis completes successfully with a result.
final class AnalysisSuccessState extends AnalysisState {
  final String result;
  
  const AnalysisSuccessState({required this.result});
  
  @override
  List<Object> get props => [result];
}

/// State emitted when analysis fails with an error message.
final class AnalysisFailureState extends AnalysisState {
  final String error;
  
  const AnalysisFailureState({required this.error});
  
  @override
  List<Object> get props => [error];
}
