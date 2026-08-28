import { GlobalConfig } from '../../config/GlobalConfig';

/**
 * H5 链接校正，对标 harmonydriver H5AnyDoorPage.urlCorrectionWithUrl
 */
export class WebDoorUrlHelper {
  static correct(url: string): string {
    const value = url.trim();
    if (value.length === 0) {
      return value;
    }
    if (value.startsWith(':')) {
      return `https${value}`;
    }
    if (value.startsWith('//')) {
      return `https:${value}`;
    }
    if (value.startsWith('/')) {
      const baseUrl = GlobalConfig.h5BaseUrl;
      if (baseUrl.length > 0) {
        const normalizedBase = baseUrl.endsWith('/')
          ? baseUrl.substring(0, baseUrl.length - 1)
          : baseUrl;
        return `${normalizedBase}${value}`;
      }
      return value;
    }
    if (value.indexOf('://') < 0) {
      return `https://${value}`;
    }
    return value;
  }

  static isValid(url: string): boolean {
    if (url.trim().length === 0) {
      return false;
    }
    return url.search(/\s/) < 0;
  }
}
