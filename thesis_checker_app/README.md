# Thesis Checker App

Flutter desktop application for checking student academic works and their formatting.

## What this app does

- Opens `.docx` files (file picker or drag-and-drop)
- Runs formatting analysis through a native checker library in `assets/checker/`
- Shows issues for student academic works: font, font size, alignment, line spacing, paragraph spacing, and first-line indentation
- Supports light and dark theme

## Project structure

- `lib/` - Flutter UI and state management (BLoC/Cubit)
- `assets/checker/` - native checker binaries loaded by the app (`.dylib`, `.dll`, `.so`)
- `assets/images/`, `assets/fonts/` - app assets
- `tools/` - tools for application signing

## Requirements

- Flutter SDK compatible with Dart `^3.9.2`
- For desktop builds:
  - macOS: Xcode command line tools
  - Windows: Visual Studio with Desktop C++ workload

## Run locally

```bash
cd thesis_checker/thesis_checker_app
flutter pub get
flutter run -d macos
```

If you build for another desktop target, replace the device flag accordingly.

## Tests

```bash
cd thesis_checker/thesis_checker_app
flutter test
```

## Native checker integration

This Flutter app expects native checker libraries inside:

- `thesis_checker_app/assets/checker/`

The Java project copies generated native artifacts during Maven packaging. In your Java `pom.xml`, `outputDirectory` should point to this folder (using a path valid for your current directory layout).

Example when projects are siblings:

```xml
<outputDirectory>${project.basedir}/../thesis_checker_app/assets/checker</outputDirectory>
```
