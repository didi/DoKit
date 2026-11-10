import { common } from '@kit.AbilityKit';
import { hilog } from '@kit.PerformanceAnalysisKit';
import { window } from '@kit.ArkUI';
import { AbstractKit, DoKit } from '@didi-hummer/dokit';
import { DoKitDemoConfig } from './DoKitDemoConfig';
import { buildDemoCustomKits } from './dokit/DemoCustomKits';
import { DemoKit } from './dokit/DemoKit';
import { TestSimpleDokitFloatViewKit } from './dokit/TestSimpleDokitFloatViewKit';
import { TestSimpleDokitFragmentKit } from './dokit/TestSimpleDokitFragmentKit';
import { MultiPageKit } from './dokit/MultiPageKit';
import { FloatWindow1Kit } from './dokit/FloatWindow1Kit';

const DOMAIN = 0xD0F1;
const TAG = 'App';

/**
 * Demo Application 初始化，对标 Android App.onCreate
 */
export class App {
  static onCreate(context: common.UIAbilityContext, windowStage: window.WindowStage): void {
    DoKit.Builder(context)
      .windowStage(windowStage)
      .productId(DoKitDemoConfig.PRODUCT_ID_PROD)
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

    const zone1: AbstractKit[] = [
      new DemoKit(),
      new TestSimpleDokitFloatViewKit(),
      new TestSimpleDokitFragmentKit(),
      new MultiPageKit(),
      new FloatWindow1Kit()
    ];
    const zone2: AbstractKit[] = [
      new DemoKit(),
      new TestSimpleDokitFloatViewKit()
    ];
    const zone3: AbstractKit[] = [
      new DemoKit()
    ];

    DoKit.registerCustomKits('业务专区一', zone1);
    DoKit.registerCustomKits('业务专区二', zone2);
    DoKit.registerCustomKits('业务专区', zone3);
    hilog.info(DOMAIN, TAG, 'DoKit demo initialized, productId=%{public}s', DoKitDemoConfig.PRODUCT_ID_PROD);
  }
}
