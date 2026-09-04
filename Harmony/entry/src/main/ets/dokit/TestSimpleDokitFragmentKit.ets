import { common } from '@kit.AbilityKit';
import { AbstractKit, Category, DoKit } from '@didi-hummer/dokit';
import { CustomDokitFragment } from './CustomDokitFragment';

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

  onClickWithReturn(context: common.UIAbilityContext): boolean {
    DoKit.launchFullScreen(CustomDokitFragment.CLASS_NAME, { "test": 'test' });
    return true;
  }
}
