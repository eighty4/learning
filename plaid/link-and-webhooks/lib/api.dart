import 'dart:convert';
import 'dart:io' show Platform, SocketException;

import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;

import 'auth.dart';

final bankApiHost = resolveApiHost();

String resolveApiHost() {
  if (kIsWeb) {
    return 'http://localhost:8000';
  } else if (Platform.isAndroid) {
    return 'http://10.0.2.2:8000';
  } else {
    return 'http://localhost:8000';
  }
}

void checkPath(String path) {
  if (path[0] != '/') {
    throw StateError('must have leading forward slash on request paths');
  }
}

enum HttpMethod { get, post }

Future<http.Response> justGetIt(String path,
    {bool addAuthHeader = false}) async {
  return doHttp(HttpMethod.get, path, addAuthHeader: addAuthHeader);
}

Future<http.Response> justPostIt(String path,
    {dynamic json, bool addAuthHeader = false}) {
  return doHttp(HttpMethod.post, path,
      json: json, addAuthHeader: addAuthHeader);
}

Future<http.Response> doHttp(HttpMethod method, String path,
    {dynamic json, bool addAuthHeader = false}) async {
  if (kDebugMode) {
    checkPath(path);
  }
  final uri = Uri.parse(bankApiHost + path);
  final Map<String, String> headers = {};
  if (addAuthHeader) {
    final token = await UserAuth.getAuthToken();
    headers['Authorization'] = 'Bearer $token';
  }
  switch (method) {
    case HttpMethod.get:
      return await http.get(uri, headers: headers);
    case HttpMethod.post:
      if (json == null) {
        return await http.post(uri);
      } else {
        headers['Content-Type'] = 'application/json';
        return await http.post(uri, headers: headers, body: jsonEncode(json));
      }
  }
}

class ApiError extends StateError {
  final int statusCode;

  ApiError(String message, this.statusCode) : super(message);

  ApiError.unexpectedHttpStatus(String methodName, http.Response response)
      : this(
            '$methodName()\'s call to ${response.request!.url.path} returned status code ${response.statusCode}',
            response.statusCode);

  ApiError.connectionRefused(String methodName, SocketException e)
      : this(
            '$methodName()\'s http call to ${e.address!.host} got connection refused',
            -1);
}

class NotFound extends ApiError {
  NotFound(String message) : super(message, 404);
}

class AuthError extends StateError {
  AuthError(String message) : super(message);

  AuthError.expiredToken() : super('auth token expired');

  AuthError.noAuthToken() : super('no auth token');

  AuthError.unauthorizedRequest() : super('401 http status');
}
