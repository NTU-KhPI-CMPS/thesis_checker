/// Enum representing the types of analysis errors that can occur.
enum AnalysisErrorType { finishedWithError, couldNotComplete }

/// Exception class for analysis errors.
class AnalysisException implements Exception {
  final AnalysisErrorType type;
  const AnalysisException(this.type);

  @override
  String toString() => 'AnalysisException($type)';
}
