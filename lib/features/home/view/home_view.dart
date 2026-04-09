import 'package:flutter/material.dart';
import 'package:flutter_app/core/theme/theme_cubit.dart';
import 'package:flutter_app/core/widgets/page_container.dart';
import 'package:flutter_app/features/home/bloc/file_bloc.dart';
import 'package:flutter_app/features/home/widgets/app_bar_button.dart';
import 'package:flutter_app/features/home/widgets/home_content.dart';
import 'package:flutter_app/features/loading_analysis/bloc/analysis_bloc.dart';
import 'package:flutter_app/features/loading_analysis/view/loading_analysis_view.dart';
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
  bool themeButtonIsHovered = false;

  final List<Map<String, dynamic>> buttons = [
    {'icon': 'assets/images/house.png', 'label': 'Головна'},
    {'icon': 'assets/images/hourglass.png', 'label': 'Аналіз'},
  ];

  void _onAnalysisComplete() {    
    ScaffoldMessenger.of(
      context,
    ).showSnackBar(const SnackBar(content: Text('Аналіз завершено!')));

    context.read<FileBloc>().add(ResetFileEvent());
    setState(() => selectedIndex = 0);
  }

  Widget _buildBodyContent(int index) {
    return IndexedStack(
      index: index,
      children: [
        PageContainer(
          contentAlignment: Alignment.topCenter,
          child: HomeContent(),
        ),
        PageContainer(
          contentAlignment: Alignment.center,
          child: LoadingAnalysisView(
            onAnalysisComplete: _onAnalysisComplete,
          ),
        ),
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    return BlocListener<FileBloc, FileState>(
      listener: (context, state) {
        if (state is FileUploadedState) {
          setState(() {
            selectedIndex = 1;
            context.read<AnalysisBloc>().add(
              StartAnalysisEvent(
                filePath: state.filePath,
                checkedOptions: state.checkedOptions,
              ),
            );
          });
        }
        if (state is FileUploadErrorState) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('Виникла помилка: //${state.error}')),
          );
        }
      },
      child: Scaffold(
        appBar: _buildCustomAppBar(context),
        body: _buildBodyContent(selectedIndex),
      ),
    );
  }

  AppBar _buildCustomAppBar(BuildContext context) {
    final isLight = Theme.of(context).brightness == Brightness.light;
    final textColor = Theme.of(context).textTheme.bodyLarge?.color;
    final accentColor = Theme.of(context).primaryColor;
    final borderColor = Theme.of(context).dividerColor;
    final surface2Color = Theme.of(context).colorScheme.surface;
    final textColor2 = Theme.of(context).textTheme.bodyMedium?.color;

    return AppBar(
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
                    color: accentColor,
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
                const SizedBox(width: 12.0),
                Text(
                  'Thesis checker',
                  style: TextStyle(
                    color: textColor,
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
                onTap: () {
                  context.read<ThemeCubit>().toggleTheme();
                },
                child: AnimatedContainer(
                  duration: const Duration(milliseconds: 200),
                  padding: const EdgeInsets.symmetric(
                    vertical: 6.0,
                    horizontal: 14.0,
                  ),
                  decoration: BoxDecoration(
                    border: Border.all(
                      color: themeButtonIsHovered ? accentColor : borderColor,
                    ),
                    borderRadius: const BorderRadius.all(Radius.circular(20.0)),
                    color: surface2Color,
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
                      const SizedBox(width: 5),
                      Text(
                        isLight ? 'Світла' : 'Темна',
                        style: TextStyle(
                          color: themeButtonIsHovered
                              ? accentColor
                              : textColor2,
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
        preferredSize: const Size.fromHeight(32.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Container(color: borderColor, height: 1.0),
            Padding(
              padding: const EdgeInsets.only(top: 10.0, left: 24.0),
              child: SingleChildScrollView(
                scrollDirection: Axis.horizontal,
                child: BlocBuilder<AnalysisBloc, AnalysisState>(
                  builder: (context, state) {
                    final isLocked = state is AnalysisInProgressState;
                    return Row(
                      mainAxisAlignment: MainAxisAlignment.start,
                      mainAxisSize: MainAxisSize.min,
                      children: List.generate(
                        buttons.length,
                        (index) => AppBarNavButton(
                          icon: buttons[index]['icon'],
                          label: buttons[index]['label'],
                          isActive: selectedIndex == index,
                          onTap: !isLocked ? () {
                            setState(() {
                              selectedIndex = index;
                            });
                          } : null,
                        ),
                      ),
                    );
                  },
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
