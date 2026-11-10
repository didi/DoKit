import { common } from '@kit.AbilityKit';
import { AlignRulerConfig } from '../../config/AlignRulerConfig';
import { DoKit } from '../../DoKit';
import { CloseRegistrar, CustomViewBuilder, DoKitParams } from '../../core/CustomViewRegistry';
import { DoKitFloatOptions } from '../../DoKitFloatOptions';
import { AbstractKit } from '../AbstractKit';
import { AlignRulerDoKitView } from './AlignRulerDoKitView';
import { AlignRulerManager } from './AlignRulerManager';

@Builder
function renderAlignRulerFloat(_params: DoKitParams, _register: CloseRegistrar) {
  AlignRulerDoKitView()
}

const ALIGN_RULER_FLOAT: CustomViewBuilder = wrapBuilder(renderAlignRulerFloat);

/**
 * 对齐标尺 Kit，对标 Android AlignRulerKit
 */
export class AlignRulerKit extends AbstractKit {
  get isInnerKit(): boolean {
    return true;
  }

  innerKitId(): string {
    return 'dokit_sdk_ui_ck_aligin_scaleplate';
  }

  getName(): string {
    return '对齐标尺';
  }

  getIcon(): Resource {
    return $r('app.media.dk_align_ruler');
  }

  onAppInit(_context: common.UIAbilityContext): void {
    AlignRulerConfig.setAlignRulerOpen(false);
  }

  onClickWithReturn(_context: common.UIAbilityContext): boolean {
    AlignRulerManager.getInstance().open();
    const options = new DoKitFloatOptions();
    options.draggable = false;
    options.abovePage = false;
    options.abovePanel = false;
    options.passThrough = true;
    DoKit.launchFloatingView(
      AlignRulerManager.FLOAT_NAME,
      ALIGN_RULER_FLOAT,
      undefined,
      options
    );
    return true;
  }
}
