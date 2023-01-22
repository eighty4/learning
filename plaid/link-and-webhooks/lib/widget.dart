import 'package:flutter/widgets.dart';

class Button extends StatelessWidget {
  const Button(this.text, {Key? key, required this.onTap}) : super(key: key);

  final String text;
  final GestureTapCallback onTap;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 15),
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(5),
          color: const Color.fromARGB(255, 125, 125, 0),
        ),
        child: Center(
            child: Text(
          text,
          style: const TextStyle(fontSize: 18),
        )),
      ),
    );
  }
}

class Screen extends StatelessWidget {
  const Screen({Key? key, required this.child}) : super(key: key);

  final Widget child;

  @override
  Widget build(BuildContext context) {
    return Directionality(
        textDirection: TextDirection.ltr,
        child:
            Container(padding: const EdgeInsets.only(top: 30), child: child));
  }
}
