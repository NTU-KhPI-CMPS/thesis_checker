import 'package:flutter_app/features/loading_analysis/services/runner_java_service.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';

part 'analysis_event.dart';
part 'analysis_state.dart';

/// BLoC that manages document analysis state and progress.
class AnalysisBloc extends Bloc<AnalysisEvent, AnalysisState> {
  RunnerJavaService runnerJavaService;
  AnalysisBloc({required this.runnerJavaService}) : super(AnalysisInitial()) {
    on<StartAnalysisEvent>(_onStartAnalysis);
    on<ResetAnalysisEvent>((event, emit) => emit(AnalysisInitial()));
  }

  Future<void> _onStartAnalysis(StartAnalysisEvent event, Emitter<AnalysisState> emit) async {
    try {
      emit(AnalysisInProgressState());
      await runnerJavaService.checkFile(event.filePath);
      
      // For demonstration, we just return a success message
      emit(const AnalysisSuccessState(result: "Analysis completed successfully!"));
    } catch (e) {
      emit(const AnalysisFailureState(error: "Analysis failed."));
    }
  }
}
