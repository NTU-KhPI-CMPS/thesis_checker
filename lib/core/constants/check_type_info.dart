import 'package:flutter_app/core/constants/app_icons.dart';
import 'package:flutter_app/core/enums/check.dart';
import 'package:flutter_app/core/models/check_type_model.dart';

class AvailableCheckTypes {
  static const List<CheckTypeInfo> checkTypes = [
    CheckTypeInfo(
      title: 'Шрифт',
      description: 'Назва, розмір',
      checks: [Check.fontName, Check.fontSize],
      iconPath: AppIcons.fontCheckIcon,
    ),
  ];
}
