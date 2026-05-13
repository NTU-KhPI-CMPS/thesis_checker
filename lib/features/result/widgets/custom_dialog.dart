import 'dart:math';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:thesis_checker/core/constants/app_colors.dart';
import 'package:thesis_checker/core/constants/available_check_types.dart';
import 'package:thesis_checker/features/result/widgets/checkbox_container.dart';
import 'package:thesis_checker/features/home/widgets/custom_animated_button.dart';
import 'package:thesis_checker/features/result/widgets/dialog_info_container.dart';
import 'package:thesis_checker/features/home/bloc/file_bloc.dart';

/// A modal dialog that lets users configure analysis options before starting.
class CustomDialog extends StatefulWidget {
  final String filePath;
  final String fileName;

  const CustomDialog({super.key, required this.filePath, required this.fileName});

  @override
  State<CustomDialog> createState() => _CustomDialogState();
}

class _CustomDialogState extends State<CustomDialog> with SingleTickerProviderStateMixin {
  double _scale = 0.0;

  bool _closeButtonIsHovered = false;
  bool _cancelButtonIsHovered = false;
  bool _startButtonIsHovered = false;

  bool isError = false;

  late final AnimationController _animationController;
  late final Animation<double> _shakeAnim;

  final Set<String> selectedChecks = {};
  final Set<String> selectedCategories = {};

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback(
      (_) => setState(() => _scale = 1.0)
    );

    _animationController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 500)
    );

    _shakeAnim = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(
        parent: _animationController,
        curve: Curves.elasticOut
      )
    );
  }

  @override
  void dispose() {
    super.dispose();
    _animationController.dispose();
  }

  void shake() {
    _animationController.forward(from: 0.0);
  }

  @override
  Widget build(BuildContext context) {
    final accentColor = Theme.of(context).primaryColor;
    final backgroundColor = Theme.of(context).canvasColor;
    final borderColor = Theme.of(context).inputDecorationTheme.border?.borderSide.color;
    final textColor = Theme.of(context).textTheme.bodyLarge?.color;
    final textColor2 = Theme.of(context).textTheme.bodyMedium?.color;
    final maxDialogHeight = MediaQuery.of(context).size.height * 0.9;

    final (errorContainerColor, errorTextColor) = isError
        ? (Theme.of(context).brightness == Brightness.light
              ? (AppColors.errorLight, AppColors.error)
              : (AppColors.errorDark, AppColors.error))
        : (null, null);

    final iconButtonStyle = Theme.of(context).iconButtonTheme.style;
    final defaultStates = <WidgetState>{};
    final hoverStates = <WidgetState>{WidgetState.hovered};

    final closeButtonBackgroundDefault = iconButtonStyle?.backgroundColor?.resolve(defaultStates);
    final closeButtonBackgroundHover = iconButtonStyle?.backgroundColor?.resolve(hoverStates);
    final closeButtonForegroundDefault = iconButtonStyle?.foregroundColor?.resolve(defaultStates);
    final closeButtonForegroundHover = iconButtonStyle?.foregroundColor?.resolve(hoverStates);
    final closeButtonSideDefault = iconButtonStyle?.side?.resolve(defaultStates)?.color;
    final closeButtonSideHover = iconButtonStyle?.side?.resolve(hoverStates)?.color;

    final cancelButtonBackground = Theme.of(context).scaffoldBackgroundColor;
    final cancelButtonBorderDefault = Theme.of(context).inputDecorationTheme.border?.borderSide.color;
    final cancelButtonTextDefault = textColor2;
    final cancelButtonTextHover = Theme.of(context).primaryColor;

    return AnimatedBuilder(
      animation: _shakeAnim,
      builder: (context, child) {
        final offset = sin(_shakeAnim.value * pi * 4) * 12;
        return Transform.translate(
          offset: Offset(offset, 0),
          child: child,
        );
      },
      child: Dialog(
        clipBehavior: Clip.antiAlias,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(28.0),
        ),
        child: ConstrainedBox(
          constraints: BoxConstraints(
            maxWidth: 480.0,
            maxHeight: maxDialogHeight,
          ),
          child: SingleChildScrollView(
            child: Center(
              child: AnimatedScale(
                scale: _scale,
                duration: const Duration(milliseconds: 250),
                curve: const Cubic(0.34, 1.56, 0.64, 1),
                child: Container(
                  width: double.infinity,
                  padding: const EdgeInsets.all(28.0),
                  decoration: BoxDecoration(
                    color: backgroundColor,
                    border: Border.all(color: borderColor!),
                    borderRadius: BorderRadius.all(
                      Radius.circular(28.0)
                    ),
                    boxShadow: [
                      BoxShadow(
                        color: Colors.black.withAlpha(46),
                        blurRadius: 64,
                        offset: const Offset(0, 24),
                      ),
                    ],
                  ),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Text(
                            'Налаштування перевірки',
                            style: TextStyle(
                              fontSize: 18.0,
                              fontWeight: FontWeight.w600,
                              fontFamily: 'FunnelSans',
                              color: textColor,
                            ),
                          ),
                          MouseRegion(
                            cursor: SystemMouseCursors.click,
                            onEnter: (_) => setState(() => _closeButtonIsHovered = true),
                            onExit: (_) => setState(() => _closeButtonIsHovered = false),
                            child: GestureDetector(
                              onTap: () => Navigator.of(context).pop(),
                              child: AnimatedContainer(
                                duration: const Duration(milliseconds: 150),
                                width: 30.0,
                                height: 30.0,
                                decoration: BoxDecoration(
                                  color: _closeButtonIsHovered
                                      ? closeButtonBackgroundHover
                                      : closeButtonBackgroundDefault,
                                  border: Border.all(
                                    color: _closeButtonIsHovered
                                        ? closeButtonSideHover!
                                        : closeButtonSideDefault!,
                                  ),
                                  borderRadius: const BorderRadius.all(
                                    Radius.circular(8.0),
                                  ),
                                ),
                                child: Center(
                                  child: Text(
                                    '✕',
                                    style: TextStyle(
                                      fontSize: 12.0,
                                      color: _closeButtonIsHovered
                                          ? closeButtonForegroundHover
                                          : closeButtonForegroundDefault,
                                    ),
                                  ),
                                ),
                              ),
                            ),
                          )
                        ],
                      ),
                      SizedBox(height: 10.0),
                      DialogInfoContainer(
                        borderColor: borderColor,
                        textColor: textColor!,
                        imageAsset: 'assets/images/document.png',
                        infoText: widget.fileName
                      ),
                      SizedBox(height: 12.0),
                      Text(
                        'Оберіть що перевіряти'.toUpperCase(),
                        style: TextStyle(
                            fontFamily: 'FunnelSans',
                            fontSize: 13.0,
                            fontWeight: FontWeight.w600,
                            color: textColor2,
                            letterSpacing: 0.5,
                          ),
                      ),
                      SizedBox(height: 12.0),
                      LayoutBuilder(
                        builder: (context, constraints) {
                          final isNarrow = constraints.maxWidth < 400;
                          return GridView.builder(
                            itemCount: AvailableCheckTypes.checkTypes.length,
                            shrinkWrap: true,
                            physics: const NeverScrollableScrollPhysics(),
                            gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                              crossAxisCount: isNarrow ? 1 : 2,
                              mainAxisSpacing: 14.0,
                              crossAxisSpacing: 14.0,
                              childAspectRatio: 2,
                            ),
                            itemBuilder: (context, index) {
                              final option = AvailableCheckTypes.checkTypes[index];
                              return CheckboxContainer(
                                children: [
                                  Image.asset(
                                    option.iconPath,
                                    width: 24.0,
                                    height: 24.0,
                                  ),
                                  Text(
                                    option.title,
                                    maxLines: 1,
                                    softWrap: false,
                                    overflow: TextOverflow.ellipsis,
                                    style: TextStyle(
                                      fontSize: 14.0,
                                      color: textColor,
                                      fontFamily: 'FunnelSans',
                                      fontWeight: FontWeight.w600,
                                    ),
                                  ),
                                  Expanded(
                                    child: Text(
                                      option.description,
                                      maxLines: 2,
                                      softWrap: true,
                                      style: TextStyle(
                                        fontSize: 11.0,
                                        color: textColor2,
                                        fontFamily: 'FunnelSans',
                                        overflow: TextOverflow.ellipsis
                                      ),
                                    ),
                                  ),
                                ],
                                onTap: () {
                                  final checkCodes = option.checks
                                      .map((check) => check.name)
                                      .toList();
                                  setState(() {
                                    if (checkCodes.isEmpty) {
                                      if (selectedCategories.contains(option.title)) {
                                        selectedCategories.remove(option.title);
                                      } else {
                                        selectedCategories.add(option.title);
                                      }
                                      return;
                                    }

                                    final hasAll = checkCodes.every(selectedChecks.contains);
                                    if (hasAll) {
                                      selectedChecks.removeAll(checkCodes);
                                      selectedCategories.remove(option.title);
                                    } else {
                                      selectedChecks.addAll(checkCodes);
                                      selectedCategories.add(option.title);
                                    }
                                  });
                                },
                              );
                            },
                          );
                        },
                      ),
                      SizedBox(height: 5.0),
                      Row(
                        children: [
                          Image.asset(
                            'assets/images/lamp.png',
                            width: 20.0,
                            height: 20.0,
                          ),
                          SizedBox(width: 10.0),
                          Expanded(
                            child: Text(
                              'Можна обрати декілька перевірок одночасно',
                              softWrap: true,
                              style: TextStyle(
                                fontSize: 12.0,
                                color: textColor2,
                                fontFamily: 'FunnelSans',
                                fontWeight: FontWeight.w600,
                              ),
                            ),
                          ),
                        ],
                      ),
                      SizedBox(height: 15.0,),
                      if (isError) ...[
                        DialogInfoContainer(
                          borderColor: errorContainerColor!,
                          textColor: errorTextColor!,
                          imageAsset: 'assets/images/warning.png',
                          infoText: 'Оберіть хоча б одну перевірку'
                        ),
                        SizedBox(height: 15.0),
                      ],
                      Row(
                        children: [
                          Expanded(
                            child: MouseRegion(
                              cursor: SystemMouseCursors.click,
                              onEnter: (_) => setState(() => _cancelButtonIsHovered = true),
                              onExit: (_) => setState(() => _cancelButtonIsHovered = false),
                              child: GestureDetector(
                                onTap: () => Navigator.of(context).pop(),
                                child: AnimatedContainer(
                                  duration: const Duration(milliseconds: 180),
                                  curve: Curves.easeOut,
                                  padding: const EdgeInsets.symmetric(vertical: 10.0, horizontal: 20.0),
                                  decoration: BoxDecoration(
                                    color: cancelButtonBackground,
                                    border: Border.all(
                                      color: _cancelButtonIsHovered
                                          ? cancelButtonTextHover
                                          : cancelButtonBorderDefault!,
                                      width: 1.0,
                                    ),
                                    borderRadius: BorderRadius.circular(12.0),
                                  ),
                                  child: Center(
                                    child: Text(
                                      'Скасувати',
                                      style: TextStyle(
                                        fontSize: 14.0,
                                        fontWeight: FontWeight.w600,
                                        fontFamily: 'FunnelSans',
                                        color: _cancelButtonIsHovered
                                            ? cancelButtonTextHover
                                            : cancelButtonTextDefault,
                                      ),
                                    ),
                                  ),
                                ),
                              ),
                            ),
                          ),
                          const SizedBox(width: 12.0),
                          Expanded(
                            child: CustomAnimatedButton(
                              text: '▶ Почати аналіз',
                              buttonIsHovered: _startButtonIsHovered,
                              accentColor: accentColor,
                              onTap: () {
                                if (selectedCategories.isNotEmpty) {
                                  final categoryByTitle = {
                                    for (final type in AvailableCheckTypes.checkTypes)
                                      type.title: type,
                                  };
                                  final hasCategoryWithoutChecks = selectedCategories.any(
                                    (title) => (categoryByTitle[title]?.checks.isEmpty ?? false),
                                  );
                                  // TODO: Remove this fallback once 'Інші' has explicit checks.
                                  // When 'Інші' gets checks, delete hasCategoryWithoutChecks and use:
                                  // final checksToRun = selectedChecks.toList(growable: false);
                                  final checksToRun = hasCategoryWithoutChecks || selectedChecks.isEmpty
                                      ? const <String>[]
                                      : selectedChecks.toList(growable: false);

                                  context.read<FileBloc>().add(
                                    FileDroppedEvent.withOptions(
                                      widget.filePath,
                                      widget.fileName,
                                      selectedChecks: checksToRun,
                                      selectedCategories: selectedCategories.toList(growable: false),
                                    ),
                                  );
                                  Navigator.of(context).pop();
                                } else {
                                  setState(() => isError = true);
                                  shake();
                                }
                              },
                              onHover: (isHovered) => setState(() => _startButtonIsHovered = isHovered)
                            ),
                          ),
                        ],
                      )
                    ],
                  ),
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}

