import { CustomViewRegistry } from './CustomViewRegistry';
import { DoKitViewManager, ViewConfig } from './DoKitViewManager';

/**
 * ViewConfig 注入悬浮窗容器，仅用于 launchFloatingView / attachView 路径
 * 拖拽逻辑对齐 MainIconDoKitView，不影响其他内置悬浮窗
 */
@Component
export struct ViewConfigDoKitView {
  @State config: ViewConfig = new ViewConfig('', false);
  containerWidth: number = 0;
  containerHeight: number = 0;

  private readonly dragThreshold: number = 3;
  private readonly edgeMargin: number = 16;

  @State private viewX: number = 0;
  @State private viewY: number = 0;
  @State private viewWidth: number = 0;
  @State private viewHeight: number = 0;
  @State private dragStartX: number = 0;
  @State private dragStartY: number = 0;
  @State private dragMoved: boolean = false;
  @State private positionInitialized: boolean = false;

  private toNumber(value?: Length): number {
    if (value === undefined) {
      return 0;
    }
    return Number(value);
  }

  aboutToAppear(): void {
    this.viewX = this.toNumber(this.config.viewPoint.x);
    this.viewY = this.toNumber(this.config.viewPoint.y);
  }

  private clamp(value: number, min: number, max: number): number {
    return Math.min(Math.max(value, min), max);
  }

  private getMaxX(): number {
    return Math.max(0, this.containerWidth - this.viewWidth);
  }

  private getMaxY(): number {
    return Math.max(0, this.containerHeight - this.viewHeight);
  }

  private initPositionIfNeeded(): void {
    if (this.positionInitialized || this.containerWidth <= 0 || this.viewWidth <= 0) {
      return;
    }
    this.viewX = this.toNumber(this.config.viewPoint.x);
    this.viewY = this.toNumber(this.config.viewPoint.y);
    this.viewX = this.clamp(this.viewX, 0, this.getMaxX());
    this.viewY = this.clamp(this.viewY, 0, this.getMaxY());
    this.positionInitialized = true;
    this.syncViewPoint()
  }

  private syncViewPoint(): void {
    // 反向同步给config，避免下次渲染重置
    this.config.viewPoint = {
      x:this.viewX,
      y:this.viewY
    }
  }

  private onPanStart(): void {
    this.dragStartX = this.viewX;
    this.dragStartY = this.viewY;
    this.dragMoved = false;
  }

  private onPanMove(event?: GestureEvent): void {
    if (!event) {
      return;
    }
    if (Math.abs(event.offsetX) > this.dragThreshold || Math.abs(event.offsetY) > this.dragThreshold) {
      this.dragMoved = true;
    }
    this.viewX = this.clamp(this.dragStartX + event.offsetX, 0, this.getMaxX());
    this.viewY = this.clamp(this.dragStartY + event.offsetY, 0, this.getMaxY());
  }

  private onPanEnd(event?: GestureEvent): void {
    if (event &&
      (Math.abs(event.offsetX) > this.dragThreshold || Math.abs(event.offsetY) > this.dragThreshold || this.dragMoved)) {
      const centerX = this.viewX + this.viewWidth / 2;
      const targetX = centerX < this.containerWidth / 2 ? this.edgeMargin : this.getMaxX() - this.edgeMargin;
      const targetY = this.clamp(this.viewY, 0, this.getMaxY());
      animateTo({ duration: 240, curve: Curve.EaseOut }, () => {
        this.viewX = targetX;
        this.viewY = targetY;
        this.syncViewPoint();
      });
      setTimeout(() => {
        this.dragMoved = false;
      }, 260);
      return;
    }
    this.syncViewPoint();
  }

  build() {
    Stack() {
      CustomViewRegistry.getBuilder(this.config.name)?.builder(this.config.params, this.config.closeHandler)
    }
    .onAreaChange((_oldValue: Area, newValue: Area) => {
      this.viewWidth = Number(newValue.width);
      this.viewHeight = Number(newValue.height);
      this.initPositionIfNeeded();
    })
    .gesture(
      PanGesture({ direction: PanDirection.All, fingers: 1, distance: 1 })
        .onActionStart(() => {
          this.onPanStart();
        })
        .onActionUpdate((event?: GestureEvent) => {
          this.onPanMove(event);
        })
        .onActionEnd((event?: GestureEvent) => {
          this.onPanEnd(event);
        })
    )
    .position({ x: this.viewX, y: this.viewY })
    .hitTestBehavior(HitTestMode.Default)
  }
}
