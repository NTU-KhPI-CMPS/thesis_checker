import 'dart:io' show Platform;
import 'dart:ffi' as ffi;

import 'package:ffi/ffi.dart';
import 'package:path/path.dart' as p;

final class GraalIsolate extends ffi.Opaque {}
final class GraalIsolateThread extends ffi.Opaque {}
final class GraalCreateIsolateParams extends ffi.Opaque {}

typedef CreateIsolateC = ffi.Int32 Function(
    ffi.Pointer<GraalCreateIsolateParams> params,
    ffi.Pointer<ffi.Pointer<GraalIsolate>> isolate,
    ffi.Pointer<ffi.Pointer<GraalIsolateThread>> thread,
);
typedef CreateIsolateDart = int Function(
    ffi.Pointer<GraalCreateIsolateParams> params,
    ffi.Pointer<ffi.Pointer<GraalIsolate>> isolate,
    ffi.Pointer<ffi.Pointer<GraalIsolateThread>> thread,
);

typedef RunChecksC = ffi.Int32 Function(
    ffi.Pointer<GraalIsolateThread> thread,
    ffi.Int32 numberOfFiles,
    ffi.Pointer<ffi.Pointer<Utf8>> filePaths,
    ffi.Pointer<Utf8> resultDirectory,
);
typedef RunChecksDart = int Function(
    ffi.Pointer<GraalIsolateThread> thread,
    int numberOfFiles,
    ffi.Pointer<ffi.Pointer<Utf8>> filePaths,
    ffi.Pointer<Utf8> resultDirectory,
);

class ThesisCheckerService {
  late final ffi.DynamicLibrary _dylib;
  late final RunChecksDart _runChecksFunc;
  ffi.Pointer<GraalIsolateThread>? _threadPtr;
  bool get isInitialized => _threadPtr != null;

  Future<int> runThesisChecks({
    required List<String> files,
    required String resultDirectory,
  }) async {
    if (!isInitialized) {
        _init();
    }

    int length = files.length;
    final pointerArray = calloc<ffi.Pointer<Utf8>>(length);
    for (int i = 0; i < length; i++) {
      pointerArray[i] = files[i].toNativeUtf8();
    }

    final resultDirC = resultDirectory.toNativeUtf8();

    try {
      return _runChecksFunc(_threadPtr!, length, pointerArray, resultDirC);
    } finally {
      for (int i = 0; i < length; i++) {
        calloc.free(pointerArray[i]);
      }
      calloc.free(pointerArray);

      calloc.free(resultDirC);
    }
  }

  void _init() {
    _dylib = ffi.DynamicLibrary.open(_getLibPath());

    final createIsolate = _dylib.lookupFunction<CreateIsolateC, CreateIsolateDart>('graal_create_isolate');
    final isolatePtr = calloc<ffi.Pointer<GraalIsolate>>();
    final threadPtr = calloc<ffi.Pointer<GraalIsolateThread>>();

    final result = createIsolate(ffi.nullptr, isolatePtr, threadPtr);
    if (result != 0) {
      throw Exception('Failed to initialize GraalVM isolate.');
    }

    _threadPtr = threadPtr.value;
    _runChecksFunc = _dylib.lookupFunction<RunChecksC, RunChecksDart>('run_thesis_checks');

    calloc.free(isolatePtr);
    calloc.free(threadPtr);
  }

  String _getLibPath() {
    final executableDir = p.dirname(Platform.resolvedExecutable);

    switch (Platform.operatingSystem) {
      case "macos":
        return p.join(
          executableDir,
          '..',
          'Frameworks',
          'App.framework',
          'Resources',
          'flutter_assets',
          'assets',
          'java-thesis-checker.dylib',
        );
      case "windows":
        return p.join(
          executableDir,
          'data',
          'flutter_assets',
          'assets',
          'java-thesis-checker.dll',
        );
      case "linux":
        return p.join(
          executableDir,
          'data',
          'flutter_assets',
          'assets',
          'libjava-thesis-checker.so',
        );
      default:
        throw UnsupportedError('Unsupported platform for FFI.');
    }
  }
}
