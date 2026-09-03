import { common } from '@kit.AbilityKit';
import { AbstractKit, Category, DoKit } from '@didi/dokit';
import { DemoDoKitView } from './DemoDoKitView';

/** 对标 Android dokit/DemoKit */
export class DemoKit extends AbstractKit {
  static id =100;

  constructor() {
    super();
    this.category = Category.BIZ;
  }

  getName(): string {
    return 'demo';
  }

  getIcon(): Resource {
    return $r('app.media.dk_sys_info');
  }

  onClickWithReturn(_context: common.UIAbilityContext): boolean {

    const zone3: AbstractKit[] = [
      new DemoKit(),
      // new TestSimpleDokitFloatViewKit(),
      // new TestSimpleDokitFragmentKit()
    ];

    DoKit.registerCustomKits('业务专区-'+DemoKit.id++, zone3)
    DoKit.launchFloating(DemoDoKitView.CLASS_NAME);
    return true;
  }
}
