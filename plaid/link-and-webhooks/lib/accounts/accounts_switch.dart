import 'package:flutter/widgets.dart';

import '../routes.dart';
import 'accounts_all.dart';
import 'accounts_new.dart';

class DisplayAccountsScreen extends StatelessWidget {
  const DisplayAccountsScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final route = ModalRoute.of(context);
    if (route?.settings.arguments == null) {
      return const DisplayAllLinkedAccountsScreen();
    } else if (route?.settings.arguments is DisplayLinkedAccountsArguments) {
      return DisplayNewlyLinkedAccountsScreen(
          route?.settings.arguments as DisplayLinkedAccountsArguments);
    } else {
      throw StateError('here we go');
    }
  }
}
