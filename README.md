# Pleos NaviHelper SDK Android Sample

`NaviDevApp` is a sample Android app that demonstrates how to use the APIs exposed by the
Pleos NaviHelper SDK.

The sample focuses on NaviHelper SDK behavior: choose an API, execute it
with sample data, and inspect API-call logs and listener callbacks.

## What This Sample Demonstrates

- Initializing and releasing `NaviHelper`.
- Calling supported `NaviHelper` request/get APIs with sample data.
- Requesting a route with a destination and ordered waypoints in one call.
- Receiving `NaviHelperEventListener` callbacks from the navigation app.

## Requirements

- Android device or emulator environment where the Pleos navigation service is available.
- A compatible navigation app installed and configured as the default navigation app.
- NaviHelper SDK dependencies resolvable from the configured Maven repositories.
- Navi permissions declared in `AndroidManifest.xml`.

This app sends requests to the default navigation app through NaviService. Responses
and events depend on that app's supported features and current state. The sample
does not generate navigation responses itself.

## SDK Repository

The sample downloads NaviHelper 3.0.5 from the Pleos Playground Maven repository:

```kotlin
maven("https://nexus-playground.pleos.ai/repository/maven-releases/")
implementation("ai.pleos.playground:NaviHelper:3.0.5")
```

No repository token or `credentials.gradle` file is required.
The SDK and its transitive dependencies must be published to this repository before
building the sample in a fresh environment.

## Key Integration Files

- `app/src/main/java/ai/pleos/playground/navidevapp/screen/vm/NaviHelperViewModel.kt`
  contains the direct `NaviHelper` calls and `NaviHelperEventListener` callbacks.
- `app/src/main/java/ai/pleos/playground/navidevapp/data/model/SdkApi.kt`
  defines the Helper API list used by the sample.
- `app/src/main/java/ai/pleos/playground/navidevapp/data/sample/`
  contains sample request payloads.
- `app/src/main/java/ai/pleos/playground/navidevapp/screen/NaviSdkScreen.kt`
  wires NaviHelper SDK actions, selected APIs, and log state to the sample screen.

## Build

```bash
./gradlew assembleDebug
./gradlew assembleRelease
```
