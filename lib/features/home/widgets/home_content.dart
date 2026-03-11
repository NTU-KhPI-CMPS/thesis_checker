import 'package:flutter/material.dart';
import 'package:flutter_app/core/constants/app_colors.dart';
import 'package:flutter_app/features/home/widgets/check_item.dart';
import 'package:flutter_app/features/home/widgets/upload_dropzone.dart';

class HomeContent extends StatelessWidget {
  const HomeContent({super.key});

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const UploadDropzone(),
        const SizedBox(height: 28.0),
        Text(
          'Що перевіряємо'.toUpperCase(),
          style: const TextStyle(
            fontFamily: 'FunnelSans',
            fontSize: 13.0,
            fontWeight: FontWeight.w600,
            color: AppColors.text2,
            letterSpacing: 0.5,
          ),
        ),
        const SizedBox(height: 28.0),
        LayoutBuilder(
          builder: (context, constraints) {
            final crossAxisCount = constraints.maxWidth < 500 ? 1 : 2;
            return GridView(
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: crossAxisCount,
                crossAxisSpacing: 16.0,
                mainAxisSpacing: 16.0,
                mainAxisExtent: 56.0,
              ),
              children: const [
                CheckItem(child: Text('1')),
                CheckItem(child: Text('2')),
                CheckItem(child: Text('3')),
                CheckItem(child: Text('4')),
              ],
            );
          },
        ),
      ],
    );
  }
}
