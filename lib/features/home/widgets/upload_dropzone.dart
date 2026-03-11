import 'package:desktop_drop/desktop_drop.dart';
import 'package:dotted_border/dotted_border.dart';
import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:flutter_app/core/constants/app_colors.dart';
import 'package:flutter_app/features/home/bloc/file_bloc.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

class UploadDropzone extends StatefulWidget {
  const UploadDropzone({super.key});

  @override
  State<UploadDropzone> createState() => _UploadDropzoneState();
}

class _UploadDropzoneState extends State<UploadDropzone> {
  bool _buttonIsHovered = false;
  bool _containerIsHovered = false;

  @override
  Widget build(BuildContext context) {
    return MouseRegion(
      onEnter: (_) => setState(() => _containerIsHovered = true),
      onExit: (_) => setState(() => _containerIsHovered = false),
      child: TweenAnimationBuilder<Color?>(
        tween: ColorTween(
          begin: AppColors.border,
          end: _containerIsHovered ? AppColors.accent : AppColors.border,
        ),
        duration: const Duration(milliseconds: 200),
        curve: Curves.easeInOut,
        builder: (context, color, child) {
          return DottedBorder(
            options: RoundedRectDottedBorderOptions(
              padding: EdgeInsets.zero,
              radius: const Radius.circular(8.0),
              dashPattern: const [8, 6],
              color: color ?? AppColors.border,
              strokeWidth: 4.0,
            ),
            child: child!,
          );
        },
        child: DropTarget(
          onDragDone: (details) {
            final file = details.files.first;
            context.read<FileBloc>().add(FilePickedEvent(file.path, file.name));
          },
          child: AnimatedContainer(
            duration: const Duration(milliseconds: 200),
            curve: Curves.easeInOut,
            width: double.infinity,
            height: 300.0,
            decoration: BoxDecoration(
              color: _containerIsHovered ? AppColors.bg : AppColors.surface,
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
                const SizedBox(height: 16.0),
                const Text(
                  'Завантажте документ',
                  textAlign: TextAlign.center,
                  style: TextStyle(
                    color: AppColors.text,
                    fontSize: 20.0,
                    fontFamily: 'FunnelSans',
                    fontWeight: FontWeight.w600,
                  ),
                ),
                const Text(
                  'Перетягніть .docx файл або натисніть щоб обрати',
                  textAlign: TextAlign.center,
                  style: TextStyle(
                    color: AppColors.text2,
                    fontSize: 14.0,
                    fontFamily: 'FunnelSans',
                    fontWeight: FontWeight.w600,
                  ),
                ),
                const SizedBox(height: 24.0),
                MouseRegion(
                  onEnter: (_) => setState(() => _buttonIsHovered = true),
                  onExit: (_) => setState(() => _buttonIsHovered = false),
                  child: GestureDetector(
                    onTap: () async {
                      final bloc = context.read<FileBloc>();
                      final result = await FilePicker.platform.pickFiles(
                        type: FileType.custom,
                        allowedExtensions: ['docx'],
                      );
                      if (result != null) {
                        final file = result.files.first;
                        bloc.add(FilePickedEvent(file.path!, file.name));
                      }
                    },
                    child: TweenAnimationBuilder<double>(
                      tween: Tween(begin: 0.0, end: _buttonIsHovered ? -2.0 : 0.0),
                      duration: const Duration(milliseconds: 200),
                      curve: Curves.easeInOut,
                      builder: (context, offset, child) {
                        return Transform.translate(
                          offset: Offset(0, offset),
                          child: child,
                        );
                      },
                      child: AnimatedContainer(
                        duration: const Duration(milliseconds: 200),
                        padding: const EdgeInsets.symmetric(
                          vertical: 8.0,
                          horizontal: 24.0,
                        ),
                        decoration: BoxDecoration(
                          color: _buttonIsHovered
                              ? AppColors.accent.withAlpha(200)
                              : AppColors.accent,
                          borderRadius: BorderRadius.circular(8.0),
                        ),
                        child: Text(
                          '+ Обрати файл',
                          style: TextStyle(
                            color: _buttonIsHovered ? Colors.grey[100] : Colors.white,
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
