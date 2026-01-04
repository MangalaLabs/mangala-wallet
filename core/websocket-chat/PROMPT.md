I'm working on a Kotlin Multiplatform (KMP) crypto wallet application called Mangala Wallet. Yesterday, we implemented a WebSocket chat module in the `core/websocket-chat`
directory with the following features:

## What was implemented:
1. **WebSocket Chat Module** (`core/websocket-chat`):
    - Secure WebSocket connections (WSS only) with JWT authentication
    - Message queuing with SQLDelight persistence for offline support
    - Network monitoring (WiFi/Mobile/Roaming detection)
    - Device state monitoring (battery, thermal, memory)
    - Lifecycle-aware connection management
    - Adaptive reconnection strategy based on network quality and device state
    - Platform-specific handlers for Android (Doze mode, WorkManager) and iOS (Background tasks)
    - Message priority and expiration
    - Comprehensive test coverage

## Key Components Created:
- `NetworkMonitor` - Monitors network state changes
- `DeviceStateMonitor` - Tracks battery, thermal state, and memory
- `LifecycleAwareConnectionManager` - Makes intelligent connection decisions
- `AdaptiveReconnectionStrategy` - Smart exponential backoff based on conditions
- `PersistentMessageQueue` - SQLDelight-backed message queue
- Platform handlers for Android Doze mode and iOS background tasks

## Current Status:
- Module is fully implemented and integrated with the `conversationui` feature
- All mobile-specific edge cases are handled (network transitions, battery optimization, thermal throttling, etc.)
- Comprehensive test suite is in place

## Project Structure:
- Main app: `composeApp/`
- Common modules: `common/` (ui, utils, mokoresources, test)
- Core modules: `core/` (ai, biometry, pin, twofactorauth, websocket-chat)
- Features: `features/` (addressbook, conversationui, favorites, qrcode, recenttransactions)
- Data layer: `data/` (local, model)

## Tech Stack:
- Kotlin Multiplatform (Android, iOS, Desktop)
- Compose Multiplatform for UI
- SQLDelight for database
- Koin for dependency injection
- Ktor for networking
- Voyager for navigation

The current branch is `feature/conversation-ui-mangala-bot` and all changes have been committed and pushed to `feature/websocket`.
