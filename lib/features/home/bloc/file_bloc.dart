import 'package:flutter_app/core/utils/file_extensions.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';

part 'file_event.dart';
part 'file_state.dart';

/// BLoC that validates dropped files and emits upload states.
class FileBloc extends Bloc<FileEvent, FileState> {
  FileBloc() : super(FileInitial()) {
    on<FileDroppedEvent>(_onFileDroppedEvent);
    on<ResetFileEvent>((event, emit) => emit(FileInitial()));
  }

  void _onFileDroppedEvent(FileDroppedEvent e, Emitter emit) {
    final fileExtension = e.fileName.split('.').last;
    if (FileExtensions.isSupported(fileExtension)) {
      emit(FileUploadedState(
        filePath: e.filePath,
        fileName: e.fileName,
        checkedOptions: e.checkedOptions,
      ));
    } else {
      emit(FileUploadErrorState(error: 'Файл з розширенням $fileExtension не підтримується'));
    }
  }
}
