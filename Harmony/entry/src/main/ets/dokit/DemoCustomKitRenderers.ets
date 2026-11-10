import { DoKitParams, FragmentCloseHandler } from '@didi-hummer/dokit';
import { CustomDokitFragment, buildCustomDokitFragment } from './CustomDokitFragment';

/** 宿主按 fragmentClass 渲染自定义全屏页，对标 UniversalActivity FRAGMENT_CUSTOM */
@Builder
export function buildEntryCustomFullScreen(fragmentClass: string, params: DoKitParams, onClose: FragmentCloseHandler) {
  if (fragmentClass === CustomDokitFragment.CLASS_NAME) {
    buildCustomDokitFragment(params, onClose);
  }
}
