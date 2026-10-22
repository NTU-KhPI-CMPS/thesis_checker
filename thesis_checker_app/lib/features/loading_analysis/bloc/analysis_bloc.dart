import 'package:thesis_checker/core/errors/analysis_exception.dart';
import 'package:thesis_checker/data/repositories/analysis_repository.dart';
import 'package:thesis_checker/models/analysis_result.dart';
import 'package:thesis_checker/features/result/cubit/result_cubit.dart';
import 'package:thesis_checker/models/check_type_info.dart';
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
      final AnalysisResult analysisResult = await analysisRepository.checkFile(
        event.filePath,
        fileName: event.fileName,
        selectedChecks: event.selectedChecks
      );

      resultCubit.setResult(analysisResult);
      emit(AnalysisDoneState());
    } on AnalysisException catch (e) {
      emit(AnalysisFailureState(type: e.type));
    } catch (e) {
      emit(const AnalysisFailureState(type: AnalysisErrorType.couldNotComplete));
    }
  }
}
