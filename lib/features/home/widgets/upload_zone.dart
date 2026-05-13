import 'package:desktop_drop/desktop_drop.dart';
import 'package:dotted_border/dotted_border.dart';
import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:thesis_checker/features/home/bloc/file_bloc.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:thesis_checker/features/home/widgets/custom_animated_button.dart';

/// Interactive drop area for uploading thesis files.
class UploadZone extends StatefulWidget {
  const UploadZone({super.key});

  @override
  State<UploadZone> createState() => _UploadZoneState();
}

/// Internal state for [UploadZone].
class _UploadZoneState extends State<UploadZone> {
  bool buttonIsHovered = false;
  bool containerIsHovered = false;

  @override
  Widget build(BuildContext context) {
    final borderColor = Theme.of(context).dividerColor;
    final accentColor = Theme.of(context).primaryColor;
    final surfaceColor = Theme.of(context).scaffoldBackgroundColor;
    final surface2Color = Theme.of(context).inputDecorationTheme.fillColor;
    final textColor = Theme.of(context).textTheme.bodyLarge?.color;
    final textColor2 = Theme.of(context).textTheme.bodyMedium?.color;

    return MouseRegion(
      onEnter: (event) => setState(() => containerIsHovered = true),
      onExit: (event) => setState(() => containerIsHovered = false),
      child: TweenAnimationBuilder<Color?>(
        tween: ColorTween(
          begin: borderColor,
          end: containerIsHovered ? accentColor : borderColor,
        ),
        duration: const Duration(milliseconds: 200),
        curve: Curves.easeInOut,
        builder: (context, color, child) {
          return DottedBorder(
            options: RoundedRectDottedBorderOptions(
              padding: EdgeInsets.zero,
              radius: Radius.circular(8.0),
              dashPattern: [8, 6],
              color: color ?? borderColor,
              strokeWidth: 4.0,
            ),
            child: child!,
          );
        },
        child: DropTarget(
          onDragEntered: (details) => setState(() => containerIsHovered = true),
          onDragExited: (details) => setState(() => containerIsHovered = false),
          onDragDone: (details) {
            final file = details.files.first;
            final filePath = file.path;
            final fileName = file.name;
            context.read<FileBloc>().add(
              FileDroppedEvent.onlyPath(
                filePath,
                fileName,
              ),
            );
          },
          child: AnimatedContainer(
            duration: Duration(milliseconds: 200),
            curve: Curves.easeInOut,
            width: double.infinity,
            height: 300.0,
            decoration: BoxDecoration(
              color: containerIsHovered ? surface2Color : surfaceColor,
              borderRadius: BorderRadius.circular(8.0),
            ),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Image.asset(
                  'assets/images/open_folder.png',
                  width: 48.0,
                  height: 48.0,
                ),
                SizedBox(height: 16.0),
                Text(
                  'Завантажте документ',
                  textAlign: TextAlign.center,
                  style: TextStyle(
                    color: textColor,
                    fontSize: 20.0,
                    fontFamily: 'FunnelSans',
                    fontWeight: FontWeight.w600,
                  ),
                ),
                Text(
                  'Перетягніть .docx файл або натисніть щоб обрати',
                  textAlign: TextAlign.center,
                  style: TextStyle(
                    color: textColor2,
                    fontSize: 14.0,
                    fontFamily: 'FunnelSans',
                    fontWeight: FontWeight.w600,
                  ),
                ),
                SizedBox(height: 24.0),
                CustomAnimatedButton(
                    text: '+ Обрати файл',
                    buttonIsHovered: buttonIsHovered,
                    accentColor: accentColor,
                    onTap: () async {
                      final bloc = context.read<FileBloc>();
                      final file = await FilePicker.platform.pickFiles(
                        type: FileType.custom,
                        allowedExtensions: ['docx'],
                      );
                      if (file != null) {
                        final filePath = file.files.first.path!;
                        final fileName = file.files.first.name;
                        bloc.add(
                          FileDroppedEvent.onlyPath(
                            filePath,
                            fileName,
                          ),
                        );
                      }
                  },
                  onHover: (isHovered) => setState(() => buttonIsHovered = isHovered)
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
