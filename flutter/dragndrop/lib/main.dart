import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:jdenticon_dart/jdenticon_dart.dart';

class Friend {
  final String name;
  final String iconSvgString;

  Friend(this.name) : iconSvgString = Jdenticon.toSvg(name);

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is Friend && runtimeType == other.runtimeType && name == other.name;

  @override
  int get hashCode => name.hashCode;
}

class FriendGroup {
  final String name;
  final Set<Friend> members;

  FriendGroup(
    this.name, {
    Set<Friend>? members,
  }) : members = members ?? <Friend>{};
}

final friends = [
  Friend('Amelia'),
  Friend('Brad'),
  Friend('Carrie'),
  Friend('Derek'),
  Friend('Emily'),
  Friend('Fred'),
  Friend('Gabby'),
];

void main() {
  runApp(const DragToDropApp());
}

class DragToDropApp extends StatelessWidget {
  const DragToDropApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      color: Colors.white,
      title: 'Drag To Drop Flutter App',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const Scaffold(
          body: DragToDropWidget(), backgroundColor: Color(0xFFF7F7F7)),
    );
  }
}

class DragToDropWidget extends StatefulWidget {
  const DragToDropWidget({Key? key}) : super(key: key);

  @override
  State<DragToDropWidget> createState() => _DragToDropWidgetState();
}

class _DragToDropWidgetState extends State<DragToDropWidget> {
  final List<FriendGroup> _groups = [
    FriendGroup("co-workers"),
    FriendGroup("family"),
    FriendGroup("neighbors"),
  ];

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Column(
        children: [
          Padding(
            padding: const EdgeInsets.symmetric(vertical: 30.0),
            child: ListView.separated(
              shrinkWrap: true,
              itemBuilder: (context, index) {
                final FriendGroup group = _groups[index];
                return DragTarget<Friend>(
                    builder: (context, candidates, rejected) {
                  return Padding(
                    padding: const EdgeInsets.symmetric(vertical: 10.0),
                    child: FriendGroupDisplay(group,
                        highlighted: candidates.isNotEmpty),
                  );
                }, onAccept: (friend) {
                  setState(() {
                    group.members.add(friend);
                  });
                });
              },
              separatorBuilder: (context, index) {
                return const SizedBox(height: 5);
              },
              itemCount: _groups.length,
            ),
          ),
          Expanded(
              child: GridView.count(
            crossAxisCount: 2,
            children: List.generate(friends.length, (index) {
              final friend = friends[index];
              return LongPressDraggable<Friend>(
                data: friend,
                child: FriendDisplay(friend),
                childWhenDragging: FriendDisplay(friend, dragging: true),
                feedback: DraggingFriendImage(friend),
              );
            }),
          )),
        ],
      ),
    );
  }
}

class FriendDisplay extends StatelessWidget {
  final Friend friend;
  final bool dragging;

  const FriendDisplay(this.friend, {Key? key, this.dragging = false})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        SvgPicture.string(
          friend.iconSvgString,
          fit: BoxFit.contain,
          height: 100,
          width: 100,
        ),
        AnimatedDefaultTextStyle(
          duration: const Duration(milliseconds: 200),
          curve: Curves.bounceInOut,
          style: TextStyle(
              fontSize: 24, color: dragging ? Colors.blue : Colors.black),
          child: Text(
            friend.name,
          ),
        ),
      ],
    );
  }
}

class DraggingFriendImage extends StatelessWidget {
  final Friend friend;

  const DraggingFriendImage(this.friend, {Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return SvgPicture.string(
      friend.iconSvgString,
      fit: BoxFit.contain,
      height: 100,
      width: 100,
    );
  }
}

class FriendGroupDisplay extends StatelessWidget {
  final FriendGroup group;
  final bool highlighted;

  const FriendGroupDisplay(this.group, {Key? key, required this.highlighted})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 30.0),
      child: Row(
        children: [
          AnimatedDefaultTextStyle(
            duration: const Duration(milliseconds: 200),
            curve: Curves.bounceInOut,
            style: TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
                color: highlighted ? Colors.green : Colors.black),
            child: Text(
              group.name,
            ),
          ),
          const Spacer(),
          Text(
            'has ${group.members.length.toString()} friend${group.members.length == 1 ? '' : 's'}',
            style: const TextStyle(fontSize: 16, fontStyle: FontStyle.italic),
          ),
        ],
      ),
    );
  }
}
