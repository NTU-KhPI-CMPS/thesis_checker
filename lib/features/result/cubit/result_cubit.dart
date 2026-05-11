import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:thesis_checker/models/analysis_result.dart';
import 'package:thesis_checker/features/result/cubit/result_state.dart';

class ResultCubit extends Cubit<ResultState> {
  ResultCubit() : super(ResultInitial());

  void setResult(AnalysisResult result) {
    emit(ResultLoaded(result: result));
  }
}
