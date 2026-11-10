import { common } from '@kit.AbilityKit';
import { LayoutBorderConfig } from '../../config/LayoutBorderConfig';
import { DoKit } from '../../DoKit';
import { CloseRegistrar, CustomViewBuilder, DoKitParams } from '../../core/CustomViewRegistry';
import { DoKitFloatOptions } from '../../DoKitFloatOptions';
import { AbstractKit } from '../AbstractKit';
import { LayoutBorderDoKitView } from './LayoutBorderDoKitView';
import { LayoutBorderManager } from './LayoutBorderManager';

@Builder
function renderLayoutBorderFloat(_params: DoKitParams, _register: CloseRegistrar) {
  LayoutBorderDoKitView()
}

const LAYOUT_BORDER_FLOAT: CustomViewBuilder = wrapBuilder(renderLayoutBorderFloat);

/**
 * 布局边框 Kit，对标 Android LayoutBorderKit。
 * 三维布局层级依赖控件绘制缓存，鸿蒙没有对应能力，这里只打开边框。
 */
export class LayoutBorderKit extends AbstractKit {
  get isInnerKit(): boolean {
    return true;
  }

  innerKitId(): string {
    return 'dokit_sdk_ui_ck_border';
  }

  getName(): string {
    return '布局边框';
  }

  getIcon(): Resource {
    return $r('app.media.dk_view_border');
  }

  onAppInit(_context: common.UIAbilityContext): void {
    LayoutBorderConfig.setLayoutBorderOpen(false);
  }

  onClickWithReturn(_context: common.UIAbilityContext): boolean {
    LayoutBorderManager.getInstance().open();
    const options = new DoKitFloatOptions();
    options.draggable = false;
    options.abovePage = false;
    options.abovePanel = false;
    options.passThrough = true;
    DoKit.launchFloatingView(
      LayoutBorderManager.FLOAT_NAME,
      LAYOUT_BORDER_FLOAT,
      undefined,
      options
    );
    return true;
  }
}
