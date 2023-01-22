import 'package:flutter/foundation.dart';
import 'package:jwt_decoder/jwt_decoder.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'api.dart';

const _authTokenPrefKey = "auth_token";

class UserAuth {
  String? token;

  static UserAuth? _instance;

  static UserAuth _get() {
    _instance ??= UserAuth();
    return _instance!;
  }

  static Future<void> setAuthToken(String authToken) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_authTokenPrefKey, authToken);
    _get().token = authToken;
    if (kDebugMode) print('auth token saved $authToken');
  }

  static Future<String> getAuthToken() async {
    var token = _get().token;
    if (token != null) {
      return token;
    }
    final prefs = await SharedPreferences.getInstance();
    token = _instance!.token = prefs.getString(_authTokenPrefKey);
    if (token == null) {
      throw AuthError.noAuthToken();
    } else if (JwtDecoder.isExpired(token)) {
      prefs.remove(_authTokenPrefKey);
      throw AuthError.expiredToken();
    }
    return token;
  }

  static Future<void> removeAuthToken() async {
    _get().token = null;
    final prefs = await SharedPreferences.getInstance();
    prefs.remove(_authTokenPrefKey);
  }
}
