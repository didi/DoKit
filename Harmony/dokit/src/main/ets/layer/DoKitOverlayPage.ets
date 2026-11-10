import { GlobalConfig } from '../config/GlobalConfig';
import { buildUniversalFragment } from '../core/SystemFragmentRigistry';
import { buildCustomFragment, CustomFragmentRegistry } from '../core/CustomFragmentRegistry';
import { DoKitPageStack } from '../core/DoKitPageStack';
import { DoKitLauncher } from '../core/DoKitLauncher';
import { DoKitParams } from '../core/CustomViewRegistry';
import { DoKitWindowUtil } from '../util/DoKitWindowUtil';
import { DoKitOverlayBuilder } from './DoKitOverlayTable';

/**
 * 全屏功能页。
 * 工具面板、悬浮球和自定义浮窗不在这里画。
 * 层表挂上这一层时，画的是页面栈顶。
 */
@Component
export struct DoKitOverlayPage {
  /** 页面栈里还有全屏页。 */
  @State private hasFullscreen: boolean = false;
  /** 栈顶全屏页的注册名。用它选出要画的 Builder。 */
  @State private activeFragmentClass: string = '';
  /** 栈顶是系统页还是宿主注册的自定义页。两边 Builder 不在同一张表。 */
  @State private activeIsSystemFragment: boolean = false;
  /** 页面栈版本。和页名一起放进 key，换页时拆掉旧内容重建。 */
  @State private pageRevision: number = 0;
  /** 状态栏高度，单位 px。需要避让时再换成 vp。 */
  @StorageProp(DoKitWindowUtil.TOP_RECT_HEIGHT) private topRectHeightPx: number = 0;
  /** 底部导航栏高度，单位 px。 */
  @StorageProp(DoKitWindowUtil.NAV_INSET_PX) private navInsetPx: number = 0;

  /** 出现时按当前页面栈画一次。换页会拆掉这个节点再建。 */
  aboutToAppear(): void {
    this.syncFullscreenFromStack();
  }

  /**
   * 从页面栈顶抄出当前全屏页。
   * 页名决定画哪一页，系统标记决定查哪张注册表，栈版本放进 key 用来换页重建。
   * 栈空时清掉这些状态，build 里的全屏分支就不会进。
   */
  private syncFullscreenFromStack(): void {
    const top = DoKitPageStack.peek();
    this.pageRevision = DoKitPageStack.getRevision();
    if (top === undefined) {
      this.hasFullscreen = false;
      this.activeFragmentClass = '';
      this.activeIsSystemFragment = false;
      return;
    }
    this.hasFullscreen = true;
    this.activeFragmentClass = top.name;
    this.activeIsSystemFragment = top.isSystem;
  }

  /** 全屏页里的返回。退一层；退完最后一层就关掉整个全屏。 */
  private onFullscreenClose(): void {
    DoKitLauncher.popFullScreen();
  }

  /** 当前栈顶带进来的参数。栈空时是空对象。 */
  private pageParams(): DoKitParams {
    const top = DoKitPageStack.peek();
    if (top === undefined) {
      return {};
    }
    return top.params;
  }

  build() {
    Stack() {
      // 非调试包不画全屏页。
      if (GlobalConfig.debugMode && this.hasFullscreen && this.activeFragmentClass.length > 0) {
        // 全屏内容必须直接写在 build 里，不能抽成 @Builder。
        // 写进 @Builder 时，关闭和页内按钮收不到点击。
        // 页名或栈版本变了就换 key。@Builder 的参数变了不会就地刷新，不换 key 的话压栈后仍停在旧页。
        Column() {
          // 系统页和宿主自定义页各有一张注册表，按栈顶标记选一张。
          if (this.activeIsSystemFragment) {
            buildUniversalFragment(this.activeFragmentClass, this.pageParams(), () => {
              this.onFullscreenClose();
            });
          } else if (CustomFragmentRegistry.has(this.activeFragmentClass)) {
            buildCustomFragment(this.activeFragmentClass, this.pageParams(), () => {
              this.onFullscreenClose();
            });
          } else {
            // 名字不在任何注册表里。给出提示和关闭，避免白屏又退不出去。
            // 这里直接清栈。正常页走 onFullscreenClose，只退一层。
            Column() {
              Text(`未注册全屏页: ${this.activeFragmentClass}`)
                .fontSize(14)
                .fontColor('#666666')
                .margin(16)
              Button('关闭')
                .onClick(() => {
                  DoKitLauncher.finishFullScreen();
                })
            }
            .width('100%')
            .height('100%')
            .justifyContent(FlexAlign.Center)
          }
        }
        .width('100%')
        .height('100%')
        .padding({
          top: DoKitWindowUtil.statusBarInsetVp(this.topRectHeightPx),
          bottom: DoKitWindowUtil.navInsetVp(this.navInsetPx)
        })
        .backgroundColor(Color.White)
        // Default：页内按钮能点，空白处也不把点击漏给底下的业务页。
        .hitTestBehavior(HitTestMode.Default)
        .key(`dokit_fs_${this.activeFragmentClass}_${this.pageRevision}`)
      }
    }
    .width('100%')
    .height('100%')
    .backgroundColor(Color.Transparent)
    // 正在显示全屏页时用 Default，点击留在这一层。没有页时用 None，不挡业务页。
    .hitTestBehavior(this.hasFullscreen ? HitTestMode.Default : HitTestMode.None)
  }
}

/** 全屏页这一层。 */
@Builder
function renderPageLayer(_name: string, _stageW: number, _stageH: number, _top: number, _bottom: number) {
  DoKitOverlayPage()
}

export const PAGE_LAYER_BUILDER: DoKitOverlayBuilder = wrapBuilder(renderPageLayer);
