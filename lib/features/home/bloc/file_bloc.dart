import 'package:flutter_app/core/constants/file_extensions.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';

part 'file_event.dart';
part 'file_state.dart';

class FileBloc extends Bloc<FileEvent, FileState> {
  FileBloc() : super(FileInitial()) {
    on<FileDroppedEvent>(_onFileDroppedEvent);
  }

  void _onFileDroppedEvent(FileDroppedEvent e, Emitter emit) {
    final fileExtension = e.fileName.split('.').last;
    if (FileExtensions.isSupported(fileExtension)) {
      emit(FileUploadedState(
        filePath: e.filePath,
        fileName: e.fileName
      ));
    } else {
      emit(FileUploadErrorState(error: 'Файл з розширенням $fileExtension не підтримується'));
    }
  }
}
