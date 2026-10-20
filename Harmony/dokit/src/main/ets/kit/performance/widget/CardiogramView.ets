import { DataSourceFactory } from '../datasource/DataSourceFactory';
import { IDataSource } from '../datasource/IDataSource';
import { LineData } from './LineData';

/**
 * 平滑滚动折线图，对标 Android CardiogramView + LineRender
 * Y 轴按指标自适应，避免帧率等数值挤在中间看起来「一直是平的」
 */
@Component
export struct CardiogramView {
  performanceType: number = DataSourceFactory.TYPE_FPS;
  intervalMs: number = 1000;

  private static readonly DEFAULT_ITEM_COUNT: number = 12;
  private static readonly MAX_ITEM_COUNT: number = 14;
  private static readonly FRAME_DELAY: number = 32;
  private static readonly PADDING_TOP: number = 28;
  private static readonly PADDING_BOTTOM: number = 4;
  /** 圆点按参考图缩小一倍（原 5 → 2.5）；线宽/线色仍对标 Android */
  private static readonly POINT_RADIUS: number = 1.5;
  private static readonly LINE_WIDTH: number = 1;
  private static readonly LINE_COLOR: string = '#4C00C9F4';
  private static readonly POINT_COLOR: string = '#FF00C9F4';

  private readonly settings: RenderingContextSettings = new RenderingContextSettings(true);
  private readonly canvasCtx: CanvasRenderingContext2D = new CanvasRenderingContext2D(this.settings);
  private animTimer: number = -1;
  private canvasReady: boolean = false;
  private canvasWidth: number = 0;
  private canvasHeight: number = 0;
  private itemWidth: number = 0;
  private totalFrameCount: number = 31;
  private currentFrameCount: number = 0;
  private dataList: LineData[] = [];
  private dataSource: IDataSource | null = null;
  /** 当前绘制用的 Y 轴上限，随数据动态抬升 */
  private axisMax: number = 60;

  aboutToAppear(): void {
    const source: IDataSource = DataSourceFactory.createDataSource(this.performanceType);
    this.dataSource = source;
    this.totalFrameCount = Math.max(1, Math.floor(this.intervalMs / CardiogramView.FRAME_DELAY));
    this.axisMax = 1;
    this.dataList = [];
    // 预填到 MAX，一展示就是满屏折线+填充
    for (let i = 0; i < CardiogramView.MAX_ITEM_COUNT; i++) {
      this.dataList.push(source.createData());
    }
    this.refreshAxisMax();
    this.animTimer = setInterval(() => {
      this.onAnimTick();
    }, CardiogramView.FRAME_DELAY);
  }

  aboutToDisappear(): void {
    if (this.animTimer >= 0) {
      clearInterval(this.animTimer);
      this.animTimer = -1;
    }
    this.canvasReady = false;
  }

  /** Y 轴跟随折线窗口峰值 */
  private refreshAxisMax(): void {
    let peak = 0;
    for (let i = 0; i < this.dataList.length; i++) {
      if (this.dataList[i].value > peak) {
        peak = this.dataList[i].value;
      }
    }
    let next = peak > 0 ? peak * 1.15 : 1;
    if (next < 1) {
      next = 1;
    }
    if (next > this.axisMax) {
      this.axisMax = next;
    } else {
      this.axisMax = this.axisMax * 0.9 + next * 0.1;
    }
    if (this.axisMax < 1) {
      this.axisMax = 1;
    }
  }

  private onAnimTick(): void {
    this.currentFrameCount++;
    if (this.currentFrameCount >= this.totalFrameCount) {
      this.currentFrameCount = 0;
      const source = this.dataSource;
      if (source !== null) {
        this.dataList.push(source.createData());
      }
      if (this.dataList.length > CardiogramView.MAX_ITEM_COUNT) {
        this.dataList.shift();
      }
      this.refreshAxisMax();
    }
    this.draw();
  }

  private clampValue(value: number): number {
    if (value > this.axisMax) {
      return this.axisMax;
    }
    if (value < 0) {
      return 0;
    }
    return value;
  }

  private valueToY(value: number, graphHeight: number): number {
    const v = this.clampValue(value);
    return (1 - v / this.axisMax) * graphHeight + CardiogramView.PADDING_TOP;
  }

  private getTranslateX(): number {
    const progress = this.currentFrameCount / this.totalFrameCount;
    const emptySlots = Math.max(0, CardiogramView.DEFAULT_ITEM_COUNT - this.dataList.length);
    return -this.itemWidth * progress + this.itemWidth * emptySlots;
  }

  private draw(): void {
    if (!this.canvasReady || this.canvasWidth <= 0 || this.canvasHeight <= 0) {
      return;
    }
    const ctx = this.canvasCtx;
    ctx.clearRect(0, 0, this.canvasWidth, this.canvasHeight);

    const graphHeight = this.canvasHeight - CardiogramView.PADDING_TOP - CardiogramView.PADDING_BOTTOM;
    if (graphHeight <= 0 || this.itemWidth <= 0) {
      return;
    }

    const translateX = this.getTranslateX();
    const drawCount = Math.min(this.dataList.length, CardiogramView.DEFAULT_ITEM_COUNT + 1);
    const bottomY = graphHeight + CardiogramView.PADDING_TOP;

    for (let index = 0; index < drawCount; index++) {
      const current = this.dataList[index];
      const pointX = translateX + index * this.itemWidth;
      const pointY = this.valueToY(current.value, graphHeight);
      const hasNext = index < this.dataList.length - 1;

      if (hasNext) {
        const next = this.dataList[index + 1];
        const nextX = pointX + this.itemWidth;
        const nextY = this.valueToY(next.value, graphHeight);

        // 对标 Android：#3300BFFF → #33434352 渐变填充
        const gradient = ctx.createLinearGradient(pointX, CardiogramView.PADDING_TOP, nextX, bottomY);
        gradient.addColorStop(0, '#3300BFFF');
        gradient.addColorStop(1, '#33434352');
        ctx.beginPath();
        ctx.moveTo(pointX, pointY);
        ctx.lineTo(pointX, bottomY);
        ctx.lineTo(nextX, bottomY);
        ctx.lineTo(nextX, nextY);
        ctx.closePath();
        ctx.fillStyle = gradient;
        ctx.fill();

        ctx.beginPath();
        ctx.moveTo(pointX, pointY);
        ctx.lineTo(nextX, nextY);
        ctx.strokeStyle = CardiogramView.LINE_COLOR;
        ctx.lineWidth = CardiogramView.LINE_WIDTH;
        ctx.lineCap = 'butt';
        ctx.lineJoin = 'miter';
        ctx.stroke();
      }

      ctx.beginPath();
      ctx.arc(pointX, pointY, CardiogramView.POINT_RADIUS, 0, Math.PI * 2);
      ctx.fillStyle = CardiogramView.POINT_COLOR;
      ctx.fill();

      let showLabel = false;
      let labelAlpha = 1;
      if (index === this.dataList.length - 2) {
        showLabel = true;
        labelAlpha = 1;
      } else if (index === this.dataList.length - 3) {
        showLabel = true;
        labelAlpha = 1 - this.currentFrameCount / this.totalFrameCount;
      }
      if (showLabel && current.label !== null && current.label.length > 0) {
        ctx.globalAlpha = labelAlpha;
        ctx.fillStyle = '#FFFFFF';
        ctx.font = '10vp HarmonyOS Sans';
        ctx.textAlign = 'center';
        ctx.fillText(current.label, pointX, pointY - 20);
        ctx.globalAlpha = 1;
      }
    }
  }

  build() {
    Canvas(this.canvasCtx)
      .width('100%')
      .height('100%')
      .backgroundColor(Color.Transparent)
      .hitTestBehavior(HitTestMode.None)
      .onReady(() => {
        this.canvasReady = true;
        this.draw();
      })
      .onAreaChange((_oldValue: Area, newValue: Area) => {
        this.canvasWidth = Number(newValue.width);
        this.canvasHeight = Number(newValue.height);
        this.itemWidth = this.canvasWidth / CardiogramView.DEFAULT_ITEM_COUNT;
        this.draw();
      })
  }
}
