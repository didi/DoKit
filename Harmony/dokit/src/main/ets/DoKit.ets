import { common } from '@kit.AbilityKit';
import { window } from '@kit.ArkUI';
import { DokitThirdLibInfo } from './aop/DokitThirdLibInfo';
import { DoKitCallBack } from './DoKitCallBack';
import { DoKitReal } from './DoKitReal';
import { GlobalConfig } from './config/GlobalConfig';
import { WebDoorCallback } from './kit/webdoor/WebDoorCallback';
import { AbstractKit } from './kit/AbstractKit';
import { DoKitManager } from './core/DoKitManager';
import { DoKitViewManager, ViewConfig } from './core/DoKitViewManager';
import { DoKitFloatOptions } from './DoKitFloatOptions';
import { SystemFragmentBuilder } from './core/FragmentRenderer';
import { DoKitLauncher } from './core/DoKitLauncher';
import { CustomViewBuilder, DoKitParams } from './core/CustomViewRegistry';
import { DoKitOverlayManager } from './layer/DoKitOverlayManager';

/** 安装配置。配完调用 build，调试包才会真正装上。 */
export class DoKitBuilder {
  /** 宿主 UIAbility。安装和读取 rawfile 时用。 */
  private context: common.UIAbilityContext;
  /** 产品标识。build 时写入全局配置。 */
  private mProductId: string = '';
  /** 调试模式。非调试不安装，也不挂浮层。 */
  private mDebugMode: boolean = true;
  /** 按分组登记的业务 Kit。有内容时，不再使用 mCustomKitList。 */
  private mCustomKits: Map<string, AbstractKit[]> = new Map();
  /** 不分组的业务 Kit。只在没有分组表时，放进默认业务分组。 */
  private mCustomKitList: AbstractKit[] = [];
  /** 关闭数据上报。 */
  private mDisableUpload: boolean = false;
  /** 悬浮球初始开关。 */
  private mAlwaysShowMainIcon: boolean = true;
  /** 文件管理服务端口。 */
  private mFileManagerHttpPort: number = 8089;
  /** 一机多控 WebSocket 端口。 */
  private mMcWsPort: number = 4444;
  /** 数据库密码。键是库名。 */
  private mDatabasePass: Map<string, string> = new Map();
  /** 帧率、CPU、内存、网络的数据回调。 */
  private mCallBack: DoKitCallBack | null = null;
  /** H5 任意门回调。 */
  private mWebDoorCallback: WebDoorCallback | null = null;
  /** H5 任意门相对路径的前缀。 */
  private mH5BaseUrl: string = '';
  /** 三方库名和大小。build 时合并进三方库信息。 */
  private mThirdLibInfos: Map<string, string> = new Map();
  /** 三方库信息的 rawfile 文件名。 */
  private mThirdLibRawFile: string = '';
  /** 主窗口。调试模式下 build 时交给浮层。 */
  private mWindowStage: window.WindowStage | null = null;

  /** 记下宿主上下文。 */
  constructor(context: common.UIAbilityContext) {
    this.context = context;
  }

  /** 设置产品标识。 */
  productId(id: string): DoKitBuilder {
    this.mProductId = id;
    return this;
  }

  /** 设置是否为调试包。非调试时 build 不安装。 */
  debug(isDebug: boolean): DoKitBuilder {
    this.mDebugMode = isDebug;
    return this;
  }

  /** 绑定主窗口。build 时只记下窗口，主窗 loadContent 成功后再调用 DoKit.addRootComponentContent。 */
  windowStage(stage: window.WindowStage): DoKitBuilder {
    this.mWindowStage = stage;
    return this;
  }

  /** 按分组登记业务 Kit。分组名会显示在工具面板上。 */
  customKits(kits: Map<string, AbstractKit[]>): DoKitBuilder {
    this.mCustomKits = kits;
    return this;
  }

  /** 登记一组不分组的业务 Kit。已经调用 customKits 时，这份列表不会用。 */
  customKitList(kits: AbstractKit[]): DoKitBuilder {
    this.mCustomKitList = kits;
    return this;
  }

  /** 关闭数据上报。 */
  disableUpload(): DoKitBuilder {
    this.mDisableUpload = true;
    return this;
  }

  /** 设置悬浮球安装后的初始开关。 */
  alwaysShowMainIcon(alwaysShow: boolean): DoKitBuilder {
    this.mAlwaysShowMainIcon = alwaysShow;
    return this;
  }

  /** 设置文件管理服务端口。默认 8089。 */
  fileManagerHttpPort(port: number): DoKitBuilder {
    this.mFileManagerHttpPort = port;
    return this;
  }

  /** 设置一机多控 WebSocket 端口。默认 4444。 */
  mcWSPort(port: number): DoKitBuilder {
    this.mMcWsPort = port;
    return this;
  }

  /** 设置数据库密码。键是库名。 */
  databasePass(map: Map<string, string>): DoKitBuilder {
    this.mDatabasePass = map;
    return this;
  }

  /** 设置性能数据回调。 */
  callBack(callback: DoKitCallBack): DoKitBuilder {
    this.mCallBack = callback;
    return this;
  }

  /** 设置 H5 任意门回调。 */
  webDoorCallback(callback: WebDoorCallback): DoKitBuilder {
    this.mWebDoorCallback = callback;
    return this;
  }

  /** 设置 H5 任意门相对路径的前缀。 */
  h5BaseUrl(url: string): DoKitBuilder {
    this.mH5BaseUrl = url;
    return this;
  }

  /** 直接写入三方库名和大小。 */
  thirdLibInfos(infos: Map<string, string>): DoKitBuilder {
    this.mThirdLibInfos = infos;
    return this;
  }

  /** 从 rawfile 读取三方库信息。 */
  thirdLibRawFile(fileName: string): DoKitBuilder {
    this.mThirdLibRawFile = fileName;
    return this;
  }

  /** 写入全局配置并安装。调试模式且绑了窗口时，记下主窗口。 */
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
    if (this.mDebugMode && this.mWindowStage !== null) {
      DoKitOverlayManager.getInstance().addRootComponent(this.mWindowStage);
    }
  }
}

/**
 * DoKit 鸿蒙版入口，对标 Android DoKit / DoKit.Builder
 */
export class DoKit {
  /** 创建安装配置。 */
  static Builder(context: common.UIAbilityContext): DoKitBuilder {
    return new DoKitBuilder(context);
  }

  /** @deprecated 请使用 DoKit.Builder(context).windowStage(stage).build()，主窗 loadContent 成功后再调用 DoKit.addRootComponentContent() */
  static install(context: common.UIAbilityContext, _windowStage?: window.WindowStage): void {
    DoKit.installInternal(context, true, new Map(), []);
    if (_windowStage !== undefined) {
      DoKitOverlayManager.getInstance().addRootComponent(_windowStage);
    }
  }

  /** 调试模式才安装，并按配置设置悬浮球初始开关。非调试直接返回。 */
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
    DoKitViewManager.overlayState.init(GlobalConfig.alwaysShowMainIcon);
  }

  /** 按分组名把业务 Kit 登记进工具面板。同名分组会覆盖。 */
  static registerCustomKits(groupName: string, kits: AbstractKit[]) {
    DoKitManager.addCustomKits(groupName, kits);
  }

  /** 主窗 loadContent 成功后，把浮层挂到主窗口上。需要先通过 build 或 install 记下窗口。和 removeRootComponentContent 成对。 */
  static addRootComponentContent(): void {
    if (!GlobalConfig.debugMode) {
      return;
    }
    DoKitOverlayManager.getInstance().addRootComponentContent();
  }

  /** 卸下已挂上的浮层内容。窗口和层表还留着。和 addRootComponentContent 成对。 */
  static removeRootComponentContent(): void {
    DoKitOverlayManager.getInstance().removeRootComponentContent();
  }


  /**
   * 自定义浮窗：同名只留一个。已在时只更新画法和摆放，不改位置，不先卸再挂。
   * 位置写在 DoKitFloatOptions 的 x、y 上，单位 vp，两个都给才生效，且只在第一次打开时生效。
   */
  static launchFloatingView(
    targetName: string,
    builder: CustomViewBuilder,
    params?: DoKitParams,
    options?: DoKitFloatOptions
  ): void {
    DoKitLauncher.launchFloatingView(targetName, builder, params, options);
  }

  /** 按名字卸下浮窗。 */
  static removeFloatingView(targetName: string): void {
    DoKitLauncher.removeFloatingView(targetName);
  }

  /** 按名字取一条浮窗记录。没有就返回空。 */
  static getViewConfigByName(name: string): ViewConfig | undefined {
    return DoKitViewManager.getViewConfigByName(name);
  }

  /** 打开全屏工具根页（清栈）。传入 builder 时由启动器按系统/自定义登记画法。 */
  static launchFullScreen(
    targetName: string,
    builder?: SystemFragmentBuilder,
    params?: DoKitParams,
    isSystemFragment: boolean = false
  ): void {
    DoKitLauncher.launchFullScreen(targetName, builder, params, isSystemFragment);
  }

  /** 全屏内打开下级页（压栈，支持 A→B→回 A）。传入 builder 时由启动器按系统/自定义登记画法。 */
  static pushFullScreen(
    targetName: string,
    builder?: SystemFragmentBuilder,
    params?: DoKitParams,
    isSystemFragment: boolean = false
  ): void {
    DoKitLauncher.pushFullScreen(targetName, builder, params, isSystemFragment);
  }

  /** 按名字从全屏栈里清除页面。同名的都删掉。 */
  static popFullScreenPage(targetName: string): void {
    DoKitLauncher.popFullScreenPage(targetName);
  }

  /** 关闭栈顶页面。 */
  static popFullScreen(): void {
    DoKitLauncher.popFullScreen();
  }

  /** 关掉全部全屏页，并清空页面栈。 */
  static closeFullScreen(): void {
    DoKitLauncher.finishFullScreen();
  }

  /** 显示悬浮球。全屏页开着时浮层仍会先藏球。 */
  static attachMainIcon(): void {
    DoKitViewManager.attachMainIcon();
  }

  /** 隐藏悬浮球。 */
  static detachMainIcon(): void {
    DoKitViewManager.detachMainIcon();
  }

  /** 打开工具面板。 */
  static showToolPanel(): void {
    DoKitViewManager.attachToolPanel();
  }

  /** 关掉工具面板。已经关着就不再刷新。 */
  static detachToolPanel(): void {
    DoKitViewManager.detachToolPanel();
  }

  /** 悬浮球开关是否开着。全屏页盖住时，开关仍可能是开的。 */
  static get isMainIconShow(): boolean {
    return DoKitViewManager.isMainIconShow();
  }

  /** 是否已经安装过。重复 build 不会再装一次。 */
  static isInstalled(): boolean {
    return DoKitReal.isInit;
  }
}
