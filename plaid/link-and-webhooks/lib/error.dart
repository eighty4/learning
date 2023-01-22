import 'package:banking_buddy/routes.dart';
import 'package:flutter/material.dart';

class ErrorScreen extends StatelessWidget {
  const ErrorScreen({Key? key}) : super(key: key);

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
                      const Text(
                          'The cloud dropped an error.\nFeel free to try again.',
                          textAlign: TextAlign.center),
                      const Spacer(),
                      Padding(
                          padding: const EdgeInsets.symmetric(horizontal: 20),
                          child: ElevatedButton(
                            child: const Text('Start over'),
                            onPressed: () {
                              Navigator.pushNamed(context, startScreenRoute);
                            },
                          ))
                    ]))));
  }
}
