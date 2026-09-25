import 'package:get_it/get_it.dart';
import 'package:thesis_checker/core/theme/theme_cubit.dart';
import 'package:thesis_checker/data/repositories/analysis_repository.dart';
import 'package:thesis_checker/data/services/runner_java_service.dart';
import 'package:thesis_checker/data/services/thesis_checker_service.dart';
import 'package:thesis_checker/features/home/bloc/file_bloc.dart';
import 'package:thesis_checker/features/loading_analysis/bloc/analysis_bloc.dart';
import 'package:thesis_checker/features/result/cubit/result_cubit.dart';

GetIt getIt = GetIt.instance;

void setupLocator() {
  // init services
  getIt.registerSingleton<ThesisCheckerService>(ThesisCheckerService());
  getIt.registerSingleton<RunnerJavaService>(RunnerJavaService(
      thesisCheckerService: getIt<ThesisCheckerService>()));

  // init repositories
  getIt.registerSingleton<AnalysisRepository>(AnalysisRepository(
      runnerJavaService: getIt<RunnerJavaService>()));

  // init BLoC's and Cubit's
  getIt.registerSingleton<ThemeCubit>(ThemeCubit());
  getIt.registerSingleton<ResultCubit>(ResultCubit());

  getIt.registerSingleton<FileBloc>(FileBloc());
  getIt.registerSingleton<AnalysisBloc>(AnalysisBloc(
      analysisRepository: getIt<AnalysisRepository>(),
      resultCubit: getIt<ResultCubit>()));
}
