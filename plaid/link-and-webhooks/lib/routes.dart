import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

import 'model.dart';

const accountsScreenRoute = "/accounts";
const activityScreenRoute = "/activity";
const errorScreenRoute = "/error";
const linkScreenRoute = "/link";
const startScreenRoute = "/start";

void errorRedirect(BuildContext context, Exception e) {
  if (kDebugMode) {
    print('error redirect on: $e');
  }
  Navigator.pushNamed(context, errorScreenRoute);
}

class DisplayLinkedAccountsArguments {
  DisplayLinkedAccountsArguments(this.bank, this.publicLinkToken);

  final Bank bank;
  final String publicLinkToken;
}
