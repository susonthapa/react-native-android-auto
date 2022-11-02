/**
 * @format
 */
import React from 'react';
import { AppRegistry } from 'react-native';
import App from './src/App';
import AndroidAuto from './src/AndroidAuto';

import { render } from 'react-native-android-auto';

AppRegistry.registerRunnable('androidAuto', () => {
  render(React.createElement(AndroidAuto));
});

AppRegistry.registerComponent('main', () => App);