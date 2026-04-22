# Live Cricket Scores (Volley + REST API)

A simple, good-looking Android application that fetches and displays **current/live cricket matches** using a REST API.

## Features

- Fetch current matches from a REST API (Volley)
- Clean dark UI with match cards
- Loading state (progress bar)
- Error handling with a clear on-screen message + toast
- Manual refresh button + auto-load on app launch
- “LIVE” badge when match status looks live/in progress

## REST API Used

- Endpoint: `https://api.cricketdata.org/api/v1/currentMatches`
- The API response is read from the `data` array.

## Mandatory Setup

1. **Volley dependency** (already added in this project):
   - `com.android.volley:volley:1.2.1`
2. **Internet permission** (already present):
   - `<uses-permission android:name="android.permission.INTERNET" />`

## Project Structure

```
app/src/main/java/com/example/exp6/
└── MainActivity.kt                # Volley call + UI rendering

app/src/main/res/layout/
├── activity_main.xml              # Main screen
└── item_match_card.xml            # Match card UI
```

## How it Works

1. App launches and calls the API using Volley.
2. Shows a progress bar while loading.
3. Parses the `data` array and dynamically adds a card view for each match.
4. Errors are shown in a single place (`tvError`) with a retry button.

## Build

Use Android Studio Run, or build from terminal:

```bash
./gradlew assembleDebug
```

APK output:
- `app/build/outputs/apk/debug/app-debug.apk`
