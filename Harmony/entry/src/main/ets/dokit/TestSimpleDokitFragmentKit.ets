import { common } from '@kit.AbilityKit';
import { AbstractKit, Category, DoKit } from '@didi/dokit';
import { DemoKitPageIds } from './DemoKitPageIds';

/** 对标 Android dokit/TestSimpleDokitFragmentKit */
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
    DoKit.launchFullScreen(DemoKitPageIds.CUSTOM_DOKIT_FRAGMENT, { "test": 'test' });
    return true;
  }
}
