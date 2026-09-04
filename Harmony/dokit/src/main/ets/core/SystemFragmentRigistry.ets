import { BundleKey } from '../constant/BundleKey';
import { FragmentIndex } from '../constant/FragmentIndex';
import { DoKitBundle, FragmentCloseHandler, SystemFragmentBuilder } from './FragmentRenderer';
import { SysInfoFragment, renderSysInfoFragment } from '../kit/sysinfo/SysInfoPage';
import { WebDoorFragment, renderWebDoorFragment } from '../kit/webdoor/WebDoorPage';
import { WebDoorDefaultFragment, renderWebDoorDefaultFragment } from '../kit/webdoor/WebDoorWebViewPage';
import { FileExplorerFragment, renderFileExplorerFragment } from '../kit/fileexplorer/FileExplorerPage';
import { ThirdLibInfoFragment, renderThirdLibInfoFragment } from '../kit/sysinfo/ThirdLibInfoPage';
import { DataCleanFragment, renderDataCleanFragment } from '../kit/dataclean/DataCleanPage';
import { CrashCaptureMainFragmentMeta, renderCrashCaptureMainFragment } from '../kit/crash/CrashCaptureMainFragment';
import { BlockMonitorFragmentMeta, renderBlockMonitorFragment } from '../kit/blockmonitor/BlockMonitorFragment';
import { FrameInfoFragmentMeta, renderFrameInfoFragment } from '../kit/parameter/frameInfo/FrameInfoFragment';
import { CpuMainPageFragmentMeta, renderCpuMainPageFragment } from '../kit/parameter/cpu/CpuMainPageFragment';
import { RamMainPageFragmentMeta, renderRamMainPageFragment } from '../kit/parameter/ram/RamMainPageFragment';

/**
 * 全屏工具页容器，对标 Android kit/core/UniversalActivity
 */
export class SystemFragmentRegistry {
  private static readonly fragments: Map<string, SystemFragmentBuilder> = new Map();
  private static fragmentName: string = '';
  private static initialized: boolean = false;
  private static isSystemFragment: boolean = false;
  private static bundle: DoKitBundle = {};
  private static openHandler: ((fragmentName: string, isSystemFragment: boolean) => void) | null = null;
  private static closeHandler: (() => void) | null = null;

  static initDefaultFragments(): void {
    if (SystemFragmentRegistry.initialized) {
      return;
    }
    SystemFragmentRegistry.registerSystemFragment(SysInfoFragment.CLASS_NAME, wrapBuilder(renderSysInfoFragment));
    SystemFragmentRegistry.registerSystemFragment(WebDoorFragment.CLASS_NAME, wrapBuilder(renderWebDoorFragment));
    SystemFragmentRegistry.registerSystemFragment(WebDoorDefaultFragment.CLASS_NAME,
      wrapBuilder(renderWebDoorDefaultFragment));
    SystemFragmentRegistry.registerSystemFragment(FileExplorerFragment.CLASS_NAME,
      wrapBuilder(renderFileExplorerFragment));
    SystemFragmentRegistry.registerSystemFragment(ThirdLibInfoFragment.CLASS_NAME,
      wrapBuilder(renderThirdLibInfoFragment));
    SystemFragmentRegistry.registerSystemFragment(DataCleanFragment.CLASS_NAME, wrapBuilder(renderDataCleanFragment));
    SystemFragmentRegistry.registerSystemFragment(CrashCaptureMainFragmentMeta.CLASS_NAME,
      wrapBuilder(renderCrashCaptureMainFragment));
    SystemFragmentRegistry.registerSystemFragment(BlockMonitorFragmentMeta.CLASS_NAME,
      wrapBuilder(renderBlockMonitorFragment));
    SystemFragmentRegistry.registerSystemFragment(FrameInfoFragmentMeta.CLASS_NAME,
      wrapBuilder(renderFrameInfoFragment));
    SystemFragmentRegistry.registerSystemFragment(CpuMainPageFragmentMeta.CLASS_NAME,
      wrapBuilder(renderCpuMainPageFragment));
    SystemFragmentRegistry.registerSystemFragment(RamMainPageFragmentMeta.CLASS_NAME,
      wrapBuilder(renderRamMainPageFragment));
    SystemFragmentRegistry.initialized = true;
  }

  static registerSystemFragment(fragmentName: string, builder: SystemFragmentBuilder): void {
    SystemFragmentRegistry.fragments.set(fragmentName, builder);
  }

  static getSystemFragmentBuilder(fragmentName: string): SystemFragmentBuilder | undefined {
    return SystemFragmentRegistry.fragments.get(fragmentName);
  }

  static hasSystemFragment(fragmentName: string): boolean {
    return SystemFragmentRegistry.fragments.has(fragmentName);
  }

  static setOpenHandler(handler: ((fragmentName: string, isSystemFragment: boolean) => void) | null): void {
    SystemFragmentRegistry.openHandler = handler;
  }

  static setCloseHandler(handler: (() => void) | null): void {
    SystemFragmentRegistry.closeHandler = handler;
  }

  static showContent(fragmentName: string, bundle?: DoKitBundle, isSystemFragment: boolean = false): void {
    SystemFragmentRegistry.fragmentName = fragmentName;
    SystemFragmentRegistry.bundle = bundle ?? {};
    SystemFragmentRegistry.isSystemFragment = isSystemFragment;
    SystemFragmentRegistry.bundle[BundleKey.FRAGMENT_INDEX] = isSystemFragment ?
      FragmentIndex.FRAGMENT_SYSTEM : FragmentIndex.FRAGMENT_CUSTOM;
    SystemFragmentRegistry.bundle[isSystemFragment ? BundleKey.SYSTEM_FRAGMENT_CLASS :
      BundleKey.CUSTOM_FRAGMENT_CLASS] = fragmentName;
    if (SystemFragmentRegistry.openHandler !== null) {
      SystemFragmentRegistry.openHandler(fragmentName, isSystemFragment);
    }
  }

  /**
   * 对标 Android UniversalActivity.onCreate 中按 FRAGMENT_INDEX 解析 Fragment Class
   */
  static resolvefragmentName(bundle: DoKitBundle): string {
    const index = bundle[BundleKey.FRAGMENT_INDEX] as number;
    if (!index) {
      return '';
    }
    switch (index) {
      case FragmentIndex.FRAGMENT_SYSTEM:
        return (bundle[BundleKey.SYSTEM_FRAGMENT_CLASS] as string) ?? '';
      case FragmentIndex.FRAGMENT_CUSTOM:
        return (bundle[BundleKey.CUSTOM_FRAGMENT_CLASS] as string) ?? '';
      default:
        return '';
    }
  }

  static finish(): void {
    SystemFragmentRegistry.clearContent();
    if (SystemFragmentRegistry.closeHandler !== null) {
      SystemFragmentRegistry.closeHandler();
    }
  }

  static getFragmentName(): string {
    return SystemFragmentRegistry.fragmentName;
  }

  static getBundle(): DoKitBundle {
    return SystemFragmentRegistry.bundle;
  }

  static isSystemFragmentActive(): boolean {
    return SystemFragmentRegistry.isSystemFragment;
  }

  static hasActiveFragment(): boolean {
    return SystemFragmentRegistry.fragmentName.length > 0;
  }

  static clearContent(): void {
    SystemFragmentRegistry.fragmentName = '';
    SystemFragmentRegistry.bundle = {};
    SystemFragmentRegistry.isSystemFragment = false;
  }
}

@Builder
export function buildUniversalFragment(fragmentName: string, bundle: DoKitBundle, onClose: FragmentCloseHandler) {
  SystemFragmentRegistry.getSystemFragmentBuilder(fragmentName)?.builder(bundle, onClose);
}
