import { useEffect } from 'react';
import { ActivityIndicator, FlatList, Modal, Pressable, StyleSheet, Text, View } from 'react-native';
import { styles as home } from './HomeScreen';
import { Stepper } from './Stepper';
import { strings, useColors } from './ui';
import { useCartViewModel } from './viewModels';

export function CartScreen({ onStartShopping }: { onStartShopping: () => void }) {
  const { uiState, onScreenViewed, onAddClick, onDecrementClick, onRemoveClick, onBuyClick } = useCartViewModel();
  const colors = useColors();
  useEffect(onScreenViewed, []); // eslint-disable-line react-hooks/exhaustive-deps

  return (
    <View style={home.grow}>
      <Text style={[home.title, { color: colors.onBackground }]}>{strings.cartTitle}</Text>
      {uiState.lines.length === 0 ? (
        <View style={home.center}>
          <Text style={{ color: colors.onBackground }}>{strings.cartEmpty}</Text>
          <Pressable onPress={onStartShopping}>
            <Text style={[home.link, { color: colors.primary }]}>{strings.startShopping}</Text>
          </Pressable>
        </View>
      ) : (
        <>
          <FlatList
            data={uiState.lines}
            keyExtractor={line => line.product.id}
            renderItem={({ item }) => (
              <View style={home.row}>
                <Text style={home.emoji}>{item.product.emoji}</Text>
                <View style={home.grow}>
                  <Text style={{ color: colors.onBackground }}>{item.product.name}</Text>
                  <Text style={{ color: colors.onSurfaceVariant }}>{strings.priceEach(item.unitPriceText)}</Text>
                  <Text style={{ color: colors.onBackground }}>{item.totalText}</Text>
                </View>
                <Stepper
                  quantity={item.quantity}
                  onAdd={() => onAddClick(item.product)}
                  onDecrement={() => onDecrementClick(item.product)}
                />
                <Pressable onPress={() => onRemoveClick(item.product)} hitSlop={8}>
                  <Text style={{ color: colors.onSurfaceVariant }}>✕</Text>
                </Pressable>
              </View>
            )}
          />
          <View style={[styles.footer, { borderColor: colors.outlineVariant }]}>
            <Text style={[styles.total, { color: colors.onBackground }]}>
              {strings.total} {uiState.totalText}
            </Text>
            <Pressable onPress={onBuyClick} style={[styles.buy, { backgroundColor: colors.primary }]}>
              <Text style={[styles.buyLabel, { color: colors.onPrimary }]}>{strings.buy}</Text>
            </Pressable>
          </View>
        </>
      )}

      {/* Blocks the whole app while the Order is placed and while its result shows. */}
      <Modal visible={uiState.isOrderOverlayVisible} transparent animationType="fade">
        <View style={styles.scrim}>
          <View style={[styles.card, { backgroundColor: colors.surfaceContainer }]}>
            {uiState.isPlacingOrder && (
              <>
                <ActivityIndicator color={colors.primary} />
                <Text style={{ color: colors.onBackground }}>{strings.placingOrder}</Text>
              </>
            )}
            {uiState.placedOrderTotalText !== null && (
              <Text style={[styles.result, { color: colors.onBackground }]}>
                ✅ {strings.orderPlaced} {uiState.placedOrderTotalText}
              </Text>
            )}
            {uiState.orderError !== null && (
              <>
                <Text style={[styles.result, { color: colors.error }]}>❌ {strings.orderFailed}</Text>
                <Text style={{ color: colors.onBackground }}>{strings.orderFailedHint}</Text>
              </>
            )}
          </View>
        </View>
      </Modal>
    </View>
  );
}

const styles = StyleSheet.create({
  footer: { flexDirection: 'row', alignItems: 'center', padding: 16, borderTopWidth: StyleSheet.hairlineWidth },
  total: { flex: 1, fontSize: 18, fontWeight: '600' },
  result: { fontSize: 18, fontWeight: '600' },
  buy: { paddingHorizontal: 24, paddingVertical: 12, borderRadius: 24 },
  buyLabel: { fontWeight: '600' },
  scrim: { flex: 1, alignItems: 'center', justifyContent: 'center', backgroundColor: 'rgba(0,0,0,0.4)' },
  card: { padding: 24, borderRadius: 16, alignItems: 'center', gap: 12, minWidth: 240 },
});
