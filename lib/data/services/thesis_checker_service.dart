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
    ffi.Int32 numberOfChecks,
    ffi.Pointer<ffi.Pointer<Utf8>> checkCodes,
);
typedef RunChecksDart = int Function(
    ffi.Pointer<GraalIsolateThread> thread,
    int numberOfFiles,
    ffi.Pointer<ffi.Pointer<Utf8>> filePaths,
    ffi.Pointer<Utf8> resultDirectory,
    int numberOfChecks,
    ffi.Pointer<ffi.Pointer<Utf8>> checkCodes,
);

class ThesisCheckerService {
  late final ffi.DynamicLibrary _dylib;
  late final RunChecksDart _runChecksFunc;
  ffi.Pointer<GraalIsolateThread>? _threadPtr;
  bool get isInitialized => _threadPtr != null;

  Future<int> runThesisChecks({
    required List<String> files,
    required String resultDirectory,
    required List<String> selectedChecks,
  }) async {
    if (!isInitialized) {
        _init();
    }

    final fileCount = files.length;
    final pointerArray = calloc<ffi.Pointer<Utf8>>(fileCount);
    for (int i = 0; i < fileCount; i++) {
      pointerArray[i] = files[i].toNativeUtf8();
    }

    final checks = selectedChecks;
    final checkCount = checks.length;
    final checksArray = checkCount > 0
        ? calloc<ffi.Pointer<Utf8>>(checkCount)
        : ffi.nullptr;
    for (int i = 0; i < checkCount; i++) {
      checksArray[i] = checks[i].toNativeUtf8();
    }

    final resultDirC = resultDirectory.toNativeUtf8();

    try {
      return _runChecksFunc(
        _threadPtr!,
        fileCount,
        pointerArray,
        resultDirC,
        checkCount,
        checksArray,
      );
    } finally {
      for (int i = 0; i < fileCount; i++) {
        calloc.free(pointerArray[i]);
      }
      calloc.free(pointerArray);

      for (int i = 0; i < checkCount; i++) {
        calloc.free(checksArray[i]);
      }
      if (checkCount > 0) {
        calloc.free(checksArray);
      }

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
          'checker',
          'java-thesis-checker.dylib',
        );
      case "windows":
        return p.join(
          executableDir,
          'data',
          'flutter_assets',
          'assets',
          'checker',
          'java-thesis-checker.dll',
        );
      case "linux":
        return p.join(
          executableDir,
          'data',
          'flutter_assets',
          'assets',
          'checker',
          'libjava-thesis-checker.so',
        );
      default:
        throw UnsupportedError('Unsupported platform for FFI.');
    }
  }
}
