import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:flutter/widgets.dart';
import 'package:plaid_flutter/plaid_flutter.dart';

const noBankImage =
    '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';

const Color defaultPrimaryColor = Color.fromARGB(255, 90, 100, 126);

const Color defaultSecondaryColor = Color.fromARGB(0, 90, 100, 126);

Color fromHexString(String hex, bool opaque) {
  String hexToParse =
      (opaque ? 'ff' : '00') + (hex.length == 7 ? hex.substring(1) : hex);
  return Color(int.parse(hexToParse, radix: 16));
}

class Bank {
  Bank.withDefaultPrimaryColor(this.id, this.name, this.accounts,
      {logoImageBase64 = noBankImage})
      : primaryColor = defaultPrimaryColor,
        transitionColor = defaultSecondaryColor,
        logoBytes = base64Decode(logoImageBase64);

  Bank.withHexStringPrimaryColor(
      this.id, this.name, String hexColor, this.accounts,
      {logoImageBase64 = noBankImage})
      : primaryColor = fromHexString(hexColor, true),
        transitionColor = fromHexString(hexColor, false),
        logoBytes = base64Decode(logoImageBase64);

  factory Bank.fromLinkMetadata(LinkSuccessMetadata metadata) {
    List<Account> checking = [];
    List<Account> savings = [];
    List<Account> credit = [];
    for (var linked in metadata.accounts) {
      final account =
          Account.withoutBalances(linked.id, linked.name, linked.mask);
      if (linked.subtype == 'checking') {
        checking.add(account);
      } else if (linked.subtype == 'credit card') {
        credit.add(account);
      } else if (linked.subtype == 'savings') {
        savings.add(account);
      } else {
        if (kDebugMode) print('omitting account type ${linked.subtype}');
      }
    }
    return Bank.withDefaultPrimaryColor(
      metadata.institution.id,
      metadata.institution.name,
      Accounts(checking, savings, credit),
    );
  }

  final Color primaryColor;
  final Color transitionColor;
  final String id;
  final String name;
  final Uint8List logoBytes; // 152 x 152 png
  final Accounts accounts;
}

class Accounts {
  Accounts(this.checking, this.savings, this.credit);

  final List<Account> checking;
  final List<Account> credit;
  final List<Account> savings;
}

class Account {
  Account(this.id, this.name, this.maskedAcctNumber, this.balances);

  Account.withoutBalances(this.id, this.name, this.maskedAcctNumber)
      : balances = null;

  final String id;
  final String name;
  final String maskedAcctNumber;
  final Balances? balances;
}

class Balances {
  final double? available;
  final double current;
  final double? limit;

  Balances(this.available, this.current, this.limit);
}

class Transaction {
  Transaction(this.name, this.amount, this.when);

  final String name;
  final double amount;
  final DateTime when;
}

List<Bank> banksFromJson(String json) {
  return List<Bank>.from(
      jsonDecode(json).map((bankJson) => bankFromJsonMap(bankJson)));
}

Bank bankFromJson(String json) {
  Map bankJson = jsonDecode(json);
  return bankFromJsonMap(bankJson);
}

Bank bankFromJsonMap(Map<dynamic, dynamic> bankJson) {
  final bankId = bankJson['bankId'];
  final name = bankJson['name'];
  final color = bankJson['primaryColor'];
  final accounts = accountsFromJsonMap(bankJson['accounts']);
  final logoImage = bankJson['logo'] ?? noBankImage;
  return Bank.withHexStringPrimaryColor(bankId, name, color, accounts,
      logoImageBase64: logoImage);
}

Accounts accountsFromJsonMap(accountJson) {
  final checking = List<Account>.from(accountJson['checking']
      .map((accountJson) => accountFromJsonMap(accountJson)));
  final savings = List<Account>.from(accountJson['savings']
      .map((accountJson) => accountFromJsonMap(accountJson)));
  final credit = List<Account>.from(accountJson['credit']
      .map((accountJson) => accountFromJsonMap(accountJson)));
  return Accounts(checking, savings, credit);
}

Account accountFromJsonMap(accountJson) {
  return Account(accountJson['accountId'], accountJson['name'],
      accountJson['mask'], balancesFromJsonMap(accountJson['balance']));
}

Balances balancesFromJsonMap(balanceJson) {
  final available = balanceJson['available'] != null
      ? double.parse(balanceJson['available'])
      : null;
  final current = double.parse(balanceJson['current']);
  final limit =
      balanceJson['limit'] != null ? double.parse(balanceJson['limit']) : null;
  return Balances(available, current, limit);
}
