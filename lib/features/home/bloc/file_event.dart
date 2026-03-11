part of 'file_bloc.dart';

sealed class FileEvent extends Equatable {
  const FileEvent();

  @override
  List<Object> get props => [];
}

final class FilePickedEvent extends FileEvent {
  final String filePath;
  final String fileName;

  const FilePickedEvent(this.filePath, this.fileName);

  @override
  List<Object> get props => [filePath, fileName];
}
