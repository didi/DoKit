import { DoKitViewManager } from '../core/DoKitViewManager';
import { DoKitWindowUtil } from '../util/DoKitWindowUtil';
import { DoKitOverlayBuilder } from './DoKitOverlayTable';

const DOKIT_BALL_X: string = 'dokitBallX';
const DOKIT_BALL_Y: string = 'dokitBallY';
const DOKIT_BALL_LAND_X: string = 'dokitBallLandX';
const DOKIT_BALL_LAND_Y: string = 'dokitBallLandY';

@Component
export struct DoKitOverlayBall {
  private readonly ballSize: number = 58;
  private readonly edgeMargin: number = 16;
  private readonly dragThreshold: number = 4;
  private readonly tapLimitMs: number = 1000;
  /** 自己量到的窗口宽高，单位 vp。不从 @Builder 传入，避免参数不刷新时一直是 0。 */
  private stageW: number = 0;
  private stageH: number = 0;
  /** 状态栏高度，单位 px。和宿主读的是同一份。 */
  @StorageProp(DoKitWindowUtil.TOP_RECT_HEIGHT) @Watch('onInsetChange') private topRectHeightPx: number = 0;
  /** 导航栏高度，单位 px。 */
  @StorageProp(DoKitWindowUtil.NAV_INSET_PX) @Watch('onInsetChange') private navInsetPx: number = 0;
  @State private x: number = 0;
  @State private y: number = 0;
  /** 量到有效窗口后才显示，避免先在左上角闪一下。 */
  @State private placed: boolean = false;
  private land: boolean = false;
  private downX: number = 0;
  private downY: number = 0;
  private originX: number = 0;
  private originY: number = 0;
  private downTime: number = 0;
  private dragMoved: boolean = false;

  /** 宽高都超过球本身，才算能摆、能拖。过小的第一次测量不锁位置。 */
  private frameReady(): boolean {
    return this.stageW > this.ballSize && this.stageH > this.ballSize;
  }

  /** 布局量到新的宽高后再摆。还不够大就保持未摆放，等下一次。 */
  private onArea(area: Area): void {
    const width = Number(area.width);
    const height = Number(area.height);
    if (width <= 1 || height <= 1) {
      return;
    }
    if (width === this.stageW && height === this.stageH) {
      return;
    }
    this.stageW = width;
    this.stageH = height;
    this.onFrameChange();
  }

  /** 状态栏或导航栏高度后到时，只把现有坐标收进新范围，不把球丢回右下角。 */
  onInsetChange(): void {
    if (!this.placed || !this.frameReady()) {
      return;
    }
    this.clampIntoFrame(true);
  }

  /** 尺寸就绪后恢复位置。横竖屏切换时先存当前方向，再读另一个方向。 */
  private onFrameChange(): void {
    if (!this.frameReady()) {
      return;
    }
    const land = this.stageW > this.stageH;
    if (!this.placed) {
      this.land = land;
      if (!this.restore(land)) {
        this.placeDefault();
      } else {
        this.clampIntoFrame(false);
      }
      this.placed = true;
      return;
    }
    if (land === this.land) {
      this.clampIntoFrame(false);
      return;
    }
    this.write(this.land);
    this.land = land;
    if (!this.restore(land)) {
      this.placeDefault();
    } else {
      this.clampIntoFrame(false);
    }
  }

  /** 读出这个方向上次放下的位置。没有存过，或是尺寸为 0 时写下的原点，则返回 false。 */
  private restore(land: boolean): boolean {
    const savedX = AppStorage.get<number>(land ? DOKIT_BALL_LAND_X : DOKIT_BALL_X);
    const savedY = AppStorage.get<number>(land ? DOKIT_BALL_LAND_Y : DOKIT_BALL_Y);
    if (savedX === undefined || savedY === undefined) {
      return false;
    }
    // 拖动范围是 0 时松手会把 (0, 0) 存下来。那不是用户放下的位置。
    if (savedX === 0 && savedY === 0) {
      return false;
    }
    this.x = savedX;
    this.y = savedY;
    return true;
  }

  /** 把当前位置记到这个方向。 */
  private write(land: boolean): void {
    if (land) {
      AppStorage.setOrCreate(DOKIT_BALL_LAND_X, this.x);
      AppStorage.setOrCreate(DOKIT_BALL_LAND_Y, this.y);
      return;
    }
    AppStorage.setOrCreate(DOKIT_BALL_X, this.x);
    AppStorage.setOrCreate(DOKIT_BALL_Y, this.y);
  }

  /** 按当前横竖屏保存位置。 */
  private save(): void {
    this.write(this.land);
  }

  /** 把数值限制在最小值和最大值之间。 */
  private clamp(value: number, min: number, max: number): number {
    return Math.min(Math.max(value, min), max);
  }

  /** 球的最上沿，停在状态栏下面。 */
  private minY(): number {
    return Math.max(0, DoKitWindowUtil.topInsetVp(this.topRectHeightPx));
  }

  /** 球的最右沿，给球本身留出宽度。 */
  private maxX(): number {
    return Math.max(0, this.stageW - this.ballSize);
  }

  /** 球的最下沿，避开导航栏。 */
  private maxY(): number {
    return Math.max(this.minY(), this.stageH - this.ballSize - Math.max(0, DoKitWindowUtil.navInsetVp(this.navInsetPx)));
  }

  /** 收到当前窗口里。坐标变了且需要记住时才写回。 */
  private clampIntoFrame(save: boolean): void {
    const x = this.clamp(this.x, 0, this.maxX());
    const y = this.clamp(this.y, this.minY(), this.maxY());
    if (x === this.x && y === this.y) {
      return;
    }
    this.x = x;
    this.y = y;
    if (save) {
      this.save();
    }
  }

  /** 没有存过位置时，放在右下角并记住。 */
  private placeDefault(): void {
    if (!this.frameReady()) {
      return;
    }
    this.x = Math.max(0, this.maxX() - this.edgeMargin);
    this.y = Math.max(this.minY(), this.maxY() - this.edgeMargin);
    this.placed = true;
    this.save();
  }

  /** 取出这次触摸的触点。 */
  private pointOf(event: TouchEvent): TouchObject | undefined {
    if (event.touches.length > 0) {
      return event.touches[0];
    }
    if (event.changedTouches.length > 0) {
      return event.changedTouches[0];
    }
    return undefined;
  }

  /** 移动超过 4vp 才算拖。松手后贴到左右边缘。短按打开工具面板。窗口还没量到时不处理，避免把原点存下来。 */
  private onBallTouch(event: TouchEvent): void {
    this.onFrameChange();
    if (!this.frameReady()) {
      return;
    }
    const point = this.pointOf(event);
    if (point === undefined) {
      return;
    }
    if (event.type === TouchType.Down) {
      this.downX = point.displayX;
      this.downY = point.displayY;
      this.originX = this.x;
      this.originY = this.y;
      this.downTime = Date.now();
      this.dragMoved = false;
      return;
    }
    if (event.type === TouchType.Move) {
      const dx = point.displayX - this.downX;
      const dy = point.displayY - this.downY;
      if (!this.dragMoved) {
        if (Math.abs(dx) <= this.dragThreshold && Math.abs(dy) <= this.dragThreshold) {
          return;
        }
        this.dragMoved = true;
      }
      this.x = this.clamp(this.originX + dx, 0, this.maxX());
      this.y = this.clamp(this.originY + dy, this.minY(), this.maxY());
      return;
    }
    if (event.type === TouchType.Up || event.type === TouchType.Cancel) {
      const quick = Date.now() - this.downTime < this.tapLimitMs;
      if (!this.dragMoved && quick && event.type === TouchType.Up) {
        DoKitViewManager.attachToolPanel();
        return;
      }
      if (!this.dragMoved) {
        return;
      }
      const centerX = this.x + this.ballSize / 2;
      const targetX = centerX < this.stageW / 2 ? Math.min(this.edgeMargin, this.maxX()) :
        Math.max(0, this.maxX() - this.edgeMargin);
      const targetY = this.clamp(this.y, this.minY(), this.maxY());
      animateTo({ duration: 240, curve: Curve.EaseOut }, () => {
        this.x = targetX;
        this.y = targetY;
        this.save();
      });
    }
  }

  /** 铺满后自己量宽高。空白点透，只有图片接收点击。 */
  build() {
    Stack({ alignContent: Alignment.TopStart }) {
      Image($r('app.media.dk_doraemon'))
        .width(this.ballSize)
        .height(this.ballSize)
        .objectFit(ImageFit.Contain)
        .position({ x: this.x, y: this.y })
        .visibility(this.placed ? Visibility.Visible : Visibility.None)
        .hitTestBehavior(HitTestMode.Default)
        .monopolizeEvents(true)
        .onTouch((event: TouchEvent) => {
          this.onBallTouch(event);
        })
    }
    .width('100%')
    .height('100%')
    .hitTestBehavior(HitTestMode.Transparent)
    .onAreaChange((_oldValue: Area, area: Area) => {
      this.onArea(area);
    })
  }
}

/** 悬浮球这一层。宽高自己量，不从这里传入。 */
@Builder
function renderBallLayer(_name: string, _stageW: number, _stageH: number, _top: number, _bottom: number) {
  // 命中写在组件上。只写在内部 Stack 时，组件自己仍是 Default，铺满后会挡住页面。
  DoKitOverlayBall()
    .width('100%')
    .height('100%')
    .hitTestBehavior(HitTestMode.Transparent)
}

export const BALL_LAYER_BUILDER: DoKitOverlayBuilder = wrapBuilder(renderBallLayer);
