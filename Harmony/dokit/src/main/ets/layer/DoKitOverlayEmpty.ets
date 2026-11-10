import { CloseRegistrar, CustomViewBuilder, DoKitParams } from '../core/CustomViewRegistry';
import { DoKitOverlayBuilder } from './DoKitOverlayTable';

/** 没有画法时画出的空节点。宽高为 0，不接收点击。 */
@Component
export struct DoKitOverlayEmptyNode {
  build() {
    Row()
      .width(0)
      .height(0)
      .hitTestBehavior(HitTestMode.None)
  }
}

/** 整层没有画法。参数是层名字、宽、高、顶部避让、底部避让。 */
@Builder
function renderEmptyLayer(_name: string, _stageW: number, _stageH: number, _top: number, _bottom: number) {
  DoKitOverlayEmptyNode()
}

/** 浮窗这一层在，里面的内容没登记。参数是打开参数和关闭函数登记。 */
@Builder
function renderEmptyFloat(_params: DoKitParams, _register: CloseRegistrar) {
  DoKitOverlayEmptyNode()
}

/** 层和浮窗内容缺少画法时的占位。调用方按自己的参数类型取其中一份。 */
export class DoKitOverlayEmpty {
  /** 整层没有画法。 */
  static readonly LAYER: DoKitOverlayBuilder = wrapBuilder(renderEmptyLayer);
  /** 浮窗内容没有登记。 */
  static readonly FLOAT: CustomViewBuilder = wrapBuilder(renderEmptyFloat);
}
