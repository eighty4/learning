import 'dart:convert';
import 'dart:io';

import 'package:flutter/foundation.dart';

import 'api.dart';
import 'model.dart';

Future<String> fetchAuthToken(String email) async {
  try {
    final response = await justPostIt('/user/identity', json: {"email": email});
    if (kDebugMode) print('Login api response status ${response.statusCode}');
    if (response.statusCode == 200) {
      return jsonDecode(response.body)['token'];
    } else {
      throw ApiError.unexpectedHttpStatus('fetchAuthToken', response);
    }
  } on SocketException {
    rethrow;
  } catch (e) {
    if (kDebugMode) print("error fetching auth token $e");
    throw ApiError(e.toString(), -1);
  }
}

Future<Bank> saveBankToken(String institutionId, String publicToken) async {
  final response = await justPostIt('/plaid/link', addAuthHeader: true, json: {
    'institutionId': institutionId,
    'publicToken': publicToken,
  });
  switch (response.statusCode) {
    case 401:
      throw AuthError.unauthorizedRequest();
    case 201:
      return bankFromJson(response.body);
    default:
      throw ApiError.unexpectedHttpStatus('saveBankToken', response);
  }
}

Future<List<Bank>> fetchAccountData() async {
  final response = await justGetIt('/accounts', addAuthHeader: true);
  switch (response.statusCode) {
    case 401:
      throw AuthError.unauthorizedRequest();
    case 200:
      return banksFromJson(response.body);
    case 404:
      throw NotFound("no bank data");
    default:
      throw ApiError.unexpectedHttpStatus('fetchAccountData', response);
  }
}

Future<String> fetchLinkToken() async {
  final response = await justGetIt('/plaid/link', addAuthHeader: true);
  switch (response.statusCode) {
    case 401:
    case 403:
      throw AuthError.unauthorizedRequest();
    case 201:
      return jsonDecode(response.body)['linkToken'];
    default:
      throw ApiError.unexpectedHttpStatus('fetchLinkToken', response);
  }
}

Future<void> sandboxLink() async {
  final response = await justGetIt('/plaid/link/sandbox', addAuthHeader: true);
  switch (response.statusCode) {
    case 401:
    case 403:
      throw AuthError.unauthorizedRequest();
    case 201:
      return;
    default:
      throw ApiError.unexpectedHttpStatus('fetchLinkToken', response);
  }
}
