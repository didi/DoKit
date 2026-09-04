import { DoKitViewManager } from '../DoKitViewManager';

/** 主入口悬浮球，对标 Android kit/main/MainIconDoKitView */
@Component
export struct MainIconDoKitView {
  ballSize: number = 58;
  @Link ballX: number;
  @Link ballY: number;
  containerWidth: number = 0;
  containerHeight: number = 0;
  dragThreshold: number = 3;

  @State private dragStartX: number = 0;
  @State private dragStartY: number = 0;
  @State private dragMoved: boolean = false;

  private clamp(value: number, min: number, max: number): number {
    return Math.min(Math.max(value, min), max);
  }

  private getMaxBallX(): number {
    return Math.max(0, this.containerWidth - this.ballSize);
  }

  private getMaxBallY(): number {
    return Math.max(0, this.containerHeight - this.ballSize);
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

  build() {
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
        DoKitViewManager.attachToolPanel();
      })
  }
}
