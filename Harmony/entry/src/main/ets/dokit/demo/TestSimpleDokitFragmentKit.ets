import { common } from '@kit.AbilityKit';
import { AbstractKit, Category, CustomKitPageIds, DoKit } from '@didi/dokit';

/** 对标 Android TestSimpleDokitFragmentKit */
export class TestSimpleDokitFragmentKit extends AbstractKit {
  constructor() {
    super();
    this.category = Category.BIZ;
  }

  getName(): string {
    return '自定义全屏';
  }

  getIcon(): Resource {
    return $r('app.media.dk_sys_info');
  }

  onClickWithReturn(_context: common.UIAbilityContext): boolean {
    DoKit.launchFullScreen(CustomKitPageIds.DEMO_FULL);
    return true;
  }
}
