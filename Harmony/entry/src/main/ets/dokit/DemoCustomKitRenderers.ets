import { CustomKitCloseHandler, DoKitBundle } from '@didi/dokit';
import { CustomDokitFragment, buildCustomDokitFragment } from './CustomDokitFragment';
import { DemoDoKitView, buildDemoDoKitView } from './DemoDoKitView';
import { TestSimpleDoKitFloatView, buildTestSimpleDoKitFloatView } from './TestSimpleDoKitFloatView';

/** 宿主按 fragmentClass 渲染自定义全屏页，对标 UniversalActivity FRAGMENT_CUSTOM */
@Builder
export function buildEntryCustomFullScreen(fragmentClass: string, bundle: DoKitBundle, onClose: CustomKitCloseHandler) {
  if (fragmentClass === CustomDokitFragment.CLASS_NAME) {
    buildCustomDokitFragment(bundle, onClose);
  }
}

/** 宿主按 targetClass 渲染自定义悬浮页，对标 DoKitViewManager.attach */
@Builder
export function buildEntryCustomFloating(targetClass: string, bundle: DoKitBundle, onClose: CustomKitCloseHandler) {
  if (targetClass === DemoDoKitView.CLASS_NAME) {
    buildDemoDoKitView(bundle, onClose);
  } else if (targetClass === TestSimpleDoKitFloatView.CLASS_NAME) {
    buildTestSimpleDoKitFloatView(bundle, onClose);
  }
}
