import { AbilityConstant, UIAbility, Want } from '@kit.AbilityKit';
import { hilog } from '@kit.PerformanceAnalysisKit';
import { window } from '@kit.ArkUI';
import { DoKitDemoInitializer } from '../dokit/DoKitDemoInitializer';

const DOMAIN = 0x0000;
const TAG = 'EntryAbility';

export default class EntryAbility extends UIAbility {
  onCreate(want: Want, launchParam: AbilityConstant.LaunchParam): void {
    hilog.info(DOMAIN, TAG, 'DoKit Demo onCreate');
  }

  onWindowStageCreate(windowStage: window.WindowStage): void {
    hilog.info(DOMAIN, TAG, 'DoKit Demo onWindowStageCreate');
    const abilityContext = this.context;
    if (abilityContext !== null && abilityContext !== undefined) {
      DoKitDemoInitializer.init(abilityContext);
    } else {
      hilog.error(DOMAIN, TAG, 'UIAbilityContext is null, skip DoKit init');
    }
    windowStage.loadContent('pages/Index', (err) => {
      if (err.code) {
        hilog.error(DOMAIN, TAG, 'Failed to load Index. Cause: %{public}s', JSON.stringify(err));
      }
    });
  }
}
