import { BundleKey } from '../../constant/BundleKey';
import { FragmentIndex } from '../../constant/FragmentIndex';
import { DoKitBundle, FragmentCloseHandler, SystemFragmentBuilder } from './FragmentRenderer';
import { SysInfoFragment, renderSysInfoFragment } from '../sysinfo/SysInfoPage';
import { WebDoorFragment, renderWebDoorFragment } from '../webdoor/WebDoorPage';
import { WebDoorDefaultFragment, renderWebDoorDefaultFragment } from '../webdoor/WebDoorWebViewPage';
import { FileExplorerFragment, renderFileExplorerFragment } from '../fileexplorer/FileExplorerPage';
import { ThirdLibInfoFragment, renderThirdLibInfoFragment } from '../sysinfo/ThirdLibInfoPage';
import { DataCleanFragment, renderDataCleanFragment } from '../dataclean/DataCleanPage';
import { CrashCaptureMainFragmentMeta, renderCrashCaptureMainFragment } from '../crash/CrashCaptureMainFragment';
import { BlockMonitorFragmentMeta, renderBlockMonitorFragment } from '../blockmonitor/BlockMonitorFragment';
import { FrameInfoFragmentMeta, renderFrameInfoFragment } from '../parameter/frameInfo/FrameInfoFragment';
import { CpuMainPageFragmentMeta, renderCpuMainPageFragment } from '../parameter/cpu/CpuMainPageFragment';
import { RamMainPageFragmentMeta, renderRamMainPageFragment } from '../parameter/ram/RamMainPageFragment';

/**
 * 全屏工具页容器，对标 Android kit/core/UniversalActivity
 */
export class UniversalActivity {
  private static readonly systemFragments: Map<string, SystemFragmentBuilder> = new Map();
  private static initialized: boolean = false;

  private static fragmentClass: string = '';
  private static bundle: DoKitBundle = {};
  private static isSystemFragment: boolean = false;

  private static openHandler: ((fragmentClass: string, isSystemFragment: boolean) => void) | null = null;
  private static closeHandler: (() => void) | null = null;

  static initDefaultFragments(): void {
    if (UniversalActivity.initialized) {
      return;
    }
    UniversalActivity.registerSystemFragment(SysInfoFragment.CLASS_NAME,
      wrapBuilder(renderSysInfoFragment));
    UniversalActivity.registerSystemFragment(WebDoorFragment.CLASS_NAME,
      wrapBuilder(renderWebDoorFragment));
    UniversalActivity.registerSystemFragment(WebDoorDefaultFragment.CLASS_NAME,
      wrapBuilder(renderWebDoorDefaultFragment));
    UniversalActivity.registerSystemFragment(FileExplorerFragment.CLASS_NAME,
      wrapBuilder(renderFileExplorerFragment));
    UniversalActivity.registerSystemFragment(ThirdLibInfoFragment.CLASS_NAME,
      wrapBuilder(renderThirdLibInfoFragment));
    UniversalActivity.registerSystemFragment(DataCleanFragment.CLASS_NAME,
      wrapBuilder(renderDataCleanFragment));
    UniversalActivity.registerSystemFragment(CrashCaptureMainFragmentMeta.CLASS_NAME,
      wrapBuilder(renderCrashCaptureMainFragment));
    UniversalActivity.registerSystemFragment(BlockMonitorFragmentMeta.CLASS_NAME,
      wrapBuilder(renderBlockMonitorFragment));
    UniversalActivity.registerSystemFragment(FrameInfoFragmentMeta.CLASS_NAME,
      wrapBuilder(renderFrameInfoFragment));
    UniversalActivity.registerSystemFragment(CpuMainPageFragmentMeta.CLASS_NAME,
      wrapBuilder(renderCpuMainPageFragment));
    UniversalActivity.registerSystemFragment(RamMainPageFragmentMeta.CLASS_NAME,
      wrapBuilder(renderRamMainPageFragment));
    UniversalActivity.initialized = true;
  }

  static registerSystemFragment(fragmentClass: string, builder: SystemFragmentBuilder): void {
    UniversalActivity.systemFragments.set(fragmentClass, builder);
  }

  static getSystemFragmentBuilder(fragmentClass: string): SystemFragmentBuilder | undefined {
    return UniversalActivity.systemFragments.get(fragmentClass);
  }

  static hasSystemFragment(fragmentClass: string): boolean {
    return UniversalActivity.systemFragments.has(fragmentClass);
  }

  static setOpenHandler(handler: ((fragmentClass: string, isSystemFragment: boolean) => void) | null): void {
    UniversalActivity.openHandler = handler;
  }

  static setCloseHandler(handler: (() => void) | null): void {
    UniversalActivity.closeHandler = handler;
  }

  static showContent(fragmentClass: string, bundle?: DoKitBundle, isSystemFragment: boolean = false): void {
    UniversalActivity.fragmentClass = fragmentClass;
    UniversalActivity.bundle = bundle ?? {};
    UniversalActivity.isSystemFragment = isSystemFragment;
    UniversalActivity.bundle[BundleKey.FRAGMENT_INDEX] = isSystemFragment ?
      FragmentIndex.FRAGMENT_SYSTEM : FragmentIndex.FRAGMENT_CUSTOM;
    UniversalActivity.bundle[isSystemFragment ? BundleKey.SYSTEM_FRAGMENT_CLASS :
      BundleKey.CUSTOM_FRAGMENT_CLASS] = fragmentClass;
    if (UniversalActivity.openHandler !== null) {
      UniversalActivity.openHandler(fragmentClass, isSystemFragment);
    }
  }

  /**
   * 对标 Android UniversalActivity.onCreate 中按 FRAGMENT_INDEX 解析 Fragment Class
   */
  static resolveFragmentClass(bundle: DoKitBundle): string {
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
    UniversalActivity.clearContent();
    if (UniversalActivity.closeHandler !== null) {
      UniversalActivity.closeHandler();
    }
  }

  static getFragmentClass(): string {
    return UniversalActivity.fragmentClass;
  }

  static getBundle(): DoKitBundle {
    return UniversalActivity.bundle;
  }

  static isSystemFragmentActive(): boolean {
    return UniversalActivity.isSystemFragment;
  }

  static hasActiveFragment(): boolean {
    return UniversalActivity.fragmentClass.length > 0;
  }

  static clearContent(): void {
    UniversalActivity.fragmentClass = '';
    UniversalActivity.bundle = {};
    UniversalActivity.isSystemFragment = false;
  }
}

@Builder
export function buildUniversalFragment(
  fragmentClass: string,
  bundle: DoKitBundle,
  onClose: FragmentCloseHandler
) {
  UniversalActivity.getSystemFragmentBuilder(fragmentClass)?.builder(bundle, onClose);
}
