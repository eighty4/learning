import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(const MaterialApp(home: Scaffold(body: KeyboardMenu())));
}

class KeyboardMenu extends StatefulWidget {
  static const items = <String>['Friends', 'Projects', 'Settings', 'Profile'];

  const KeyboardMenu({Key? key}) : super(key: key);

  @override
  State<KeyboardMenu> createState() => _KeyboardMenuState();
}

class _KeyboardMenuState extends State<KeyboardMenu> {
  String selected = KeyboardMenu.items[1];

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(20),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: [
          FocusScope(
            autofocus: true,
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: KeyboardMenu.items
                  .map((item) => MenuItem(item,
                      selected: item == selected,
                      onSelected: () => selectItem(item)))
                  .toList(),
            ),
          ),
          Text('$selected selected', style: const TextStyle(fontSize: 24)),
        ],
      ),
    );
  }

  selectItem(String item) {
    setState(() {
      selected = item;
    });
  }
}

class MenuItem extends StatefulWidget {
  final String text;
  final bool selected;
  final VoidCallback onSelected;

  const MenuItem(this.text,
      {super.key, required this.selected, required this.onSelected});

  @override
  State<MenuItem> createState() => _MenuItemState();
}

class _MenuItemState extends State<MenuItem> {
  late FocusNode focusNode;
  bool focused = false;

  @override
  void initState() {
    super.initState();
    focusNode = FocusNode();
    focusNode.addListener(() {
      _onFocusChange(focusNode.hasFocus);
    });
  }

  @override
  void dispose() {
    super.dispose();
    focusNode.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return RawKeyboardListener(
      autofocus: widget.selected,
      focusNode: focusNode,
      onKey: (key) {
        if (key.isKeyPressed(LogicalKeyboardKey.enter)) {
          widget.onSelected();
        }
      },
      child: Container(
          padding: const EdgeInsets.symmetric(vertical: 20),
          decoration: BoxDecoration(
              border: Border.all(
                  color: focused ? Colors.blue : Colors.transparent, width: 3)),
          child: Center(
              child: Text(widget.text,
                  style: TextStyle(
                      fontSize: 36,
                      decoration:
                          widget.selected ? TextDecoration.underline : null,
                      decorationThickness: 3,
                      decorationColor:
                          widget.selected ? Colors.red : Colors.transparent)))),
    );
  }

  void _onFocusChange(bool focused) {
    setState(() {
      this.focused = focused;
    });
  }
}
