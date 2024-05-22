import { WebPlugin } from '@capacitor/core';

import type { OcraFingerPrintPluginPlugin } from './definitions';

export class OcraFingerPrintPluginWeb
  extends WebPlugin
  implements OcraFingerPrintPluginPlugin
{
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO IS ---------------------------------------', options);
    return options;
  }
  async  testPluginMethod(options: { number1: any,number2:any; }): Promise<{ value: any; }> {
    alert(options.number1);
    console.log('options are :',options)
    const sum = options.number1 + options.number2;
    return { value:  sum};
  }

  async getDevice(): Promise<{ value: any; }> {
    return { value: 'Device reader only works on mobile appssss'};
  }
}
