import 'package:banking_buddy/main.dart' as app;
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';

// import 'package:shared_preferences/shared_preferences.dart';
// SharedPreferences.setMockInitialValues({'': true});

Future<void> main() async {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  group('end-to-end test', () {
    testWidgets('login', (WidgetTester tester) async {
      app.main();
      await tester.pumpAndSettle();
      await Future<void>.delayed(const Duration(milliseconds: 100));

      final emailField = find.byWidgetPredicate((w) => w is TextField);
      await tester.tap(emailField);
      await tester.enterText(emailField, 'asdf@asdf.com');
      await tester.pumpAndSettle();

      await tester.tap(find.byWidgetPredicate((w) => w is ElevatedButton));
      await tester.pumpAndSettle();

      expect(find.text("Link bank account"), findsOneWidget);
    });
  });
}
