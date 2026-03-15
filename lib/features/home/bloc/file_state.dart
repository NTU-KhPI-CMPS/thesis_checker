part of 'file_bloc.dart';

sealed class FileState extends Equatable {
  const FileState();
  
  @override
  List<Object> get props => [];
}

final class FileInitial extends FileState {}

final class FileUploadedState extends FileState {
  final String filePath;
  final DateTime _timestamp;

  FileUploadedState({required this.filePath})
    : _timestamp = DateTime.now();

  @override
  List<Object> get props => [filePath, _timestamp];
}

final class FileUploadErrorState extends FileState {
  final String error;
  final DateTime _timestamp;

  FileUploadErrorState({required this.error})
    : _timestamp = DateTime.now();

  @override
  List<Object> get props => [error, _timestamp];
}
