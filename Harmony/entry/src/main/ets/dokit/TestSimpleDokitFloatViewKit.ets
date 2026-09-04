import { common } from '@kit.AbilityKit';
import { AbstractKit, Category, DoKit } from '@didi-hummer/dokit';
import { TestSimpleDoKitFloatView } from './TestSimpleDoKitFloatView';

/** 对标 Android dokit/TestSimpleDokitFloatViewKit */
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
    DoKit.launchFloating(TestSimpleDoKitFloatView.CLASS_NAME);
    return true;
  }
}
