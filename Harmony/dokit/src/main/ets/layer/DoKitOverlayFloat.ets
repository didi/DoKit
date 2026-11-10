import { CloseHandler, CloseRegistrar, CustomViewBuilder, CustomViewRegistry, DoKitParams } from '../core/CustomViewRegistry';
import { DoKitViewManager } from '../core/DoKitViewManager';
import { DoKitOverlayEmpty } from './DoKitOverlayEmpty';
import { DoKitOverlayBuilder } from './DoKitOverlayTable';

@Component
export struct DoKitOverlayFloat {
  private readonly dragThreshold: number = 4;
  private readonly longPressMs: number = 500;
  @Prop name: string = '';
  contentBuilder: CustomViewBuilder = DoKitOverlayEmpty.FLOAT;
  @Prop @Watch('onFrameChange') stageW: number = 0;
  @Prop @Watch('onFrameChange') stageH: number = 0;
  @Prop topInset: number = 0;
  @Prop bottomInset: number = 0;
  @State private x: number = 20;
  @State private y: number = 200;
  @State private contentW: number = 0;
  @State private contentH: number = 0;
  private placed: boolean = false;
  private land: boolean = false;
  private originX: number = 0;
  private originY: number = 0;
  private boundParams: DoKitParams = {};
  /** 业务画法登记的关闭函数。长按卸窗后调用。 */
  private floatClose?: CloseHandler = undefined;
  /** 画法执行时收下业务的关闭函数。 */
  private readonly saveFloatClose: CloseRegistrar = (handler: CloseHandler): void => {
    this.floatClose = handler;
  };
  /** 长按关闭。先卸窗，再通知业务。 */
  private readonly onClose: CloseHandler = (): void => {
    if (this.floatClose !== undefined) {
      this.floatClose();
    }
    DoKitViewManager.removeFloatingView(this.name);
  };

  /** 记下打开时传入的参数，并按窗口大小恢复位置。 */
  aboutToAppear(): void {
    this.boundParams = this.getFloatParams();
    this.onFrameChange();
  }

  /** 尺寸就绪后读出位置。横竖屏切换时先存当前方向，再读另一个方向。 */
  private onFrameChange(): void {
    if (this.stageW <= 1 || this.stageH <= 1) {
      return;
    }
    const land = this.stageW > this.stageH;
    if (!this.placed) {
      this.land = land;
      this.applySaved(land);
      this.placed = true;
      return;
    }
    if (land === this.land) {
      return;
    }
    this.writeSaved(this.land);
    this.land = land;
    this.applySaved(land);
  }

  /** 读出这个方向存过的位置。 */
  private applySaved(land: boolean): void {
    const config = DoKitViewManager.getViewConfigByName(this.name);
    if (config === undefined) {
      return;
    }
    if (land) {
      this.x = config.landscapeX;
      this.y = config.landscapeY;
      return;
    }
    this.x = config.portraitX;
    this.y = config.portraitY;
  }

  /** 把当前位置写回这个方向。 */
  private writeSaved(land: boolean): void {
    const config = DoKitViewManager.getViewConfigByName(this.name);
    if (config === undefined) {
      return;
    }
    config.viewPoint = { x: this.x, y: this.y };
    if (land) {
      config.landscapeX = this.x;
      config.landscapeY = this.y;
      return;
    }
    config.portraitX = this.x;
    config.portraitY = this.y;
  }

  /** 把数值限制在最小值和最大值之间。 */
  private clamp(value: number, min: number, max: number): number {
    return Math.min(Math.max(value, min), max);
  }

  /** 浮窗的最上沿，停在状态栏下面。 */
  private minY(): number {
    return Math.max(0, this.topInset);
  }

  /** 浮窗的最右沿，按内容宽度留空。 */
  private maxX(): number {
    return Math.max(0, this.stageW - this.contentW);
  }

  /** 浮窗的最下沿，避开导航栏。 */
  private maxY(): number {
    return Math.max(this.minY(), this.stageH - this.contentH - Math.max(0, this.bottomInset));
  }

  /** 取出打开浮窗时传入的参数。 */
  private getFloatParams(): DoKitParams {
    const config = DoKitViewManager.getViewConfigByName(this.name);
    if (config === undefined || config.params === undefined) {
      return {};
    }
    return config.params;
  }

  /** 热区等于量到的内容宽高。还没量到时是 0，不挡页面。 */
  private hitRegion(): Rectangle {
    const region: Rectangle = {
      x: 0,
      y: 0,
      width: this.contentW,
      height: this.contentH
    };
    return region;
  }

  /** 卡片内容。量到宽高后，把位置收进屏幕里。 */
  @Builder
  private CardContent() {
    Column() {
      this.contentBuilder.builder(this.boundParams, this.saveFloatClose)
    }
    .onAreaChange((_oldValue: Area, area: Area) => {
      const width = Number(area.width);
      const height = Number(area.height);
      if (width <= 1 || height <= 1) {
        return;
      }
      if (width === this.contentW && height === this.contentH) {
        return;
      }
      this.contentW = width;
      this.contentH = height;
      this.x = this.clamp(this.x, 0, this.maxX());
      this.y = this.clamp(this.y, this.minY(), this.maxY());
    })
  }

  /** 浮窗都画成可拖的小卡片。 */
  build() {
    Stack({ alignContent: Alignment.TopStart }) {
      this.CardContent()
    }
    .position({ x: this.x, y: this.y })
    .hitTestBehavior(HitTestMode.Default)
    .monopolizeEvents(true)
    .responseRegion(this.hitRegion())
    .parallelGesture(
      GestureGroup(GestureMode.Exclusive,
        LongPressGesture({ repeat: false, duration: this.longPressMs })
          .onAction(() => {
            this.onClose();
          }),
        PanGesture({ direction: PanDirection.All, fingers: 1, distance: this.dragThreshold })
          .onActionStart(() => {
            this.originX = this.x;
            this.originY = this.y;
          })
          .onActionUpdate((event?: GestureEvent) => {
            if (event === undefined) {
              return;
            }
            this.x = this.clamp(this.originX + event.offsetX, 0, this.maxX());
            this.y = this.clamp(this.originY + event.offsetY, this.minY(), this.maxY());
          })
          .onActionEnd(() => {
            this.writeSaved(this.land);
          })
      )
    )
  }
}

/** 按浮窗名字取出已注册的画法。没有则返回空内容。 */
function getFloatBuilder(name: string): CustomViewBuilder {
  const found = CustomViewRegistry.getBuilder(name);
  if (found !== undefined) {
    return found;
  }
  return DoKitOverlayEmpty.FLOAT;
}

/** 一条浮窗。画法按名字从注册表里取。 */
@Builder
function renderFloatLayer(name: string, stageW: number, stageH: number, top: number, bottom: number) {
  DoKitOverlayFloat({
    name: name,
    contentBuilder: getFloatBuilder(name),
    stageW: stageW,
    stageH: stageH,
    topInset: top,
    bottomInset: bottom
  })
}

export const FLOAT_LAYER_BUILDER: DoKitOverlayBuilder = wrapBuilder(renderFloatLayer);
