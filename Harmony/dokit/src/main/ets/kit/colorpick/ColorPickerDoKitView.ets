import promptAction from '@ohos.promptAction';
import { image } from '@kit.ImageKit';
import { ColorPickConstants } from './ColorPickConstants';
import { ColorPickManager } from './ColorPickManager';
import { ColorPickerPatch, ImageCapture } from './ImageCapture';
import { ColorUtil } from '../../util/ColorUtil';

/**
 * 取色放大镜 + 底部信息栏，对标 Android
 * ColorPickerDoKitView / ColorPickerView / ColorPickerInfoDoKitView
 */
@Component
export struct ColorPickerDoKitView {
  containerWidth: number = 0;
  containerHeight: number = 0;

  private readonly viewSize: number = px2vp(ColorPickConstants.PICK_VIEW_SIZE);
  private readonly ringWidth: number = px2vp(ColorPickConstants.PIX_INTERVAL * 2 + 4);
  private readonly cellSize: number = px2vp(ColorPickConstants.PICK_VIEW_SIZE) / ColorPickConstants.PICK_AREA_SIZE;
  private readonly imageCapture: ImageCapture = new ImageCapture();

  @State private areaWidth: number = 0;
  @State private areaHeight: number = 0;
  @State private pickerX: number = 0;
  @State private pickerY: number = 0;
  @State private lensVisible: boolean = true;
  @State private colorInt: number = 0xFF333333;
  @State private infoText: string = '#333333   0,0';
  @State private partPixelMap: image.PixelMap | undefined = undefined;
  @State private positionReady: boolean = false;
  @State private dragStartX: number = 0;
  @State private dragStartY: number = 0;
  @State private gridVersion: number = 0;

  private captureTimer: number = -1;
  private captureSeq: number = 0;

  aboutToAppear(): void {
    this.areaWidth = this.containerWidth;
    this.areaHeight = this.containerHeight;
    this.initPosition();
    this.captureInfo(300);
  }

  aboutToDisappear(): void {
    this.cancelCaptureTimer();
    this.releasePartPixelMap();
    this.imageCapture.destroy();
  }

  private initPosition(): void {
    if (this.areaWidth <= 0 || this.areaHeight <= 0) {
      return;
    }
    if (this.positionReady) {
      this.clampPosition();
      return;
    }
    this.pickerX = (this.areaWidth - this.viewSize) / 2;
    this.pickerY = (this.areaHeight - this.viewSize) / 2;
    this.clampPosition();
    this.positionReady = true;
  }

  private clampPosition(): void {
    const minX = -this.viewSize / 2;
    const maxX = this.areaWidth - this.viewSize / 2 - this.cellSize;
    const minY = -this.viewSize / 2;
    const maxY = this.areaHeight - this.viewSize / 2 - this.cellSize;
    this.pickerX = Math.min(Math.max(this.pickerX, minX), Math.max(minX, maxX));
    this.pickerY = Math.min(Math.max(this.pickerY, minY), Math.max(minY, maxY));
  }

  private cancelCaptureTimer(): void {
    if (this.captureTimer >= 0) {
      clearTimeout(this.captureTimer);
      this.captureTimer = -1;
    }
  }

  private captureInfo(delay: number): void {
    this.cancelCaptureTimer();
    this.lensVisible = false;
    const seq = ++this.captureSeq;
    this.captureTimer = setTimeout(() => {
      this.captureTimer = -1;
      this.runCapture(seq);
    }, delay);
  }

  private async runCapture(seq: number): Promise<void> {
    const success = await this.imageCapture.capture();
    if (seq !== this.captureSeq) {
      return;
    }
    this.lensVisible = true;
    if (success) {
      this.showInfo();
      return;
    }
    promptAction.showToast({ message: '取色截图失败，请重试', duration: 2000 });
  }

  private getPixelScale(): number {
    const snapW = this.imageCapture.getWidth();
    if (this.areaWidth <= 0 || snapW <= 0) {
      return 1;
    }
    return snapW / this.areaWidth;
  }

  private toMapX(viewX: number): number {
    return this.imageCapture.getOriginX() + Math.round(viewX * this.getPixelScale());
  }

  private toMapY(viewY: number): number {
    const scale = this.getPixelScale();
    const extra = this.imageCapture.getHeight() - this.areaHeight * scale;
    // extra≈0：浮层已经和截图对齐，不能再加状态栏，否则会偏下。
    // extra>0：截图更高。全加到顶部会偏下，完全不补会偏上；顶部最多吃 3/4，剩余留给底栏/比例差。
    let top = 0;
    if (extra > 2) {
      const originY = Math.max(0, this.imageCapture.getOriginY());
      const bottom = Math.max(0, this.imageCapture.getBottomInset());
      let insetTop = extra - bottom;
      if (insetTop < 0) {
        insetTop = extra;
      }
      top = Math.min(insetTop, extra * 0.75);
      if (originY > extra * 0.5) {
        top = Math.min(top, originY);
      }
    }
    return Math.round(top + viewY * scale);
  }

  private getPickSize(): number {
    const mappedLens = Math.max(1, this.toMapX(this.viewSize) - this.toMapX(0));
    const pickSize = Math.round(mappedLens / ColorPickConstants.PIX_INTERVAL);
    if (pickSize < 2) {
      return ColorPickConstants.PICK_AREA_SIZE;
    }
    return pickSize;
  }

  private showInfo(): void {
    const pickSize = this.getPickSize();
    const startX = this.toMapX(this.pickerX + this.viewSize / 2) - Math.floor(pickSize / 2);
    const startY = this.toMapY(this.pickerY + this.viewSize / 2) - Math.floor(pickSize / 2);
    const patch = this.imageCapture.getPartPatch(startX, startY, pickSize);
    if (patch === null) {
      return;
    }
    this.applyPatch(patch);
  }

  private applyPatch(patch: ColorPickerPatch): void {
    const oldMap = this.partPixelMap;
    this.partPixelMap = patch.pixelMap;
    if (oldMap !== undefined) {
      oldMap.release().then(() => {
      }).catch(() => {
      });
    }
    this.colorInt = patch.colorInt;
    const patchWidth = this.readPatchSize(patch);
    this.infoText = `${ColorUtil.parseColorInt(patch.colorInt)}   ${patch.startX + Math.floor(patchWidth / 2)},${
      patch.startY + Math.floor(patchWidth / 2)}`;
    this.gridVersion++;
  }

  private readPatchSize(patch: ColorPickerPatch): number {
    try {
      return patch.pixelMap.getImageInfoSync().size.width;
    } catch (_err) {
      return ColorPickConstants.PICK_AREA_SIZE;
    }
  }

  private releasePartPixelMap(): void {
    const oldMap = this.partPixelMap;
    this.partPixelMap = undefined;
    if (oldMap !== undefined) {
      oldMap.release().then(() => {
      }).catch(() => {
      });
    }
  }

  private onPanStart(): void {
    this.dragStartX = this.pickerX;
    this.dragStartY = this.pickerY;
    this.captureInfo(100);
  }

  private onPanMove(event?: GestureEvent): void {
    if (!event) {
      return;
    }
    this.pickerX = this.dragStartX + event.offsetX;
    this.pickerY = this.dragStartY + event.offsetY;
    this.clampPosition();
    this.showInfo();
  }

  private onClose(): void {
    ColorPickManager.getInstance().close();
  }

  private getRingColor(): string {
    return ColorUtil.toHexColor(this.colorInt);
  }

  private getFocusColor(): string {
    return ColorUtil.isColdColor(this.colorInt) ? '#FFFFFF' : '#000000';
  }

  private getInfoBarTop(): number {
    return Math.max(16, this.areaHeight - 95);
  }

  build() {
    Stack() {
      Stack() {
        if (this.partPixelMap !== undefined) {
          Image(this.partPixelMap)
            .width('100%')
            .height('100%')
            .objectFit(ImageFit.Fill)
            .interpolation(ImageInterpolation.None)
            .borderRadius(this.viewSize / 2)
            .clip(true)
        } else {
          Circle()
            .width('100%')
            .height('100%')
            .fill('#66000000')
        }

        ColorPickerGrid({
          viewSize: this.viewSize,
          cellSize: this.cellSize,
          version: this.gridVersion
        })

        Column()
          .width(this.cellSize)
          .height(this.cellSize)
          .border({
            width: 1.5,
            color: this.getFocusColor()
          })
          .hitTestBehavior(HitTestMode.None)

        ColorPickerRingText({
          viewSize: this.viewSize + 1,//去掉毛边
          ringWidth: this.ringWidth,
          infoText: this.infoText,
          textColor: this.getFocusColor(),
          ringColor: this.getRingColor()
        })
      }
      .width(this.viewSize)
      .height(this.viewSize)
      .alignContent(Alignment.Center)
      .clip(false)
      .position({ x: this.pickerX, y: this.pickerY })
      .visibility(this.lensVisible ? Visibility.Visible : Visibility.Hidden)
      .hitTestBehavior(HitTestMode.Default)
      .gesture(
        PanGesture({ direction: PanDirection.All, fingers: 1, distance: 1 })
          .onActionStart(() => {
            this.onPanStart();
          })
          .onActionUpdate((event?: GestureEvent) => {
            this.onPanMove(event);
          })
      )

      Row() {
        Column()
          .width(14)
          .height(14)
          .backgroundColor(this.getRingColor())
          .margin({ left: 16, right: 10 })

        Text(this.infoText)
          .fontSize(14)
          .fontColor('#333333')
          .layoutWeight(1)

        Text('✕')
          .fontSize(18)
          .fontColor('#666666')
          .padding({ left: 8, right: 15 })
          .onClick(() => {
            this.onClose();
          })
      }
      .width(this.areaWidth > 30 ? this.areaWidth - 30 : 300)
      .height(50)
      .backgroundColor('#F2FFFFFF')
      .borderRadius(8)
      .shadow({ radius: 10, color: '#1A000000', offsetX: 0, offsetY: 2 })
      .alignItems(VerticalAlign.Center)
      .position({ x: 15, y: this.getInfoBarTop() })
      .hitTestBehavior(HitTestMode.Default)
    }
    .width('100%')
    .height('100%')
    .hitTestBehavior(HitTestMode.Transparent)
    .onAreaChange((_oldValue: Area, newValue: Area) => {
      this.areaWidth = Number(newValue.width);
      this.areaHeight = Number(newValue.height);
      this.initPosition();
    })
  }
}

@Component
struct ColorPickerGrid {
  viewSize: number = 0;
  cellSize: number = 16;
  @Prop @Watch('onVersionChange') version: number = 0;
  private readonly settings: RenderingContextSettings = new RenderingContextSettings(true);
  private readonly context: CanvasRenderingContext2D = new CanvasRenderingContext2D(this.settings);

  aboutToAppear(): void {
    this.drawGrid();
  }

  private onVersionChange(): void {
    this.drawGrid();
  }

  private drawGrid(): void {
    const ctx = this.context;
    const size = this.viewSize;
    if (size <= 0 || this.cellSize <= 0) {
      return;
    }
    ctx.clearRect(0, 0, size, size);
    ctx.beginPath();
    for (let i = 0; i <= size; i += this.cellSize) {
      ctx.moveTo(i, 0);
      ctx.lineTo(i, size);
      ctx.moveTo(0, i);
      ctx.lineTo(size, i);
    }
    ctx.lineWidth = 0.6;
    ctx.strokeStyle = '#55CCCCCC';
    ctx.stroke();
  }

  build() {
    Canvas(this.context)
      .width(this.viewSize)
      .height(this.viewSize)
      .borderRadius(this.viewSize / 2)
      .clip(true)
      .hitTestBehavior(HitTestMode.None)
      .onReady(() => {
        this.drawGrid();
      })
  }
}

/**
 * 色环 + 顶部弧线色值画在同一张 Canvas 上，避免 vp/px 混用导致字和边框错位。
 * 对标 Android ColorPickerView.drawRing / drawTextOnPath
 */
@Component
struct ColorPickerRingText {
  viewSize: number = 0;
  ringWidth: number = 0;
  @Prop @Watch('onRingTextChange') infoText: string = '';
  @Prop @Watch('onRingTextChange') textColor: string = '#000000';
  @Prop @Watch('onRingTextChange') ringColor: string = '#FFFFFF';
  private readonly settings: RenderingContextSettings = new RenderingContextSettings(true);
  private readonly context: CanvasRenderingContext2D = new CanvasRenderingContext2D(this.settings);

  aboutToAppear(): void {
    this.drawRingText();
  }

  private onRingTextChange(): void {
    this.drawRingText();
  }

  private getCanvasSize(): number {
    if (this.context.width > 0) {
      return this.context.width;
    }
    return vp2px(this.viewSize);
  }

  private getRingWidth(size: number): number {
    if (this.viewSize <= 0) {
      return 0;
    }
    return size * this.ringWidth / this.viewSize;
  }

  private drawRingText(): void {
    const ctx = this.context;
    const size = this.getCanvasSize();
    if (size <= 0) {
      return;
    }
    ctx.clearRect(0, 0, size, size);
    const ringW = this.getRingWidth(size);
    const cx = size / 2;
    const cy = size / 2;
    const outerR = size / 2 - 0.5;
    const innerR = Math.max(1, outerR - ringW);
    const midR = (outerR + innerR) / 2;
    this.drawRing(ctx, cx, cy, outerR, innerR, midR, ringW);
    this.drawArcText(ctx, cx, cy, midR, size);
  }

  private drawRing(ctx: CanvasRenderingContext2D, cx: number, cy: number, outerR: number,
    innerR: number, midR: number, ringW: number): void {
    ctx.beginPath();
    ctx.arc(cx, cy, midR, 0, Math.PI * 2);
    ctx.lineWidth = ringW;
    ctx.strokeStyle = this.ringColor;
    ctx.stroke();

    ctx.beginPath();
    ctx.arc(cx, cy, outerR, 0, Math.PI * 2);
    ctx.lineWidth = 0.2;
    ctx.strokeStyle = '#333333';
    ctx.stroke();

    ctx.beginPath();
    ctx.arc(cx, cy, innerR, 0, Math.PI * 2);
    ctx.stroke();
  }

  private drawArcText(ctx: CanvasRenderingContext2D, cx: number, cy: number, radius: number,
    size: number): void {
    const text = this.infoText;
    if (text.length === 0 || this.viewSize <= 0 || radius <= 0) {
      return;
    }
    const fontPx = Math.max(24, Math.round(size * 28 / this.viewSize));
    ctx.font = `${fontPx}px monospace`;
    ctx.fillStyle = this.textColor;
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    const count = text.length;
    const widths: number[] = [];
    let totalWidth = 0;
    for (let i = 0; i < count; i++) {
      const width = ctx.measureText(text.charAt(i)).width;
      widths.push(width);
      totalWidth += width;
    }
    let arcPos = -totalWidth / 2;
    for (let i = 0; i < count; i++) {
      const charWidth = widths[i];
      const angle = -Math.PI / 2 + (arcPos + charWidth / 2) / radius;
      ctx.save();
      ctx.translate(cx + radius * Math.cos(angle), cy + radius * Math.sin(angle));
      ctx.rotate(angle + Math.PI / 2);
      ctx.fillText(text.charAt(i), 0, 0);
      ctx.restore();
      arcPos += charWidth;
    }
  }

  build() {
    Canvas(this.context)
      .width(this.viewSize)
      .height(this.viewSize)
      .hitTestBehavior(HitTestMode.None)
      .onReady(() => {
        this.drawRingText();
      })
  }
}
