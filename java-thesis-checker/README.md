# Java Thesis Checker

Java module for checking formatting of student academic works in `.docx` files.

## What this module does

- Parses `.docx` documents with Apache POI
- Applies formatting checks for student academic works
- Generates structured validation results
- Builds a native library (`.dylib`, `.dll`, `.so`) for Flutter integration

## Project structure

- `src/main/java/com/cmps/thesischecker/` - entrypoint and orchestration (`Main`)
- `src/main/java/com/cmps/thesischecker/checker/` - checker interface and implementations
- `src/main/java/com/cmps/thesischecker/model/` - report/error models
- `src/main/java/com/cmps/thesischecker/requirements/` - formatting requirements configuration
- `src/main/java/com/cmps/thesischecker/argparser/` - CLI argument parsers
- `src/main/java/com/cmps/thesischecker/utils/` - utility classes

## Requirements

- Maven 3.9+
- GraalVM Native Image toolchain (for native library build)

## Build

From `java-thesis-checker/` run:

```bash
mvn clean package
```

This build also runs native compilation via `native-maven-plugin` and produces native artifacts in `target/`.

## Run tests

```bash
mvn test
```

## Integration with Flutter app

The Maven resources plugin copies native libraries to Flutter assets directory:

- `thesis_checker_app/assets/checker/`

Current `pom.xml` output directory (when Java and Flutter projects are siblings):

```xml
<outputDirectory>${project.basedir}/../thesis_checker_app/assets/checker</outputDirectory>
```
