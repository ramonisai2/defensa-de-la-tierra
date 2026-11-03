import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.defensadelatierra.miniinvaders',
  appName: 'Defensa de la Tierra',
  webDir: 'www',
  plugins: {
    AdMob: {
      androidAppId: 'ca-app-pub-3940256099942544~3347511713',
    },
  },
};

export default config;