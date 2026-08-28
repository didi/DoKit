import { KitItem } from '../model/KitModels';

/** 已实现工具的内置 KitId */
export class KitIds {
  static readonly APP_INFO: string = 'dokit_sdk_comm_ck_appinfo';
  static readonly H5_DOOR: string = 'dokit_sdk_comm_ck_h5';
  static readonly SANDBOX: string = 'dokit_sdk_comm_ck_sandbox';
  static readonly THIRD_LIB_INFO: string = 'dokit_sdk_comm_ck_thirdlibinfo';
  static readonly DEV_PAGE: string = 'dokit_sdk_comm_ck_devpage';
  static readonly DATA_CLEAN: string = 'dokit_sdk_comm_ck_cache';
}

/** 工具点击后的路由动作 */
export enum KitRouteAction {
  OPEN_APP_INFO = 0,
  OPEN_H5_DOOR = 1,
  OPEN_SANDBOX = 2,
  OPEN_THIRD_LIB_INFO = 3,
  OPEN_DEV_PAGE = 4,
  OPEN_DATA_CLEAN = 5,
  SHOW_DEV_TIP = 6
}

/**
 * 工具点击路由，对标 Android 各 Kit.onClickWithReturn
 */
export class KitRouter {
  static isImplemented(innerKitId: string): boolean {
    return innerKitId === KitIds.APP_INFO
      || innerKitId === KitIds.H5_DOOR
      || innerKitId === KitIds.SANDBOX
      || innerKitId === KitIds.THIRD_LIB_INFO
      || innerKitId === KitIds.DEV_PAGE
      || innerKitId === KitIds.DATA_CLEAN;
  }

  static resolveAction(item: KitItem): KitRouteAction {
    if (item.innerKitId === KitIds.APP_INFO) {
      return KitRouteAction.OPEN_APP_INFO;
    }
    if (item.innerKitId === KitIds.H5_DOOR) {
      return KitRouteAction.OPEN_H5_DOOR;
    }
    if (item.innerKitId === KitIds.SANDBOX) {
      return KitRouteAction.OPEN_SANDBOX;
    }
    if (item.innerKitId === KitIds.THIRD_LIB_INFO) {
      return KitRouteAction.OPEN_THIRD_LIB_INFO;
    }
    if (item.innerKitId === KitIds.DEV_PAGE) {
      return KitRouteAction.OPEN_DEV_PAGE;
    }
    if (item.innerKitId === KitIds.DATA_CLEAN) {
      return KitRouteAction.OPEN_DATA_CLEAN;
    }
    return KitRouteAction.SHOW_DEV_TIP;
  }
}
