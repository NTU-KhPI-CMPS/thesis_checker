import 'package:flutter_app/core/constants/available_check_types.dart';
import 'package:flutter_app/core/enums/check.dart';
import 'package:flutter_app/core/models/check_type_info.dart';
import 'package:flutter_app/features/result/models/found_error.dart';

/// Resolves check type groups for errors by parsing check codes from 'category'.
class CheckTypeGrouping {
  static Check? parseCheckFromCode(String? code) {
    if (code == null) {
      return null;
    }

    final normalizedCode = code.trim().toUpperCase();

    for (final check in Check.values) {
      if (check.name == normalizedCode) {
        return check;
      }
    }

    return null;
  }

  static Check? parseCheckFromError(FoundError error) {
    return parseCheckFromCode(error.category);
  }

  static CheckTypeInfo resolveTypeByError(FoundError error) {
    final parsedCheck = parseCheckFromError(error);

    if (parsedCheck != null) {
      for (final type in AvailableCheckTypes.checkTypes) {
        if (type.checks.contains(parsedCheck)) {
          return type;
        }
      }
    }

    // we need to remove this code if we add types in 'other' checks
    for (final type in AvailableCheckTypes.checkTypes) {
      if (type.checks.isEmpty) {
        return type;
      }
    }

    return AvailableCheckTypes.checkTypes.first;
  }

  static Map<String, int> countErrorsByType(List<FoundError> errors) {
    final result = <String, int> {
      for (final type in AvailableCheckTypes.checkTypes) type.title: 0,
    };

    for (final error in errors) {
      final type = resolveTypeByError(error);
      result[type.title] = (result[type.title] ?? 0) + 1;
    }

    return result;
  }

  static List<FoundError> filterErrorsByType(List<FoundError> errors, CheckTypeInfo selectedType) {
    return errors
        .where((error) => resolveTypeByError(error).title == selectedType.title)
        .toList();
  }
}
