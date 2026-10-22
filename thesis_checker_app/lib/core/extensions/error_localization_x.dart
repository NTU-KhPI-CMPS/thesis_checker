import 'package:flutter/material.dart';
import 'package:thesis_checker/data/models/format_error_api.dart';
import 'package:thesis_checker/l10n/error_message_resolvers.dart';
import 'package:thesis_checker/l10n/l10n.dart';

/// An extension on [FormatErrorApi] to provide localized error messages.
extension ErrorLocalizationX on FormatErrorApi {
  /// Returns a localized error message based on the [FormatErrorApi] id.
  String getLocalizedMessage(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    if (l10n == null) return id;

    return localizeByErrorId(l10n, id);
  }
}
