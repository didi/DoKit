import { abilityAccessCtrl, bundleManager, Permissions } from '@kit.AbilityKit';
import { deviceInfo } from '@kit.BasicServicesKit';
import { statfs } from '@kit.CoreFileKit';
import { display } from '@kit.ArkUI';
import { DoKitManager } from '../../core/DoKitManager';
import { SysInfoListItem } from './SysInfoModels';

/**
 * 采集 App / 设备 / 权限信息，对标 Android SysInfoFragment
 */
export class SysInfoCollector {
  private static readonly PERM_LOCATION_APPROX: Permissions = 'ohos.permission.APPROXIMATELY_LOCATION';
  private static readonly PERM_LOCATION: Permissions = 'ohos.permission.LOCATION';
  private static readonly PERM_READ_MEDIA: Permissions = 'ohos.permission.READ_MEDIA';
  private static readonly PERM_WRITE_MEDIA: Permissions = 'ohos.permission.WRITE_MEDIA';
  private static readonly PERM_CAMERA: Permissions = 'ohos.permission.CAMERA';
  private static readonly PERM_MICROPHONE: Permissions = 'ohos.permission.MICROPHONE';
  private static readonly PERM_INTERNET: Permissions = 'ohos.permission.INTERNET';
  private static readonly PERM_NETWORK_INFO: Permissions = 'ohos.permission.GET_NETWORK_INFO';

  static collect(): SysInfoListItem[] {
    const items: SysInfoListItem[] = [];
    SysInfoCollector.addAppInfo(items);
    SysInfoCollector.addDeviceInfo(items);
    SysInfoCollector.addPermissionInfo(items);
    return items;
  }

  private static addAppInfo(items: SysInfoListItem[]): void {
    items.push(SysInfoListItem.title('App信息'));
    try {
      const bundleInfo = bundleManager.getBundleInfoForSelfSync(
        bundleManager.BundleFlag.GET_BUNDLE_INFO_WITH_APPLICATION
      );
      items.push(SysInfoListItem.info('包名', bundleInfo.name));
      items.push(SysInfoListItem.info('应用版本名', bundleInfo.versionName ?? '-'));
      items.push(SysInfoListItem.info('应用版本号', String(bundleInfo.versionCode ?? '-')));
      items.push(SysInfoListItem.info('最低系统版本号', String(bundleInfo.minCompatibleVersionCode ?? '-')));
      items.push(SysInfoListItem.info('目标系统版本号', String(bundleInfo.targetVersion ?? '-')));
    } catch (err) {
      items.push(SysInfoListItem.info('包名', '-'));
      items.push(SysInfoListItem.info('读取失败', String(err)));
    }
  }

  private static addDeviceInfo(items: SysInfoListItem[]): void {
    items.push(SysInfoListItem.title('手机信息'));
    const brand = deviceInfo.brand ?? '';
    const model = deviceInfo.productModel ?? '';
    items.push(SysInfoListItem.info('手机型号', `${brand} ${model}`.trim() || '-'));
    const osName = deviceInfo.osFullName ?? deviceInfo.displayVersion ?? '-';
    items.push(SysInfoListItem.info('系统版本', `${osName} (${deviceInfo.sdkApiVersion ?? '-'})`));

    try {
      const context = DoKitManager.getContext();
      const path = context?.filesDir ?? '/data';
      const freeBytes = statfs.getFreeSizeSync(path);
      items.push(SysInfoListItem.info('应用存储剩余空间', SysInfoCollector.formatSize(freeBytes)));
    } catch (_err) {
      items.push(SysInfoListItem.info('应用存储剩余空间', '-'));
    }

    try {
      const displayInfo = display.getDefaultDisplaySync();
      items.push(SysInfoListItem.info('分辨率', `${displayInfo.width}x${displayInfo.height}`));
      items.push(SysInfoListItem.info('DENSITY', String(displayInfo.densityPixels ?? '-')));
      const inch = SysInfoCollector.calcScreenInch(displayInfo.width, displayInfo.height, displayInfo.densityPixels);
      items.push(SysInfoListItem.info('屏幕尺寸', inch > 0 ? `${inch.toFixed(2)} inch` : '-'));
    } catch (_err) {
      items.push(SysInfoListItem.info('分辨率', '-'));
    }
  }

  private static addPermissionInfo(items: SysInfoListItem[]): void {
    items.push(SysInfoListItem.title('权限信息'));
    items.push(SysInfoListItem.info(
      '地理位置权限',
      SysInfoCollector.checkAnyPermission([
        SysInfoCollector.PERM_LOCATION_APPROX,
        SysInfoCollector.PERM_LOCATION
      ]),
      true
    ));
    items.push(SysInfoListItem.info(
      '磁盘权限',
      SysInfoCollector.checkAllPermissions([
        SysInfoCollector.PERM_READ_MEDIA,
        SysInfoCollector.PERM_WRITE_MEDIA
      ]),
      true
    ));
    items.push(SysInfoListItem.info(
      '拍照权限',
      SysInfoCollector.checkSinglePermission(SysInfoCollector.PERM_CAMERA),
      true
    ));
    items.push(SysInfoListItem.info(
      '麦克风权限',
      SysInfoCollector.checkSinglePermission(SysInfoCollector.PERM_MICROPHONE),
      true
    ));
    items.push(SysInfoListItem.info(
      '网络权限',
      SysInfoCollector.checkSinglePermission(SysInfoCollector.PERM_INTERNET),
      true
    ));
    items.push(SysInfoListItem.info(
      '网络状态权限',
      SysInfoCollector.checkSinglePermission(SysInfoCollector.PERM_NETWORK_INFO),
      true
    ));
  }

  private static getAccessTokenId(): number {
    const bundleInfo = bundleManager.getBundleInfoForSelfSync(
      bundleManager.BundleFlag.GET_BUNDLE_INFO_WITH_APPLICATION
    );
    return bundleInfo.appInfo.accessTokenId;
  }

  private static checkSinglePermission(permission: Permissions): string {
    try {
      const atManager = abilityAccessCtrl.createAtManager();
      const status = atManager.checkAccessTokenSync(SysInfoCollector.getAccessTokenId(), permission);
      return status === abilityAccessCtrl.GrantStatus.PERMISSION_GRANTED ? 'YES' : 'NO';
    } catch (_err) {
      return 'NO';
    }
  }

  private static checkAnyPermission(permissions: Permissions[]): string {
    for (let i = 0; i < permissions.length; i++) {
      if (SysInfoCollector.checkSinglePermission(permissions[i]) === 'YES') {
        return 'YES';
      }
    }
    return 'NO';
  }

  private static checkAllPermissions(permissions: Permissions[]): string {
    for (let i = 0; i < permissions.length; i++) {
      if (SysInfoCollector.checkSinglePermission(permissions[i]) === 'NO') {
        return 'NO';
      }
    }
    return permissions.length > 0 ? 'YES' : 'NO';
  }

  private static formatSize(bytes: number): string {
    if (bytes <= 0) {
      return '0B';
    }
    const units: string[] = ['B', 'KB', 'MB', 'GB', 'TB'];
    let value = bytes;
    let unitIndex = 0;
    while (value >= 1024 && unitIndex < units.length - 1) {
      value /= 1024;
      unitIndex++;
    }
    return `${value.toFixed(unitIndex === 0 ? 0 : 2)}${units[unitIndex]}`;
  }

  private static calcScreenInch(widthPx: number, heightPx: number, density: number): number {
    if (widthPx <= 0 || heightPx <= 0 || density <= 0) {
      return 0;
    }
    const dpi = density * 160;
    const widthInch = widthPx / dpi;
    const heightInch = heightPx / dpi;
    return Math.sqrt(widthInch * widthInch + heightInch * heightInch);
  }
}
