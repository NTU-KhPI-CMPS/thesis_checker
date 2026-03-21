import 'package:flutter_app/core/utils/file_extensions.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';

import '../../../core/utils/java_file_check_runner.dart';

part 'file_event.dart';
part 'file_state.dart';

/// BLoC that validates dropped files and emits upload states.
class FileBloc extends Bloc<FileEvent, FileState> {
  FileBloc() : super(FileInitial()) {
    on<FileDroppedEvent>(_onFileDroppedEvent);
  }

  void _onFileDroppedEvent(FileDroppedEvent e, Emitter emit) {
    final fileExtension = e.filePath.split('.').last;
    if (FileExtensions.isSupported(fileExtension)) {
      emit(FileUploadedState(filePath: e.filePath));
      checkFile(e.filePath);
    } else {
      emit(FileUploadErrorState(error: 'Файл з розширенням $fileExtension не підтримується'));
    }
  }
}
