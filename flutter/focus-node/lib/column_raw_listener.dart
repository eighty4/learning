import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'styled_menu_item.dart';

class ColumnRawListener extends StatefulWidget {
  static const items = <String>['Friends', 'Projects', 'Settings', 'Profile'];

  const ColumnRawListener({super.key});

  @override
  State<ColumnRawListener> createState() => _ColumnRawListenerState();
}

class _ColumnRawListenerState extends State<ColumnRawListener> {
  String? selected;

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Text('Use arrow and enter keys',
              style: TextStyle(fontSize: 24)),
          const Text('to select an item:', style: TextStyle(fontSize: 24)),
          Padding(
            padding: const EdgeInsets.all(20),
            child: FocusScope(
              autofocus: true,
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                crossAxisAlignment: CrossAxisAlignment.center,
                children: ColumnRawListener.items
                    .map((item) => RawKeyboardSelectItem(item,
                        selected: item == selected,
                        onSelected: () => _selectItem(item)))
                    .toList(),
              ),
            ),
          ),
          Text(selected == null ? '' : '$selected selected',
              style: const TextStyle(fontSize: 24)),
        ],
      ),
    );
  }

  void _selectItem(String item) {
    setState(() {
      selected = item;
    });
  }
}

class RawKeyboardSelectItem extends StatefulWidget {
  final String text;
  final bool selected;
  final VoidCallback onSelected;

  const RawKeyboardSelectItem(this.text,
      {super.key, required this.selected, required this.onSelected});

  @override
  State<RawKeyboardSelectItem> createState() => _RawKeyboardSelectItemState();
}

class _RawKeyboardSelectItemState extends State<RawKeyboardSelectItem> {
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
      child: StyledMenuItem(widget.text,
          focused: focused, selected: widget.selected),
    );
  }

  void _onFocusChange(bool focused) {
    setState(() {
      this.focused = focused;
    });
  }
}
