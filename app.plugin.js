const { withAndroidManifest, AndroidConfig, withDangerousMod } = require('@expo/config-plugins');
const { writeFileSync } = require('fs');
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

      const filePath = join(
        config.modRequest.projectRoot,
        'android',
        'app',
        'src',
        'main',
        'res',
        'xml',
        'automotive_app_desc.xml'
      );

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
