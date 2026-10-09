import { window } from '@kit.ArkUI';
import { DoKitManager } from '../../core/DoKitManager';
import { AlignRulerManager } from './AlignRulerManager';

/**
 * 对齐标尺浮层。
 * Android 拆成准星、全屏标尺、底部信息条三层悬浮窗；鸿蒙挂在同一个 Overlay 上，交互与读数保持一致。
 * 距离单位是 px。勾选「包含状态栏高度」只改「上」，十字位置不变。
 * 非沉浸式浮层从状态栏下沿起算，勾选后给「上」加上状态栏高度。
 * 沉浸式（isLayoutFullScreen）浮层从屏幕顶边起算，「上」已含状态栏：勾选后不再加，未勾选则减去。
 * 准星不限制边界，可以整颗拖出屏幕，四边距离可以为负或大于屏幕。
 * 信息条整窗可拖（含左右 15vp 透明边），关闭按钮和勾选框仍只响应点击。
 */
@Component
export struct AlignRulerDoKitView {
  containerWidth: number = 0;
  containerHeight: number = 0;

  private readonly markerSize: number = 60;
  private readonly infoBarHeight: number = 100;
  private readonly infoSideMargin: number = 15;

  @State private areaWidth: number = 0;
  @State private areaHeight: number = 0;
  @State private markerX: number = 0;
  @State private markerY: number = 0;
  @State private infoX: number = 15;
  @State private infoY: number = 0;
  @State private includeStatusBar: boolean = false;
  @State private statusBarPx: number = 0;
  /** 窗口 layoutFullScreen。沉浸式时浮层原点已在屏幕顶边。 */
  @State private layoutFullScreen: boolean = false;
  @State private positionReady: boolean = false;
  @StorageProp('topRectHeight') @Watch('onStoredStatusBarChange') private storedStatusBarPx: number = 0;

  private dragStartX: number = 0;
  private dragStartY: number = 0;
  private infoDragStartX: number = 0;
  private infoDragStartY: number = 0;
  private infoDragged: boolean = false;
  private alive: boolean = true;
  private statusBarWindow: window.Window | null = null;

  aboutToAppear(): void {
    this.alive = true;
    this.areaWidth = this.containerWidth;
    this.areaHeight = this.containerHeight;
    this.initPosition();
    this.loadStatusBar();
  }

  aboutToDisappear(): void {
    this.alive = false;
    this.statusBarWindow = null;
  }

  private onStoredStatusBarChange(): void {
    this.readStatusBarFromWindow();
  }

  private async loadStatusBar(): Promise<void> {
    this.readStatusBarFromWindow();
    const context = DoKitManager.getContext();
    if (context === null) {
      return;
    }
    try {
      const mainWindow = await window.getLastWindow(context);
      if (!this.alive) {
        return;
      }
      this.statusBarWindow = mainWindow;
      this.readStatusBarFromWindow();
    } catch (_err) {
    }
  }

  private readStatusBarFromWindow(): void {
    let height = 0;
    if (this.statusBarWindow !== null) {
      try {
        height = this.statusBarWindow.getWindowAvoidArea(window.AvoidAreaType.TYPE_SYSTEM).topRect.height;
      } catch (_err) {
        height = 0;
      }
    }
    if (height <= 0) {
      height = this.storedStatusBarPx;
    }
    if (height > 0) {
      this.statusBarPx = height;
    }
    const fullScreen = this.readLayoutFullScreen();
    if (fullScreen !== this.layoutFullScreen) {
      this.layoutFullScreen = fullScreen;
    }
  }

  /**
   * 与 DoKitFloatingWindow 相同：isLayoutFullScreen 为沉浸式。
   * 读不到窗口时按非沉浸式处理，避免在安全区内把状态栏减掉。
   */
  private readLayoutFullScreen(): boolean {
    if (this.statusBarWindow !== null) {
      try {
        return this.statusBarWindow.getWindowProperties().isLayoutFullScreen;
      } catch (_err) {
      }
    }
    try {
      const stage = AppStorage.get<window.WindowStage>('windowStage');
      if (stage !== undefined && stage !== null) {
        return stage.getMainWindowSync().getWindowProperties().isLayoutFullScreen;
      }
    } catch (_err) {
    }
    return false;
  }

  private initPosition(): void {
    if (this.areaWidth <= 0 || this.areaHeight <= 0) {
      return;
    }
    if (this.positionReady) {
      this.clampInfo();
      return;
    }
    // Android 初始值是屏幕宽高的一半，表示准星左上角，圆心再偏半个准星
    this.markerX = this.areaWidth / 2;
    this.markerY = this.areaHeight / 2;
    this.infoX = this.infoSideMargin;
    this.infoY = this.areaHeight - 150;
    this.clampInfo();
    this.positionReady = true;
  }

  private clampInfo(): void {
    const barWidth = this.getInfoBarWidth();
    const maxX = Math.max(0, this.areaWidth - barWidth);
    const maxY = Math.max(0, this.areaHeight - this.infoBarHeight);
    this.infoX = Math.min(Math.max(this.infoX, 0), maxX);
    this.infoY = Math.min(Math.max(this.infoY, 0), maxY);
  }

  private getInfoBarWidth(): number {
    if (this.areaWidth <= 0 || this.areaHeight <= 0) {
      return 0;
    }
    const shortSide = Math.min(this.areaWidth, this.areaHeight);
    return Math.max(0, Math.min(shortSide, this.areaWidth) - this.infoSideMargin * 2);
  }

  private getCenterX(): number {
    return vp2px(this.markerX + this.markerSize / 2);
  }

  private getCenterY(): number {
    return vp2px(this.markerY + this.markerSize / 2);
  }

  private getLeftDistance(): number {
    return Math.round(this.getCenterX());
  }

  private getRightDistance(): number {
    return Math.round(vp2px(this.areaWidth) - this.getCenterX());
  }

  private getTopDistance(): number {
    let top = this.getCenterY();
    if (this.layoutFullScreen) {
      if (!this.includeStatusBar) {
        top -= this.statusBarPx;
      }
    } else if (this.includeStatusBar) {
      top += this.statusBarPx;
    }
    return Math.round(top);
  }

  private getBottomDistance(): number {
    return Math.round(vp2px(this.areaHeight) - this.getCenterY());
  }

  private getAlignInfoText(): string {
    return `位置：左${this.getLeftDistance()} 右${this.getRightDistance()} 上${this.getTopDistance()} 下${this.getBottomDistance()}`;
  }

  private onMarkerPanStart(): void {
    this.dragStartX = this.markerX;
    this.dragStartY = this.markerY;
  }

  private onMarkerPanMove(event?: GestureEvent): void {
    if (!event) {
      return;
    }
    this.markerX = this.dragStartX + event.offsetX;
    this.markerY = this.dragStartY + event.offsetY;
  }

  private onInfoPanStart(): void {
    this.infoDragged = true;
    this.infoDragStartX = this.infoX;
    this.infoDragStartY = this.infoY;
  }

  private onInfoPanMove(event?: GestureEvent): void {
    if (!event) {
      return;
    }
    this.infoX = this.infoDragStartX + event.offsetX;
    this.infoY = this.infoDragStartY + event.offsetY;
    this.clampInfo();
  }

  private onClose(): void {
    AlignRulerManager.getInstance().close();
  }

  build() {
    Stack() {
      Stack({ alignContent: Alignment.Center }) {
        Circle()
          .width('100%')
          .height('100%')
          .fill('#64FFFFFF')
        Circle()
          .width(40)
          .height(40)
          .fill('#32CC3A4B')
        Circle()
          .width(5)
          .height(5)
          .fill('#CC3A4B')
        Circle()
          .width('100%')
          .height('100%')
          .fill(Color.Transparent)
          .stroke('#64337CC4')
          .strokeWidth(px2vp(4))
      }
      .width(this.markerSize)
      .height(this.markerSize)
      .position({ x: this.markerX, y: this.markerY })
      .hitTestBehavior(HitTestMode.Default)
      .gesture(
        PanGesture({ direction: PanDirection.All, fingers: 1, distance: 1 })
          .onActionStart(() => {
            this.onMarkerPanStart();
          })
          .onActionUpdate((event?: GestureEvent) => {
            this.onMarkerPanMove(event);
          })
      )

      if (this.positionReady) {
        AlignRulerCanvas({
          centerX: this.getCenterX(),
          centerY: this.getCenterY(),
          left: this.getLeftDistance(),
          right: this.getRightDistance(),
          top: this.getTopDistance(),
          bottom: this.getBottomDistance(),
          areaWidth: this.areaWidth,
          areaHeight: this.areaHeight
        })
          .width('100%')
          .height('100%')
          .hitTestBehavior(HitTestMode.None)
      }

      if (this.positionReady && this.getInfoBarWidth() > 0) {
        Column() {
          Row() {
            Text(this.getAlignInfoText())
              .fontSize(14)
              .fontColor('#333333')
              .layoutWeight(1)
              .maxLines(2)
              .margin({ left: 10, right: 10 })
              .hitTestBehavior(HitTestMode.None)

            Image($r('app.media.dk_close_icon'))
              .width(22)
              .height(22)
              .margin({ right: 15 })
              .onClick(() => {
                this.onClose();
              })
          }
          .width('100%')
          .height(50)

          Row() {
            Text('包含状态栏高度')
              .fontSize(14)
              .fontColor('#333333')
              .layoutWeight(1)
              .margin({ left: 10, right: 10 })
              .hitTestBehavior(HitTestMode.None)

            Image(this.includeStatusBar ? $r('app.media.dk_checkbox_checked') : $r('app.media.dk_checkbox_unchecked'))
              .width(44)
              .height(25)
              .margin({ right: 15 })
              .objectFit(ImageFit.Contain)
              .onClick(() => {
                this.includeStatusBar = !this.includeStatusBar;
              })
          }
          .width('100%')
          .height(50)
        }
        .width(this.getInfoBarWidth())
        .height(this.infoBarHeight)
        .backgroundColor(Color.White)
        .borderRadius(4)
        .border({ width: px2vp(1), color: '#CCCCCC' })
        .position({ x: this.infoX, y: this.infoY })
        .hitTestBehavior(HitTestMode.Default)
        .responseRegion({
          x: -this.infoSideMargin,
          y: 0,
          width: this.getInfoBarWidth() + this.infoSideMargin * 2,
          height: this.infoBarHeight
        })
        .gesture(
          PanGesture({ direction: PanDirection.All, fingers: 1, distance: 4 })
            .onActionStart(() => {
              this.onInfoPanStart();
            })
            .onActionUpdate((event?: GestureEvent) => {
              this.onInfoPanMove(event);
            })
        )
      }
    }
    .width('100%')
    .height('100%')
    .clip(false)
    .hitTestBehavior(HitTestMode.Transparent)
    .onAreaChange((_oldValue: Area, newValue: Area) => {
      this.areaWidth = Number(newValue.width);
      this.areaHeight = Number(newValue.height);
      if (!this.positionReady) {
        this.initPosition();
        return;
      }
      if (!this.infoDragged) {
        this.infoX = this.infoSideMargin;
        this.infoY = this.areaHeight - 150;
      }
      this.clampInfo();
      this.readStatusBarFromWindow();
    })
  }
}

/**
 * 全屏十字和四边距离，对标 Android AlignLineView。不接收触摸，方便拖下面的准星。
 */
@Component
struct AlignRulerCanvas {
  @Prop @Watch('redraw') centerX: number = 0;
  @Prop @Watch('redraw') centerY: number = 0;
  @Prop @Watch('redraw') left: number = 0;
  @Prop @Watch('redraw') right: number = 0;
  @Prop @Watch('redraw') top: number = 0;
  @Prop @Watch('redraw') bottom: number = 0;
  @Prop @Watch('redraw') areaWidth: number = 0;
  @Prop @Watch('redraw') areaHeight: number = 0;

  private readonly settings: RenderingContextSettings = new RenderingContextSettings(true);
  private readonly context: CanvasRenderingContext2D = new CanvasRenderingContext2D(this.settings);

  aboutToAppear(): void {
    this.redraw();
  }

  private redraw(): void {
    const ctx = this.context;
    const width = ctx.width;
    const height = ctx.height;
    if (width <= 0 || height <= 0) {
      return;
    }
    ctx.clearRect(0, 0, width, height);
    const x = this.toCanvasX(width);
    const y = this.toCanvasY(height);
    this.drawLines(ctx, x, y, width, height);
    this.drawLabels(ctx, x, y, width, height);
  }

  private toCanvasX(canvasWidth: number): number {
    const base = vp2px(this.areaWidth);
    if (base <= 0) {
      return this.centerX;
    }
    return this.centerX * canvasWidth / base;
  }

  private toCanvasY(canvasHeight: number): number {
    const base = vp2px(this.areaHeight);
    if (base <= 0) {
      return this.centerY;
    }
    return this.centerY * canvasHeight / base;
  }

  private drawLines(ctx: CanvasRenderingContext2D, x: number, y: number, width: number, height: number): void {
    ctx.beginPath();
    ctx.moveTo(0, y);
    ctx.lineTo(width, y);
    ctx.moveTo(x, 0);
    ctx.lineTo(x, height);
    ctx.lineWidth = 1;
    ctx.strokeStyle = '#CC3A4B';
    ctx.stroke();
  }

  private drawLabels(ctx: CanvasRenderingContext2D, x: number, y: number, width: number, height: number): void {
    ctx.font = `${Math.max(1, Math.round(vp2px(14)))}px sans-serif`;
    ctx.fillStyle = '#333333';
    ctx.textAlign = 'left';
    ctx.textBaseline = 'alphabetic';
    ctx.fillText(`${this.left}`, x / 2, y);
    ctx.fillText(`${this.right}`, (x + width) / 2, y);
    ctx.fillText(`${this.top}`, x, y / 2);
    ctx.fillText(`${this.bottom}`, x, (y + height) / 2);
  }

  build() {
    Canvas(this.context)
      .width('100%')
      .height('100%')
      .hitTestBehavior(HitTestMode.None)
      .onReady(() => {
        this.redraw();
      })
      .onAreaChange(() => {
        this.redraw();
      })
  }
}
