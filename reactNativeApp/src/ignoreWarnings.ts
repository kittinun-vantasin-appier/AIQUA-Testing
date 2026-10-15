import { LogBox } from 'react-native';

// Imported before the SDK: it warns as soon as it loads that its optional WebView component is unavailable, and we
// don't use it.
LogBox.ignoreLogs(['[Appier:RNAiquaWebView]']);
