/**
 * @format
 */
import React from 'react';
import { AppRegistry } from 'react-native';
import App from './src/App';
import RootApp from './src/RootApp';

import { render } from 'react-native-android-auto';

AppRegistry.registerRunnable('androidAuto', () => {
  render(React.createElement(RootApp));
});

AppRegistry.registerComponent('main', () => App);
