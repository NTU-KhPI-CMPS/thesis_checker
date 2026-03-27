import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';

part 'analysis_event.dart';
part 'analysis_state.dart';

/// BLoC that manages document analysis state and progress.
class AnalysisBloc extends Bloc<AnalysisEvent, AnalysisState> {
  AnalysisBloc() : super(AnalysisInitial()) {
    on<StartAnalysisEvent>(_onStartAnalysis);
    on<ResetAnalysisEvent>((event, emit) => emit(AnalysisInitial()));
  }

  Future<void> _onStartAnalysis(AnalysisEvent event, Emitter<AnalysisState> emit) async {
    emit(AnalysisInProgressState());
    try {
      // Simulate analysis process
      await Future.delayed(const Duration(seconds: 5));
      
      // For demonstration, we just return a success message
      emit(const AnalysisSuccessState(result: "Analysis completed successfully!"));
    } catch (e) {
      emit(const AnalysisFailureState(error: "Analysis failed."));
    }
  }
}
