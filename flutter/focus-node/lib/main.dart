import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

import 'column_raw_listener.dart';
import 'grid_shortcuts.dart';
import 'row_focus.dart';

void main() {
  runApp(const MaterialApp(
      home: SafeArea(child: Scaffold(body: FocusNodeExamples()))));
}

enum FocusNodeExample { columnRawListener, rowFocus, gridShortcuts }

extension on FocusNodeExample {
  String label() {
    switch (this) {
      case FocusNodeExample.columnRawListener:
        return 'Column - RawKeyboardListener';
      case FocusNodeExample.gridShortcuts:
        return 'Grid - Shortcuts';
      case FocusNodeExample.rowFocus:
        return 'Row - Focus';
    }
  }
}

class FocusNodeExamples extends StatefulWidget {
  const FocusNodeExamples({super.key});

  @override
  State<FocusNodeExamples> createState() => _FocusNodeExamplesState();
}

class _FocusNodeExamplesState extends State<FocusNodeExamples> {
  FocusNodeExample? example;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(15),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          const Padding(
            padding: EdgeInsets.all(15),
            child: Text(
              'Choose an example for focus_manager.dart APIs:',
              style: TextStyle(fontSize: 24),
              textAlign: TextAlign.center,
            ),
          ),
          Center(
            child: DropdownButton(
                value: example,
                items: FocusNodeExample.values
                    .map((fne) => DropdownMenuItem(
                        value: fne,
                        child: Text(fne.label(),
                            style: const TextStyle(fontSize: 18))))
                    .toList(),
                onChanged: _onChanged),
          ),
          Padding(
            padding: const EdgeInsets.symmetric(vertical: 15),
            child: Text(
              example == null ? '' : 'Use down key to move focus to example',
              style: const TextStyle(fontSize: 18, fontStyle: FontStyle.italic),
              textAlign: TextAlign.center,
            ),
          ),
          Container(height: 2, color: Colors.black),
          _exampleWidget(),
        ],
      ),
    );
  }

  _onChanged(FocusNodeExample? example) {
    if (kDebugMode) {
      print('Opening example ${example?.name}');
    }
    setState(() {
      this.example = example;
    });
  }

  Widget _exampleWidget() {
    switch (example) {
      case FocusNodeExample.columnRawListener:
        return const ColumnRawListener();
      case FocusNodeExample.gridShortcuts:
        return const GridShortcuts();
      case FocusNodeExample.rowFocus:
        return const RowFocus();
      default:
        return const Expanded(
          child: FlutterLogo(
            size: 200,
          ),
        );
    }
  }
}
