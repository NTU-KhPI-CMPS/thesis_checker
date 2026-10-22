enum AnalysisErrorType { finishedWithError, couldNotComplete }

class AnalysisException implements Exception {
  final AnalysisErrorType type;
  const AnalysisException(this.type);

  @override
  String toString() => 'AnalysisException($type)';
}
