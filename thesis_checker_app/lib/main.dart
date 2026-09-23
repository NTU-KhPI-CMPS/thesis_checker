import 'package:flutter/material.dart';
import 'package:thesis_checker/app.dart';
import 'package:thesis_checker/core/di/injection_container.dart';
import 'package:window_manager/window_manager.dart';

/// Entry point of the application.
void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await windowManager.ensureInitialized();

  setupLocator();

  WindowOptions windowOptions = const WindowOptions(
    size: Size(800, 600),
    minimumSize: Size(400, 300),
    center: true,
    title: 'Thesis Checker',
  );
  windowManager.waitUntilReadyToShow(windowOptions, () async {
    await windowManager.show();
    await windowManager.focus();
  });

  runApp(const App());
}
