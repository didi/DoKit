import { MultiCardiogramView } from './widget/MultiCardiogramView';

class LegendItem {
  id: string;
  title: string;
  color: string;
  label: string;

  constructor(id: string, title: string, color: string, label: string) {
    this.id = id;
    this.title = title;
    this.color = color;
    this.label = label;
  }
}

/**
 * 性能指标浮层：四指标合并为一张多色折线图 + 图例
 * 图例随采样刷新最新值
 */
@Component
export struct PerformanceDoKitView {
  @Prop showFps: boolean = false;
  @Prop showCpu: boolean = false;
  @Prop showRam: boolean = false;
  @Prop showNetwork: boolean = false;
  @Prop @Watch('onTopOffsetChange') topOffset: number = 88;
  @Prop containerHeight: number = 0;
  onClose: () => void = () => {};

  private readonly legendHeight: number = 22;
  /** 与 MultiCardiogramView 一致；遮罩高度随开启轨数自适应 */
  private readonly bandHeight: number = 80;
  private readonly bandGap: number = 0;
  private readonly canvasPadTop: number = 2;
  private readonly canvasPadBottom: number = 8;
  private readonly viewPadTop: number = 0;
  private readonly viewPadBottom: number = 4;
  /** 与设置页开关左右 padding 一致 */
  private readonly sidePad: number = 16;
  private readonly dragThreshold: number = 3;

  @State private viewY: number = 88;
  @State private userDragged: boolean = false;
  @State private legendItems: LegendItem[] = [];
  /** 强制图例 Text 随数据刷新 */
  @State private legendEpoch: number = 0;
  private dragStartY: number = 0;

  private getActiveCount(): number {
    let n = 0;
    if (this.showFps) {
      n++;
    }
    if (this.showCpu) {
      n++;
    }
    if (this.showRam) {
      n++;
    }
    if (this.showNetwork) {
      n++;
    }
    return Math.max(n, 1);
  }

  private getChartBodyHeight(): number {
    const n = this.getActiveCount();
    const bands = n * this.bandHeight + Math.max(n - 1, 0) * this.bandGap;
    return this.viewPadTop + this.viewPadBottom + this.canvasPadTop + this.canvasPadBottom + bands;
  }

  private getTotalHeight(): number {
    return this.legendHeight + this.getChartBodyHeight();
  }

  aboutToAppear(): void {
    this.viewY = this.topOffset;
  }

  private onTopOffsetChange(): void {
    if (!this.userDragged) {
      this.viewY = this.topOffset;
    }
  }

  private getMaxY(): number {
    const h = this.getTotalHeight();
    if (this.containerHeight <= h) {
      return Math.max(this.topOffset, 0);
    }
    return this.containerHeight - h;
  }

  private clamp(value: number, min: number, max: number): number {
    if (value < min) {
      return min;
    }
    if (value > max) {
      return max;
    }
    return value;
  }

  private onPanStart(): void {
    this.dragStartY = this.viewY;
  }

  private onPanMove(event?: GestureEvent): void {
    if (!event) {
      return;
    }
    if (Math.abs(event.offsetY) > this.dragThreshold) {
      this.userDragged = true;
    }
    this.viewY = this.clamp(this.dragStartY + event.offsetY, 0, this.getMaxY());
  }

  private onPanEnd(event?: GestureEvent): void {
    if (event && Math.abs(event.offsetY) > this.dragThreshold) {
      this.userDragged = true;
    }
    this.viewY = this.clamp(this.viewY, 0, this.getMaxY());
  }

  private onSeriesSnapshot(titles: string[], colors: string[], labels: string[]): void {
    // 延后到下一帧，避免 build/re-render 过程中改 @State
    const titleCopy: string[] = titles.slice();
    const colorCopy: string[] = colors.slice();
    const labelCopy: string[] = labels.slice();
    setTimeout(() => {
      const items: LegendItem[] = [];
      const count = Math.min(titleCopy.length, Math.min(colorCopy.length, labelCopy.length));
      const nextEpoch = this.legendEpoch + 1;
      for (let i = 0; i < count; i++) {
        const id = `${titleCopy[i]}_${labelCopy[i]}_${nextEpoch}`;
        items.push(new LegendItem(id, titleCopy[i], colorCopy[i], labelCopy[i]));
      }
      this.legendItems = items;
      this.legendEpoch = nextEpoch;
    }, 0);
  }

  @Builder
  private LegendRow() {
    Row({ space: 8 }) {
      ForEach(this.legendItems, (item: LegendItem) => {
        Row({ space: 2 }) {
          Column()
            .width(5)
            .height(5)
            .borderRadius(2.5)
            .backgroundColor(item.color)
          Text(`${item.title} ${item.label}`)
            .fontSize(8)
            .fontColor('#FFFFFF')
            .maxLines(1)
            .constraintSize({ maxWidth: 80 })
        }
      }, (item: LegendItem) => item.id)
      Blank().layoutWeight(1)
      // 右对齐到与下方图表右缘一致（原先 24 居中会偏左）
      Text('✕')
        .fontSize(11)
        .fontColor('#CCCCCC')
        .width(16)
        .height(22)
        .textAlign(TextAlign.End)
        .onClick(() => {
          this.onClose();
        })
    }
    .width('100%')
    .height(this.legendHeight)
    .padding({ left: this.sidePad, right: this.sidePad })
    .hitTestBehavior(HitTestMode.Default)
  }

  build() {
    Column() {
      this.LegendRow()
      MultiCardiogramView({
        showFps: this.showFps,
        showCpu: this.showCpu,
        showRam: this.showRam,
        showNetwork: this.showNetwork,
        intervalMs: 1000,
        onSeriesSnapshot: (titles: string[], colors: string[], labels: string[]) => {
          this.onSeriesSnapshot(titles, colors, labels);
        }
      })
        .width('100%')
        .height(this.getChartBodyHeight())
        .padding({
          top: this.viewPadTop,
          bottom: this.viewPadBottom,
          left: this.sidePad,
          right: this.sidePad
        })
        .backgroundColor(Color.Transparent)
        .hitTestBehavior(HitTestMode.None)
    }
    .width('100%')
    .height(this.getTotalHeight())
    .backgroundColor('#AA000000')
    .position({ x: 0, y: this.viewY })
    .hitTestBehavior(HitTestMode.Default)
    .gesture(
      PanGesture({ direction: PanDirection.Vertical, fingers: 1, distance: 1 })
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
  }
}
