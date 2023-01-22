import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

import '../api.dart';
import '../endpoints.dart' show fetchAccountData;
import '../model.dart';
import '../routes.dart';
import 'accounts_widgets.dart';

class DisplayAllLinkedAccountsScreen extends StatefulWidget {
  const DisplayAllLinkedAccountsScreen({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return DisplayAllLinkedAccountsState();
  }
}

class DisplayAllLinkedAccountsState extends State {
  DisplayAllLinkedAccountsState();

  List<Bank> banks = [];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: SafeArea(
            child:
                ListView(children: banks.map((e) => BankSection(e)).toList())));
  }

  @override
  void initState() {
    super.initState();
    fetchAccountState();
  }

  void fetchAccountState() async {
    try {
      final banks = await fetchAccountData();
      setState(() {
        this.banks = banks;
      });
    } on AuthError {
      if (kDebugMode) print('not authed, redirecting from /accounts to /start');
      Navigator.pushNamed(context, startScreenRoute);
    } on NotFound {
      if (!mounted) rethrow;
      if (kDebugMode) {
        print('no linked banks, redirecting from /accounts to /link');
      }
      Navigator.pushNamed(context, linkScreenRoute);
    } on ApiError catch (e) {
      if (kDebugMode) {
        print('unhandled error on fetchAccountData() $e');
      }
      rethrow;
    } on SocketException catch (e) {
      errorRedirect(context, e);
    }
  }
}
