# Android Macrobenchmarks

Run on a physical device or emulator with API 29 or newer:

```bash
./gradlew :macrobenchmark:connectedCheck
```

The benchmark target is the profileable `release` variant of `:androidApp`.
Results are written by AndroidX Benchmark as instrumentation output; compare runs on
the same device, Android version, and power state.

Included scenarios:

- `coldStartup`: cold launch timing (StartupTimingMetric).
- `homeScrolling`: repeated vertical Home scrolling (FrameTimingMetric).
- `searchNavigationAndInput`: opens Search and enters a fixed query.
- `playerTransition`: opens and closes the mini-player.

For `playerTransition`, start playback once before running the benchmark so the
mini-player is present. Keep network conditions and the loaded Home feed consistent
when comparing scroll and Search results.
