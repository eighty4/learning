import 'dart:math';

import 'package:flutter/material.dart';

void main() {
  runApp(MaterialApp(home: Scaffold(body: GroceryList())));
}

final groceries = [
  "bananas",
  "bread",
  "butter",
  "broccoli",
  "beignets",
  "beans",
  "bran",
  "bacon",
  "beef",
  "bay leaves",
];

Stream<List<String>?> initGroceryItemStream() {
  print("initializing grocery item stream");
  final r = Random();
  return Stream.periodic(const Duration(seconds: 1), (i) {
    if (i == 0) {
      return null;
    }
    final c = 10 - r.nextInt(10);
    return List.generate(c, (index) => groceries[r.nextInt(10)])
        .toSet()
        .toList();
  });
}

class GroceryList extends StatelessWidget {
  // do not create Stream instance within build()
  final groceryItemStream = initGroceryItemStream();

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
          StreamBuilder(
              stream: groceryItemStream,
              builder: (context, snapshot) {
                print("build StreamBuilder");
                print(
                    "snapshot connectionState=${snapshot.connectionState} hasData=${snapshot.hasData}");

                if (snapshot.hasData && snapshot.hasData) {
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
              })
        ],
      ),
    );
  }
}
