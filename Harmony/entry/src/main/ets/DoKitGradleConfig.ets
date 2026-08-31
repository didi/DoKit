/**
 * Demo 依赖配置，对标 Android doraemonkit.gradle + app/build.gradle dokit 扩展
 *
 * runType:
 *   0 - 本地源码依赖（开发模式）
 *   1 - ohpm 远程依赖（发布模式，待接入）
 */
export interface DoKitModuleConfig {
  dokit: boolean;
  dokitFt: boolean;
  dokitMc: boolean;
  dokitGpsMock: boolean;
}

export interface DoKitPluginConfig {
  gpsEnable: boolean;
  bigImageEnable: boolean;
  networkEnable: boolean;
}

export class DoKitGradleConfig {
  static readonly runType: number = 0;
  static readonly modules: DoKitModuleConfig = {
    dokit: true,
    dokitFt: false,
    dokitMc: false,
    dokitGpsMock: false
  };
  static readonly plugin: DoKitPluginConfig = {
    gpsEnable: true,
    bigImageEnable: true,
    networkEnable: true
  };
}
