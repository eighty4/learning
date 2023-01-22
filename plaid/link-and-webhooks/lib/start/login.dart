import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

import '../api.dart';
import '../auth.dart';
import '../endpoints.dart' show fetchAuthToken;
import '../routes.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() => LoginScreenState();
}

class LoginScreenState extends State<LoginScreen> {
  final RegExp _emailValidation = RegExp(r".{2,}@.{2,}");
  String _email = "";
  bool _valid = false;
  bool _remoting = false;
  String? _apiError;
  bool _hasBlurred = false;
  final String apiErrorText =
      "An error occurred talking to our servers.\nTry again later.";

  @override
  Widget build(BuildContext context) {
    final List<Widget> widgets = _remoting
        ? [
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: const [
                CircularProgressIndicator(),
              ],
            )
          ]
        : [
            Padding(
              padding: const EdgeInsets.all(8.0),
              child: TextField(
                decoration: InputDecoration(
                    labelText: "Email",
                    errorText:
                        !_valid && _hasBlurred ? 'Not a valid email' : null),
                onSubmitted: (String email) {
                  if (canSubmit()) {
                    login(context);
                  }
                },
                onChanged: setEmail,
                onEditingComplete: () {
                  if (_email.isNotEmpty) {
                    setState(() {
                      _hasBlurred = true;
                    });
                  }
                },
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(8.0),
              child: ElevatedButton(
                onPressed: canSubmit() ? () => login(context) : null,
                child: const Text('Login'),
              ),
            ),
          ];
    if (!_remoting && _apiError != null) {
      widgets.add(Text(
        _apiError!,
        textAlign: TextAlign.center,
        style: const TextStyle(height: 1.5),
      ));
    }
    return Scaffold(
        body: Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: widgets,
    ));
  }

  bool canSubmit() {
    return _valid;
  }

  void setEmail(String email) {
    if (email != _email) {
      setState(() {
        _valid = _emailValidation.hasMatch(email);
        _email = email;
      });
    }
  }

  Future<void> login(BuildContext context) async {
    if (kDebugMode) {
      print('Starting login api call for $_email');
    }

    setState(() {
      _remoting = true;
      _apiError = null;
    });

    try {
      handleAuthToken(await fetchAuthToken(_email));
    } on SocketException catch (e) {
      errorRedirect(context, e);
    } catch (e) {
      handleApiError(e as ApiError);
    }
  }

  void handleAuthToken(String token) {
    UserAuth.setAuthToken(token);
    Navigator.pushNamed(context, linkScreenRoute);
  }

  void handleApiError(ApiError e) {
    if (kDebugMode) print('Login api error $e');
    String apiErrorText;
    switch (e.statusCode) {
      case 400:
        apiErrorText = "Check that your email is valid and try again.";
        break;
      default:
        apiErrorText =
            "An error occurred talking to our servers.\nTry again later.";
        break;
    }
    setState(() {
      _apiError = apiErrorText;
      _remoting = false;
    });
  }
}
