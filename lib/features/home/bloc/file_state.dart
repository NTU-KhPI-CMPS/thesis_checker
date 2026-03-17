part of 'file_bloc.dart';

/// Base class for all file upload states.
sealed class FileState extends Equatable {
  const FileState();
  
  @override
  List<Object> get props => [];
}

/// Initial state before a file has been validated.
final class FileInitial extends FileState {}

/// State emitted when a supported file has been accepted.
final class FileUploadedState extends FileState {
  final String filePath;
  final DateTime _timestamp;

  FileUploadedState({required this.filePath})
    : _timestamp = DateTime.now();

  @override
  List<Object> get props => [filePath, _timestamp];
}

/// State emitted when file validation fails.
final class FileUploadErrorState extends FileState {
  final String error;
  final DateTime _timestamp;

  FileUploadErrorState({required this.error})
    : _timestamp = DateTime.now();

  @override
  List<Object> get props => [error, _timestamp];
}
