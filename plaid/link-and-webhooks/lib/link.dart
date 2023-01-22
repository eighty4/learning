import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:plaid_flutter/plaid_flutter.dart';

import 'api.dart';
import 'config.dart';
import 'endpoints.dart' show fetchLinkToken, sandboxLink;
import 'model.dart';
import 'routes.dart';

class LinkAccountScreen extends StatefulWidget {
  const LinkAccountScreen({Key? key}) : super(key: key);

  @override
  State<LinkAccountScreen> createState() => LinkAccountScreenState();
}

class LinkAccountScreenState extends State<LinkAccountScreen> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: SafeArea(
            child: Container(
                padding: const EdgeInsets.only(top: 200, bottom: 60),
                child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      const Text('Welcome to Bank\nwith a flashy bling bling',
                          textAlign: TextAlign.center),
                      const Spacer(),
                      Padding(
                          padding: const EdgeInsets.symmetric(horizontal: 20),
                          child: ElevatedButton(
                            child: const Text('Link bank account'),
                            onPressed: () {
                              if (AppConfig.devMode) {
                                _sandboxLink(context);
                              } else {
                                _linkStart(context);
                              }
                            },
                          ))
                    ]))));
  }

  void _sandboxLink(BuildContext context) async {
    try {
      await sandboxLink();
      if (!mounted) return;
      Navigator.pushNamed(context, accountsScreenRoute);
    } on SocketException catch (e) {
      errorRedirect(context, e);
    }
  }

  void _linkStart(BuildContext context) async {
    try {
      final linkToken = await fetchLinkToken();
      final linkConfig = LinkTokenConfiguration(
        token: linkToken,
      );
      PlaidLink.open(configuration: linkConfig);
      PlaidLink.onSuccess((publicLinkToken, metadata) async {
        _onLinkSuccess(context, publicLinkToken, metadata);
      });
      PlaidLink.onExit(_onLinkExit);
      if (kDebugMode) print('link open');
    } on AuthError {
      Navigator.pushNamed(context, startScreenRoute);
    } on SocketException catch (e) {
      errorRedirect(context, e);
    }
  }

  void _onLinkExit(LinkError? error, LinkExitMetadata metadata) {
    if (error == null) {
      if (kDebugMode) print('link exit');
    } else {
      if (kDebugMode) print('link error ${error.message}');
    }
  }

  void _onLinkSuccess(BuildContext context, String publicLinkToken,
      LinkSuccessMetadata metadata) {
    if (kDebugMode) {
      print(
          'link success $publicLinkToken ${metadata.institution.id} ${metadata.institution.name}');
    }
    Navigator.pushNamed(context, accountsScreenRoute,
        arguments: DisplayLinkedAccountsArguments(
            Bank.fromLinkMetadata(metadata),
            publicLinkToken));
  }
}
