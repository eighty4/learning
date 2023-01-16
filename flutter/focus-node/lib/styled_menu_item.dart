import 'package:flutter/material.dart';

class StyledMenuItem extends StatelessWidget {
  final String text;
  final bool focused;
  final bool selected;

  const StyledMenuItem(this.text,
      {super.key, required this.focused, required this.selected});

  @override
  Widget build(BuildContext context) {
    return Container(
        padding: const EdgeInsets.all(20),
        decoration: BoxDecoration(
            border: Border.all(
                color: focused ? Colors.deepPurple : Colors.transparent,
                width: 3)),
        child: Center(
            child: Text(text,
                style: TextStyle(
                    fontSize: 36,
                    decoration: selected ? TextDecoration.underline : null,
                    decorationThickness: 3,
                    decorationColor:
                        selected ? Colors.teal : Colors.transparent))));
  }
}
