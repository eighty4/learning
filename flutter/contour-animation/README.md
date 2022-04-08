# Flutter animation along a custom path

Uses [Path.computeMetrics](https://api.flutter.dev/flutter/dart-ui/Path/computeMetrics.html)
and [PathMetric.getTangentForOffset](https://api.flutter.dev/flutter/dart-ui/PathMetric/getTangentForOffset.html)
to calculate an animated position with the animation controller's progress value translated to the length of the path.

See [ButtonState.calcOffset](https://github.com/eighty4/learning/blob/main/flutter/contour-animation/lib/main.dart#L126)
for the animated position calculation.

```
flutter run
```

📌 I could not get animation progress to be tracked on a timing curve (easeIn, bounceOut, etc.) when customizing
an animation in this fashion. The animation controller's listener always fires in linear intervals.

📌 Running this sample on macos and triggering the animation with a tap immediately after alt tabbing away from and back
to the app will cause the animation to skip renders until after the animation completes.
