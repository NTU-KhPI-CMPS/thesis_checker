import 'package:flutter/material.dart';
import 'package:flutter_app/core/constants/app_colors.dart';
import 'package:flutter_app/core/widgets/page_container.dart';
import 'package:flutter_app/features/home/bloc/file_bloc.dart';
import 'package:flutter_app/features/home/widgets/app_bar_button.dart';
import 'package:flutter_app/features/home/widgets/home_content.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

/// Main screen that renders navigation, page content, and upload feedback.
class HomeView extends StatefulWidget {
  const HomeView({super.key});

  @override
  State<HomeView> createState() => _HomeViewState();
}

/// Internal state for [HomeView].
class _HomeViewState extends State<HomeView> {
  int selectedIndex = 0;

  bool isLight = true;
  bool themeButtonIsHovered = false;

  final List<Map<String, dynamic>> buttons = [
    {'icon': 'assets/images/house.png', 'label': 'Головна'},
  ];

  final List<Widget> views = [
    HomeContent(),
  ];

  @override
  Widget build(BuildContext context) {
    return BlocListener<FileBloc, FileState>(
      listener: (context, state) {
        if (state is FileUploadedState) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text('Файл ${state.filePath} успішно завантажено!'),
            ),
          );
        }
        if (state is FileUploadErrorState) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text('Виникла помилка: ${state.error}'),
            ),
          );
        }
      },
      child: Scaffold(
        backgroundColor: AppColors.surface,
        appBar: _buildCustomAppBar(),
        body: PageContainer(child: views[selectedIndex]),
      ),
    );
  }

  AppBar _buildCustomAppBar() {
    return AppBar(
      backgroundColor: AppColors.surface,
      titleSpacing: 0.0,
      automaticallyImplyLeading: false,
      toolbarHeight: 85.0,
      title: Padding(
        padding: const EdgeInsets.symmetric(vertical: 16.0, horizontal: 24.0),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Row(
              children: [
                Container(
                  width: 32.0,
                  height: 32.0,
                  decoration: BoxDecoration(
                    color: AppColors.accent,
                    borderRadius: BorderRadius.circular(8.0),
                  ),
                  child: Padding(
                    padding: const EdgeInsets.all(4.0),
                    child: Image.asset(
                      'assets/images/page_facing_up.png',
                      width: 20.0,
                      height: 20.0,
                    ),
                  ),
                ),
                SizedBox(width: 12.0),
                Text(
                  'Thesis checker',
                  style: TextStyle(
                    color: AppColors.text,
                    fontFamily: 'FunnelSans',
                    fontWeight: FontWeight.w600,
                    fontSize: 17.0,
                  ),
                ),
              ],
            ),
            MouseRegion(
              onEnter: (event) => setState(() => themeButtonIsHovered = true),
              onExit: (event) => setState(() => themeButtonIsHovered = false),
              child: GestureDetector(
                child: AnimatedContainer(
                  duration: Duration(milliseconds: 200),
                  padding: EdgeInsets.symmetric(
                    vertical: 6.0,
                    horizontal: 14.0,
                  ),
                  decoration: BoxDecoration(
                    border: Border.all(
                      color: themeButtonIsHovered
                          ? AppColors.accent
                          : AppColors.border,
                    ),
                    borderRadius: BorderRadius.all(Radius.circular(20.0)),
                    color: isLight ? AppColors.surface2 : AppColors.text2,
                  ),
                  child: Row(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      Image.asset(
                        isLight
                            ? 'assets/images/sunny.png'
                            : 'assets/images/moon.png',
                        width: 20.0,
                        height: 20.0,
                      ),
                      SizedBox(width: 5),
                      Text(
                        isLight ? 'Світла' : 'Темна',
                        style: TextStyle(
                          color: themeButtonIsHovered
                              ? AppColors.accent
                              : AppColors.text2,
                          fontSize: 13.0,
                          fontFamily: 'FunnelSans',
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
      bottom: PreferredSize(
        preferredSize: Size.fromHeight(32.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Container(color: Colors.grey[300], height: 1.0),
            Padding(
              padding: const EdgeInsets.only(top: 10.0, left: 24.0),
              child: SingleChildScrollView(
                scrollDirection: Axis.horizontal,
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.start,
                  mainAxisSize: MainAxisSize.min,
                  children: List.generate(
                    buttons.length,
                    (index) => AppBarNavButton(
                      icon: buttons[index]['icon'],
                      label: buttons[index]['label'],
                      isActive: selectedIndex == index,
                      onTap: () {
                        setState(() {
                          selectedIndex = index;
                        });
                      },
                    ),
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
