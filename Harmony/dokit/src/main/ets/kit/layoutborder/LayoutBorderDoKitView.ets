import { LengthMetricsUnit } from '@kit.ArkUI';
import { LayoutBorderManager } from './LayoutBorderManager';
import { LayoutBorderRect, LayoutBorderTree } from './LayoutBorderTree';

/**
 * 布局边框浮层。全屏 Canvas 画红色虚线，不吃页面触摸。
 * 底部关闭条对标安卓布局边框浮层的位置：距底 125vp，左右留 15vp。
 * 画布用 px，和测量宽高同一单位。
 */
@Component
export struct LayoutBorderDoKitView {
  private readonly barHeight: number = 80;
  private readonly bottomOffset: number = 125;
  private readonly sideMargin: number = 15;
  private readonly refreshInterval: number = 1000;

  @State private areaWidth: number = 0;
  @State private areaHeight: number = 0;
  @State private barX: number = 15;
  @State private barY: number = 0;
  @State private positionReady: boolean = false;

  private originX: number = 0;
  private originY: number = 0;
  private dragStartX: number = 0;
  private dragStartY: number = 0;
  private barDragged: boolean = false;
  private refreshTimer: number = -1;
  private rects: LayoutBorderRect[] = [];
  private readonly settings: RenderingContextSettings = new RenderingContextSettings(true);
  private readonly context: CanvasRenderingContext2D =
    new CanvasRenderingContext2D(this.settings, LengthMetricsUnit.PX);

  aboutToAppear(): void {
    this.refreshTimer = setInterval(() => {
      this.refresh();
    }, this.refreshInterval);
  }

  aboutToDisappear(): void {
    if (this.refreshTimer >= 0) {
      clearInterval(this.refreshTimer);
      this.refreshTimer = -1;
    }
  }

  private getBarWidth(): number {
    if (this.areaWidth <= 0 || this.areaHeight <= 0) {
      return 0;
    }
    const shortSide = Math.min(this.areaWidth, this.areaHeight);
    return Math.max(0, shortSide - this.sideMargin * 2);
  }

  private placeBar(): void {
    const barWidth = this.getBarWidth();
    this.barX = Math.max(0, (this.areaWidth - barWidth) / 2);
    this.barY = this.areaHeight - this.bottomOffset;
    this.clampBar();
  }

  private clampBar(): void {
    const barWidth = this.getBarWidth();
    const maxX = Math.max(0, this.areaWidth - barWidth);
    const maxY = Math.max(0, this.areaHeight - this.barHeight);
    this.barX = Math.min(Math.max(this.barX, 0), maxX);
    this.barY = Math.min(Math.max(this.barY, 0), maxY);
  }

  private readOrigin(area: Area): void {
    const position = area.globalPosition;
    const x = Number(position.x);
    const y = Number(position.y);
    if (!Number.isNaN(x)) {
      this.originX = x;
    }
    if (!Number.isNaN(y)) {
      this.originY = y;
    }
  }

  private refresh(): void {
    if (!this.positionReady || this.areaWidth <= 0 || this.areaHeight <= 0) {
      return;
    }
    try {
      this.rects = LayoutBorderTree.collect(this.getUIContext(), this.originX, this.originY);
    } catch (_err) {
      this.rects = [];
    }
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
    if (this.rects.length === 0) {
      return;
    }
    const scaleX = this.areaWidth > 0 ? width / vp2px(this.areaWidth) : 1;
    const scaleY = this.areaHeight > 0 ? height / vp2px(this.areaHeight) : 1;
    ctx.beginPath();
    for (let i = 0; i < this.rects.length; i++) {
      const rect = this.rects[i];
      ctx.rect(rect.left * scaleX, rect.top * scaleY, rect.width * scaleX, rect.height * scaleY);
    }
    ctx.lineWidth = 4;
    ctx.setLineDash([4, 4]);
    ctx.strokeStyle = '#FF0000';
    ctx.stroke();
  }

  private onBarPanStart(): void {
    this.barDragged = true;
    this.dragStartX = this.barX;
    this.dragStartY = this.barY;
  }

  private onBarPanMove(event?: GestureEvent): void {
    if (!event) {
      return;
    }
    this.barX = this.dragStartX + event.offsetX;
    this.barY = this.dragStartY + event.offsetY;
    this.clampBar();
  }

  build() {
    Stack() {
      Canvas(this.context)
        .width('100%')
        .height('100%')
        .hitTestBehavior(HitTestMode.None)
        .onReady(() => {
          this.refresh();
        })
        .onAreaChange(() => {
          this.redraw();
        })

      if (this.positionReady && this.getBarWidth() > 0) {
        Row() {
          Text('布局边框')
            .fontSize(14)
            .fontColor('#333333')
            .margin({ left: 16 })
            .layoutWeight(1)

          Image($r('app.media.dk_close_icon'))
            .width(22)
            .height(22)
            .margin({ right: 16 })
            .objectFit(ImageFit.Contain)
            .onClick(() => {
              LayoutBorderManager.getInstance().close();
            })
        }
        .width(this.getBarWidth())
        .height(this.barHeight)
        .backgroundColor(Color.White)
        .borderRadius(4)
        .border({ width: px2vp(1), color: '#CCCCCC' })
        .position({ x: this.barX, y: this.barY })
        .hitTestBehavior(HitTestMode.Default)
        .gesture(
          PanGesture({ direction: PanDirection.All, fingers: 1, distance: 4 })
            .onActionStart(() => {
              this.onBarPanStart();
            })
            .onActionUpdate((event?: GestureEvent) => {
              this.onBarPanMove(event);
            })
        )
      }
    }
    .width('100%')
    .height('100%')
    .id(LayoutBorderTree.OVERLAY_ID)
    .hitTestBehavior(HitTestMode.Transparent)
    .onAreaChange((_oldValue: Area, newValue: Area) => {
      this.areaWidth = Number(newValue.width);
      this.areaHeight = Number(newValue.height);
      this.readOrigin(newValue);
      if (!this.positionReady) {
        this.positionReady = this.areaWidth > 0 && this.areaHeight > 0;
      }
      if (!this.barDragged) {
        this.placeBar();
      } else {
        this.clampBar();
      }
      this.refresh();
    })
  }
}
