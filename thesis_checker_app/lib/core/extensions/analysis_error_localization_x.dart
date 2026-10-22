import 'package:flutter/material.dart';
import 'package:thesis_checker/core/errors/analysis_exception.dart';
import 'package:thesis_checker/l10n/l10n.dart';

extension AnalysisErrorLocalizationX on AnalysisErrorType {
  String localizedErrorText(BuildContext context) {
    final l10n = AppLocalizations.of(context)!;

    return switch (this) {
      AnalysisErrorType.finishedWithError => l10n.analysisFinishedWithError,
      AnalysisErrorType.couldNotComplete => l10n.analysisCouldNotComplete
    };
  }
}
