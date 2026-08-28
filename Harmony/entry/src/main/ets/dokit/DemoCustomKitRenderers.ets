import { CustomKitCloseHandler, CustomKitPageIds } from '@didi/dokit';
import { buildCustomDokitFragment } from './demo/CustomDokitFragment';
import { buildDemoDoKitFloatView } from './demo/DemoDoKitFloatView';

/** 宿主按 pageId 渲染自定义全屏 Kit 页 */
@Builder
export function buildEntryCustomFullScreen(pageId: string, onClose: CustomKitCloseHandler) {
  if (pageId === CustomKitPageIds.DEMO_FULL) {
    buildCustomDokitFragment(onClose);
  }
}

/** 宿主按 pageId 渲染自定义悬浮 Kit 页 */
@Builder
export function buildEntryCustomFloating(pageId: string, onClose: CustomKitCloseHandler) {
  if (pageId === CustomKitPageIds.DEMO_FLOAT) {
    buildDemoDoKitFloatView(onClose);
  }
}
