import 'package:flutter/material.dart';

import 'accounts/accounts_switch.dart';
import 'activity.dart';
import 'error.dart';
import 'link.dart';
import 'routes.dart';
import 'start/start.dart';

// conditional import example for mobile app vs web
// import 'my_mobile_api.dart' if (dart.library.js) 'my_web_api.dart';

void main() {
  runApp(const BankApp());
}

class BankApp extends StatelessWidget {
  const BankApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
        color: Colors.black,
        child: MaterialApp(
            color: const Color.fromARGB(255, 125, 125, 125),
            // textStyle: const TextStyle(color: Colors.white),
            home: const StartAppScreen(),
            onGenerateRoute: (settings) {
              return null;
            },
            routes: {
              accountsScreenRoute: (context) => const DisplayAccountsScreen(),
              activityScreenRoute: (context) => const AccountActivityScreen(),
              errorScreenRoute: (context) => const ErrorScreen(),
              linkScreenRoute: (context) => const LinkAccountScreen(),
              startScreenRoute: (context) => const StartAppScreen(),
            },
            title: 'Bank.ng'));
  }
}
