import { ViewCheckManager } from './ViewCheckManager';
import { ViewCheckNode, ViewCheckTree } from './ViewCheckTree';

/**
 * 控件检查浮层。Android 拆成准星、全屏描边、底部信息条；鸿蒙挂在同一个 Overlay 上。
 * 准星限制在屏幕内。松手后按圆心命中 FrameNode 树，上一个 / 下一个在同一点的层级间切换。
 * 尺寸单位是 px。DoKit 浮层整棵跳过，不会选中准星自己。
 */
@Component
export struct ViewCheckDoKitView {
  containerWidth: number = 0;
  containerHeight: number = 0;

  private readonly markerSize: number = 60;
  private readonly infoSideMargin: number = 15;
  private readonly infoBottomOffset: number = 185;

  @State private areaWidth: number = 0;
  @State private areaHeight: number = 0;
  @State private markerX: number = 0;
  @State private markerY: number = 0;
  @State private infoX: number = 15;
  @State private infoY: number = 0;
  @State private infoBarHeight: number = 160;
  @State private positionReady: boolean = false;
  @State private infoText: string = '';
  @State private showSwitch: boolean = false;
  @State private borderVisible: boolean = false;
  @State private frameLeft: number = 0;
  @State private frameTop: number = 0;
  @State private frameWidth: number = 0;
  @State private frameHeight: number = 0;

  private originX: number = 0;
  private originY: number = 0;
  private dragStartX: number = 0;
  private dragStartY: number = 0;
  private infoDragStartX: number = 0;
  private infoDragStartY: number = 0;
  private infoDragged: boolean = false;
  private hits: ViewCheckNode[] = [];
  private hitIndex: number = 0;
  private lastFindKey: string = '';

  aboutToAppear(): void {
    this.areaWidth = this.containerWidth;
    this.areaHeight = this.containerHeight;
    this.initPosition();
  }

  private initPosition(): void {
    if (this.areaWidth <= 0 || this.areaHeight <= 0) {
      return;
    }
    if (this.positionReady) {
      this.clampMarker();
      this.clampInfo();
      return;
    }
    this.markerX = this.areaWidth / 2;
    this.markerY = this.areaHeight / 2;
    this.infoX = this.infoSideMargin;
    this.infoY = this.areaHeight - this.infoBottomOffset;
    this.clampMarker();
    this.clampInfo();
    this.positionReady = true;
  }

  private clampMarker(): void {
    const maxX = Math.max(0, this.areaWidth - this.markerSize);
    const maxY = Math.max(0, this.areaHeight - this.markerSize);
    this.markerX = Math.min(Math.max(this.markerX, 0), maxX);
    this.markerY = Math.min(Math.max(this.markerY, 0), maxY);
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

  private findViews(resetIndex: boolean): void {
    if (!this.positionReady || this.areaWidth <= 0 || this.areaHeight <= 0) {
      return;
    }
    const centerX = this.markerX + this.markerSize / 2;
    const centerY = this.markerY + this.markerSize / 2;
    const key = `${Math.round(this.areaWidth)}x${Math.round(this.areaHeight)}:${Math.round(centerX)}:${Math.round(centerY)}`;
    if (!resetIndex && key === this.lastFindKey) {
      return;
    }
    this.lastFindKey = key;
    this.hits = ViewCheckTree.find(this.getUIContext(), centerX, centerY, this.originX, this.originY);
    if (resetIndex || this.hitIndex >= this.hits.length) {
      this.hitIndex = 0;
    }
    this.applySelection();
  }

  private applySelection(): void {
    this.showSwitch = this.hits.length > 1;
    if (this.hits.length === 0) {
      this.infoText = '未命中控件';
      this.borderVisible = false;
      return;
    }
    const node = this.hits[this.hitIndex];
    const width = Math.round(node.right - node.left);
    const height = Math.round(node.bottom - node.top);
    const idText = node.componentId.length > 0 ? node.componentId : '';
    const lines: string[] = [];
    lines.push(`控件类型：${node.typeName}`);
    lines.push(`控件ID：${idText}`);
    lines.push(`控件尺寸：宽${width}，高${height}`);
    const extra = ViewCheckTree.describeExtra(node);
    for (let i = 0; i < extra.length; i++) {
      lines.push(extra[i]);
    }
    const pageName = node.pageName.length > 0 ? node.pageName : this.readPageName();
    if (pageName.length > 0) {
      lines.push(`当前页面：${pageName}`);
    }
    this.infoText = lines.join('\n');
    this.frameLeft = node.left - vp2px(this.originX);
    this.frameTop = node.top - vp2px(this.originY);
    this.frameWidth = node.right - node.left;
    this.frameHeight = node.bottom - node.top;
    this.borderVisible = this.frameWidth > 0 && this.frameHeight > 0;
  }

  private infoLines(): string[] {
    const text = this.infoText.length > 0 ? this.infoText : '松手查看准星下的控件';
    return text.split('\n');
  }

  private readPageName(): string {
    try {
      const state = this.getUIContext().getRouter().getState();
      if (state.name.length > 0) {
        return state.name;
      }
      return state.path;
    } catch (_err) {
      return '';
    }
  }

  private step(delta: number): void {
    const size = this.hits.length;
    if (size === 0) {
      return;
    }
    this.hitIndex = (this.hitIndex + delta + size) % size;
    this.applySelection();
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
    this.clampMarker();
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

  build() {
    Stack() {
      if (this.positionReady && this.borderVisible) {
        ViewCheckBorder({
          frameLeft: this.frameLeft,
          frameTop: this.frameTop,
          frameWidth: this.frameWidth,
          frameHeight: this.frameHeight,
          areaWidth: this.areaWidth,
          areaHeight: this.areaHeight
        })
          .width('100%')
          .height('100%')
          .hitTestBehavior(HitTestMode.None)
      }

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
      .onTouch((event?: TouchEvent) => {
        if (event !== undefined && event.type === TouchType.Up) {
          this.clampMarker();
          this.findViews(true);
        }
      })

      if (this.positionReady && this.getInfoBarWidth() > 0) {
        Stack({ alignContent: Alignment.TopStart }) {
          Row() {
            if (this.showSwitch) {
              Column() {
                Image($r('app.media.dk_pre_icon'))
                  .width(22)
                  .height(22)
                  .margin({ left: 16, right: 12 })
                  .objectFit(ImageFit.Contain)
              }
              .justifyContent(FlexAlign.Center)
              .alignSelf(ItemAlign.Stretch)
              .backgroundColor(Color.Transparent)
              .onClick(() => {
                this.step(-1);
              })
            }

            Column() {
              ForEach(this.infoLines(), (line: string, index: number) => {
                Text(line)
                  .fontSize(14)
                  .fontColor('#333333')
                  .width('100%')
                  .textAlign(TextAlign.Start)
                  .margin({ bottom: 6 })
              }, (line: string, index: number) => `${index}:${line}`)
            }
            .layoutWeight(1)
            .margin({ top: 14 })
            .padding({ right: this.showSwitch ? 0 : 38 })
            .alignSelf(ItemAlign.Start)

            if (this.showSwitch) {
              Column() {
                Image($r('app.media.dk_next_icon'))
                  .width(22)
                  .height(22)
                  .margin({ left: 12, right: 16 })
                  .objectFit(ImageFit.Contain)
              }
              .justifyContent(FlexAlign.Center)
              .alignSelf(ItemAlign.Stretch)
              .backgroundColor(Color.Transparent)
              .onClick(() => {
                this.step(1);
              })
            }
          }
          .width('100%')
          .alignItems(VerticalAlign.Center)

          Row() {
            Blank()
              .layoutWeight(1)
              .hitTestBehavior(HitTestMode.None)
            Image($r('app.media.dk_close_icon'))
              .width(22)
              .height(22)
              .margin({ top: 14, right: 16 })
              .objectFit(ImageFit.Contain)
              .onClick(() => {
                ViewCheckManager.getInstance().close();
              })
          }
          .width('100%')
          .alignItems(VerticalAlign.Top)
          .hitTestBehavior(HitTestMode.Transparent)
        }
        .width(this.getInfoBarWidth())
        .backgroundColor(Color.White)
        .borderRadius(4)
        .border({ width: px2vp(1), color: '#CCCCCC' })
        .position({ x: this.infoX, y: this.infoY })
        .hitTestBehavior(HitTestMode.Default)
        .gesture(
          PanGesture({ direction: PanDirection.All, fingers: 1, distance: 4 })
            .onActionStart(() => {
              this.onInfoPanStart();
            })
            .onActionUpdate((event?: GestureEvent) => {
              this.onInfoPanMove(event);
            })
        )
        .onAreaChange((_oldValue: Area, newValue: Area) => {
          const height = Number(newValue.height);
          if (!Number.isNaN(height) && height > 0 && Math.abs(height - this.infoBarHeight) > 1) {
            this.infoBarHeight = height;
            this.clampInfo();
          }
        })
      }
    }
    .width('100%')
    .height('100%')
    .id(ViewCheckTree.OVERLAY_ID)
    .clip(false)
    .hitTestBehavior(HitTestMode.Transparent)
    .onAreaChange((_oldValue: Area, newValue: Area) => {
      this.areaWidth = Number(newValue.width);
      this.areaHeight = Number(newValue.height);
      this.readOrigin(newValue);
      const becameReady = !this.positionReady;
      if (becameReady) {
        this.initPosition();
      } else if (!this.infoDragged) {
        this.infoX = this.infoSideMargin;
        this.infoY = this.areaHeight - this.infoBottomOffset;
        this.clampInfo();
      } else {
        this.clampInfo();
      }
      this.clampMarker();
      if (this.positionReady) {
        this.findViews(becameReady);
      }
    })
  }
}

@Component
struct ViewCheckBorder {
  @Prop @Watch('redraw') frameLeft: number = 0;
  @Prop @Watch('redraw') frameTop: number = 0;
  @Prop @Watch('redraw') frameWidth: number = 0;
  @Prop @Watch('redraw') frameHeight: number = 0;
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
    if (this.frameWidth <= 0 || this.frameHeight <= 0) {
      return;
    }
    const scaleX = this.areaWidth > 0 ? width / vp2px(this.areaWidth) : 1;
    const scaleY = this.areaHeight > 0 ? height / vp2px(this.areaHeight) : 1;
    const left = this.frameLeft * scaleX;
    const top = this.frameTop * scaleY;
    const rectWidth = this.frameWidth * scaleX;
    const rectHeight = this.frameHeight * scaleY;
    ctx.beginPath();
    ctx.rect(left, top, rectWidth, rectHeight);
    ctx.lineWidth = 1;
    ctx.setLineDash([1, 1]);
    ctx.strokeStyle = '#FF0000';
    ctx.stroke();
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
