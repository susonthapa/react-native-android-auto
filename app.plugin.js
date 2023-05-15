const { withAndroidManifest, AndroidConfig, withDangerousMod } = require('@expo/config-plugins');
const { writeFileSync, mkdirSync } = require('fs');
const { join } = require('path');

const withCarService = (config, { service = '.CarService' } = {}) => {
  return withAndroidManifest(config, async (config) => {
    let mainApplication = AndroidConfig.Manifest.getMainApplicationOrThrow(config.modResults);

    mainApplication['service'] = [
      {
        $: {
          'android:name': service,
          'android:exported': 'true',
        },
        'intent-filter': [
          {
            'action': [
              {
                $: {
                  'android:name': 'androidx.car.app.CarAppService',
                },
              },
            ],
            'category': [
              {
                $: {
                  'android:name': 'androidx.car.app.category.NAVIGATION',
                },
              },
            ],
          },
        ],
      },
    ];

    mainApplication['meta-data'] = [
      {
        $: {
          'android:name': 'androidx.car.app.minCarApiLevel',
          'android:value': '1',
        },
      },
      {
        $: {
          'android:name': 'com.google.android.gms.car.application',
          'android:resource': '@xml/automotive_app_desc',
        },
      },
    ];

    return config;
  });
};

const withAutomotiveAppDesc = (config) => {
  return withDangerousMod(config, [
    'android',
    async (config) => {
      const automotiveAppDescXml = `<?xml version="1.0" encoding="utf-8"?>
<automotiveApp>
  <uses name="template" />
</automotiveApp>`;

      const dirPath = join(
        config.modRequest.projectRoot,
        'android',
        'app',
        'src',
        'main',
        'res',
        'xml'
      );
      mkdirSync(dirPath, { recursive: true }); // This will create the directories if they don't exist

      const filePath = join(dirPath, 'automotive_app_desc.xml');

      writeFileSync(filePath, automotiveAppDescXml);

      return config;
    },
  ]);
};

module.exports = (config) => {
  config = withCarService(config);
  config = withAutomotiveAppDesc(config);
  return config;
};
