import 'package:thesis_checker/core/enums/check.dart';
import 'package:thesis_checker/models/check_type_info.dart';

class AvailableCheckTypes {
  static const List<CheckTypeInfo> checkTypes = [
    CheckTypeInfo(
      title: 'Шрифт',
      description: 'Назва, розмір',
      checks: [Check.fontName, Check.fontSize],
      iconPath: 'assets/images/abc.png',
    ),
    CheckTypeInfo(
      title: 'Інші',
      description: 'Інші перевірки, які не входять до категорій',
      checks: [],
      iconPath: 'assets/images/other_checks.png',
    )
  ];
}
