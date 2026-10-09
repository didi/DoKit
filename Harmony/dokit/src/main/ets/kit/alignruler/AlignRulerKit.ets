import { common } from '@kit.AbilityKit';
import { AlignRulerConfig } from '../../config/AlignRulerConfig';
import { AbstractKit } from '../AbstractKit';
import { AlignRulerManager } from './AlignRulerManager';

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
    return true;
  }
}
