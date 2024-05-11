import { registerPlugin } from '@capacitor/core';

import type { OcraFingerPrintPluginPlugin } from './definitions';

const OcraFingerPrintPlugin = registerPlugin<OcraFingerPrintPluginPlugin>(
  'OcraFingerPrintPlugin',
  {
    web: () => import('./web').then(m => new m.OcraFingerPrintPluginWeb()),
  },
);

export * from './definitions';
export { OcraFingerPrintPlugin };
