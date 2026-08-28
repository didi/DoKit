import { common } from '@kit.AbilityKit';
import { hilog } from '@kit.PerformanceAnalysisKit';
import { DoKit } from '@didi/dokit';
import { DoKitDemoConfig } from '../config/DoKitDemoConfig';
import { buildDemoCustomKits } from './DemoCustomKits';

const DOMAIN = 0xD0F1;
const TAG = 'DoKitDemoInit';

/**
 * Demo 统一初始化，对标 Android App.onCreate
 */
export class DoKitDemoInitializer {
  static init(context: common.UIAbilityContext): void {
    DoKit.Builder(context)
      .productId(DoKitDemoConfig.PRODUCT_ID_PROD)
      // .productId(DoKitDemoConfig.PRODUCT_ID_TEST)
      .disableUpload()
      .customKits(buildDemoCustomKits())
      .fileManagerHttpPort(DoKitDemoConfig.FILE_MANAGER_HTTP_PORT)
      .databasePass(DoKitDemoConfig.DATABASE_PASS)
      .mcWSPort(DoKitDemoConfig.MC_WS_PORT)
      .alwaysShowMainIcon(true)
      .thirdLibRawFile('dokit_third_libs.json')
      .debug(true)
      .callBack({
        onFpsCallBack: (value: number, filePath: string) => {
          hilog.info(DOMAIN, TAG, 'FPS: %{public}f, path: %{public}s', value, filePath);
        },
        onCpuCallBack: (value: number, filePath: string) => {
          hilog.info(DOMAIN, TAG, 'CPU: %{public}f, path: %{public}s', value, filePath);
        },
        onMemoryCallBack: (value: number, filePath: string) => {
          hilog.info(DOMAIN, TAG, 'Memory: %{public}f, path: %{public}s', value, filePath);
        },
        onNetworkCallBack: (record: Record<string, Object>) => {
          hilog.info(DOMAIN, TAG, 'Network: %{public}s', JSON.stringify(record));
        }
      })
      .build();

    hilog.info(DOMAIN, TAG, 'DoKit demo initialized, productId=%{public}s',
      DoKitDemoConfig.PRODUCT_ID_PROD);
  }
}
