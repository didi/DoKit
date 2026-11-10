import { common } from '@kit.AbilityKit';
import { AbstractKit, Category, DoKit } from '@didi-hummer/dokit';
import { buildMultiPageA, MultiPageA } from './MultiPagePages';

/** 全屏页 A → B → C，再按栈逐级返回 */
export class MultiPageKit extends AbstractKit {
  constructor() {
    super();
    this.category = Category.BIZ;
  }

  getName(): string {
    return '多页面kit';
  }

  getIcon(): Resource {
    return $r('app.media.dk_sys_info');
  }

  onClickWithReturn(_context: common.UIAbilityContext): boolean {
    DoKit.launchFullScreen(MultiPageA.CLASS_NAME, wrapBuilder(buildMultiPageA));
    return true;
  }
}
