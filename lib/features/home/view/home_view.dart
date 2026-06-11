import 'package:flutter/material.dart';
import 'package:thesis_checker/core/theme/theme_cubit.dart';
import 'package:thesis_checker/core/widgets/page_container.dart';
import 'package:thesis_checker/features/home/bloc/file_bloc.dart';
import 'package:thesis_checker/features/home/widgets/app_bar_button.dart';
import 'package:thesis_checker/features/home/widgets/home_content.dart';
import 'package:thesis_checker/features/loading_analysis/bloc/analysis_bloc.dart';
import 'package:thesis_checker/features/loading_analysis/view/loading_analysis_view.dart';
import 'package:thesis_checker/features/result/view/result_view.dart';
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
    {'icon': 'assets/images/bar_chart.png', 'label': 'Результати'},
  ];

  void _onAnalysisComplete() {
    context.read<FileBloc>().add(ResetFileEvent());
    setState(() => selectedIndex = 2);
  }

  void _onAnalysisFailed(String error) {
    ScaffoldMessenger.of(
      context,
    ).showSnackBar(
      SnackBar(
        content: Text(error),
        duration: const Duration(seconds: 10),
      ),
    );

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
            onAnalysisFailed: _onAnalysisFailed,
          ),
        ),
        PageContainer(
          contentAlignment: Alignment.topCenter,
          child: ResultView(),
        ),
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    final scaffoldBackgroundColor = Theme.of(context).scaffoldBackgroundColor;

    return BlocListener<FileBloc, FileState>(
      listener: (context, state) {
        if (state is FileUploadedState) {
          setState(() {
            selectedIndex = 1;
            context.read<AnalysisBloc>().add(
              StartAnalysisEvent(
                filePath: state.filePath,
                fileName: state.fileName,
                selectedChecks: state.selectedChecks
              )
            );
          });
        }
        if (state is FileUploadErrorState) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('Виникла помилка: ${state.error}')),
          );
        }
      },
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 200),
        curve: Curves.easeInOut,
        color: scaffoldBackgroundColor,
        child: Scaffold(
          backgroundColor: Colors.transparent,
          appBar: _buildCustomAppBar(context),
          body: _buildBodyContent(selectedIndex),
        ),
      ),
    );
  }

  AppBar _buildCustomAppBar(BuildContext context) {
    final theme = Theme.of(context);
    final isLight = theme.brightness == Brightness.light;
    final textColor = theme.textTheme.bodyLarge?.color;
    final accentColor = theme.primaryColor;
    final borderColor = theme.dividerColor;
    final surface2Color = theme.colorScheme.surface;
    final textColor2 = theme.textTheme.bodyMedium?.color;

    return AppBar(
      backgroundColor: Colors.transparent,
      surfaceTintColor: Colors.transparent,
      shadowColor: Colors.transparent,
      scrolledUnderElevation: 0.0,
      elevation: 0.0,
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
                  child: Image.asset('assets/images/app_icon.png'),
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
            AnimatedContainer(
              duration: const Duration(milliseconds: 200),
              curve: Curves.easeInOut,
              color: borderColor,
              height: 1.0,
            ),
            AnimatedPadding(
              duration: const Duration(milliseconds: 200),
              curve: Curves.easeInOut,
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
