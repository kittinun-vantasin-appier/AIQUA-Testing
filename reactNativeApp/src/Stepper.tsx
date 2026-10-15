import { Pressable, StyleSheet, Text, View } from 'react-native';
import { MAX_QUANTITY } from './cart';
import { useColors } from './ui';

/** "+" when there are none yet, otherwise "− n +". Taking away the last unit removes the Cart Line. */
export function Stepper({ quantity, onAdd, onDecrement }: { quantity: number; onAdd: () => void; onDecrement: () => void }) {
  const colors = useColors();
  const button = (label: string, onPress: () => void, disabled = false) => (
    <Pressable
      onPress={onPress}
      disabled={disabled}
      style={[styles.button, { backgroundColor: colors.primaryContainer, opacity: disabled ? 0.4 : 1 }]}>
      <Text style={[styles.label, { color: colors.onPrimaryContainer }]}>{label}</Text>
    </Pressable>
  );
  if (quantity === 0) return button('+', onAdd);
  return (
    <View style={styles.row}>
      {button('−', onDecrement)}
      <Text style={[styles.quantity, { color: colors.onBackground }]}>{quantity}</Text>
      {button('+', onAdd, quantity >= MAX_QUANTITY)}
    </View>
  );
}

const styles = StyleSheet.create({
  row: { flexDirection: 'row', alignItems: 'center', gap: 8 },
  button: { width: 32, height: 32, borderRadius: 16, alignItems: 'center', justifyContent: 'center' },
  label: { fontSize: 18, fontWeight: '600' },
  quantity: { minWidth: 16, textAlign: 'center' },
});
