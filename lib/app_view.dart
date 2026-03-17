import 'package:flutter/material.dart';
import 'package:flutter_app/features/home/view/home_view.dart';

/// Root view that configures the app-level [MaterialApp].
class AppView extends StatelessWidget {
  const AppView({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: const HomeView(),
    );
  }
}
