import React, { useEffect, useState } from 'react';
import { Image, Text, View } from 'react-native';

import {
  Screen,
  ScreenManager,
  useCarNavigation
} from 'react-native-android-auto';

const DeliveryListScreen = () => {
  const navigation = useCarNavigation()
  return (
    <list-template title={'Shopify Local Delivery'}>
      <item-list header="Delivery Lists">
        <row key={1} title={"Today's Delivery"} texts={['Delivery 1']} />
        {/* <action title="Add Delivery" texts={['hello world']} /> */}
        <action title="Add Delivery" onPress={() => navigation.pop()} />
      </item-list>
    </list-template>
  );
};

function TestMap() {
  const [count, setCount] = useState(0)
  useEffect(() => {
    setInterval(() => {
      setCount((count) => count + 1)
    }, 1000)
  }, [])
  return (
    <View style={{ width: 100, height: 100, backgroundColor: 'red' }}>
      <Text>This is a TestMap {count}</Text>
    </View>
  )
}

const Main = () => {
  const navigation = useCarNavigation()
  return (
    <navigation-template actionStrip={{
      actions: [
        {
          title: 'Action One',
          onPress: () => navigation.push('list')
        },
        {
          icon: Image.resolveAssetSource(require('./images/click.png')),
        },
        {
          icon: Image.resolveAssetSource(require('./images/click.png')),
        },
        {
          icon: Image.resolveAssetSource(require('./images/click.png')),
        },
      ]
    }}
      mapActionStrip={{
        actions: [
          {
            icon: Image.resolveAssetSource(require('./images/click.png')),
          },
          {
            icon: Image.resolveAssetSource(require('./images/click.png')),
          },
          {
            icon: Image.resolveAssetSource(require('./images/click.png')),
          },
          {
            icon: Image.resolveAssetSource(require('./images/click.png')),
          },
        ]
      }}
      id='test-android-auto' component={TestMap} />
  );
};

const RootApp = () => {

  return (
    <ScreenManager>
      <Screen name="root" render={Main} />
      <Screen name="list" render={DeliveryListScreen} />
    </ScreenManager>
  );
};

export default RootApp;

// const styles = StyleSheet.create({});
