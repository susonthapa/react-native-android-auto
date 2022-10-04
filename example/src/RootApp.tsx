import React from 'react';

import {
  Screen,
  ScreenManager
} from 'react-native-android-auto';

const DeliveryListScreen = () => {
  return (
    <pane-template title={'Shopify Local Delivery'}>
      <item-list header="Delivery Lists">
        <row key={1} title={"Today's Delivery"} texts={['Delivery 1']} />
        {/* <action title="Add Delivery" texts={['hello world']} /> */}
        <action title="Add Delivery" />
      </item-list>
    </pane-template>
  );
};

const Main = () => {
  return (
    <list-template title={'Shopify Local Delivery test'} isLoading={false}>
      <item-list header="Delivery Lists">
        <row key={1} title={"Today's Delivery"} texts={['Delivery 1']} />
      </item-list>
    </list-template>
  );
};

const RootApp = () => {
  console.log(`TODO: RootApp Render`);
  
  return (
    <ScreenManager>
      <Screen name="root" render={Main} />
      <Screen name="deliveryList" render={DeliveryListScreen} />
    </ScreenManager>
  );
};

export default RootApp;

// const styles = StyleSheet.create({});
