import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'styled_menu_item.dart';

class GridShortcuts extends StatefulWidget {
  static const items = <int>[1, 2, 3, 4, 5, 6, 7];

  const GridShortcuts({super.key});

  @override
  State<GridShortcuts> createState() => _GridShortcutsState();
}

class _GridShortcutsState extends State<GridShortcuts> {
  int? selected;

  @override
  Widget build(BuildContext context) {
    final size = MediaQuery.of(context).size;
    return Expanded(
        child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
      const Text('Use arrow and enter keys', style: TextStyle(fontSize: 24)),
      const Text('to select an item:', style: TextStyle(fontSize: 24)),
      Shortcuts(
        shortcuts: <LogicalKeySet, Intent>{
          LogicalKeySet(LogicalKeyboardKey.enter): const SelectItemIntent(),
        },
        child: FocusScope(
            autofocus: true,
            child: Container(
              constraints: BoxConstraints(maxWidth: size.width),
              child: GridView.count(
                shrinkWrap: true,
                crossAxisCount: 4,
                mainAxisSpacing: 0,
                crossAxisSpacing: 0,
                padding: const EdgeInsets.all(20),
                children: GridShortcuts.items
                    .map((item) => ShortcutSelectItem(item,
                        selected: selected == item,
                        onSelected: () => _selectItem(item)))
                    .toList(),
              ),
            )),
      ),
      Text(selected == null ? '' : '$selected selected',
          style: const TextStyle(fontSize: 24)),
    ]));
  }

  void _selectItem(int item) {
    setState(() {
      selected = item;
    });
  }
}

class SelectItemIntent extends Intent {
  const SelectItemIntent();
}

class SelectItemAction extends Action<SelectItemIntent> {
  final VoidCallback onSelect;

  SelectItemAction({required this.onSelect});

  @override
  Object? invoke(covariant SelectItemIntent intent) {
    onSelect();
    return null;
  }
}

class ShortcutSelectItem extends StatefulWidget {
  final int item;
  final bool selected;
  final VoidCallback onSelected;

  const ShortcutSelectItem(this.item,
      {super.key, required this.selected, required this.onSelected});

  @override
  State<ShortcutSelectItem> createState() => _ShortcutSelectItemState();
}

class _ShortcutSelectItemState extends State<ShortcutSelectItem> {
  bool focused = false;

  @override
  Widget build(BuildContext context) {
    return Center(
        child: Actions(
      actions: {
        SelectItemIntent: SelectItemAction(onSelect: widget.onSelected),
      },
      child: Focus(
        onFocusChange: _onFocusChange,
        child: StyledMenuItem(widget.item.toString(),
            focused: focused, selected: widget.selected),
      ),
    ));
  }

  void _onFocusChange(bool focused) {
    setState(() {
      this.focused = focused;
    });
  }
}
