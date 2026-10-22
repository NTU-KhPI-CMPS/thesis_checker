import 'package:thesis_checker/core/enums/check.dart';
import 'package:thesis_checker/models/check_type_info.dart';

/// Class that holds the available check types for document analysis.

class AvailableCheckTypes {
  static const List<CheckTypeInfo> checkTypes = [
    CheckTypeInfo(
      category: CheckCategory.font,
      checks: [Check.fontName, Check.fontSize],
      iconPath: 'assets/images/abc.png',
    ),
    CheckTypeInfo(
      category: CheckCategory.spacing,
      checks: [Check.lineSpacing, Check.indentation, Check.spacing],
      iconPath: 'assets/images/up_down_arrow.png',
    ),
    CheckTypeInfo(
      category: CheckCategory.alignment,
      checks: [Check.alignment],
      iconPath: 'assets/images/left_right_arrow.png',
    ),
    CheckTypeInfo(
      category: CheckCategory.formula,
      checks: [Check.formula],
      iconPath: 'assets/images/formula.png',
    ),
    CheckTypeInfo(
      category: CheckCategory.list,
      checks: [Check.listFormatting],
      iconPath: 'assets/images/list_item.png',
    ),
    CheckTypeInfo(
      category: CheckCategory.other,
      checks: [Check.structuralElement, Check.other],
      iconPath: 'assets/images/other_checks.png',
    ),
  ];
}
