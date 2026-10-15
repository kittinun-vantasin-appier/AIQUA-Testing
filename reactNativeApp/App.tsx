import './src/ignoreWarnings';
import { useState } from 'react';
import { Pressable, StatusBar, StyleSheet, Text, useColorScheme, View } from 'react-native';
import { SafeAreaProvider, SafeAreaView } from 'react-native-safe-area-context';
import { startAiqua } from './src/aiqua';
import { CartScreen } from './src/CartScreen';
import { HomeScreen } from './src/HomeScreen';
import { strings, useColors } from './src/ui';
import { useMainViewModel } from './src/viewModels';

startAiqua();

type Tab = 'home' | 'cart';

/** Two tabs. Only the selected one is mounted, so mounting a screen is what "the screen appeared" means. */
function App() {
  const [tab, setTab] = useState<Tab>('home');
  const { cartBadgeText } = useMainViewModel();
  const colors = useColors();
  const isDark = useColorScheme() === 'dark';

  const tabButton = (value: Tab, label: string, badge: string | null = null) => (
    <Pressable style={styles.tab} onPress={() => setTab(value)}>
      <Text style={[styles.tabLabel, { color: tab === value ? colors.primary : colors.onSurfaceVariant }]}>
        {label}
        {badge && <Text style={{ color: colors.error }}> ({badge})</Text>}
      </Text>
    </Pressable>
  );

  return (
    <SafeAreaProvider>
      <StatusBar barStyle={isDark ? 'light-content' : 'dark-content'} />
      <SafeAreaView style={[styles.fill, { backgroundColor: colors.background }]}>
        <View style={styles.fill}>
          {tab === 'home' ? <HomeScreen /> : <CartScreen onStartShopping={() => setTab('home')} />}
        </View>
        <View style={[styles.tabs, { borderColor: colors.outlineVariant }]}>
          {tabButton('home', strings.tabHome)}
          {tabButton('cart', strings.tabCart, cartBadgeText)}
        </View>
      </SafeAreaView>
    </SafeAreaProvider>
  );
}

const styles = StyleSheet.create({
  fill: { flex: 1 },
  tabs: { flexDirection: 'row', borderTopWidth: StyleSheet.hairlineWidth },
  tab: { flex: 1, alignItems: 'center', paddingVertical: 12 },
  tabLabel: { fontWeight: '600' },
});

export default App;
