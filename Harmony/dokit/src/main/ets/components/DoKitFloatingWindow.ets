import { GlobalConfig } from '../config/GlobalConfig';
import { DokitMemoryConfig } from '../config/DokitMemoryConfig';
import { CustomDoKitViewRenderer, CustomFragmentRenderer, DoKitIntent, DoKitViewCloseHandler } from '../core/DoKitIntent';
import { DoKitViewManager, ViewConfig } from '../core/DoKitViewManager';
import { buildUniversalFragment, SystemFragmentRegistry } from '../core/SystemFragmentRigistry';
import { DoKitManager } from '../core/DoKitManager';
import { MainIconDoKitView } from '../core/main/MainIconDoKitView';
import { ToolPanelDoKitView } from '../core/toolpanel/ToolPanelDoKitView';
import { PerformanceDokitViewManager } from '../kit/performance/PerformanceDokitViewManager';
import { PerformanceDoKitView } from '../kit/performance/PerformanceDoKitView';
import { FpsFrameCollector } from '../kit/performance/FpsFrameCollector';
import { DataSourceFactory } from '../kit/performance/datasource/DataSourceFactory';
import { DoKitOverlayState } from '../model/DoKitOverlayState';
import { KitGroup } from '../model/KitModels';
import { DoKitBundle } from '../core/FragmentRenderer';
import { CustomViewRegistry } from '../core/CustomViewRegistry';

@Builder
function defaultCustomFullScreenPage(_fragmentClass: string, _bundle: DoKitBundle, _onClose: () => void) {
  Column().width('100%').height('100%')
}

/**
 * DoKit Overlay 根容器，对标 Android DoKitFrameLayout + DoKitViewManager 挂载点
 * 宿主：Stack() { 业务页; DoKitFloatingWindow() }
 */
@Component
export struct DoKitFloatingWindow {
  @BuilderParam customFullScreenPage: CustomFragmentRenderer = defaultCustomFullScreenPage;

  private readonly ballSize: number = 58;
  private readonly ballMarginRight: number = 16;
  private readonly ballMarginBottom: number = 108;

  @State private containerWidth: number = 0;
  @State private containerHeight: number = 0;
  @State private ballX: number = 0;
  @State private ballY: number = 0;
  @State private showPanel: boolean = false;
  @State private activeFragmentClass: string = '';
  @State private activeIsSystemFragment: boolean = false;
  @State private kitGroups: KitGroup[] = [];
  @State private showCustomFloat: boolean = false;
  @State private activeFloatingClass: string = '';
  @State private activeFloatingBundle: DoKitBundle = {};
  @State private showFpsOverlay: boolean = false;
  @State private showCpuOverlay: boolean = false;
  @State private showRamOverlay: boolean = false;
  @State private fpsMonitorActive: boolean = false;
  @State private viewRefresh: number = 0;
  @State private viewConfigs:ViewConfig[]= [];

  @StorageLink(DoKitOverlayState.ICON_VISIBLE_KEY) iconVisible: boolean = true;
  @StorageLink(DoKitOverlayState.PANEL_OPEN_KEY) @Watch('onPanelOpenRequest') panelOpenRequest: boolean = false;

  aboutToAppear(): void {
    DoKitViewManager.setToolPanelChangeHandler((visible: boolean) => {
      if (visible) {
        this.kitGroups = DoKitManager.getKitGroups();
      }
      this.showPanel = visible;
    });
    SystemFragmentRegistry.setOpenHandler((fragmentClass: string, isSystemFragment: boolean) => {
      this.openFragment(fragmentClass, isSystemFragment);
    });
    SystemFragmentRegistry.setCloseHandler(() => {
      this.closeFragment();
    });
    DoKitViewManager.setFloatingAttachHandler((intent: DoKitIntent) => {
      this.activeFloatingClass = intent.targetClass;
      this.activeFloatingBundle = intent.bundle;
      this.showCustomFloat = true;
    });
    DoKitViewManager.setFloatingDetachHandler(() => {
      this.showCustomFloat = false;
      this.activeFloatingClass = '';
      this.activeFloatingBundle = {};
    });

    DoKitViewManager.setWindowRefreshHandler(() => {
      this.viewRefresh = 0;
      this.viewConfigs = DoKitViewManager.getViewConfigList();
      // 为了触发重新渲染，先使用0清除数据，然后重新添加到视图上
      this.viewRefresh ++;
    })

    PerformanceDokitViewManager.setOverlayChangeHandler(() => {
      this.syncPerformanceOverlayState();
    });
    this.syncPerformanceOverlayState();
  }

  aboutToDisappear(): void {
    DoKitViewManager.setToolPanelChangeHandler(null);
    SystemFragmentRegistry.setOpenHandler(null);
    SystemFragmentRegistry.setCloseHandler(null);
    DoKitViewManager.setFloatingAttachHandler(null);
    DoKitViewManager.setFloatingDetachHandler(null);
    DoKitViewManager.setWindowRefreshHandler(null);
    PerformanceDokitViewManager.setOverlayChangeHandler(null);
  }

  private syncPerformanceOverlayState(): void {
    this.showFpsOverlay = PerformanceDokitViewManager.isFpsOverlayOpen();
    this.showCpuOverlay = PerformanceDokitViewManager.isCpuOverlayOpen();
    this.showRamOverlay = PerformanceDokitViewManager.isRamOverlayOpen();
    this.fpsMonitorActive = DokitMemoryConfig.FPS_STATUS;
  }

  private getPerformanceOverlayTop(showFps: boolean, showCpu: boolean, performanceType: number): number {
    let top = 88;
    if (performanceType === DataSourceFactory.TYPE_CPU || performanceType === DataSourceFactory.TYPE_RAM) {
      if (showFps) {
        top += 52;
      }
    }
    if (performanceType === DataSourceFactory.TYPE_RAM && showCpu) {
      top += 52;
    }
    return top;
  }

  private onPanelOpenRequest(): void {
    if (this.panelOpenRequest) {
      this.kitGroups = DoKitManager.getKitGroups();
      this.showPanel = true;
      DoKitOverlayState.clearPanelOpenRequest();
    }
  }

  private clamp(value: number, min: number, max: number): number {
    return Math.min(Math.max(value, min), max);
  }

  private initBallPosition(): void {
    if (this.containerWidth <= 0 || this.containerHeight <= 0) {
      return;
    }
    const maxX = Math.max(0, this.containerWidth - this.ballSize);
    const maxY = Math.max(0, this.containerHeight - this.ballSize);
    if (this.ballX === 0 && this.ballY === 0) {
      this.ballX = this.clamp(maxX - this.ballMarginRight, 0, maxX);
      this.ballY = this.clamp(maxY - this.ballMarginBottom, 0, maxY);
      return;
    }
    this.ballX = this.clamp(this.ballX, 0, maxX);
    this.ballY = this.clamp(this.ballY, 0, maxY);
  }

  private closeFragment(): void {
    SystemFragmentRegistry.clearContent();
    this.activeFragmentClass = '';
    this.activeIsSystemFragment = false;
  }

  private openFragment(fragmentClass: string, isSystemFragment: boolean): void {
    DoKitViewManager.detachToolPanel();
    this.activeFragmentClass = fragmentClass;
    this.activeIsSystemFragment = isSystemFragment;
  }

  private shouldBlockHitThrough(): boolean {
    return this.activeFragmentClass.length > 0 || this.showPanel || this.showCustomFloat ||
      this.showFpsOverlay || this.showCpuOverlay || this.showRamOverlay;
  }

  @Builder
  private buildUniversalContent() {
    if (this.activeIsSystemFragment) {
      buildUniversalFragment(this.activeFragmentClass, SystemFragmentRegistry.getBundle(), () => {
        SystemFragmentRegistry.finish();
      });
    } else {
      Column() {
        this.customFullScreenPage(this.activeFragmentClass, SystemFragmentRegistry.getBundle(), () => {
          SystemFragmentRegistry.finish();
        });
      }
      .width('100%')
      .height('100%')
      .backgroundColor(Color.White)
      .hitTestBehavior(HitTestMode.Default)
    }
  }

  build() {
    Stack() {
      if (GlobalConfig.debugMode) {
        if (this.activeFragmentClass.length > 0) {
          this.buildUniversalContent()
        } else if (this.showPanel) {
          Stack() {
            ToolPanelDoKitView({
              kitGroups: this.kitGroups,
              onClose: () => {
                DoKitViewManager.detachToolPanel();
              }
            })
          }
          .width('100%')
          .height('100%')
          .hitTestBehavior(HitTestMode.Default)
        } else if (this.iconVisible) {
          Row() {
            MainIconDoKitView({
              ballSize: this.ballSize,
              ballX: $ballX,
              ballY: $ballY,
              containerWidth: this.containerWidth,
              containerHeight: this.containerHeight
            })
          }
          .width('100%')
          .height('100%')
          .hitTestBehavior(HitTestMode.Transparent)
        }

        if (this.fpsMonitorActive) {
          FpsFrameCollector()
        }

        if (this.showFpsOverlay) {
          PerformanceDoKitView({
            performanceType: DataSourceFactory.TYPE_FPS,
            title: '帧率',
            unit: 'FPS',
            topOffset: 88,
            onClose: () => {
              PerformanceDokitViewManager.closeOverlayFromView(DataSourceFactory.TYPE_FPS);
              this.syncPerformanceOverlayState();
            }
          })
        }

        if (this.showCpuOverlay) {
          PerformanceDoKitView({
            performanceType: DataSourceFactory.TYPE_CPU,
            title: 'CPU',
            unit: '%',
            topOffset: this.getPerformanceOverlayTop(this.showFpsOverlay, false, DataSourceFactory.TYPE_CPU),
            onClose: () => {
              PerformanceDokitViewManager.closeOverlayFromView(DataSourceFactory.TYPE_CPU);
              this.syncPerformanceOverlayState();
            }
          })
        }

        if (this.showRamOverlay) {
          PerformanceDoKitView({
            performanceType: DataSourceFactory.TYPE_RAM,
            title: '内存',
            unit: 'MB',
            topOffset: this.getPerformanceOverlayTop(this.showFpsOverlay, this.showCpuOverlay,
              DataSourceFactory.TYPE_RAM),
            onClose: () => {
              PerformanceDokitViewManager.closeOverlayFromView(DataSourceFactory.TYPE_RAM);
              this.syncPerformanceOverlayState();
            }
          })
        }

        if (this.viewRefresh >= 0 && this.viewConfigs.length > 0) {
          ForEach(this.viewConfigs, (config: ViewConfig) => {
            Column() {
              CustomViewRegistry.getBuilder(config.name)?.builder(config.params, config.closeHandler)
            }
            .width('100%')
            .height('100%')
          })
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
