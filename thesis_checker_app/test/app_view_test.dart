import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mocktail/mocktail.dart';
import 'package:thesis_checker/app_view.dart';
import 'package:thesis_checker/core/theme/theme_cubit.dart';
import 'package:thesis_checker/features/home/bloc/file_bloc.dart';
import 'package:thesis_checker/features/loading_analysis/bloc/analysis_bloc.dart';
import 'package:thesis_checker/features/result/cubit/result_cubit.dart';
import 'package:thesis_checker/features/result/cubit/result_state.dart';

class MockThemeCubit extends Mock implements ThemeCubit {}
class MockFileBloc extends Mock implements FileBloc {}
class MockAnalysisBloc extends Mock implements AnalysisBloc {}
class MockResultCubit extends Mock implements ResultCubit {}

void main() {
  late MockThemeCubit mockThemeCubit;
  late MockFileBloc mockFileBloc;
  late MockAnalysisBloc mockAnalysisBloc;
  late MockResultCubit mockResultCubit;

  setUp(() {
    mockThemeCubit = MockThemeCubit();
    mockFileBloc = MockFileBloc();
    mockAnalysisBloc = MockAnalysisBloc();
    mockResultCubit = MockResultCubit();

    // Arrange: Set up default states for the mocks
    when(() => mockFileBloc.state).thenReturn(FileInitial());
    when(() => mockFileBloc.stream).thenAnswer((_) => Stream.value(FileInitial()));

    when(() => mockAnalysisBloc.state).thenReturn(AnalysisInitial());
    when(() => mockAnalysisBloc.stream).thenAnswer((_) => Stream.value(AnalysisInitial()));

    when(() => mockResultCubit.state).thenReturn(ResultInitial());
    when(() => mockResultCubit.stream).thenAnswer((_) => Stream.value(ResultInitial()));
  });

  Future<void> pumpAppView(WidgetTester tester) async {
    await tester.pumpWidget(
      MultiBlocProvider(
        providers: [
          BlocProvider<ThemeCubit>.value(value: mockThemeCubit),
          BlocProvider<FileBloc>.value(value: mockFileBloc),
          BlocProvider<AnalysisBloc>.value(value: mockAnalysisBloc),
          BlocProvider<ResultCubit>.value(value: mockResultCubit),
        ],
        child: const AppView()
      )
    );
  }

  group('AppView tests', () {
    testWidgets('AppView builds MaterialApp with correct theme mode (Light mode)', (WidgetTester tester) async {
      // 1. Arrange: Set up the ThemeCubit to emit a light theme state
      when(() => mockThemeCubit.state).thenReturn(ThemeLight());
      when(() => mockThemeCubit.stream).thenAnswer((_) => Stream.value(ThemeLight()));

      // 2. Act: Pump the AppView widget
      await pumpAppView(tester);

      // Assert: Verify that a MaterialApp is found and has the correct theme mode
      final materialAppFinder = find.byType(MaterialApp);
      expect(materialAppFinder, findsOneWidget);

      final materialApp = tester.widget<MaterialApp>(materialAppFinder);
      expect(materialApp.themeMode, ThemeMode.light);
    });

    testWidgets('AppView builds MaterialApp with correct theme mode (Dark mode)', (WidgetTester tester) async {
      // 1. Arrange: Set up the ThemeCubit to emit a dark theme state
      when(() => mockThemeCubit.state).thenReturn(ThemeDark());
      when(() => mockThemeCubit.stream).thenAnswer((_) => Stream.value(ThemeDark()));

      // 2. Act: Pump the AppView widget
      await pumpAppView(tester);

      // Assert: Verify that a MaterialApp is found and has the correct theme mode
      final materialAppFinder = find.byType(MaterialApp);
      expect(materialAppFinder, findsOneWidget);

      final materialApp = tester.widget<MaterialApp>(materialAppFinder);
      expect(materialApp.themeMode, ThemeMode.dark);
    });
  });
}
