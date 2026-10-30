const fs = require('fs');
const path = require('path');
const { withDangerousMod } = require('expo/config-plugins');

module.exports = function withModeloRostros(config) {
  return withDangerousMod(config, [
    'android',
    async (config) => {
      const origen = path.join(config.modRequest.projectRoot, 'assets', 'modelos');
      const destino = path.join(
        config.modRequest.platformProjectRoot,
        'app', 'src', 'main', 'assets'
      );

      if (!fs.existsSync(origen)) return config;

      fs.mkdirSync(destino, { recursive: true });
      for (const archivo of fs.readdirSync(origen)) {
        if (archivo.endsWith('.tflite')) {
          fs.copyFileSync(path.join(origen, archivo), path.join(destino, archivo));
        }
      }
      return config;
    },
  ]);
};
