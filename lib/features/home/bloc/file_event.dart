part of 'file_bloc.dart';

/// Base class for all file-related events.
sealed class FileEvent extends Equatable {
  const FileEvent();

  @override
  List<Object> get props => [];
}

/// Event fired when a file path is provided by drag-and-drop or picker.
final class FileDroppedEvent extends FileEvent {
  final String filePath;
  final String fileName;
  final List<String>? selectedChecks;
  final List<String>? selectedCategories;

  const FileDroppedEvent(
    this.filePath,
    this.fileName, {
    this.selectedChecks,
    this.selectedCategories,
  });

  @override
  List<Object> get props => [filePath, fileName, selectedChecks ?? [], selectedCategories ?? []];
}

/// Event to reset the file state to initial
final class ResetFileEvent extends FileEvent {}
