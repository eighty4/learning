import 'dart:math';

import 'package:flutter/material.dart';

void main() {
  runApp(MaterialApp(home: Scaffold(body: GroceryList())));
}

Future<List<String>> loadGroceryItems() async {
  print("loading grocery items");
  await Future.delayed(Duration(milliseconds: 100 + Random().nextInt(900)));
  return List.of(["butter", "bread", "bacon", "broccoli"]);
}

class GroceryList extends StatelessWidget {
  // do not create Future instance within build()
  final loadingGroceryItems = loadGroceryItems();

  GroceryList({super.key});

  @override
  Widget build(BuildContext context) {
    print("build GroceryList");
    return Container(
      padding: const EdgeInsets.all(50),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text("Groceries", style: TextStyle(fontSize: 48)),
          FutureBuilder<List<String>>(
              future: loadingGroceryItems,
              builder: (context, snapshot) {
                print("build FutureBuilder");
                print(
                    "snapshot connectionState=${snapshot.connectionState} hasData=${snapshot.hasData}");
                if (snapshot.connectionState == ConnectionState.done) {
                  final items = snapshot.data!;
                  return Padding(
                    padding: const EdgeInsets.all(30),
                    child: ListView.separated(
                        shrinkWrap: true,
                        itemCount: items.length,
                        separatorBuilder: (context, index) =>
                            const SizedBox(height: 20),
                        itemBuilder: (context, i) => Text(items[i],
                            style: const TextStyle(fontSize: 24))),
                  );
                } else {
                  return const Text("loading..");
                }
              }),
        ],
      ),
    );
  }
}
