import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mocktail/mocktail.dart';
import 'package:parameterized_test/parameterized_test.dart';
import 'package:thesis_checker/app_view.dart';
import 'package:thesis_checker/core/localization/app_localization_bloc.dart';
import 'package:thesis_checker/core/theme/theme_cubit.dart';
import 'package:thesis_checker/features/home/bloc/file_bloc.dart';
import 'package:thesis_checker/features/loading_analysis/bloc/analysis_bloc.dart';
import 'package:thesis_checker/features/result/cubit/result_cubit.dart';
import 'package:thesis_checker/features/result/cubit/result_state.dart';
import 'package:thesis_checker/models/application_localization.dart';

class MockThemeCubit extends Mock implements ThemeCubit {}
class MockFileBloc extends Mock implements FileBloc {}
class MockAppLocalizationBloc extends Mock implements AppLocalizationBloc {}
class MockAnalysisBloc extends Mock implements AnalysisBloc {}
class MockResultCubit extends Mock implements ResultCubit {}

void main() {
  late MockThemeCubit mockThemeCubit;
  late MockFileBloc mockFileBloc;
  late MockAppLocalizationBloc mockAppLocalizationBloc;
  late MockAnalysisBloc mockAnalysisBloc;
  late MockResultCubit mockResultCubit;

  setUp(() {
    mockThemeCubit = MockThemeCubit();
    mockFileBloc = MockFileBloc();
    mockAppLocalizationBloc = MockAppLocalizationBloc();
    mockAnalysisBloc = MockAnalysisBloc();
    mockResultCubit = MockResultCubit();

    // Arrange: Set up default states for the mocks
    when(() => mockThemeCubit.state).thenReturn(ThemeMode.system);
    when(() => mockThemeCubit.stream).thenAnswer((_) => Stream.value(ThemeMode.system));

    when(() => mockFileBloc.state).thenReturn(FileInitial());
    when(() => mockFileBloc.stream).thenAnswer((_) => Stream.value(FileInitial()));

    when(() => mockAppLocalizationBloc.state).thenReturn(AppLocalizationState());
    when(() => mockAppLocalizationBloc.stream).thenAnswer((_) => Stream.value(AppLocalizationState()));

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
          BlocProvider<AppLocalizationBloc>.value(value: mockAppLocalizationBloc),
          BlocProvider<AnalysisBloc>.value(value: mockAnalysisBloc),
          BlocProvider<ResultCubit>.value(value: mockResultCubit),
        ],
        child: const AppView()
      )
    );
  }

  parameterizedGroup(
    'AppView theme mode tests',
    ThemeMode.values,
    (ThemeMode theme) {
      testWidgets(
        'AppView builds MaterialApp with correct theme mode (${theme.name} mode)',
        (WidgetTester tester) async {
          // Arrange: Set up the ThemeCubit to emit a chose theme state
          when(() => mockThemeCubit.state).thenReturn(theme);
          when(() => mockThemeCubit.stream).thenAnswer((_) => Stream.value(theme));

          // Act: Pump the AppView widget
          await pumpAppView(tester);

          // Assert: Verify that a MaterialApp is found and has the correct theme mode
          final materialAppFinder = find.byType(MaterialApp);
          expect(materialAppFinder, findsOneWidget);

          final materialApp = tester.widget<MaterialApp>(materialAppFinder);
          expect(materialApp.themeMode, theme);
        }
      );
    }
  );

  parameterizedGroup(
    'AppView localization tests',
    ApplicationLocalization.values,
    (ApplicationLocalization selectedLocalization) {
      testWidgets(
        'AppView builds MaterialApp with correct localization: ${selectedLocalization.name}',
        (WidgetTester tester) async {
          // Arrange: Set up the AppLocalizationBloc to emit a chose localization state
          final state = AppLocalizationState(localization: selectedLocalization);
          when(() => mockAppLocalizationBloc.state).thenReturn(state);
          when(() => mockAppLocalizationBloc.stream)
              .thenAnswer((_) => Stream.value(state));

          // Act: Pump the AppView widget
          await pumpAppView(tester);

          // Assert: Verify localization is set correctly in MaterialApp
          final materialApp = tester.widget<MaterialApp>(find.byType(MaterialApp));
          expect(materialApp.locale, selectedLocalization.locale);
        }
      );
    }
  );
}
