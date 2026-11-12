import { common } from '@kit.AbilityKit';
import { AbstractKit, DoKit } from '@didi-hummer/dokit';
import { FloatWindow1Builder, FloatWindow1View } from './FloatWindow1View';

/** 悬浮窗测试 kit */
export class FloatWindow1Kit extends AbstractKit {
  getName(): string {
    return '浮窗1';
  }

  getIcon(): Resource {
    return $r('app.media.dk_sys_info');
  }

  onClickWithReturn(_context: common.UIAbilityContext): boolean {

    DoKit.launchFloatingView(
      FloatWindow1View.CLASS_NAME,
      wrapBuilder(FloatWindow1Builder)
    );
    return false;
  }
}
