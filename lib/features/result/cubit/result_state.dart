import 'package:equatable/equatable.dart';
import 'package:flutter_app/features/result/models/analysis_result.dart';

sealed class ResultState extends Equatable {
  const ResultState();

  @override
  List<Object?> get props => [];
}

final class ResultInitial extends ResultState {}

final class ResultLoaded extends ResultState {
  final AnalysisResult result;

  const ResultLoaded({required this.result});

  @override
  List<Object?> get props => [result];
}
