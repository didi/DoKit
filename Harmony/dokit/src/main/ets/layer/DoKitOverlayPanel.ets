import { GlobalConfig } from '../config/GlobalConfig';
import { DoKitViewManager } from '../core/DoKitViewManager';
import { DoKitPageStack } from '../core/DoKitPageStack';
import { DoKitManager } from '../core/DoKitManager';
import { ToolPanelDoKitView } from '../core/toolpanel/ToolPanelDoKitView';
import { KitGroup } from '../model/KitModels';
import { DoKitWindowUtil } from '../util/DoKitWindowUtil';
import { DoKitOverlayBuilder } from './DoKitOverlayTable';

/**
 * 工具面板。
 * 全屏页、悬浮球和自定义浮窗不在这里画。
 * 有全屏页时这一层不画，把点击让给全屏页。
 */
@Component
export struct DoKitOverlayPanel {
  /** 工具面板是否该画出来。全屏页开着时为 false。 */
  @State private showPanel: boolean = false;
  /** 工具面板要展示的 kit 分组。出现时从 DoKitManager 取一次。 */
  @State private kitGroups: KitGroup[] = [];
  /** 状态栏高度，单位 px。需要避让时再换成 vp。 */
  @StorageProp(DoKitWindowUtil.TOP_RECT_HEIGHT) private topRectHeightPx: number = 0;
  /** 底部导航栏高度，单位 px。 */
  @StorageProp(DoKitWindowUtil.NAV_INSET_PX) private navInsetPx: number = 0;

  /** 出现时按当前面板开关画一次。开关变化会拆掉这个节点再建。 */
  aboutToAppear(): void {
    this.reloadPanel();
  }

  /** 抄出工具分组。全屏页还在时不画面板，避免两块叠在一起抢点击。 */
  private reloadPanel(): void {
    this.kitGroups = DoKitManager.getKitGroups();
    this.showPanel = DoKitViewManager.isPanelVisible() && DoKitPageStack.isEmpty();
  }

  build() {
    Stack() {
      // 非调试包不画面板。全屏页开着时 showPanel 已是 false。
      if (GlobalConfig.debugMode && this.showPanel) {
        Column() {
          ToolPanelDoKitView({
            kitGroups: this.kitGroups,
            onClose: () => {
              DoKitViewManager.detachToolPanel();
            }
          })
        }
        .width('100%')
        .height('100%')
        .padding({
          top: DoKitWindowUtil.statusBarInsetVp(this.topRectHeightPx),
          bottom: DoKitWindowUtil.navInsetVp(this.navInsetPx)
        })
        .backgroundColor(Color.White)
        // Default：面板自己接收点击，并挡住下面的业务页。
        .hitTestBehavior(HitTestMode.Default)
      }
    }
    .width('100%')
    .height('100%')
    .backgroundColor(Color.Transparent)
    // 正在显示面板时用 Default，点击留在这一层。没显示时用 None，不挡业务页。
    .hitTestBehavior(this.showPanel ? HitTestMode.Default : HitTestMode.None)
  }
}

/** 工具面板这一层。 */
@Builder
function renderPanelLayer(_name: string, _stageW: number, _stageH: number, _top: number, _bottom: number) {
  DoKitOverlayPanel()
}

export const PANEL_LAYER_BUILDER: DoKitOverlayBuilder = wrapBuilder(renderPanelLayer);
