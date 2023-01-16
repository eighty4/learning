import 'package:flutter/material.dart';

import 'styled_menu_item.dart';

class RowFocus extends StatefulWidget {
  static const items = <int>[1, 2, 3, 4, 5];

  const RowFocus({super.key});

  @override
  State<RowFocus> createState() => _RowFocusState();
}

class _RowFocusState extends State<RowFocus> {
  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Text('Use arrow keys', style: TextStyle(fontSize: 24)),
          const Text('to focus an item:', style: TextStyle(fontSize: 24)),
          Padding(
            padding: const EdgeInsets.all(20),
            child: FocusScope(
              autofocus: true,
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: RowFocus.items
                    .map((item) => FocusUnableToSelectItem(item))
                    .toList(),
              ),
            ),
          ),
          const Padding(
            padding: EdgeInsets.symmetric(horizontal: 20),
            child: Text(
                'Focus is unable to handle selection without a GestureDetector, RawKeyboardListener or Shortcuts',
                style: TextStyle(fontSize: 24),
                textAlign: TextAlign.center),
          ),
        ],
      ),
    );
  }
}

class FocusUnableToSelectItem extends StatefulWidget {
  final int item;

  const FocusUnableToSelectItem(this.item, {super.key});

  @override
  State<FocusUnableToSelectItem> createState() =>
      _FocusUnableToSelectItemState();
}

class _FocusUnableToSelectItemState extends State<FocusUnableToSelectItem> {
  bool focused = false;

  @override
  Widget build(BuildContext context) {
    return Focus(
      onFocusChange: _onFocusChange,
      child: StyledMenuItem(widget.item.toString(),
          selected: false, focused: focused),
    );
  }

  void _onFocusChange(bool focused) {
    setState(() => this.focused = focused);
  }
}
