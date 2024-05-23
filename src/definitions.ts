declare module "@capacitor/core"{
  interface CapacitorGlobal {
    OcraFingerPrintPlugin: OcraFingerPrintPluginPlugin;
  }
  }
export interface OcraFingerPrintPluginPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  testPluginMethod(options: { number1: any,number2:any }) : Promise<{value:any}>;
  getDevice(): Promise<{ image: string; message: string }>;

}
