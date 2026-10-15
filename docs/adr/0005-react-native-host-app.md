# The React Native Host App is a separate app with its own TypeScript logic

Besides the Android and iOS apps, the grocery app is built a third time in React Native (`reactNativeApp/`), integrating
AIQUA's React Native SDK (`@appier/react-native-sdk`). It is a standalone app, built the way an AIQUA customer with a
React Native app would build theirs, so its ViewModels, Repositories and Services are rewritten in TypeScript rather
than reused from `sharedLogic` through a native module. A real React Native customer has no Kotlin logic underneath,
and the point is to exercise the React Native SDK from JavaScript, as they would. We rejected embedding React Native
screens in the native apps (brownfield), which tests a narrower question about two SDKs sharing one process.

The code keeps the same layering as `sharedLogic` (stateless Services, Repositories that own state, one
ViewModel-like hook per screen that sends the events, see [ADR-0003](./0003-repositories-hold-state-services-are-stateless.md)
and [ADR-0004](./0004-analytics-events-come-from-viewmodels.md)), using plain hooks and no state library. That way
"why did React Native send X when native sent Y?" can be answered by reading the same layer in both. The only file
shared with `sharedLogic` is `product.json`, because Product IDs, Categories and Prices go into events and copies
would drift; text and colours are copied, since drifting there only changes the look.

The React Native app is lower priority than the native apps, and only matches them as far as the React Native SDK
allows: it has no Inbox, because that SDK has no inbox support.
