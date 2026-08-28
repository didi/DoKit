/**
 * DoKit 全局配置，对标 Android DoKitManager + GlobalConfig
 */
import { WebDoorCallback } from '../kit/webdoor/WebDoorCallback';

export class GlobalConfig {
  static productId: string = '';
  static disableUpload: boolean = false;
  static alwaysShowMainIcon: boolean = true;
  static fileManagerHttpPort: number = 8089;
  static mcWsPort: number = 4444;
  static databasePass: Map<string, string> = new Map();
  static debugMode: boolean = true;
  /** H5任意门相对路径前缀，对标 harmonydriver EnvProxy.getH5Url() */
  static h5BaseUrl: string = '';
  static webDoorCallback: WebDoorCallback | null = null;
}
