import 'package:flutter/material.dart';
import 'package:flutter_app/core/constants/available_check_types.dart';
import 'package:flutter_app/features/home/widgets/info_card.dart';
import 'package:flutter_app/features/home/widgets/upload_zone.dart';

/// Primary content area of the home screen.
class HomeContent extends StatelessWidget {
  const HomeContent({super.key});

  @override
  Widget build(BuildContext context) {
    final textColor2 = Theme.of(context).textTheme.bodyMedium?.color;
    final textColor = Theme.of(context).textTheme.bodyLarge?.color;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const UploadZone(),
        const SizedBox(height: 28.0,),
        Text(
          'Що перевіряємо'.toUpperCase(),
          style: TextStyle(
            fontFamily: 'FunnelSans',
            fontSize: 13.0,
            fontWeight: FontWeight.w600,
            color: textColor2,
            letterSpacing: 0.5,
          ),
        ),
        const SizedBox(height: 28.0,),
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
                mainAxisExtent: 76.0,
              ),
              children: List.generate(
                AvailableCheckTypes.checkTypes.length,
                (index) => InfoCard(
                   children: [
                    Image.asset(
                      AvailableCheckTypes.checkTypes[index].iconPath,
                      width: 20.0,
                      height: 20.0,
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                      child: Column(
                        mainAxisSize: MainAxisSize.min,
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            AvailableCheckTypes.checkTypes[index].title,
                            style: TextStyle(
                              fontSize: 14.0,
                              fontFamily: 'FunnelSans',
                              fontWeight: FontWeight.w600,
                              color: textColor,
                            ),
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                          ),
                          Text(
                            AvailableCheckTypes.checkTypes[index].description,
                            style: TextStyle(
                              fontSize: 14.0,
                              fontFamily: 'FunnelSans',
                              fontWeight: FontWeight.w600,
                              color: textColor2,
                            ),
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              )
            );
          },
        ),
      ],
    );
  }
}
