import { common } from '@kit.AbilityKit';
import { ColorPickConfig } from '../../config/ColorPickConfig';
import { AbstractKit } from '../AbstractKit';
import { ColorPickManager } from './ColorPickManager';

/**
 * 取色器 Kit，对标 Android ColorPickerKit
 */
export class ColorPickerKit extends AbstractKit {
  get isInnerKit(): boolean {
    return true;
  }

  innerKitId(): string {
    return 'dokit_sdk_ui_ck_color_pick';
  }

  getName(): string {
    return '取色器';
  }

  getIcon(): Resource {
    return $r('app.media.dk_color_picker');
  }

  onAppInit(_context: common.UIAbilityContext): void {
    ColorPickConfig.setColorPickOpen(false);
  }

  onClickWithReturn(_context: common.UIAbilityContext): boolean {
    ColorPickManager.getInstance().open();
    return true;
  }
}
