import 'package:flutter/material.dart';
import 'package:flutter_app/features/home/bloc/file_bloc.dart';
import 'package:flutter_app/features/home/widgets/checkbox_container.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

/// A modal dialog that lets users configure analysis options before starting.
class CustomDialog extends StatefulWidget {
  final String filePath;
  final String fileName;

  const CustomDialog({super.key, required this.filePath, required this.fileName});

  @override
  State<CustomDialog> createState() => _CustomDialogState();
}

class _CustomDialogState extends State<CustomDialog> {
  double _scale = 0.0;
  bool _closeButtonIsHovered = false;
  bool _cancelButtonIsHovered = false;
  bool _startButtonIsHovered = false;

  List<Map<String, dynamic>> checkOptions = [
    {'icon': 'assets/images/abc.png', 'label': 'Шрифт', 'subLabel': 'Назва', 'value': 'FONT'},
  ];

  List<String> selectedChecks = [];

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback(
      (_) => setState(() => _scale = 1.0),
    );
  }

  @override
  Widget build(BuildContext context) {
    final backgroundColor = Theme.of(context).canvasColor;
    final borderColor = Theme.of(context).inputDecorationTheme.border?.borderSide.color;
    final textColor = Theme.of(context).textTheme.bodyLarge?.color;
    final textColor2 = Theme.of(context).textTheme.bodyMedium?.color;

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

    return Dialog(
      clipBehavior: Clip.antiAlias,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(28.0),
      ),
      child: SingleChildScrollView(
        child: ConstrainedBox(
          constraints: BoxConstraints(
            maxWidth: 480.0,
          ),
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
                            color: Theme.of(context).textTheme.bodyLarge?.color,
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
                    Container(
                      alignment: Alignment.centerLeft,
                      width: double.infinity,
                      padding: const EdgeInsets.symmetric(horizontal: 12.0, vertical: 8.0),
                      decoration: BoxDecoration(
                        color: borderColor,
                        borderRadius: BorderRadius.all(
                          Radius.circular(8.0),
                        ),
                      ),
                      child: Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          Image.asset(
                            'assets/images/page_facing_up.png',
                            width: 20.0,
                            height: 20.0,
                          ),
                          SizedBox(width: 10.0),
                          Text(
                            widget.fileName,
                            style: TextStyle(
                              fontSize: 14.0,
                              color: textColor,
                              overflow: TextOverflow.ellipsis,
                              fontFamily: 'FunnelSans',
                              fontWeight: FontWeight.w600,
                            ),
                          ),
                        ],
                      ),
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
                          itemCount: checkOptions.length,
                          shrinkWrap: true,
                          physics: const NeverScrollableScrollPhysics(),
                          gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                            crossAxisCount: isNarrow ? 1 : 2,
                            mainAxisSpacing: 14.0,
                            crossAxisSpacing: 14.0,
                            childAspectRatio: 2,
                          ),
                          itemBuilder: (context, index) {
                            final option = checkOptions[index];
                            return CheckboxContainer(
                              children: [
                                Image.asset(
                                  option['icon'],
                                  width: 24.0,
                                  height: 24.0,
                                ),
                                Text(
                                  option['label'],
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
                                Text(
                                  option['subLabel'],
                                  maxLines: 2,
                                  softWrap: true,
                                  style: TextStyle(
                                    fontSize: 11.0,
                                    color: textColor2,
                                    fontFamily: 'FunnelSans',
                                    overflow: TextOverflow.ellipsis
                                  ),
                                ),
                              ],
                              onTap: () {
                                final label = option['value'] as String;
                                setState(() {
                                  if (selectedChecks.contains(label)) {
                                    selectedChecks.remove(label);
                                  } else {
                                    selectedChecks.add(label);
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
                          child: MouseRegion(
                            cursor: SystemMouseCursors.click,
                            onEnter: (_) => setState(() => _startButtonIsHovered = true),
                            onExit: (_) => setState(() => _startButtonIsHovered = false),
                            child: GestureDetector(
                              onTap: () {
                                context.read<FileBloc>().add(
                                  FileDroppedEvent(
                                    widget.filePath,
                                    widget.fileName,
                                    selectedChecks,
                                  ),
                                );
                                Navigator.of(context).pop();
                              },
                              child: TweenAnimationBuilder<double>(
                                tween: Tween<double>(
                                  begin: 0.0,
                                  end: _startButtonIsHovered ? -2.0 : 0.0,
                                ),
                                duration: const Duration(milliseconds: 150),
                                curve: Curves.easeOut,
                                builder: (context, value, child) {
                                  return Transform.translate(
                                    offset: Offset(0.0, value),
                                    child: child,
                                  );
                                },
                                child: AnimatedContainer(
                                  duration: const Duration(milliseconds: 180),
                                  curve: Curves.easeOut,
                                  padding: const EdgeInsets.symmetric(vertical: 10.0, horizontal: 20.0),
                                  decoration: BoxDecoration(
                                    color: Theme.of(context).primaryColor.withAlpha(
                                      _startButtonIsHovered ? 209 : 255,
                                    ),
                                    borderRadius: BorderRadius.circular(12.0),
                                  ),
                                  child: const Center(
                                    child: Text(
                                      '▶ Почати аналіз',
                                      style: TextStyle(
                                        fontSize: 14.0,
                                        fontWeight: FontWeight.w600,
                                        fontFamily: 'FunnelSans',
                                        color: Colors.white,
                                      ),
                                    ),
                                  ),
                                ),
                              ),
                            ),
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
    );
  }
}
