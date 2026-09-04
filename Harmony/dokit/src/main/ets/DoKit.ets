import { common } from '@kit.AbilityKit';
import { window } from '@kit.ArkUI';
import { DokitThirdLibInfo } from './aop/DokitThirdLibInfo';
import { DoKitCallBack } from './DoKitCallBack';
import { DoKitReal } from './DoKitReal';
import { GlobalConfig } from './config/GlobalConfig';
import { DoKitConstants } from './constant/DoKitConstants';
import { WebDoorCallback } from './kit/webdoor/WebDoorCallback';
import { AbstractKit } from './kit/AbstractKit';
import { DEFAULT_BIZ_GROUP_NAME } from './kit/KitItemHelper';
import { DoKitManager } from './core/DoKitManager';
import { DoKitViewManager, ViewConfig } from './core/DoKitViewManager';
import { DoKitBundle, SystemFragmentBuilder } from './core/FragmentRenderer';
import { DoKitViewLaunchMode } from './core/DoKitIntent';
import { SystemFragmentRegistry } from './core/SystemFragmentRigistry';
import { DoKitLauncher } from './core/DoKitLauncher';
import { DoKitOverlayState } from './model/DoKitOverlayState';
import { CustomViewBuilder, CustomViewRegistry, DoKitParams } from './core/CustomViewRegistry';

export { DoKitConstants } from './constant/DoKitConstants';

export class DoKitBuilder {
  private context: common.UIAbilityContext;
  private mProductId: string = '';
  private mDebugMode: boolean = true;
  private mCustomKits: Map<string, AbstractKit[]> = new Map();
  private mCustomKitList: AbstractKit[] = [];
  private mDisableUpload: boolean = false;
  private mAlwaysShowMainIcon: boolean = true;
  private mFileManagerHttpPort: number = 8089;
  private mMcWsPort: number = 4444;
  private mDatabasePass: Map<string, string> = new Map();
  private mCallBack: DoKitCallBack | null = null;
  private mWebDoorCallback: WebDoorCallback | null = null;
  private mH5BaseUrl: string = '';
  private mThirdLibInfos: Map<string, string> = new Map();
  private mThirdLibRawFile: string = '';

  constructor(context: common.UIAbilityContext) {
    this.context = context;
  }

  productId(id: string): DoKitBuilder {
    this.mProductId = id;
    return this;
  }

  debug(isDebug: boolean): DoKitBuilder {
    this.mDebugMode = isDebug;
    return this;
  }

  /** @deprecated Overlay 方案不再需要 windowStage，保留仅为 API 兼容 */
  windowStage(_stage: window.WindowStage): DoKitBuilder {
    return this;
  }

  /** 注入业务 Kit 分组，对标 Android customKits(mapKits) */
  customKits(kits: Map<string, AbstractKit[]>): DoKitBuilder {
    this.mCustomKits = kits;
    return this;
  }

  /** 注入业务 Kit 列表，对标 Android customKits(listKits)，默认归入「业务专区」 */
  customKitList(kits: AbstractKit[]): DoKitBuilder {
    this.mCustomKitList = kits;
    return this;
  }

  disableUpload(): DoKitBuilder {
    this.mDisableUpload = true;
    return this;
  }

  alwaysShowMainIcon(alwaysShow: boolean): DoKitBuilder {
    this.mAlwaysShowMainIcon = alwaysShow;
    return this;
  }

  fileManagerHttpPort(port: number): DoKitBuilder {
    this.mFileManagerHttpPort = port;
    return this;
  }

  mcWSPort(port: number): DoKitBuilder {
    this.mMcWsPort = port;
    return this;
  }

  databasePass(map: Map<string, string>): DoKitBuilder {
    this.mDatabasePass = map;
    return this;
  }

  callBack(callback: DoKitCallBack): DoKitBuilder {
    this.mCallBack = callback;
    return this;
  }

  /** H5任意门全局回调，对标 Android DoKit.Builder.webDoorCallback */
  webDoorCallback(callback: WebDoorCallback): DoKitBuilder {
    this.mWebDoorCallback = callback;
    return this;
  }

  /** H5相对路径前缀，如 https://example.com */
  h5BaseUrl(url: string): DoKitBuilder {
    this.mH5BaseUrl = url;
    return this;
  }

  /** 注入三方库信息，key 为库名（含版本），value 为体积字节数字符串 */
  thirdLibInfos(infos: Map<string, string>): DoKitBuilder {
    this.mThirdLibInfos = infos;
    return this;
  }

  /** 从宿主 rawfile 加载三方库 JSON，对标 Android Gradle 插件编译期注入 */
  thirdLibRawFile(fileName: string): DoKitBuilder {
    this.mThirdLibRawFile = fileName;
    return this;
  }

  build(): void {
    GlobalConfig.productId = this.mProductId;
    GlobalConfig.disableUpload = this.mDisableUpload;
    GlobalConfig.alwaysShowMainIcon = this.mAlwaysShowMainIcon;
    GlobalConfig.fileManagerHttpPort = this.mFileManagerHttpPort;
    GlobalConfig.mcWsPort = this.mMcWsPort;
    GlobalConfig.databasePass = this.mDatabasePass;
    GlobalConfig.debugMode = this.mDebugMode;
    GlobalConfig.h5BaseUrl = this.mH5BaseUrl;
    GlobalConfig.webDoorCallback = this.mWebDoorCallback;
    if (this.mThirdLibInfos.size > 0) {
      DokitThirdLibInfo.merge(this.mThirdLibInfos);
    }
    if (this.mThirdLibRawFile.length > 0) {
      DokitThirdLibInfo.loadFromRawFile(this.context, this.mThirdLibRawFile);
    }
    DoKitManager.setCallBack(this.mCallBack);
    DoKit.installInternal(this.context, this.mDebugMode, this.mCustomKits, this.mCustomKitList);
  }
}

/**
 * DoKit 鸿蒙版入口，对标 Android DoKit / DoKit.Builder
 */
export class DoKit {
  static Builder(context: common.UIAbilityContext): DoKitBuilder {
    return new DoKitBuilder(context);
  }

  /** @deprecated 请使用 DoKit.Builder(context).build()，并在页面 Stack 中挂载 DoKitFloatingWindow */
  static install(context: common.UIAbilityContext, _windowStage?: window.WindowStage): void {
    DoKit.installInternal(context, true, new Map(), []);
  }

  static installInternal(
    context: common.UIAbilityContext,
    debugMode: boolean,
    customKits: Map<string, AbstractKit[]>,
    customKitList: AbstractKit[]
  ): void {
    if (!debugMode) {
      return;
    }
    DoKitReal.install(context, customKits, customKitList);
    DoKitOverlayState.init(GlobalConfig.alwaysShowMainIcon);
  }

  /**
   * 注册业务专区 kits
   *
   * @param groupName
   * @param kits
   */
  static registerCustomKits(groupName: string, kits: AbstractKit[]) {
    DoKitManager.addCustomKits(groupName, kits);
  }

  /** 启动悬浮窗，对标 Android DoKit.launchFloating(Class, mode, bundle) */
  static launchFloating(
    targetName: string,
    mode: DoKitViewLaunchMode = DoKitViewLaunchMode.SINGLE_INSTANCE,
    bundle?: DoKitBundle
  ): void {
    DoKitLauncher.launchFloating(targetName, mode, bundle);
  }

  static launchFloatingView(
    targetName: string,
    builder: CustomViewBuilder,
    params?: DoKitParams
  ): void {
    CustomViewRegistry.registerBuilder(targetName,builder);

    const config = new ViewConfig(targetName, true, params, { x: 20, y: 200 }, () => {
      DoKitViewManager.detachView(targetName);
    });

    DoKitViewManager.attachView(targetName, config);

  }

  static removeFloatingView(targetName: string){
    DoKitViewManager.detachView(targetName)
  }

  /** 移除悬浮窗，对标 Android DoKit.removeFloating(Class) */
  static removeFloating(targetClass: string): void {
    DoKitLauncher.removeFloating(targetClass);
  }

  /** 启动全屏页，对标 Android DoKit.launchFullScreen(Class, context, bundle, isSystemFragment) */
  static launchFullScreen(
    targetClass: string,
    bundle?: DoKitBundle,
    isSystemFragment: boolean = false
  ): void {
    DoKitLauncher.launchFullScreen(targetClass, bundle, isSystemFragment);
  }

  /** 注册系统 Fragment，对标扩展 UniversalActivity 映射 */
  static registerSystemFragment(fragmentClass: string, builder: SystemFragmentBuilder): void {
    SystemFragmentRegistry.registerSystemFragment(fragmentClass, builder);
  }

  static closeFullScreen(): void {
    DoKitLauncher.finishFullScreen();
  }

  static show(): void {
    DoKitViewManager.attachMainIcon();
  }

  static hide(): void {
    DoKitViewManager.detachMainIcon();
  }

  static showToolPanel(): void {
    DoKitViewManager.attachToolPanel();
  }

  static get isMainIconShow(): boolean {
    return DoKitOverlayState.isIconVisible();
  }

  static isInstalled(): boolean {
    return DoKitReal.isInit;
  }
}
