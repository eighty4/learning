# Flutter StreamBuilder<T>

Building the interface from [AsyncSnapshot](https://api.flutter.dev/flutter/widgets/AsyncSnapshot-class.html) updates from a [StreamBuilder](https://api.flutter.dev/flutter/widgets/StreamBuilder-class.html) widget.

📌 the Flutter engine will call build at any time updates occur in the widget tree above the StreamBuilder's position so be sure not to create the Stream instance within a build sequence -- use a field for a StatelessWidget or the State lifecycle events for a StatefulWidget

## Build and run

Run `flutter create` with an appropriate platform value for your environment: `android`, `macos`, `windows`, `linux`, `ios` or a comma-separated list of platforms.

```bash
flutter create --project-name stream_builder  --platforms android .
flutter run
```
