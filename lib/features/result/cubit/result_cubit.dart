import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_app/features/result/models/analysis_result.dart';
import 'package:flutter_app/features/result/cubit/result_state.dart';

class ResultCubit extends Cubit<ResultState> {
  ResultCubit() : super(ResultInitial());

  void setResult(AnalysisResult result) {
    emit(ResultLoaded(result: result));
  }
}
