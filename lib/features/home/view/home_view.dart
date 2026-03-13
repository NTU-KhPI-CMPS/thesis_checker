import 'package:dotted_border/dotted_border.dart';
import 'package:flutter/material.dart';
import 'package:flutter_app/core/constants/app_colors.dart';

class HomeView extends StatefulWidget {
  const HomeView({super.key});

  @override
  State<HomeView> createState() => _HomeViewState();
}

class _HomeViewState extends State<HomeView> {
  int selectedIndex = 0;

  bool buttonIsHovered = false;
  bool containerIsHovered = false;
  bool isLight = true;
  bool themeButtonIsHovered = false;


  final List<Map<String, dynamic>> buttons = [
    {'icon': 'assets/images/house.png', 'label': 'Головна'},
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.surface,
      appBar: _buildCustomAppBar(),
      body: _mainContainer(),
    );
  }

  Container _mainContainer() {
    return Container(
      width: double.infinity,
      height: double.infinity,
      decoration: BoxDecoration(
        color: AppColors.bg,
        border: Border(
          top: BorderSide(
            color: AppColors.border,
            width: 1.0,
          ),
        ),
      ),
      child: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.all(24.0),
          child: Center(
            child: ConstrainedBox(
              constraints: BoxConstraints(maxWidth: 820.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  MouseRegion(
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
                                onTap: () {},
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
                          mainAxisExtent: 56.0,
                        ),
                        children: [
                          _buildCheckItem(Text('1')),
                          _buildCheckItem(Text('2')),
                          _buildCheckItem(Text('3')),
                          _buildCheckItem(Text('4')),
                        ],
                      );
                    },
                  ),
                ],
              ),
            ),
          ),
        ),
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
        padding: const EdgeInsets.symmetric(
          vertical: 16.0, 
          horizontal: 24.0
        ),
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
                    borderRadius: BorderRadius.circular(8.0)
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
                  duration: Duration(
                    milliseconds: 200
                  ),
                  padding: EdgeInsets.symmetric(
                    vertical: 6.0,
                    horizontal: 14.0,
                  ),
                  decoration: BoxDecoration(
                    border: Border.all(
                      color: themeButtonIsHovered? AppColors.accent : AppColors.border
                    ),
                    borderRadius: BorderRadius.all(
                      Radius.circular(20.0)
                    ),
                    color: isLight? AppColors.surface2 : AppColors.text2
                  ),
                  child: Row(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      Image.asset(
                        isLight ? 'assets/images/sunny.png' : 'assets/images/moon.png',
                        width: 20.0,
                        height: 20.0,
                      ),
                      SizedBox(width: 5,),
                      Text(
                        isLight ? 'Світла' : 'Темна',
                        style: TextStyle(
                          color: themeButtonIsHovered? AppColors.accent : AppColors.text2,
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
            Container(
              color: Colors.grey[300],
              height: 1.0,
            ),
            Padding(
              padding: const EdgeInsets.only(
                top: 10.0,
                left: 24.0
              ),
              child: SingleChildScrollView(
                scrollDirection: Axis.horizontal,
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.start,
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    _buildAnimatedButtons(0),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  GestureDetector _buildAnimatedButtons(int buttonIndex) {
    final bool isActive = selectedIndex == buttonIndex;

    return GestureDetector(
      onTap: () {
        setState(() {
          selectedIndex = buttonIndex;
        });
      },
      child: Transform.translate(
        offset: isActive ? Offset(0, 1.0) : Offset.zero,
        child: AnimatedContainer(
          padding: const EdgeInsets.symmetric(
            vertical: 6.0,
            horizontal: 12.0,
          ),
          duration: const Duration(milliseconds: 300),
          curve: Curves.easeInOut,
          decoration: BoxDecoration(
            color: isActive ? AppColors.bg : AppColors.bg.withAlpha(0),
            border: Border(
              top: BorderSide(
                color: isActive ? AppColors.border : AppColors.border.withAlpha(0),
                width: 1.0,
              ),
              left: BorderSide(
                color: isActive ? AppColors.border : AppColors.border.withAlpha(0),
                width: 1.0,
              ),
              right: BorderSide(
                color: isActive ? AppColors.border : AppColors.border.withAlpha(0),
                width: 1.0,
              ),
              bottom: BorderSide.none,
            ),
            borderRadius: const BorderRadius.only(
              topLeft: Radius.circular(8.0),
              topRight: Radius.circular(8.0),
            ),
          ),
          child: Row(
            children: [
              Image.asset(
                buttons[buttonIndex]['icon'],
                width: 20.0,
                height: 20.0,
              ),
              const SizedBox(width: 8.0),
              Text(
                buttons[buttonIndex]['label'],
                style: TextStyle(
                  color: isActive ? AppColors.accent : AppColors.text
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Container _buildCheckItem(Widget child) {
    return Container(
      padding: const EdgeInsets.symmetric(
        vertical: 14.0,
        horizontal: 16.0,
      ),
      decoration: BoxDecoration(
        color: AppColors.surface,
        border: Border.all(
          color: AppColors.border,
          width: 1.0,
        ),
        borderRadius: BorderRadius.circular(12.0),
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.center,
        spacing: 12.0,
        children: [child],
      ),
    );
  }
}
