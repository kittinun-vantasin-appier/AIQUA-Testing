const path = require('path');
const { getDefaultConfig, mergeConfig } = require('@react-native/metro-config');

// The Catalog (product.json) is shared with the native apps, so Metro must see sharedLogic's moko files.
const sharedFiles = path.resolve(__dirname, '../sharedLogic/src/commonMain/moko-resources/files');

/**
 * Metro configuration
 * https://reactnative.dev/docs/metro
 *
 * @type {import('@react-native/metro-config').MetroConfig}
 */
const config = {
  watchFolders: [sharedFiles],
};

module.exports = mergeConfig(getDefaultConfig(__dirname), config);
