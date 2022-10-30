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

const PlaceList = () => {
  return (
    <place-list-map-template title='Hello World'>
      <row title='This is row One %d' metadata={{
        type: 'place',
        distance: {
          displayDistance: 100,
          displayUnit: 3,
        },
        icon: Image.resolveAssetSource(require('./images/click.png')),
        latitude: 39.3266,
        longitude: -110.9646,
      }} onPress={() => { }} />
      <row title='This is row Two %d' metadata={{
        type: 'place',
        distance: {
          displayDistance: 100,
          displayUnit: 3,
        },
        icon: Image.resolveAssetSource(require('./images/click.png')),
        latitude: 39.3336,
        longitude: -110.9694,
      }} onPress={() => { }} />
    </place-list-map-template>
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
      navigationInfo={{
        type: 'routingInfo',
        info: {
          step: {
            lane: {
              shape: 8,
              isRecommended: true,
            },
            cue: 'Hello Step',
            lanesImage: Image.resolveAssetSource(require('./images/click.png')),
            maneuver: {
              type: 6,
              icon: Image.resolveAssetSource(require('./images/click.png')),
              roundaboutExitAngle: 10,
              roundaboutExitNumber: 2,
            },
            road: 'Custom Road',
          },
          distance: {
            displayDistance: 10,
            displayUnit: 2,
          },
          // junctionImage: Image.resolveAssetSource(require('./images/click.png')),
          isLoading: false,
          nextStep: {
            lane: {
              shape: 8,
              isRecommended: true,
            },
            cue: 'Next Step',
            lanesImage: Image.resolveAssetSource(require('./images/click.png')),
            maneuver: {
              type: 6,
              icon: Image.resolveAssetSource(require('./images/click.png')),
              roundaboutExitAngle: 10,
              roundaboutExitNumber: 2,
            },
            road: 'Next Custom Road',
          }
        }
      }}
      destinationTravelEstimate={{
        remainingDistance: {
          displayDistance: 10,
          displayUnit: 3,
        },
        destinationTime: {
          timeSinceEpochMillis: 1666056013736,
          id: 'America/Cayman',
        },
        remainingTimeSeconds: 60000,
      }}

      id='test-android-auto'
      component={TestMap}
    />
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
