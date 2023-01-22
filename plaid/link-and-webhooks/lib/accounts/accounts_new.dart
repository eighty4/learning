import 'package:flutter/material.dart';

import '../api.dart';
import '../endpoints.dart' show saveBankToken;
import '../routes.dart';
import 'accounts_widgets.dart';

class DisplayNewlyLinkedAccountsScreen extends StatefulWidget {
  const DisplayNewlyLinkedAccountsScreen(this.arguments, {Key? key})
      : super(key: key);

  final DisplayLinkedAccountsArguments arguments;

  @override
  State<StatefulWidget> createState() {
    return DisplayNewlyLinkedAccountsState();
  }
}

class DisplayNewlyLinkedAccountsState
    extends State<DisplayNewlyLinkedAccountsScreen> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(body: SafeArea(child: BankSection(widget.arguments.bank)));
  }

  @override
  void initState() {
    super.initState();
    saveLinkedBank();
  }

  saveLinkedBank() async {
    try {
      await saveBankToken(
          widget.arguments.bank.id, widget.arguments.publicLinkToken);
    } on AuthError {
      Navigator.pushNamed(context, startScreenRoute);
    }
  }
}
