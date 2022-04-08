import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

void main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  static const String _title = 'Flutter Contour Animation';

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
        title: _title,
        home: Column(
          children: [
            Row(
              children: const [
                ButtonContainer(),
              ],
            )
          ],
        ));
  }
}

class ButtonContainer extends StatefulWidget {
  const ButtonContainer({Key? key}) : super(key: key);

  @override
  State<ButtonContainer> createState() => ButtonContainerState();
}

class ButtonContainerState extends State<ButtonContainer> {
  @override
  Widget build(BuildContext context) {
    return Container(
        height: 300,
        width: 200,
        color: Colors.orangeAccent,
        child: Stack(clipBehavior: Clip.none, children: [
          const Button(),
          Positioned(
              top: 0,
              left: 200,
              child: Container(
                  width: 100,
                  height: 200,
                  color: Colors.limeAccent,
                  child: CustomPaint(painter: PathPainter(AnimationPath()))))
        ]));
  }
}

class Button extends StatefulWidget {
  const Button({Key? key}) : super(key: key);

  @override
  State<Button> createState() => ButtonState();
}

class ButtonState extends State<Button> with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation _animation;
  late final Path _path = AnimationPath();

  @override
  void initState() {
    _controller = AnimationController(
        vsync: this, duration: const Duration(milliseconds: 200));
    _animation = CurvedAnimation(parent: _controller, curve: Curves.easeIn)
      ..addListener(() {
        if (kDebugMode) {
          print('animation.value=${_controller.value}');
          print("animation listener");
        }
        setState(() {});
      });
    _controller.addStatusListener((status) {
      switch (status) {
        case AnimationStatus.forward:
        case AnimationStatus.reverse:
          if (kDebugMode) {
            print("animation starting");
          }
          break;
        case AnimationStatus.completed:
        case AnimationStatus.dismissed:
          if (kDebugMode) {
            print("animation completed");
          }
          setState(() {});
          break;
      }
    });
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    final offset = calcOffset();
    return Positioned(
        top: offset.dy,
        left: offset.dx,
        child: GestureDetector(
          onTap: () {
            if (kDebugMode) {
              print("tap!");
            }
            if (_controller.isCompleted) {
              _controller.reverse();
            } else if (_controller.isDismissed) {
              _controller.forward();
            }
          },
          child: const FlutterLogo(size: 100),
        ));
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  Offset calcOffset() {
    if (_controller.value == 0.0 || _controller.value == 1.0) {
      if (kDebugMode) {
        print("not animating!");
      }
      return notAnimatingOffset();
    }
    final pathMetric = _path.computeMetrics().elementAt(0);
    final distance = pathMetric.length * _controller.value;
    if (kDebugMode) {
      print('distance=$distance animation.value=${_controller.value}');
    }
    final tangent = pathMetric.getTangentForOffset(distance);
    if (tangent == null) {
      if (kDebugMode) {
        print("no tangent!");
      }
      return notAnimatingOffset();
    } else {
      final result = Offset(100 - tangent.position.dx, tangent.position.dy);
      if (kDebugMode) {
        print("animation offset $result");
      }
      return result;
    }
  }

  Offset notAnimatingOffset() {
    return Offset(0, _controller.value == 0.0 ? 200 : 0);
  }
}

class PathPainter extends CustomPainter {
  Path path;
  bool invert;

  PathPainter(this.path, {this.invert = false});

  @override
  void paint(Canvas canvas, Size size) {
    Paint paint = Paint()
      ..color = Colors.black
      ..style = PaintingStyle.stroke
      ..strokeWidth = 3.0;

    if (invert) {
      canvas.scale(-1, 1);
      canvas.translate(-size.width, 0);
    }

    canvas.drawPath(path, paint);
  }

  @override
  bool shouldRepaint(CustomPainter oldDelegate) => true;
}

class AnimationPath extends Path {
  static const Size size = Size(100, 200);

  AnimationPath() {
    moveTo(size.width, size.height);
    quadraticBezierTo(-size.width / 2, size.height * .8, size.width, 0);
  }
}
