## libwebrtc

Use [libwebp-build-steps/c-plus-plus](libwebp-build-steps/c-plus-plus) to provide libwebp dependency for these examples.

### Mac

Mac builds require the xcode app and not the command line tools. You might need to run this command:

```
sudo xcode-select -s /Applications/Xcode.app/Contents/Developer
```

### Windows

[Debugging tools for Windows](https://docs.microsoft.com/en-us/windows-hardware/drivers/debugger/debugger-download-tools) must be installed before building libwebrtc.

Windows Developer Mode must be enabled from Developer Settings.

https://docs.microsoft.com/en-us/windows/apps/get-started/enable-your-device-for-development#accessing-settings-for-developers

### libwebrtc.py

This script updates and builds libwebrtc.

#### Debug build

```
python3 libwebrtc.py --build
```

#### Update sources to HEAD of origin/main

```
python3 libwebrtc.py --update --build
```

#### Update sources to explicit revision of origin/main

```
python3 libwebrtc.py --update=63bcd6e61
```

#### Update sources to explicit revision of origin/main

```
python3 libwebrtc.py --update=63bcd6e61
```

#### Update and build

```
./libwebrtc.py --build --update
```

#### Release build

```
python3 libwebrtc.py --release
```

#### List built libwebrtc versions on local machine

```
python3 libwebrtc.py --list
```
