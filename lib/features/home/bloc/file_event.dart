part of 'file_bloc.dart';

sealed class FileEvent extends Equatable {
  const FileEvent();

  @override
  List<Object> get props => [];
}

final class FileDroppedEvent extends FileEvent {
  final String filePath;

  const FileDroppedEvent(this.filePath);

  @override
  List<Object> get props => [filePath];
}
