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
    console.log(`TODO: mounting TestMap`);
    const interval = setInterval(() => {
      setCount((count) => count + 1)
    }, 1000)
    return () => {
      console.log(`TODO: unmounting TestMap`);
      clearInterval(interval)
    }
  }, [])
  return (
    <View style={{ width: 100, height: 100, backgroundColor: 'red' }}>
      <Text>This is a TestMap {count}</Text>
    </View>
  )
}

const MemoTestMap = React.memo(TestMap)

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

const GridMenu = () => {
  useEffect(() => {
    console.log(`TODO: mounting GridMenu`);
    return () => console.log('TODO: unmounting GridMenu')
  }, [])
  return (
    <grid-template title='Grid Menus' headerAction={'back'}>
      <grid-item title='Menu One' image={Image.resolveAssetSource(require('./images/gear.png'))} />
      <grid-item title='Awesome Title' image={Image.resolveAssetSource(require('./images/gear.png'))} />
      <grid-item title='Another Menu' image={Image.resolveAssetSource(require('./images/gear.png'))} />
      <grid-item title='Test Menu' image={Image.resolveAssetSource(require('./images/gear.png'))} />
      <grid-item title='Food Menu' image={Image.resolveAssetSource(require('./images/gear.png'))} />
      <grid-item title='Settings Menu' image={Image.resolveAssetSource(require('./images/gear.png'))} />
    </grid-template>
  )
}

const Main = () => {
  const navigation = useCarNavigation()
  useEffect(() => {
    console.log('TODO: mounting Main')
    return () => console.log('TODO: unmounting Main')
  }, [])
  return (
    <navigation-template actionStrip={{
      actions: [
        {
          title: 'Action One',
          onPress: () => navigation.push('grid-menu'),
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
      // navigationInfo={{
      //   type: 'routingInfo',
      //   info: {
      //     step: {
      //       lane: {
      //         shape: 8,
      //         isRecommended: true,
      //       },
      //       cue: 'Hello Step',
      //       lanesImage: Image.resolveAssetSource(require('./images/click.png')),
      //       maneuver: {
      //         type: 6,
      //         icon: Image.resolveAssetSource(require('./images/click.png')),
      //         roundaboutExitAngle: 10,
      //         roundaboutExitNumber: 2,
      //       },
      //       road: 'Custom Road',
      //     },
      //     distance: {
      //       displayDistance: 10,
      //       displayUnit: 2,
      //     },
      //     // junctionImage: Image.resolveAssetSource(require('./images/click.png')),
      //     isLoading: false,
      //     nextStep: {
      //       lane: {
      //         shape: 8,
      //         isRecommended: true,
      //       },
      //       cue: 'Next Step',
      //       lanesImage: Image.resolveAssetSource(require('./images/click.png')),
      //       maneuver: {
      //         type: 6,
      //         icon: Image.resolveAssetSource(require('./images/click.png')),
      //         roundaboutExitAngle: 10,
      //         roundaboutExitNumber: 2,
      //       },
      //       road: 'Next Custom Road',
      //     }
      //   }
      // }}
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

const AndroidAuto = () => {

  useEffect(() => {
    console.log(`TODO: AndroidAuto mounting`);
    return () => {
      console.log(`TODO: AndroidAuto unmounting`)
    }
  }, [])

  return (
    <ScreenManager>
      <Screen name="root" render={Main} />
      <Screen name="grid-menu" render={GridMenu} />
      <Screen name="list" render={DeliveryListScreen} />
    </ScreenManager>
  );
};

export default AndroidAuto;
