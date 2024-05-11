export interface OcraFingerPrintPluginPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
