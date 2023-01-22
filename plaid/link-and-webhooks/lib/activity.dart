import 'package:flutter/widgets.dart';
import 'package:intl/intl.dart';

import 'model.dart';
import 'widget.dart';

const bankImage =
    'iVBORw0KGgoAAAANSUhEUgAAAJgAAACYCAMAAAAvHNATAAAAkFBMVEVHcEz////////////////////////+/P3////////////////////////////////////////////////////jGDcCImovSoVccJ8ZN3jlI0H+6+7w8vZDW5HsVWv2r7pvgauVo8HU2uboNlLj5u/vb4P0mKf719yCkrW9xdj6y9KyvNLxhZansszIz9/4v8iIUCTnAAAAFHRSTlMAgZcI0Kqg/OEXaevCJj5HH7ovV9YxZyUAAAclSURBVHjazVyJdqowEHVBQbStS5qAgiKLikv7/3/3kgBCKDxJCJI5p8tREi5zJzdDtsGgpY3Xm+F0slpoxlzX54a2WE2mw816POjRPszRygA1ZqxG5kcPoJbmtBZTAd3UXL4T1eeoAagnuNHnmwgccqBKsQ07J3VmakDINHPWZWCNdCBs+qircPuYtoBFoU27YHQ9ARJsspYdW1JgUWgyY21szoE0m5vSeoVPDUg1TY6wzaZAuk0l8Pk1Bx3Y/KstriHoyIbtaFyAzmwxU43G9nQOQccmSOcIdG4jEVGdgDfYhFtsxwvwFltwIptp4E2mcTXOmQHeZgYHsrEG3mjaWLX44o6zCXizTZTRLyE9G4IerEEf8AV6sZf95mzeD7D5K9FYgJ5soWCANQizL9CjfakXYC/DbAp6tWnt+yPo2T5V6Lo5unMT9G5mF5G/O0RBEP22GqrSZ9JzCv2+/aa23Z92UvOMdVsasMdSbN8tsK3lOex6Ouf/34MM20GX47IPQTedCJJtdHi66Jr5bRudRWr8kKGtu+g7s+0+x3bYZ24TgFZS2aXeNq4otqyWHW4KQSTEpr6UlU7ncVWkTz+L1sek2TPOZwsvcbHEb8Fv+0PZBdf7WVjL+ETftyA26+EUanvG1ff+ynC9J5i52kFR/jVuWASZz1IalZuinqCixgFNE9KKsAZWEvL3grCeC/wSyAcBxWicUDteCsvNYIWee3t6qJJa/r4gT7IbDqHoRzuBhWIGKDrqVbL7tICzhzI4E8RbCss+ZkAv6SfQfjCMMhxGv8IJYyMR890UhOeUgBKoeqE3KMIK7qyzznem0b6QMkMkuJ5AC1DL3gpOegXD0a4hl8vXuB5/WPSesKwwD8ITA4vtkvJWuj290vNlM3UN3TKGOGcxzq87BAwspo5fppUG1yYa+zKxSBujnemCYz3ddclZPO/rYTGQ/3Jck2I0CLEHwu5ySrwWAo4EV1FM2fuWYb1OIo3msv8TV7ir+t5bNrp/y7CuDcWfrwPPowv95O4qsshmh9e9AKwkyHhSMcer0ojDtq5LLKS4PLASJVvxZBYoa4y3ancxLOpsF/AdNO8DVs07Siby3Wp3sXcuxXxw4LgNjv4xZz/mXCA81rir2Nh2bHBt73xJ8ljgRTfMReIa1InmiWVxz/sGvB5sWrx8F+/OuOu8Fw2uzDYtRl2LZLGNkQ36111jZbIoPIpYpJFhquSuvdA4xlR8zCJvjttTfXRtD2K1T7hkrHqAgIn6UmOMRId9Vq1mHKjTGKoO25ZBn9mC65WyKv5PjNTLcRd9uWw5yVwf9aLRlUm/vDkHlsb9rlVl84EuC1fEalfL2nRZwNjWGJxBa2DzDmiMdq0rnA8M6Ty2pjEJfk125LenMZELWVO650BKa8wFdiWrVerRn+ynha1krv44tRRVthOXOXn6e5VW1bTP6fn/J4obNYFt2s+6dWNr7te3N9l4MDBUxGVwDhG8zVb9LBdrNqhiqgjMFJ/b7dY+BgMlo99QYUVPdYc0UDPIzMoJCBchNxkBuyD610euAxzk4X8t5OCfZBzK8ZDtxvR7bBYoFnEQGaLFX4QAxOnX+DPkkkmnByLjkaFlw6QmF8UlCMvKKRtoezYkFeg2tCkwCD3gQHJrFzpH6CX5F4KuRS/8gbZlJY+SFXEgdPFjQIiB3SCiX4f4MhtivEcy8udgWBZ6JNVbVSH2R8kgSoriGl0YJyWhnwHzoZ2Mcx7JB/hmOgb2rDgr4pAiIUyBJU8S4ssupGJau0c/JVV5uIzzR8UqpgXJg9FBVguG9JY+9GyUAUtuTL+NE6SJx7IPkyL4amh5+BcFZrtuSIChi40vT4AhmA5M6rZ7KwyeEvusnkjFVCIyReRA10fkWXx4ecBLCszKKmGAIaQnDKZFHOh5kPxkVFJg2GJQBnaDF5/w/pfJ8tQzptInKI504PxIgWEAKbDQJjeroTIp8qDAMI7QY6l0j3Q2haUymWkJmSSxerIee8wlpRCMf2LyLARYmAFzbunjkeDHreQICsBIEfo9BgYwvR5xC/WYR4Hp9Kmewe/h1u9A9PNzKU7/FJf3MC+XpHV7DgipAhB98IkEHJ9y4aFHKhe2TWfqfZRWmxdx8Ee+j8UjkwsC1vawUlyIXDwSubCxptxI5Q5yi6+USq0CLKur2BKaLo1dDqhQUjaSsUyrC4ctlVo0XE4sWi8F7ChF7HUHS92YhfTlprJedAdKuqxqI9By3j+u6iX9qi4CV3fZvLIbDdTdmqHuZhZlt/+ou2FK3S1m6m7KU3Ybo7obP9XdKqvu5mJ1t2Mru4Fd3S3/6h6SoO6xEuoexKHu0SUKH/ai7vE46h4opO4RTAofWqXuMV/qHowm+yg53ZR5wOhSzcP3FD6uUOEDHhU+ElPhQ0QVPnZV5YNqVT7aNyNVxcOQ816ho+Oj/wExr689egtFJAAAAABJRU5ErkJggg==';

class TransactionGroup {
  TransactionGroup(
      {required this.bank, required this.account, required this.transactions});

  final Bank bank;
  final Account account;
  final List<Transaction> transactions;

  double calcTotal() {
    return transactions.fold(0, (total, tx) => total + tx.amount);
  }
}

class AccountActivityScreen extends StatefulWidget {
  const AccountActivityScreen({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return AccountActivity();
  }
}

class AccountActivity extends State {
  List<TransactionGroup> txGroups = [];

  @override
  void initState() {
    super.initState();
  }

  double calcTotal() {
    return txGroups.fold(0, (total, txGroup) => total + txGroup.calcTotal());
  }

  @override
  Widget build(BuildContext context) {
    double total = calcTotal();
    return Screen(
        child: ListView(children: [
      Text('You\'ve spent ${total.toStringAsFixed(2)}'),
      ...txGroups.map((txGroup) => TransactionGroupBox(txGroup)).toList()
    ]));
  }
}

class TransactionGroupBox extends StatelessWidget {
  const TransactionGroupBox(this.txGroup, {Key? key}) : super(key: key);

  final TransactionGroup txGroup;

  @override
  Widget build(BuildContext context) {
    return Dismissible(
        key: ObjectKey(txGroup),
        direction: DismissDirection.horizontal,
        behavior: HitTestBehavior.translucent,
        child: Container(
            padding: const EdgeInsets.only(bottom: 15),
            child: Column(children: [
              TransactionGroupHeader(txGroup),
              ...txGroup.transactions.map((tx) => TransactionBox(tx)).toList(),
            ])));
  }
}

class TransactionGroupHeader extends StatelessWidget {
  const TransactionGroupHeader(this.txGroup, {Key? key}) : super(key: key);

  final TransactionGroup txGroup;

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
          // color: txGroup.bank.primaryColor,
          gradient: LinearGradient(
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            colors: [
              txGroup.bank.primaryColor,
              txGroup.bank.transitionColor,
            ],
          ),
          borderRadius: const BorderRadius.only(
            topLeft: Radius.circular(20.0),
          )),
      padding: const EdgeInsets.all(15),
      child: Row(
          mainAxisAlignment: MainAxisAlignment.start,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            Image.memory(txGroup.bank.logoBytes, height: 40, width: 40),
            Expanded(
                child: Padding(
                    padding: const EdgeInsets.only(left: 20),
                    child: Text(
                      txGroup.account.name,
                      style: const TextStyle(fontSize: 22),
                    ))),
            Text('(${txGroup.account.maskedAcctNumber})',
                style: const TextStyle(fontSize: 16))
          ]),
    );
  }
}

class TransactionBox extends StatelessWidget {
  const TransactionBox(this.tx, {Key? key}) : super(key: key);

  final Transaction tx;

  @override
  Widget build(BuildContext context) {
    int dollars = tx.amount.toInt();
    double cents = tx.amount - dollars;
    return Dismissible(
        key: ObjectKey(tx),
        child: Container(
          padding: const EdgeInsets.all(15),
          child: Row(
              mainAxisAlignment: MainAxisAlignment.start,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                Container(
                  padding: const EdgeInsets.only(right: 20),
                  alignment: Alignment.center,
                  child: Text(DateFormat('M/d').format(tx.when),
                      style: const TextStyle(fontSize: 16)),
                ),
                Expanded(
                    child: Text(tx.name, style: const TextStyle(fontSize: 20))),
                Row(
                    mainAxisAlignment: MainAxisAlignment.end,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(dollars.toString(),
                          style: const TextStyle(fontSize: 24)),
                      Container(
                          width: 25,
                          padding: const EdgeInsets.only(left: 2, top: 2),
                          child: Text(cents.toStringAsFixed(2).substring(2),
                              style: const TextStyle(fontSize: 16)))
                    ])
              ]),
        ));
  }
}
