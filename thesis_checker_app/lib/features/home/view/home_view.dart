import 'package:flutter/material.dart';
import 'package:thesis_checker/core/errors/analysis_exception.dart';
import 'package:thesis_checker/core/extensions/analysis_error_localization_x.dart';
import 'package:thesis_checker/core/localization/app_localization_bloc.dart';
import 'package:thesis_checker/core/theme/theme_cubit.dart';
import 'package:thesis_checker/core/widgets/page_container.dart';
import 'package:thesis_checker/features/home/bloc/file_bloc.dart';
import 'package:thesis_checker/features/home/widgets/app_bar_button.dart';
import 'package:thesis_checker/features/home/widgets/home_content.dart';
import 'package:thesis_checker/features/home/widgets/pill_button.dart';
import 'package:thesis_checker/features/loading_analysis/bloc/analysis_bloc.dart';
import 'package:thesis_checker/features/loading_analysis/view/loading_analysis_view.dart';
import 'package:thesis_checker/features/result/view/result_view.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:thesis_checker/l10n/l10n.dart';
import 'package:thesis_checker/models/application_localization.dart';

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

  void _onAnalysisComplete() {
    context.read<FileBloc>().add(ResetFileEvent());
    setState(() => selectedIndex = 2);
  }

  void _onAnalysisFailed(AnalysisErrorType type) {
    ScaffoldMessenger.of(
      context,
    ).showSnackBar(
      SnackBar(
        content: Text(type.localizedErrorText(context)),
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

  List<({String icon, String label})> _navItems(AppLocalizations l10n) => [
    (icon: 'assets/images/house.png', label: l10n.navHome),
    (icon: 'assets/images/hourglass.png', label: l10n.navAnalysis),
    (icon: 'assets/images/bar_chart.png', label: l10n.navResults),
  ];

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context)!;
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
            SnackBar(content: Text(l10n.snackbarError(state.extension))),
          );
        }
      },
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 200),
        curve: Curves.easeInOut,
        color: scaffoldBackgroundColor,
        child: Scaffold(
          backgroundColor: Colors.transparent,
          appBar: _buildCustomAppBar(context, l10n),
          body: _buildBodyContent(selectedIndex),
        ),
      ),
    );
  }

  AppBar _buildCustomAppBar(BuildContext context, AppLocalizations l10n) {
    final theme = Theme.of(context);
    final textColor = theme.textTheme.bodyLarge?.color;
    final accentColor = theme.primaryColor;
    final borderColor = theme.dividerColor;

    final buttons = _navItems(l10n);

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
            Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                BlocBuilder<AppLocalizationBloc, AppLocalizationState>(
                  builder: (context, state) {
                    final current = state.selectedLocalization;

                    return PillButton(
                      icon: current.iconPath,
                      label: current.label,
                      onTap: () {
                        const all = ApplicationLocalization.values;
                        final next = all[(current.index + 1) % all.length];
                        context.read<AppLocalizationBloc>().add(
                          ChangeAppLocalizationEvent(selectedLocalization: next),
                        );
                      },
                    );
                  },
                ),
                const SizedBox(width: 12.0),

                BlocBuilder<ThemeCubit, ThemeMode>(
                  builder: (context, mode) {
                    final (icon, label) = switch (mode) {
                      ThemeMode.light  => ('assets/images/sunny.png', l10n.themeLight),
                      ThemeMode.dark   => ('assets/images/moon.png', l10n.themeDark),
                      ThemeMode.system => ('assets/images/system_icon.png', l10n.themeSystem),
                    };

                    return PillButton(
                      icon: icon,
                      label: label,
                      onTap: () => context.read<ThemeCubit>().toggleTheme(),
                    );
                  },
                ),
              ],
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
                          icon: buttons[index].icon,
                          label: buttons[index].label,
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
