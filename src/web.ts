import { WebPlugin } from '@capacitor/core';

import type { OcraFingerPrintPluginPlugin } from './definitions';

export class OcraFingerPrintPluginWeb
  extends WebPlugin
  implements OcraFingerPrintPluginPlugin
{
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
