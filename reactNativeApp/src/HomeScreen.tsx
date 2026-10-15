import { useEffect } from 'react';
import { ActivityIndicator, Pressable, SectionList, StyleSheet, Text, View } from 'react-native';
import { Stepper } from './Stepper';
import { strings, useColors } from './ui';
import { useHomeViewModel } from './viewModels';

export function HomeScreen() {
  const { uiState, onScreenViewed, onRetryClick, onAddClick, onDecrementClick } = useHomeViewModel();
  const colors = useColors();
  useEffect(onScreenViewed, []); // eslint-disable-line react-hooks/exhaustive-deps

  // Content wins: show the sections if there are any, otherwise the spinner while loading, or the error with Retry.
  if (uiState.sections.length === 0) {
    return (
      <View style={styles.center}>
        {uiState.isLoading ? (
          <ActivityIndicator color={colors.primary} />
        ) : uiState.error ? (
          <>
            <Text style={{ color: colors.onBackground }}>{strings.homeError}</Text>
            <Pressable onPress={onRetryClick}>
              <Text style={[styles.link, { color: colors.primary }]}>{strings.retry}</Text>
            </Pressable>
          </>
        ) : null}
      </View>
    );
  }

  return (
    <SectionList
      sections={uiState.sections}
      keyExtractor={row => row.product.id}
      ListHeaderComponent={<Text style={[styles.title, { color: colors.onBackground }]}>{strings.homeTitle}</Text>}
      renderSectionHeader={({ section }) => (
        <Text style={[styles.header, { color: colors.onSurfaceVariant }]}>{section.title}</Text>
      )}
      renderItem={({ item }) => (
        <View style={styles.row}>
          <Text style={styles.emoji}>{item.product.emoji}</Text>
          <View style={styles.grow}>
            <Text style={{ color: colors.onBackground }}>{item.product.name}</Text>
            <Text style={{ color: colors.onSurfaceVariant }}>{item.priceText}</Text>
          </View>
          <Stepper
            quantity={item.quantity}
            onAdd={() => onAddClick(item.product)}
            onDecrement={() => onDecrementClick(item.product)}
          />
        </View>
      )}
    />
  );
}

export const styles = StyleSheet.create({
  center: { flex: 1, alignItems: 'center', justifyContent: 'center', gap: 8 },
  title: { fontSize: 28, fontWeight: '700', paddingHorizontal: 16, paddingTop: 8 },
  header: { fontSize: 12, fontWeight: '600', paddingHorizontal: 16, paddingTop: 16, paddingBottom: 4 },
  row: { flexDirection: 'row', alignItems: 'center', paddingHorizontal: 16, paddingVertical: 8, gap: 12 },
  emoji: { fontSize: 32 },
  grow: { flex: 1 },
  link: { fontWeight: '600', padding: 8 },
});
