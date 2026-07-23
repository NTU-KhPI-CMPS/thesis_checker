import 'package:thesis_checker/core/enums/check.dart';
import 'package:thesis_checker/models/check_type_info.dart';

/// Class that holds the available check types for document analysis.
class AvailableCheckTypes {
  static const List<CheckTypeInfo> checkTypes = [
    CheckTypeInfo(
      title: 'Шрифт',
      description: 'Назва і розмір шрифту',
      checks: [
        Check.fontName,
        Check.fontSize
      ],
      iconPath: 'assets/images/abc.png',
    ),
    CheckTypeInfo(
      title: 'Інтервали',
      description: 'Міжрядковий і відступи навколо абзаців',
      checks: [
        Check.lineSpacing,
        Check.spacing
      ],
      iconPath: 'assets/images/up_down_arrow.png',
    ),
    CheckTypeInfo(
      title: 'Вирівнювання',
      description: 'Вирівнювання абзаців і заголовків',
      checks: [Check.alignment],
      iconPath: 'assets/images/left_right_arrow.png',
    ),
    CheckTypeInfo(
      title: 'Інші',
      description: 'Інші перевірки, які не входять до категорій',
      checks: [
        Check.listFormatting,
        Check.structuralElement,
        Check.formula,
        Check.indentation
      ],
      iconPath: 'assets/images/other_checks.png',
      isFallback: true
    )
  ];
}
