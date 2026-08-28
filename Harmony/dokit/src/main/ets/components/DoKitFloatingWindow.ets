import promptAction from '@ohos.promptAction';
import { DoKitConstants } from '../constant/DoKitConstants';
import { resolveKitIcon } from '../config/KitIconResolver';
import { GlobalConfig } from '../config/GlobalConfig';
import { InnerKitRegistry } from '../kit/core/InnerKitRegistry';
import { SimpleDoKitLauncher } from '../kit/core/SimpleDoKitLauncher';
import { SysInfoPage } from '../kit/sysinfo/SysInfoPage';
import { WebDoorNavigator } from '../kit/webdoor/WebDoorNavigator';
import { WebDoorPage } from '../kit/webdoor/WebDoorPage';
import { WebDoorWebViewPage } from '../kit/webdoor/WebDoorWebViewPage';
import { FileExplorerPage } from '../kit/fileexplorer/FileExplorerPage';
import { ThirdLibInfoPage } from '../kit/sysinfo/ThirdLibInfoPage';
import { DataCleanPage } from '../kit/dataclean/DataCleanPage';
import { CustomKitNavigator, CustomKitPageRenderer } from '../kit/CustomKitNavigator';
import { DoKitManager } from '../kit/core/DoKitManager';
import { ActiveToolPage } from '../model/ActiveToolPage';
import { KitGroup, KitGroupId, KitItem } from '../model/KitModels';
import { DoKitOverlayState } from '../model/DoKitOverlayState';

@Builder
function defaultCustomFullScreenPage(_pageId: string, _onClose: () => void) {
  Column()
    .width('100%')
    .height('100%')
}

@Builder
function defaultCustomFloatingPage(_pageId: string, _onClose: () => void) {
  Column()
    .width('100%')
    .height('100%')
}

/**
 * DoKit 悬浮球 + 工具面板 Overlay 组件，对标 harmonydriver DoKitFloatingWindow
 * 宿主在根 Stack 叠加：Stack() { 业务页; DoKitFloatingWindow() }
 */
@Component
export struct DoKitFloatingWindow {
  /** 宿主注入：按 pageId 渲染自定义全屏页 */
  @BuilderParam customFullScreenPage: CustomKitPageRenderer = defaultCustomFullScreenPage;
  /** 宿主注入：按 pageId 渲染自定义悬浮窗 */
  @BuilderParam customFloatingPage: CustomKitPageRenderer = defaultCustomFloatingPage;

  /** 对标 Android mipmap-xxhdpi/dk_doraemon.png（174px @3x ≈ 58dp） */
  private readonly ballSize: number = 58;
  private readonly ballMarginRight: number = 16;
  private readonly ballMarginBottom: number = 108;
  private readonly dragThreshold: number = 3;

  @State private containerWidth: number = 0;
  @State private containerHeight: number = 0;
  @State private ballX: number = 0;
  @State private ballY: number = 0;
  @State private dragStartX: number = 0;
  @State private dragStartY: number = 0;
  @State private dragMoved: boolean = false;
  @State private showPanel: boolean = false;
  @State private activeToolPage: ActiveToolPage = ActiveToolPage.NONE;
  @State private webDoorViewUrl: string = '';
  @State private kitGroups: KitGroup[] = [];
  @State private showCustomFloat: boolean = false;

  @StorageLink(DoKitOverlayState.ICON_VISIBLE_KEY) iconVisible: boolean = true;
  @StorageLink(DoKitOverlayState.PANEL_OPEN_KEY) @Watch('onPanelOpenRequest') panelOpenRequest: boolean = false;

  aboutToAppear(): void {
    this.kitGroups = DoKitManager.getKitGroups();
    WebDoorNavigator.setOpenWebViewHandler((url: string) => {
      this.webDoorViewUrl = url;
      this.activeToolPage = ActiveToolPage.WEB_DOOR_VIEW;
    });
    CustomKitNavigator.setOpenFullScreenHandler((_pageId: string) => {
      this.closePanel();
      this.activeToolPage = ActiveToolPage.CUSTOM_FULL;
    });
    CustomKitNavigator.setOpenFloatingHandler((_pageId: string) => {
      this.closePanel();
      this.showCustomFloat = true;
    });
    CustomKitNavigator.setCloseFullScreenHandler(() => {
      this.activeToolPage = ActiveToolPage.NONE;
    });
    CustomKitNavigator.setCloseFloatingHandler(() => {
      this.showCustomFloat = false;
    });
    SimpleDoKitLauncher.setOpenToolPageHandler((page: ActiveToolPage) => {
      this.openToolPage(page);
    });
    SimpleDoKitLauncher.setCloseToolPageHandler(() => {
      this.closeActiveToolPage();
    });
  }

  aboutToDisappear(): void {
    WebDoorNavigator.setOpenWebViewHandler(null);
    CustomKitNavigator.setOpenFullScreenHandler(null);
    CustomKitNavigator.setOpenFloatingHandler(null);
    CustomKitNavigator.setCloseFullScreenHandler(null);
    CustomKitNavigator.setCloseFloatingHandler(null);
    SimpleDoKitLauncher.setOpenToolPageHandler(null);
    SimpleDoKitLauncher.setCloseToolPageHandler(null);
  }

  private closeCustomFullScreen(): void {
    CustomKitNavigator.clearFullScreenPage();
    this.activeToolPage = ActiveToolPage.NONE;
  }

  private closeCustomFloat(): void {
    CustomKitNavigator.clearFloatingPage();
    this.showCustomFloat = false;
  }

  private onPanelOpenRequest(): void {
    if (this.panelOpenRequest) {
      this.showPanel = true;
      DoKitOverlayState.clearPanelOpenRequest();
    }
  }

  private clamp(value: number, min: number, max: number): number {
    return Math.min(Math.max(value, min), max);
  }

  private getMaxBallX(): number {
    return Math.max(0, this.containerWidth - this.ballSize);
  }

  private getMaxBallY(): number {
    return Math.max(0, this.containerHeight - this.ballSize);
  }

  private initBallPosition(): void {
    if (this.containerWidth <= 0 || this.containerHeight <= 0) {
      return;
    }
    if (this.ballX === 0 && this.ballY === 0) {
      this.ballX = this.clamp(this.getMaxBallX() - this.ballMarginRight, 0, this.getMaxBallX());
      this.ballY = this.clamp(this.getMaxBallY() - this.ballMarginBottom, 0, this.getMaxBallY());
      return;
    }
    this.ballX = this.clamp(this.ballX, 0, this.getMaxBallX());
    this.ballY = this.clamp(this.ballY, 0, this.getMaxBallY());
  }

  private togglePanel(): void {
    this.showPanel = !this.showPanel;
  }

  private closePanel(): void {
    this.showPanel = false;
  }

  private closeActiveToolPage(): void {
    this.activeToolPage = ActiveToolPage.NONE;
    this.webDoorViewUrl = '';
  }

  private openToolPage(page: ActiveToolPage): void {
    this.closePanel();
    this.activeToolPage = page;
  }

  /** 全屏 Overlay（工具页/面板/自定义悬浮窗）需拦截触摸，避免穿透到宿主业务页 */
  private shouldBlockHitThrough(): boolean {
    return this.activeToolPage !== ActiveToolPage.NONE || this.showPanel || this.showCustomFloat;
  }

  private onKitClick(item: KitItem): void {
    if (item.customKit !== undefined) {
      const context = DoKitManager.getContext();
      if (context === null) {
        return;
      }
      const hidePanel = item.customKit.onClickWithReturn(context);
      if (hidePanel) {
        this.closePanel();
      }
      return;
    }
    const innerKit = InnerKitRegistry.get(item.innerKitId);
    if (innerKit !== undefined) {
      const context = DoKitManager.getContext();
      if (context === null) {
        return;
      }
      const hidePanel = innerKit.onClickWithReturn(context);
      if (hidePanel) {
        this.closePanel();
      }
      return;
    }
    promptAction.showToast({
      message: `${item.name}：${DoKitConstants.DEV_TIP}`,
      duration: 2000
    });
  }

  private onBallDown(): void {
    this.dragStartX = this.ballX;
    this.dragStartY = this.ballY;
    this.dragMoved = false;
  }

  private onBallMove(event?: GestureEvent): void {
    if (!event) {
      return;
    }
    if (Math.abs(event.offsetX) > this.dragThreshold || Math.abs(event.offsetY) > this.dragThreshold) {
      this.dragMoved = true;
    }
    this.ballX = this.clamp(this.dragStartX + event.offsetX, 0, this.getMaxBallX());
    this.ballY = this.clamp(this.dragStartY + event.offsetY, 0, this.getMaxBallY());
  }

  private onBallUp(event?: GestureEvent): void {
    if (event &&
      (Math.abs(event.offsetX) > this.dragThreshold || Math.abs(event.offsetY) > this.dragThreshold || this.dragMoved)) {
      const centerX = this.ballX + this.ballSize / 2;
      const targetX = centerX < this.containerWidth / 2 ? 16 : this.getMaxBallX() - 16;
      const targetY = this.clamp(this.ballY, 0, this.getMaxBallY());
      animateTo({ duration: 240, curve: Curve.EaseOut }, () => {
        this.ballX = targetX;
        this.ballY = targetY;
      });
      setTimeout(() => {
        this.dragMoved = false;
      }, 260);
    }
  }

  @Builder
  private buildPanelOverlay() {
    Column() {
      Row() {
        Text('DoKit')
          .fontSize(18)
          .fontWeight(FontWeight.Bold)
          .fontColor('#333333')
          .layoutWeight(1)

        Text('v' + DoKitConstants.VERSION)
          .fontSize(12)
          .fontColor('#999999')
          .margin({ right: 12 })

        Text('✕')
          .fontSize(20)
          .fontColor('#666666')
          .padding(8)
          .onClick(() => {
            this.closePanel();
          })
      }
      .width('100%')
      .height(56)
      .padding({ left: 16, right: 8 })
      .backgroundColor(Color.White)

      Scroll() {
        Column() {
          ForEach(this.kitGroups, (group: KitGroup) => {
            Column() {
              Row() {
                Text(group.groupName)
                  .fontSize(14)
                  .fontWeight(FontWeight.Medium)
                  .fontColor('#666666')
                if (group.groupId === KitGroupId.PLATFORM) {
                  Text('(www.dokit.cn)')
                    .fontSize(12)
                    .fontColor('#FF0006')
                    .margin({ left: 8 })
                }
              }
              .width('100%')
              .margin({ top: 16, bottom: 8, left: 4 })

              Grid() {
                ForEach(group.kits.filter((k: KitItem) => k.checked), (item: KitItem) => {
                  GridItem() {
                    Column() {
                      Image(resolveKitIcon(item))
                        .width(34)
                        .height(34)
                        .objectFit(ImageFit.Contain)

                      Text(item.name)
                        .fontSize(11)
                        .fontColor('#333333')
                        .margin({ top: 6 })
                        .maxLines(2)
                        .textAlign(TextAlign.Center)
                        .width('100%')
                    }
                    .width('100%')
                    .padding({ top: 8, bottom: 8 })
                    .onClick(() => {
                      this.onKitClick(item);
                    })
                  }
                }, (item: KitItem) => item.innerKitId)
              }
              .columnsTemplate('1fr 1fr 1fr 1fr')
              .rowsGap(4)
              .columnsGap(4)
              .width('100%')
            }
            .width('100%')
            .alignItems(HorizontalAlign.Start)
          }, (group: KitGroup) => group.groupId)
        }
        .width('100%')
        .padding({ left: 12, right: 12, bottom: 24 })
      }
      .layoutWeight(1)
      .scrollBar(BarState.Off)
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#F5F6F8')
    .hitTestBehavior(HitTestMode.Default)
  }

  @Builder
  private buildFloatingBall() {
    Image($r('app.media.dk_doraemon'))
      .width(this.ballSize)
      .height(this.ballSize)
      .objectFit(ImageFit.Contain)
      .position({ x: this.ballX, y: this.ballY })
      .gesture(
        PanGesture({ direction: PanDirection.All, fingers: 1, distance: 1 })
          .onActionStart(() => {
            this.onBallDown();
          })
          .onActionUpdate((event?: GestureEvent) => {
            this.onBallMove(event);
          })
          .onActionEnd((event?: GestureEvent) => {
            this.onBallUp(event);
          })
      )
      .onClick(() => {
        if (this.dragMoved) {
          return;
        }
        this.togglePanel();
      })
  }

  build() {
    Stack() {
      if (GlobalConfig.debugMode) {
        if (this.activeToolPage === ActiveToolPage.WEB_DOOR_VIEW) {
          WebDoorWebViewPage({
            url: this.webDoorViewUrl,
            onClose: () => {
              this.closeActiveToolPage();
            }
          })
        } else if (this.activeToolPage === ActiveToolPage.SYS_INFO) {
          SysInfoPage({
            onClose: () => {
              this.closeActiveToolPage();
            }
          })
        } else if (this.activeToolPage === ActiveToolPage.WEB_DOOR) {
          WebDoorPage({
            onClose: () => {
              this.closeActiveToolPage();
            }
          })
        } else if (this.activeToolPage === ActiveToolPage.FILE_EXPLORER) {
          FileExplorerPage({
            onClose: () => {
              this.closeActiveToolPage();
            }
          })
        } else if (this.activeToolPage === ActiveToolPage.THIRD_LIB_INFO) {
          ThirdLibInfoPage({
            onClose: () => {
              this.closeActiveToolPage();
            }
          })
        } else if (this.activeToolPage === ActiveToolPage.DATA_CLEAN) {
          DataCleanPage({
            onClose: () => {
              this.closeActiveToolPage();
            }
          })
        } else if (this.activeToolPage === ActiveToolPage.CUSTOM_FULL &&
          CustomKitNavigator.hasFullScreenPage()) {
          Column() {
            this.customFullScreenPage(CustomKitNavigator.getFullScreenPageId(), () => {
              this.closeCustomFullScreen();
            })
          }
          .width('100%')
          .height('100%')
          .backgroundColor(Color.White)
          .hitTestBehavior(HitTestMode.Default)
        } else if (this.showPanel) {
          Stack() {
            this.buildPanelOverlay()
            if (this.iconVisible) {
              this.buildFloatingBall()
            }
          }
          .width('100%')
          .height('100%')
          .hitTestBehavior(HitTestMode.Default)
        } else if (this.iconVisible) {
          Row() {
            this.buildFloatingBall()
          }
          .width('100%')
          .height('100%')
          .hitTestBehavior(HitTestMode.Transparent)
        }

        if (this.showCustomFloat && CustomKitNavigator.hasFloatingPage()) {
          Stack() {
            this.customFloatingPage(CustomKitNavigator.getFloatingPageId(), () => {
              this.closeCustomFloat();
            })
          }
          .width('100%')
          .height('100%')
          .hitTestBehavior(HitTestMode.Default)
        }
      }
    }
    .width('100%')
    .height('100%')
    .hitTestBehavior(this.shouldBlockHitThrough() ? HitTestMode.Default : HitTestMode.Transparent)
    .onAreaChange((_oldValue: Area, newValue: Area) => {
      this.containerWidth = Number(newValue.width);
      this.containerHeight = Number(newValue.height);
      this.initBallPosition();
    })
  }
}
