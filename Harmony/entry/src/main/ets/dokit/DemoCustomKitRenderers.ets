import { CustomKitCloseHandler } from '@didi/dokit';
import { DemoKitPageIds } from './DemoKitPageIds';
import { buildCustomDokitFragment } from './CustomDokitFragment';
import { buildDemoDoKitView } from './DemoDoKitView';
import { buildTestSimpleDoKitFloatView } from './TestSimpleDoKitFloatView';

/** 宿主按 pageId 渲染自定义全屏 Kit 页，对标 UniversalActivity FRAGMENT_CUSTOM */
@Builder
export function buildEntryCustomFullScreen(pageId: string, onClose: CustomKitCloseHandler) {
  if (pageId === DemoKitPageIds.CUSTOM_DOKIT_FRAGMENT) {
    buildCustomDokitFragment(onClose);
  }
}

/** 宿主按 pageId 渲染自定义悬浮 Kit 页，对标 DoKitViewManager attach */
@Builder
export function buildEntryCustomFloating(pageId: string, onClose: CustomKitCloseHandler) {
  if (pageId === DemoKitPageIds.DEMO_DO_KIT_VIEW) {
    buildDemoDoKitView(onClose);
  } else if (pageId === DemoKitPageIds.TEST_SIMPLE_DOKIT_FLOAT_VIEW) {
    buildTestSimpleDoKitFloatView(onClose);
  }
}
