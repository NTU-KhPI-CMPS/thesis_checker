import 'package:flutter/material.dart';
import 'package:flutter_app/core/constants/app_colors.dart';
import 'package:flutter_app/features/home/widgets/info_card.dart';
import 'package:flutter_app/features/home/widgets/upload_zone.dart';

class HomeContent extends StatelessWidget {
  HomeContent({super.key});

  final List<Map<String, String>> infoCards = [
    {'icon': 'assets/images/abc.png', 'title': 'Шрифт', 'subtitle': 'Назва'},
  ];

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        UploadZone(),
        SizedBox(height: 28.0,),
        Text(
          'Що перевіряємо'.toUpperCase(),
          style: TextStyle(
            fontFamily: 'FunnelSans',
            fontSize: 13.0,
            fontWeight: FontWeight.w600,
            color: AppColors.text2,
            letterSpacing: 0.5,
          ),
        ),
        SizedBox(height: 28.0,),
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
                infoCards.length,
                (index) => InfoCard(
                   children: [
                    Image.asset(
                      infoCards[index]['icon']!,
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
                            infoCards[index]['title']!,
                            style: const TextStyle(
                              fontSize: 14.0,
                              fontFamily: 'FunnelSans',
                              fontWeight: FontWeight.w600,
                              color: AppColors.text,
                            ),
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                          ),
                          Text(
                            infoCards[index]['subtitle']!,
                            style: TextStyle(
                              fontSize: 14.0,
                              fontFamily: 'FunnelSans',
                              fontWeight: FontWeight.w600,
                              color: AppColors.text2,
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
