import { DataSourceFactory } from '../datasource/DataSourceFactory';
import { IDataSource } from '../datasource/IDataSource';
import { PerformanceDataManager } from '../PerformanceDataManager';
import { LineData } from './LineData';

/** 单条序列配置与运行态 */
class ChartSeries {
  type: number;
  title: string;
  color: string;
  dataSource: IDataSource;
  dataList: LineData[] = [];
  axisMax: number = 100;
  latestLabel: string = '';

  constructor(type: number, title: string, color: string, dataSource: IDataSource, axisMax: number) {
    this.type = type;
    this.title = title;
    this.color = color;
    this.dataSource = dataSource;
    this.axisMax = axisMax;
  }
}

/**
 * 多指标合并折线图：分轨 + Y 轴；最新值回调给顶部图例
 */
@Component
export struct MultiCardiogramView {
  @Prop @Watch('onEnabledChange') showFps: boolean = false;
  @Prop @Watch('onEnabledChange') showCpu: boolean = false;
  @Prop @Watch('onEnabledChange') showRam: boolean = false;
  @Prop @Watch('onEnabledChange') showNetwork: boolean = false;
  intervalMs: number = 1000;
  /** 最新数值回调给外层图例（随采样滚动刷新） */
  onSeriesSnapshot: (titles: string[], colors: string[], labels: string[]) => void = () => {};

  private static readonly DEFAULT_ITEM_COUNT: number = 12;
  private static readonly MAX_ITEM_COUNT: number = 14;
  private static readonly FRAME_DELAY: number = 32;
  /** 首轨上方留白：尽量贴紧顶部图例 */
  private static readonly PADDING_TOP: number = 2;
  /** 底部多留，避免最后一轨 0 刻度被裁切 */
  private static readonly PADDING_BOTTOM: number = 8;
  /** 每轨固定高度（含轨内顶部数字区） */
  private static readonly BAND_HEIGHT: number = 80;
  /** 图与图上下间距（分割线已去掉） */
  private static readonly BAND_GAP: number = 0;
  /** 轨内折线区顶部留白，给数值标签；收紧以缩小图与图视觉间距 */
  private static readonly INNER_PAD_TOP: number = 11;
  private static readonly INNER_PAD_BOTTOM: number = 2;
  /** 画布内左右边距；外层已有 16 对齐开关，此处不再额外加大 */
  private static readonly SIDE_MARGIN: number = 0;
  private static readonly LINE_WIDTH: number = 1;
  private static readonly POINT_RADIUS: number = 1.5;
  private static readonly LABEL_OFFSET: number = 10;
  private static readonly COLOR_FPS: string = '#FF00C9F4';
  private static readonly COLOR_CPU: string = '#FFFFD60A';
  private static readonly COLOR_RAM: string = '#FF34C759';
  private static readonly COLOR_NETWORK: string = '#FFFF6B6B';

  private readonly settings: RenderingContextSettings = new RenderingContextSettings(true);
  private readonly canvasCtx: CanvasRenderingContext2D = new CanvasRenderingContext2D(this.settings);
  private animTimer: number = -1;
  private canvasReady: boolean = false;
  private canvasWidth: number = 0;
  private canvasHeight: number = 0;
  private itemWidth: number = 0;
  private totalFrameCount: number = 31;
  private currentFrameCount: number = 0;
  private seriesList: ChartSeries[] = [];

  aboutToAppear(): void {
    this.totalFrameCount = Math.max(1, Math.floor(this.intervalMs / MultiCardiogramView.FRAME_DELAY));
    this.rebuildSeries();
    this.animTimer = setInterval(() => {
      this.onAnimTick();
    }, MultiCardiogramView.FRAME_DELAY);
  }

  aboutToDisappear(): void {
    if (this.animTimer >= 0) {
      clearInterval(this.animTimer);
      this.animTimer = -1;
    }
    this.seriesList = [];
    this.currentFrameCount = 0;
    this.canvasReady = false;
  }

  private onEnabledChange(): void {
    // 关掉哪个类型就丢掉哪个序列；仍开启的保留数据
    this.rebuildSeries();
  }

  /** 无数据时的最小刻度，避免除零；正常按折线峰值撑开 */
  private resolveMinAxis(type: number): number {
    if (type === DataSourceFactory.TYPE_FPS) {
      return 1;
    }
    if (type === DataSourceFactory.TYPE_NETWORK) {
      return 1;
    }
    if (type === DataSourceFactory.TYPE_RAM) {
      return 1;
    }
    // CPU
    return 1;
  }

  /** 初始轴上限：有历史峰值用峰值，否则用类型下限 */
  private resolveInitialAxis(type: number): number {
    if (type === DataSourceFactory.TYPE_FPS) {
      const maxFps = PerformanceDataManager.getInstance().getMaxFrameRate();
      if (maxFps > 0) {
        return maxFps;
      }
    }
    return this.resolveMinAxis(type);
  }

  private rebuildSeries(): void {
    const next: ChartSeries[] = [];
    const keepByType: Map<number, ChartSeries> = new Map();
    for (let i = 0; i < this.seriesList.length; i++) {
      keepByType.set(this.seriesList[i].type, this.seriesList[i]);
    }

    const appendIf = (enabled: boolean, type: number, title: string, color: string): void => {
      if (!enabled) {
        return;
      }
      // 仅保留「当前仍开启」的序列；关掉再开的类型不复用旧点
      const existed = keepByType.get(type);
      if (existed !== undefined) {
        next.push(existed);
        return;
      }
      const source = DataSourceFactory.createDataSource(type);
      // 新开序列：空数据，由定时采样从右往左长出，避免一打开就铺满旧值
      const series = new ChartSeries(type, title, color, source, this.resolveInitialAxis(type));
      next.push(series);
    };

    appendIf(this.showFps, DataSourceFactory.TYPE_FPS, '帧率', MultiCardiogramView.COLOR_FPS);
    appendIf(this.showCpu, DataSourceFactory.TYPE_CPU, 'CPU', MultiCardiogramView.COLOR_CPU);
    appendIf(this.showRam, DataSourceFactory.TYPE_RAM, '内存', MultiCardiogramView.COLOR_RAM);
    appendIf(this.showNetwork, DataSourceFactory.TYPE_NETWORK, '流量', MultiCardiogramView.COLOR_NETWORK);
    this.seriesList = next;
    this.emitSnapshot();
    if (this.canvasReady) {
      this.draw();
    }
  }

  /**
   * Y 轴跟随当前折线窗口内最大值（CPU / 内存 / 帧率 / 流量各自独立）
   */
  private refreshSeriesAxis(series: ChartSeries): void {
    let peak = 0;
    for (let i = 0; i < series.dataList.length; i++) {
      if (series.dataList[i].value > peak) {
        peak = series.dataList[i].value;
      }
    }
    const minAxis = this.resolveMinAxis(series.type);
    // 峰值基础上留约 15% 顶空，避免贴顶；无数据时用最小刻度
    let next = peak > 0 ? peak * 1.15 : minAxis;
    if (next < minAxis) {
      next = minAxis;
    }
    // 上升立刻跟上，下降平滑回收，避免轴跳太猛
    if (next > series.axisMax) {
      series.axisMax = next;
    } else {
      series.axisMax = series.axisMax * 0.9 + next * 0.1;
    }
    if (series.axisMax < minAxis) {
      series.axisMax = minAxis;
    }
  }

  private emitSnapshot(): void {
    const titles: string[] = [];
    const colors: string[] = [];
    const labels: string[] = [];
    for (let i = 0; i < this.seriesList.length; i++) {
      const s = this.seriesList[i];
      titles.push(s.title);
      colors.push(s.color);
      // 优先用最新点 label；空则回退数值
      let text = s.latestLabel;
      if (text.length === 0 && s.dataList.length > 0) {
        text = `${Math.round(s.dataList[s.dataList.length - 1].value)}`;
      }
      labels.push(text);
    }
    this.onSeriesSnapshot(titles, colors, labels);
  }

  private onAnimTick(): void {
    this.currentFrameCount++;
    if (this.currentFrameCount >= this.totalFrameCount) {
      this.currentFrameCount = 0;
      for (let i = 0; i < this.seriesList.length; i++) {
        const series = this.seriesList[i];
        const data = series.dataSource.createData();
        series.dataList.push(data);
        series.latestLabel = data.label !== null && data.label.length > 0
          ? data.label
          : `${Math.round(data.value)}`;
        if (series.dataList.length > MultiCardiogramView.MAX_ITEM_COUNT) {
          series.dataList.shift();
        }
        this.refreshSeriesAxis(series);
      }
      this.emitSnapshot();
    }
    this.draw();
  }

  private normalizeValue(value: number, axisMax: number): number {
    if (axisMax <= 0) {
      return 0;
    }
    let n = (value / axisMax) * 100;
    if (n > 100) {
      n = 100;
    }
    if (n < 0) {
      n = 0;
    }
    return n;
  }

  private withAlpha(color: string, alphaHex: string): string {
    if (color.length === 9 && color.charAt(0) === '#') {
      return `#${alphaHex}${color.substring(3)}`;
    }
    if (color.length === 7 && color.charAt(0) === '#') {
      return `#${alphaHex}${color.substring(1)}`;
    }
    return color;
  }

  private getBandMetrics(seriesCount: number): number[] {
    const count = Math.max(seriesCount, 1);
    const gap = count > 1 ? MultiCardiogramView.BAND_GAP : 0;
    const bandH = MultiCardiogramView.BAND_HEIGHT;
    const totalH = bandH * count + gap * Math.max(count - 1, 0);
    return [totalH, gap, bandH];
  }

  private valueToY(value: number, axisMax: number, seriesIndex: number, seriesCount: number): number {
    const metrics = this.getBandMetrics(seriesCount);
    const gap = metrics[1];
    const bandH = metrics[2];
    const bandTop = MultiCardiogramView.PADDING_TOP + seriesIndex * (bandH + gap);
    const n = this.normalizeValue(value, axisMax);
    const innerPadTop = MultiCardiogramView.INNER_PAD_TOP;
    const innerPadBottom = MultiCardiogramView.INNER_PAD_BOTTOM;
    const usable = Math.max(bandH - innerPadTop - innerPadBottom, 1);
    return bandTop + innerPadTop + (1 - n / 100) * usable;
  }

  private getChartLeft(): number {
    return MultiCardiogramView.SIDE_MARGIN;
  }

  private getChartRight(): number {
    return this.canvasWidth - MultiCardiogramView.SIDE_MARGIN;
  }

  private getTranslateX(dataLength: number): number {
    const progress = this.currentFrameCount / this.totalFrameCount;
    // 与安卓一致：空位按 MAX 算；预填满后 empty=0，一出现即满屏，只做平滑左移
    const emptySlots = Math.max(0, MultiCardiogramView.MAX_ITEM_COUNT - dataLength);
    return this.getChartLeft() - this.itemWidth * progress + this.itemWidth * emptySlots;
  }

  private formatAxisValue(series: ChartSeries, ratio: number): string {
    const v = series.axisMax * ratio;
    if (ratio <= 0) {
      return '0';
    }
    if (series.type === DataSourceFactory.TYPE_CPU) {
      return `${Math.round(v)}%`;
    }
    if (series.type === DataSourceFactory.TYPE_RAM) {
      return `${Math.round(v)}MB`;
    }
    if (series.type === DataSourceFactory.TYPE_NETWORK) {
      if (v < 1) {
        return '0';
      }
      if (v < 1024) {
        return `${Math.round(v)}K`;
      }
      return `${(v / 1024).toFixed(1)}M`;
    }
    // 帧率
    return `${Math.round(v)}`;
  }

  /** 每轨 Y 轴 + X 轴底线：轴线在左，刻度数字画在内侧 */
  private drawYAxis(series: ChartSeries, seriesIndex: number, seriesCount: number): void {
    const ctx = this.canvasCtx;
    const metrics = this.getBandMetrics(seriesCount);
    const gap = metrics[1];
    const bandH = metrics[2];
    const bandTop = MultiCardiogramView.PADDING_TOP + seriesIndex * (bandH + gap);
    const bandBottom = bandTop + bandH;
    const axisX = this.getChartLeft();
    const chartRight = this.getChartRight();
    const innerPadTop = MultiCardiogramView.INNER_PAD_TOP;
    const innerPadBottom = MultiCardiogramView.INNER_PAD_BOTTOM;
    const yTop = bandTop + innerPadTop;
    const yBottom = bandBottom - innerPadBottom;
    const yMid = (yTop + yBottom) / 2;

    ctx.beginPath();
    ctx.moveTo(axisX, yTop);
    ctx.lineTo(axisX, yBottom);
    ctx.strokeStyle = '#66FFFFFF';
    ctx.lineWidth = 1;
    ctx.stroke();

    const ticks: number[] = [yTop, yMid, yBottom];
    for (let i = 0; i < ticks.length; i++) {
      ctx.beginPath();
      ctx.moveTo(axisX, ticks[i]);
      ctx.lineTo(axisX + 3, ticks[i]);
      ctx.strokeStyle = '#66FFFFFF';
      ctx.lineWidth = 1;
      ctx.stroke();
    }

    // 每轨都画 X 轴底线
    ctx.beginPath();
    ctx.moveTo(axisX, yBottom);
    ctx.lineTo(chartRight, yBottom);
    ctx.strokeStyle = '#33999999';
    ctx.lineWidth = 0.5;
    ctx.stroke();

    // 刻度在轴线内侧（图表区域内）
    // 对标安卓 dk_font_size_10，鸿蒙 Canvas 略收一档更接近观感
    ctx.fillStyle = '#CCFFFFFF';
    ctx.font = '8vp HarmonyOS Sans';
    ctx.textAlign = 'left';
    const labelX = axisX + 5;
    ctx.fillText(this.formatAxisValue(series, 1), labelX, yTop + 2);
    ctx.fillText(this.formatAxisValue(series, 0.5), labelX, yMid + 2);
    ctx.fillText('0', labelX, yBottom);
  }

  private drawSeries(series: ChartSeries, seriesIndex: number, seriesCount: number): void {
    const ctx = this.canvasCtx;
    if (this.itemWidth <= 0 || series.dataList.length === 0) {
      return;
    }

    const translateX = this.getTranslateX(series.dataList.length);
    const drawCount = Math.min(series.dataList.length, MultiCardiogramView.DEFAULT_ITEM_COUNT + 1);
    const lineColor = this.withAlpha(series.color, '4C');
    const fillTop = this.withAlpha(series.color, '33');
    const metrics = this.getBandMetrics(seriesCount);
    const bandTop = MultiCardiogramView.PADDING_TOP + seriesIndex * (metrics[2] + metrics[1]);
    const usableBottom = bandTop + metrics[2] - MultiCardiogramView.INNER_PAD_BOTTOM;
    const chartLeft = this.getChartLeft();
    const chartRight = this.getChartRight();

    // 裁到本轨底，避免低值圆点/填充渗到下一张图之间
    const clipTop = Math.max(0, bandTop - MultiCardiogramView.LABEL_OFFSET);
    const clipBottom = usableBottom + 1;
    const clipH = Math.max(clipBottom - clipTop, 1);
    ctx.save();
    ctx.beginPath();
    ctx.rect(chartLeft, clipTop, Math.max(chartRight - chartLeft, 1), clipH);
    ctx.clip();

    for (let index = 0; index < drawCount; index++) {
      const current = series.dataList[index];
      const pointX = translateX + index * this.itemWidth;
      const pointY = this.valueToY(current.value, series.axisMax, seriesIndex, seriesCount);
      const hasNext = index < series.dataList.length - 1;

      if (hasNext) {
        const next = series.dataList[index + 1];
        const nextX = pointX + this.itemWidth;
        const nextY = this.valueToY(next.value, series.axisMax, seriesIndex, seriesCount);

        // 对标安卓：每段折线下方梯形填充，随数据点一起出现
        const gradient = ctx.createLinearGradient(pointX, bandTop + MultiCardiogramView.INNER_PAD_TOP,
          nextX, usableBottom);
        gradient.addColorStop(0, fillTop);
        gradient.addColorStop(1, '#33434352');
        ctx.beginPath();
        ctx.moveTo(pointX, pointY);
        ctx.lineTo(pointX, usableBottom);
        ctx.lineTo(nextX, usableBottom);
        ctx.lineTo(nextX, nextY);
        ctx.closePath();
        ctx.fillStyle = gradient;
        ctx.fill();

        ctx.beginPath();
        ctx.moveTo(pointX, pointY);
        ctx.lineTo(nextX, nextY);
        ctx.strokeStyle = lineColor;
        ctx.lineWidth = MultiCardiogramView.LINE_WIDTH;
        ctx.lineCap = 'butt';
        ctx.lineJoin = 'miter';
        ctx.stroke();
      }

      ctx.beginPath();
      ctx.arc(pointX, pointY, MultiCardiogramView.POINT_RADIUS, 0, Math.PI * 2);
      ctx.fillStyle = series.color;
      ctx.fill();

      let showLabel = false;
      let labelAlpha = 1;
      if (index === series.dataList.length - 2) {
        showLabel = true;
        labelAlpha = 1;
      } else if (index === series.dataList.length - 3) {
        showLabel = true;
        labelAlpha = 1 - this.currentFrameCount / this.totalFrameCount;
      }
      if (showLabel && current.label !== null && current.label.length > 0) {
        const labelY = pointY - MultiCardiogramView.LABEL_OFFSET;
        ctx.globalAlpha = labelAlpha;
        ctx.fillStyle = '#FFFFFF';
        ctx.font = '8vp HarmonyOS Sans';
        ctx.textAlign = 'center';
        ctx.fillText(current.label, pointX, Math.max(labelY, clipTop + 8));
        ctx.globalAlpha = 1;
      }
    }
    ctx.restore();
  }

  private draw(): void {
    if (!this.canvasReady || this.canvasWidth <= 0 || this.canvasHeight <= 0) {
      return;
    }
    const ctx = this.canvasCtx;
    ctx.clearRect(0, 0, this.canvasWidth, this.canvasHeight);
    const seriesCount = this.seriesList.length;
    for (let i = 0; i < seriesCount; i++) {
      this.drawYAxis(this.seriesList[i], i, seriesCount);
      this.drawSeries(this.seriesList[i], i, seriesCount);
    }
  }

  private refreshItemWidth(): void {
    const chartW = Math.max(this.canvasWidth - MultiCardiogramView.SIDE_MARGIN * 2, 1);
    this.itemWidth = chartW / MultiCardiogramView.DEFAULT_ITEM_COUNT;
  }

  build() {
    Canvas(this.canvasCtx)
      .width('100%')
      .height('100%')
      .backgroundColor(Color.Transparent)
      .hitTestBehavior(HitTestMode.None)
      .onReady(() => {
        this.canvasReady = true;
        this.refreshItemWidth();
        this.draw();
      })
      .onAreaChange((_oldValue: Area, newValue: Area) => {
        this.canvasWidth = Number(newValue.width);
        this.canvasHeight = Number(newValue.height);
        this.refreshItemWidth();
        this.draw();
      })
  }
}
