import { image } from '@kit.ImageKit';

/**
 * 颜色工具，对标 Android util/ColorUtil
 */
export class ColorUtil {
  static parseColorInt(color: number): string {
    const rgb = color & 0xFFFFFF;
    let hex = rgb.toString(16).toUpperCase();
    while (hex.length < 6) {
      hex = `0${hex}`;
    }
    return `#${hex}`;
  }

  static packArgb(alpha: number, red: number, green: number, blue: number): number {
    return ((alpha & 0xFF) << 24) | ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | (blue & 0xFF);
  }

  static isColdColor(color: number): boolean {
    const red = (color >> 16) & 0xFF;
    const green = (color >> 8) & 0xFF;
    const blue = color & 0xFF;
    const max = Math.max(red, green, blue) / 255;
    return max <= 0.8;
  }

  static packFromBytes(pixels: Uint8Array, offset: number, pixelFormat: image.PixelMapFormat): number {
    if (offset + 2 >= pixels.length) {
      return 0xFF000000;
    }
    if (pixelFormat === image.PixelMapFormat.RGB_565) {
      const value = pixels[offset] | (pixels[offset + 1] << 8);
      const red = ((value >> 11) & 0x1F) * 255 / 31;
      const green = ((value >> 5) & 0x3F) * 255 / 63;
      const blue = (value & 0x1F) * 255 / 31;
      return ColorUtil.packArgb(255, Math.round(red), Math.round(green), Math.round(blue));
    }
    if (offset + 3 >= pixels.length) {
      return 0xFF000000;
    }
    if (pixelFormat === image.PixelMapFormat.BGRA_8888) {
      return ColorUtil.packArgb(pixels[offset + 3], pixels[offset + 2], pixels[offset + 1], pixels[offset]);
    }
    return ColorUtil.packArgb(pixels[offset + 3], pixels[offset], pixels[offset + 1], pixels[offset + 2]);
  }

  static toHexColor(color: number): string {
    let hex = (color >>> 0).toString(16).toUpperCase();
    while (hex.length < 8) {
      hex = `0${hex}`;
    }
    return `#${hex}`;
  }
}
