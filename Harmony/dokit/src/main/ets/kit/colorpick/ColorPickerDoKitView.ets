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

  private toMapX(viewX: number): number {
    if (this.areaWidth <= 0 || this.imageCapture.getWidth() <= 0) {
      return Math.round(vp2px(viewX));
    }
    return Math.round(viewX / this.areaWidth * this.imageCapture.getWidth());
  }

  private toMapY(viewY: number): number {
    if (this.areaHeight <= 0 || this.imageCapture.getHeight() <= 0) {
      return Math.round(vp2px(viewY));
    }
    return Math.round(viewY / this.areaHeight * this.imageCapture.getHeight());
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

        Column()
          .width('100%')
          .height('100%')
          .borderRadius(this.viewSize / 2)
          .border({
            width: this.ringWidth,
            color: this.getRingColor()
          })
          .hitTestBehavior(HitTestMode.None)

        Text(this.infoText)
          .width(this.viewSize)
          .fontSize(10)
          .fontColor(this.getFocusColor())
          .textAlign(TextAlign.Center)
          .position({ x: 0, y: this.ringWidth / 2 - 6 })
          .hitTestBehavior(HitTestMode.None)
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
