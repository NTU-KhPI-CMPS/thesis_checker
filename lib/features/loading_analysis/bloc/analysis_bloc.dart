import 'package:thesis_checker/data/repositories/analysis_repository.dart';
import 'package:thesis_checker/models/analysis_result.dart';
import 'package:thesis_checker/features/result/cubit/result_cubit.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';

part 'analysis_event.dart';
part 'analysis_state.dart';

/// BLoC that manages document analysis state and progress.
class AnalysisBloc extends Bloc<AnalysisEvent, AnalysisState> {
  final AnalysisRepository analysisRepository;
  final ResultCubit resultCubit;

  AnalysisBloc({required this.analysisRepository, required this.resultCubit})
      : super(AnalysisInitial()) {
    on<StartAnalysisEvent>(_onStartAnalysis);
  }

  Future<void> _onStartAnalysis(StartAnalysisEvent event, Emitter<AnalysisState> emit) async {
    try {
      emit(AnalysisInProgressState());
      final AnalysisResult analysisResult = await analysisRepository.checkFile(event.filePath);

      resultCubit.setResult(analysisResult);
      emit(AnalysisDoneState());
    } catch (e) {
      final errorMessage = e.toString().replaceFirst('Exception: ', '').trim();
      emit(AnalysisFailureState(error: errorMessage));
    }
  }
}
