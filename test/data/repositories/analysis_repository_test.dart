import 'dart:io';

import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/annotations.dart';
import 'package:mockito/mockito.dart';
import 'package:thesis_checker/core/enums/check.dart';
import 'package:thesis_checker/data/models/format_error_api.dart';
import 'package:thesis_checker/data/models/report_api.dart';
import 'package:thesis_checker/data/repositories/analysis_repository.dart';
import 'package:thesis_checker/data/services/runner_java_service.dart';

import 'analysis_repository_test.mocks.dart';

@GenerateNiceMocks([MockSpec<RunnerJavaService>()])

FormatErrorApi makeError(String category) {
  return FormatErrorApi(
    id: 'id-$category',
    category: category,
    expected: 'expected',
    found: 'found',
    severity: 'error',
    title: 'title',
  );
}

void main() {
  group('AnalysisRepository.checkFile', () {
    test('groups known and unknown checks using mocked runner', () async {
      final mockRunner = MockRunnerJavaService();
      final repository = AnalysisRepository.forTest(runnerJavaService: mockRunner);
      const filePath = 'nested/thesis.docx';

      when(mockRunner.checkFile(filePath)).thenAnswer(
        (_) async => ReportApi(
          errors: [
            makeError(Check.fontName.name),
            makeError(Check.fontSize.name),
            makeError('SOMETHING_ELSE'),
          ],
        ),
      );

      final result = await repository.checkFile(filePath);

      final fontGroup = result.errorsByCategory.firstWhere(
        (item) => item.category == 'Шрифт',
      );
      final otherGroup = result.errorsByCategory.firstWhere(
        (item) => item.category == 'Інші',
      );

      expect(result.fileName, 'thesis.docx');
      expect(result.totalErrors, 3);
      expect(fontGroup.errors.length, 2);
      expect(otherGroup.errors.length, 1);
      expect(otherGroup.errors.first.category, 'SOMETHING_ELSE');
      verify(mockRunner.checkFile(filePath)).called(1);
      verifyNoMoreInteractions(mockRunner);
    });

    test('keeps all categories with empty report', () async {
      final mockRunner = MockRunnerJavaService();
      final repository = AnalysisRepository.forTest(runnerJavaService: mockRunner);
      final filePath = ['tmp', 'empty.docx'].join(Platform.pathSeparator);

      when(mockRunner.checkFile(filePath)).thenAnswer(
        (_) async => const ReportApi(errors: []),
      );

      final result = await repository.checkFile(filePath);

      expect(result.fileName, 'empty.docx');
      expect(result.totalErrors, 0);
      expect(result.errorsByCategory.length, 2);
      expect(result.errorsByCategory.every((item) => item.errors.isEmpty), isTrue);
      verify(mockRunner.checkFile(filePath)).called(1);
      verifyNoMoreInteractions(mockRunner);
    });
  });
}