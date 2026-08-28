import { common } from '@kit.AbilityKit';
import { AbstractKit, Category, CustomKitPageIds, DoKit } from '@didi/dokit';

/** 对标 Android TestSimpleDokitFloatViewKit */
export class TestSimpleDokitFloatViewKit extends AbstractKit {
  constructor() {
    super();
    this.category = Category.BIZ;
  }

  getName(): string {
    return '自定义悬浮';
  }

  getIcon(): Resource {
    return $r('app.media.dk_sys_info');
  }

  onClickWithReturn(_context: common.UIAbilityContext): boolean {
    DoKit.launchFloating(CustomKitPageIds.DEMO_FLOAT);
    return true;
  }
}
