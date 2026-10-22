import 'package:flutter/material.dart';
import 'package:thesis_checker/core/errors/analysis_exception.dart';
import 'package:thesis_checker/l10n/l10n.dart';

/// An extension on [AnalysisErrorType] to provide localized error messages.
extension AnalysisErrorLocalizationX on AnalysisErrorType {

  /// Returns a localized error message based on the [AnalysisErrorType].
  String localizedErrorText(BuildContext context) {
    final l10n = AppLocalizations.of(context)!;

    return switch (this) {
      AnalysisErrorType.finishedWithError => l10n.analysisFinishedWithError,
      AnalysisErrorType.couldNotComplete => l10n.analysisCouldNotComplete
    };
  }
}
