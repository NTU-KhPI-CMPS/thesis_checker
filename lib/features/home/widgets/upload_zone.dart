import 'package:desktop_drop/desktop_drop.dart';
import 'package:dotted_border/dotted_border.dart';
import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:flutter_app/core/constants/app_colors.dart';
import 'package:flutter_app/features/home/bloc/file_bloc.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

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
    return MouseRegion(
      onEnter: (event) => setState(() => containerIsHovered = true),
      onExit: (event) => setState(() => containerIsHovered = false),
      child: TweenAnimationBuilder<Color?>(
        tween: ColorTween(
          begin: AppColors.border,
          end: containerIsHovered ? AppColors.accent : AppColors.border,
        ),
        duration: const Duration(milliseconds: 200),
        curve: Curves.easeInOut,
        builder: (context, color, child) {
          return DottedBorder(
            options: RoundedRectDottedBorderOptions(
              padding: EdgeInsets.zero,
              radius: Radius.circular(8.0),
              dashPattern: [8, 6],
              color: color ?? AppColors.border,
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
            context.read<FileBloc>().add(FileDroppedEvent(filePath));
          },
          child: AnimatedContainer(
            duration: Duration(milliseconds: 200),
            curve: Curves.easeInOut,
            width: double.infinity,
            height: 300.0,
            decoration: BoxDecoration(
              color: containerIsHovered ? AppColors.bg : AppColors.surface,
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
                    color: AppColors.text,
                    fontSize: 20.0,
                    fontFamily: 'FunnelSans',
                    fontWeight: FontWeight.w600,
                  ),
                ),
                Text(
                  'Перетягніть .docx файл або натисніть щоб обрати',
                  textAlign: TextAlign.center,
                  style: TextStyle(
                    color: AppColors.text2,
                    fontSize: 14.0,
                    fontFamily: 'FunnelSans',
                    fontWeight: FontWeight.w600,
                  ),
                ),
                SizedBox(height: 24.0),
                MouseRegion(
                  onEnter: (event) => setState(() => buttonIsHovered = true),
                  onExit: (event) => setState(() => buttonIsHovered = false),
                  child: GestureDetector(
                    onTap: () async {
                      final bloc = context.read<FileBloc>();
                      final file = await FilePicker.platform.pickFiles(
                        type: FileType.custom,
                        allowedExtensions: ['docx'],
                      );
                      if (file != null) {
                        bloc.add(FileDroppedEvent(file.files.first.path!));
                      }
                    },
                    child: TweenAnimationBuilder<double>(
                      tween: Tween(begin: 0.0, end: buttonIsHovered ? -2.0 : 0.0),
                      duration: const Duration(milliseconds: 200),
                      curve: Curves.easeInOut,
                      builder: (context, offset, child) {
                        return Transform.translate(
                          offset: Offset(0, offset),
                          child: child,
                        );
                      },
                      child: AnimatedContainer(
                      duration: Duration(milliseconds: 200),
                      padding: EdgeInsets.symmetric(
                        vertical: 8.0,
                        horizontal: 24.0,
                      ),
                      decoration: BoxDecoration(
                        color: buttonIsHovered ? AppColors.accent.withAlpha(200): AppColors.accent,
                        borderRadius: BorderRadius.circular(8.0),
                      ),
                      child: Text(
                        '+ Обрати файл',
                        style: TextStyle(
                          color: buttonIsHovered ? Colors.grey[100]: Colors.white,
                          fontSize: 16.0,
                          fontFamily: 'FunnelSans',
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                  ),
                ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}