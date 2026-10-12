import { common } from '@kit.AbilityKit';
import { ViewCheckConfig } from '../../config/ViewCheckConfig';
import { AbstractKit } from '../AbstractKit';
import { ViewCheckManager } from './ViewCheckManager';

/**
 * 控件检查 Kit，对标 Android ViewCheckerKit
 */
export class ViewCheckKit extends AbstractKit {
  get isInnerKit(): boolean {
    return true;
  }

  innerKitId(): string {
    return 'dokit_sdk_ui_ck_widget';
  }

  getName(): string {
    return '控件检查';
  }

  getIcon(): Resource {
    return $r('app.media.dk_view_check');
  }

  onAppInit(_context: common.UIAbilityContext): void {
    ViewCheckConfig.setViewCheckOpen(false);
  }

  onClickWithReturn(_context: common.UIAbilityContext): boolean {
    ViewCheckManager.getInstance().open();
    return true;
  }
}
