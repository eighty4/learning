import 'package:flutter/widgets.dart';

import '../model.dart';

class BankSection extends StatelessWidget {
  const BankSection(this.bank, {Key? key}) : super(key: key);

  final Bank bank;

  @override
  Widget build(BuildContext context) {
    return Column(children: [
      Row(children: [Text(bank.name)]),
      ...bank.accounts.checking.map((account) => AccountDetails(account)),
      ...bank.accounts.savings.map((account) => AccountDetails(account)),
      ...bank.accounts.credit.map((account) => AccountDetails(account)),
    ]);
  }
}

class AccountDetails extends StatelessWidget {
  const AccountDetails(this.account, {Key? key}) : super(key: key);

  final Account account;

  @override
  Widget build(BuildContext context) {
    return Row(children: [Text(account.name)]);
  }
}
