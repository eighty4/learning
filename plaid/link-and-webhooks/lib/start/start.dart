import 'package:flutter/material.dart';

import '../api.dart';
import '../auth.dart';
import '../config.dart';
import '../routes.dart';
import 'login.dart';

class StartAppScreen extends StatefulWidget {
  const StartAppScreen({Key? key}) : super(key: key);

  @override
  State createState() {
    return StartAppState();
  }
}

class StartAppState extends State {
  bool loadingAuthToken = true;

  @override
  void initState() {
    super.initState();
    _loadAuthToken();
  }

  _loadAuthToken() async {
    if (AppConfig.devMode) {
      await UserAuth.removeAuthToken();
    }
    try {
      await UserAuth.getAuthToken();
      if (mounted) return;
      Navigator.pushNamed(context, accountsScreenRoute);
    } on AuthError {
      setState(() {
        loadingAuthToken = false;
      });
    } catch (e) {
      rethrow;
    }
  }

  @override
  Widget build(BuildContext context) {
    return loadingAuthToken
        ? const Scaffold(body: SizedBox.expand())
        : const LoginScreen();
  }
}
