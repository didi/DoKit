import { common } from '@kit.AbilityKit';
import { AbstractKit, DoKit, DoKitFloatOptions } from '@didi-hummer/dokit';
import { TestSimpleDoKitFloatView, TestSimpleDoKitFloatViewBuilder } from './TestSimpleDoKitFloatView';

/** 对标 Android dokit/TestSimpleDokitFloatViewKit */
export class TestSimpleDokitFloatViewKit extends AbstractKit {
  getName(): string {
    return '自定义悬浮';
  }

  getIcon(): Resource {
    return $r('app.media.dk_sys_info');
  }

  onClickWithReturn(_context: common.UIAbilityContext): boolean {

    const options = new DoKitFloatOptions();
    options.x = 120;
    options.y = 120;
    DoKit.launchFloatingView(
      TestSimpleDoKitFloatView.CLASS_NAME,
      wrapBuilder(TestSimpleDoKitFloatViewBuilder),
      undefined,
      options
    );
    return true;
  }
}
