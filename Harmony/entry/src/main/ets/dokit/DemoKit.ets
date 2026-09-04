import { common } from '@kit.AbilityKit';
import { AbstractKit, Category, DoKit } from '@didi-hummer/dokit';
import { DemoDoKitView, DemoDoKitViewBuilder } from './DemoDoKitView';

/** 对标 Android dokit/DemoKit */
export class DemoKit extends AbstractKit {
  static id = 100;

  constructor() {
    super();
    this.category = Category.BIZ;
  }

  getName(): string {
    return 'DemoKit';
  }

  getIcon(): Resource {
    return $r('app.media.dk_sys_info');
  }

  onClickWithReturn(_context: common.UIAbilityContext): boolean {

    const kits: AbstractKit[] = [
      new DemoKit()
    ];

    DoKit.registerCustomKits('业务专区-' + DemoKit.id++, kits)
    DoKit.launchFloatingView(DemoDoKitView.KIT_NAME,wrapBuilder(DemoDoKitViewBuilder));
    return true;
  }
}
