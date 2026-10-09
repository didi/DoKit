import { image } from '@kit.ImageKit';
import { window } from '@kit.ArkUI';
import { DoKitManager } from '../../core/DoKitManager';
import { ColorUtil } from '../../util/ColorUtil';

/**
 * 窗口截图像素块，对标 Android ImageCapture.getPartBitmap
 */
export class ColorPickerPatch {
  pixelMap: image.PixelMap;
  colorInt: number;
  startX: number;
  startY: number;

  constructor(pixelMap: image.PixelMap, colorInt: number, startX: number, startY: number) {
    this.pixelMap = pixelMap;
    this.colorInt = colorInt;
    this.startX = startX;
    this.startY = startY;
  }
}

/**
 * 取色截图，对标 Android kit/colorpick/ImageCapture
 *
 * 鸿蒙公开 API 12 使用 window.snapshot() 截取当前应用窗口，
 * 不依赖 MediaProjection / CUSTOM_SCREEN_CAPTURE。
 */
export class ImageCapture {
  private pixelMap: image.PixelMap | null = null;
  private mapWidth: number = 0;
  private mapHeight: number = 0;
  private originX: number = 0;
  private originY: number = 0;
  private contentWidth: number = 0;
  private contentHeight: number = 0;
  private bottomInset: number = 0;
  private capturing: boolean = false;
  private pixelFormat: image.PixelMapFormat = image.PixelMapFormat.RGBA_8888;

  async capture(): Promise<boolean> {
    if (this.capturing) {
      return this.pixelMap !== null;
    }
    const context = DoKitManager.getContext();
    if (context === null) {
      return false;
    }
    this.capturing = true;
    try {
      const win = await window.getLastWindow(context);
      const snapshot = await win.snapshot();
      this.replacePixelMap(snapshot);
      const info = snapshot.getImageInfoSync();
      this.mapWidth = info.size.width;
      this.mapHeight = info.size.height;
      this.applySnapshotLayout(win, info.size.width, info.size.height);
      // snapshot.readPixels 在 API 12 上是 BGRA 字节序；ImageInfo 常报 RGBA，
      // 按 RGBA 解会把蓝色 (#337CC4) 读成橙色 (#C47C33)。
      this.pixelFormat = ImageCapture.resolveBufferFormat(info.pixelFormat);
      return true;
    } catch (_err) {
      return false;
    } finally {
      this.capturing = false;
    }
  }

  getPartPatch(startX: number, startY: number, size: number): ColorPickerPatch | null {
    if (this.pixelMap === null || this.mapWidth <= 0 || this.mapHeight <= 0 || size <= 0) {
      return null;
    }
    let x = Math.floor(startX);
    let y = Math.floor(startY);
    if (x < 0) {
      x = 0;
    }
    if (y < 0) {
      y = 0;
    }
    if (x + size > this.mapWidth) {
      x = this.mapWidth - size;
    }
    if (y + size > this.mapHeight) {
      y = this.mapHeight - size;
    }
    if (x < 0 || y < 0) {
      return null;
    }
    try {
      const bytesPerPixel = this.getBytesPerPixel();
      const buffer = new ArrayBuffer(size * size * bytesPerPixel);
      const area: image.PositionArea = {
        pixels: buffer,
        offset: 0,
        stride: size * bytesPerPixel,
        region: {
          x: x,
          y: y,
          size: {
            width: size,
            height: size
          }
        }
      };
      this.pixelMap.readPixelsSync(area);
      const partMap = image.createPixelMapSync(buffer, {
        size: {
          width: size,
          height: size
        },
        srcPixelFormat: this.pixelFormat,
        pixelFormat: this.pixelFormat
      });
      const colorInt = this.readCenterColor(new Uint8Array(buffer), size, bytesPerPixel);
      return new ColorPickerPatch(partMap, colorInt, x, y);
    } catch (_err) {
      return null;
    }
  }

  getWidth(): number {
    return this.mapWidth;
  }

  getHeight(): number {
    return this.mapHeight;
  }

  getOriginX(): number {
    return this.originX;
  }

  getOriginY(): number {
    return this.originY;
  }

  getContentWidth(): number {
    if (this.contentWidth > 0) {
      return this.contentWidth;
    }
    return this.mapWidth;
  }

  getContentHeight(): number {
    if (this.contentHeight > 0) {
      return this.contentHeight;
    }
    return this.mapHeight;
  }

  getBottomInset(): number {
    return this.bottomInset;
  }

  destroy(): void {
    this.replacePixelMap(null);
    this.mapWidth = 0;
    this.mapHeight = 0;
    this.originX = 0;
    this.originY = 0;
    this.contentWidth = 0;
    this.contentHeight = 0;
    this.bottomInset = 0;
  }

  private replacePixelMap(next: image.PixelMap | null): void {
    const prev = this.pixelMap;
    this.pixelMap = next;
    if (prev !== null) {
      prev.release().then(() => {
      }).catch(() => {
      });
    }
  }

  /**
   * 记录截图相对内容区的顶/底 inset。
   * 浮层宽度通常能对上截图；高度差来自状态栏、底部指示条和宽高比，不能全部算到顶部。
   */
  private applySnapshotLayout(win: window.Window, snapW: number, snapH: number): void {
    this.originX = 0;
    this.originY = 0;
    this.contentWidth = snapW;
    this.contentHeight = snapH;
    this.bottomInset = 0;
    try {
      const props = win.getWindowProperties();
      const draw = props.drawableRect;
      const winRect = props.windowRect;
      const scaleY = snapH / Math.max(1, winRect.height);
      const scaleX = snapW / Math.max(1, winRect.width);
      const sys = win.getWindowAvoidArea(window.AvoidAreaType.TYPE_SYSTEM);
      const nav = win.getWindowAvoidArea(window.AvoidAreaType.TYPE_NAVIGATION_INDICATOR);
      const drawTop = Math.round(draw.top * scaleY);
      const sysTop = Math.round(sys.topRect.height * scaleY);
      // drawableRect.top 是内容区相对窗口的偏移；与系统状态栏取较大值，避免漏补。
      this.originX = Math.round(draw.left * scaleX);
      this.originY = Math.max(0, drawTop, sysTop);
      const bottomFromDraw = Math.round((winRect.height - draw.top - draw.height) * scaleY);
      const sysBottom = Math.round(sys.bottomRect.height * scaleY);
      const navBottom = Math.round(nav.bottomRect.height * scaleY);
      this.bottomInset = Math.max(0, sysBottom, navBottom, bottomFromDraw);
      this.contentWidth = Math.max(1, snapW - this.originX);
      this.contentHeight = Math.max(1, snapH - this.originY - this.bottomInset);
    } catch (_err) {
      this.originX = 0;
      this.originY = 0;
      this.bottomInset = 0;
      this.contentWidth = snapW;
      this.contentHeight = snapH;
    }
  }

  private static resolveBufferFormat(reported: image.PixelMapFormat): image.PixelMapFormat {
    if (reported === image.PixelMapFormat.RGB_565) {
      return reported;
    }
    return image.PixelMapFormat.BGRA_8888;
  }

  private getBytesPerPixel(): number {
    if (this.pixelFormat === image.PixelMapFormat.RGB_565) {
      return 2;
    }
    return 4;
  }

  private readCenterColor(pixels: Uint8Array, size: number, bytesPerPixel: number): number {
    const center = Math.floor(size / 2);
    const offset = (center * size + center) * bytesPerPixel;
    return ColorUtil.packFromBytes(pixels, offset, this.pixelFormat);
  }
}
