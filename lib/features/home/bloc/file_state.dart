part of 'file_bloc.dart';

sealed class FileState extends Equatable {
  const FileState();
  
  @override
  List<Object> get props => [];
}

final class FileInitialState extends FileState {}

final class FileLoadedState extends FileState {
  final String filePath;
  final String fileName;

  const FileLoadedState(this.filePath, this.fileName);

  @override
  List<Object> get props => [filePath, fileName];
}

final class FileErrorState extends FileState {
  const FileErrorState(this.message);

  final String message;

  @override
  List<Object> get props => [message];
}
