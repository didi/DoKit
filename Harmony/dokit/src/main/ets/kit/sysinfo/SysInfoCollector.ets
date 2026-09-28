import { abilityAccessCtrl, bundleManager, Permissions } from '@kit.AbilityKit';
import { deviceInfo } from '@kit.BasicServicesKit';
import { Environment, fileIo as fs, statfs } from '@kit.CoreFileKit';
import { display } from '@kit.ArkUI';
import { connection } from '@kit.NetworkKit';
import { wifiManager } from '@kit.ConnectivityKit';
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
  private static readonly PERM_TELEPHONY: Permissions = 'ohos.permission.GET_TELEPHONY_STATE';
  private static readonly PERM_CONTACTS: Permissions = 'ohos.permission.READ_CONTACTS';

  static async collect(): Promise<SysInfoListItem[]> {
    const items: SysInfoListItem[] = [];
    SysInfoCollector.addAppInfo(items);
    await SysInfoCollector.addDeviceInfo(items);
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

  private static async addDeviceInfo(items: SysInfoListItem[]): Promise<void> {
    items.push(SysInfoListItem.title('手机信息'));
    const brand = deviceInfo.brand ?? '';
    const model = deviceInfo.productModel ?? '';
    items.push(SysInfoListItem.info('手机型号', `${brand} ${model}`.trim() || '-'));
    const osName = deviceInfo.osFullName ?? deviceInfo.displayVersion ?? '-';
    items.push(SysInfoListItem.info('系统版本', `${osName} (${deviceInfo.sdkApiVersion ?? '-'})`));
    items.push(SysInfoListItem.info('sd卡剩余空间', SysInfoCollector.readSdSpace()));
    items.push(SysInfoListItem.info('系统剩余空间', SysInfoCollector.readRomSpace()));

    try {
      const displayInfo = display.getDefaultDisplaySync();
      items.push(SysInfoListItem.info('分辨率', `${displayInfo.width}x${displayInfo.height}`));
      items.push(SysInfoListItem.info('屏幕尺寸', SysInfoCollector.formatInch(
        SysInfoCollector.calcScreenInch(displayInfo.width, displayInfo.height, displayInfo.densityPixels)
      )));
      items.push(SysInfoListItem.info('ROOT', String(SysInfoCollector.isDeviceRooted())));
      items.push(SysInfoListItem.info('DENSITY', String(displayInfo.densityPixels ?? '-')));
    } catch (_err) {
      items.push(SysInfoListItem.info('分辨率', '-'));
      items.push(SysInfoListItem.info('屏幕尺寸', '-'));
      items.push(SysInfoListItem.info('ROOT', String(SysInfoCollector.isDeviceRooted())));
      items.push(SysInfoListItem.info('DENSITY', '-'));
    }

    items.push(SysInfoListItem.info('IP', await SysInfoCollector.readIpAddress()));
    items.push(SysInfoListItem.info('Mac', await SysInfoCollector.readMacAddress()));
    items.push(SysInfoListItem.info('IMEI', 'null'));
    items.push(SysInfoListItem.info('WebView', 'null'));
  }

  private static addPermissionInfo(items: SysInfoListItem[]): void {
    items.push(SysInfoListItem.title('权限信息'));
    items.push(SysInfoListItem.info(
      '地理位置权限',
      SysInfoCollector.checkAllPermissions([
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
      '设备信息权限',
      SysInfoCollector.checkSinglePermission(SysInfoCollector.PERM_TELEPHONY),
      true
    ));
    items.push(SysInfoListItem.info(
      '通讯录权限',
      SysInfoCollector.checkSinglePermission(SysInfoCollector.PERM_CONTACTS),
      true
    ));
  }

  private static readSdSpace(): string {
    try {
      return SysInfoCollector.formatFreeTotal(SysInfoCollector.resolveSdPath());
    } catch (_err) {
      return '-/-';
    }
  }

  private static readRomSpace(): string {
    try {
      const context = DoKitManager.getContext();
      return SysInfoCollector.formatFreeTotal(context !== null ? context.filesDir : '/data');
    } catch (_err) {
      return '-/-';
    }
  }

  private static resolveSdPath(): string {
    try {
      return Environment.getUserDocumentDir();
    } catch (_err) {
      const context = DoKitManager.getContext();
      return context !== null ? context.filesDir : '/data';
    }
  }

  private static formatFreeTotal(path: string): string {
    const free = statfs.getFreeSizeSync(path);
    const total = statfs.getTotalSizeSync(path);
    return `${SysInfoCollector.formatSize(free)}/${SysInfoCollector.formatSize(total)}`;
  }

  private static isDeviceRooted(): boolean {
    const paths: string[] = ['/system/bin/su', '/system/xbin/su', '/sbin/su'];
    for (let i = 0; i < paths.length; i++) {
      try {
        if (fs.accessSync(paths[i])) {
          return true;
        }
      } catch (_err) {
        // ignore missing path
      }
    }
    return false;
  }

  private static async readIpAddress(): Promise<string> {
    const netIp = SysInfoCollector.readConnectionIpv4();
    if (netIp.length > 0) {
      return netIp;
    }
    try {
      const info = wifiManager.getIpInfo();
      const ip = SysInfoCollector.intToIpv4(info.ipAddress);
      return ip.length > 0 ? ip : 'null';
    } catch (_err) {
      return 'null';
    }
  }

  private static readConnectionIpv4(): string {
    try {
      const netHandle = connection.getDefaultNetSync();
      const properties = connection.getConnectionPropertiesSync(netHandle);
      for (let i = 0; i < properties.linkAddresses.length; i++) {
        const address = properties.linkAddresses[i].address.address;
        if (address.indexOf('.') > 0 && address.indexOf(':') < 0) {
          return address;
        }
      }
    } catch (_err) {
      // GET_NETWORK_INFO 未授予时忽略
    }
    return '';
  }

  private static async readMacAddress(): Promise<string> {
    try {
      const info = await wifiManager.getLinkedInfo();
      if (info.macAddress !== undefined && info.macAddress.length > 0) {
        return info.macAddress;
      }
    } catch (_err) {
      // GET_WIFI_INFO 未授予或未连 Wi-Fi
    }
    return 'null';
  }

  private static intToIpv4(ip: number): string {
    if (ip === 0) {
      return '';
    }
    const value = ip >>> 0;
    return `${value & 255}.${(value >> 8) & 255}.${(value >> 16) & 255}.${(value >> 24) & 255}`;
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

  private static formatInch(inch: number): string {
    return inch > 0 ? `${inch.toFixed(2)}` : '-';
  }
}
