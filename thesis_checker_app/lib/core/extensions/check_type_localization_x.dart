import 'package:flutter/material.dart';
import 'package:thesis_checker/l10n/l10n.dart';
import 'package:thesis_checker/models/check_type_info.dart';

/// Provides localized title and description for [CheckTypeInfo].
extension CheckTypeLocalizationX on CheckTypeInfo {
  /// Returns the localized title for the check type based on its category.
  String localizedTitle(BuildContext context) {
    final l10n = AppLocalizations.of(context)!;

    return switch (category) {
      CheckCategory.font => l10n.checkTypeFontTitle,
      CheckCategory.spacing => l10n.checkTypeSpacingTitle,
      CheckCategory.alignment => l10n.checkTypeAlignmentTitle,
      CheckCategory.formula => l10n.checkTypeFormulaTitle,
      CheckCategory.list => l10n.checkTypeListTitle,
      CheckCategory.other => l10n.checkTypeOtherTitle
    };
  }

  /// Returns the localized description for the check type based on its category.
  String localizedDescription(BuildContext context) {
    final l10n = AppLocalizations.of(context)!;

    return switch (category) {
      CheckCategory.font => l10n.checkTypeFontDescription,
      CheckCategory.spacing => l10n.checkTypeSpacingDescription,
      CheckCategory.alignment => l10n.checkTypeAlignmentDescription,
      CheckCategory.formula => l10n.checkTypeFormulaDescription,
      CheckCategory.list => l10n.checkTypeListDescription,
      CheckCategory.other => l10n.checkTypeOtherDescription
    };
  }
}
