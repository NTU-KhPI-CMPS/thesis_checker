import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';

part 'file_event.dart';
part 'file_state.dart';

class FileBloc extends Bloc<FileEvent, FileState> {
  FileBloc() : super(FileInitialState()) {
    on<FilePickedEvent>(_onFilePicked);
  }

  void _onFilePicked(FilePickedEvent e, Emitter emit) {
    if (!e.fileName.endsWith('.docx')) {
      emit(const FileErrorState('Підтримуються лише файли формату .docx'));
      return;
    }
    emit(FileLoadedState(e.filePath, e.fileName));
  }
}
