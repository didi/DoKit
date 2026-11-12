import { DoKitParams } from './CustomViewRegistry';
import { FragmentCloseHandler, SystemFragmentBuilder } from './FragmentRenderer';
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
import { NetWorkMonitorFragmentMeta, renderNetWorkMonitorFragment } from '../kit/network/NetWorkMonitorFragment';
import {
  PerformanceMonitorFragmentMeta,
  renderPerformanceMonitorFragment
} from '../kit/performance/PerformanceMonitorFragment';

/**
 * 系统全屏页画法表。进出栈在 DoKitPageStack。
 * 安装时登记一次。页面栈按页面名查这张表，有则记成系统页。
 */
export class SystemFragmentRegistry {
  /** 已登记的系统页画法。键是页面名。 */
  private static readonly fragments: Map<string, SystemFragmentBuilder> = new Map();
  /** 是否已经登记过默认页。重复 init 直接返回。 */
  private static initialized: boolean = false;

  /** 登记当前已实现的系统全屏页。只执行一次。 */
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
    SystemFragmentRegistry.registerSystemFragment(PerformanceMonitorFragmentMeta.CLASS_NAME,
      wrapBuilder(renderPerformanceMonitorFragment));
    SystemFragmentRegistry.registerSystemFragment(FrameInfoFragmentMeta.CLASS_NAME,
      wrapBuilder(renderFrameInfoFragment));
    SystemFragmentRegistry.registerSystemFragment(CpuMainPageFragmentMeta.CLASS_NAME,
      wrapBuilder(renderCpuMainPageFragment));
    SystemFragmentRegistry.registerSystemFragment(RamMainPageFragmentMeta.CLASS_NAME,
      wrapBuilder(renderRamMainPageFragment));
    SystemFragmentRegistry.registerSystemFragment(NetWorkMonitorFragmentMeta.CLASS_NAME,
      wrapBuilder(renderNetWorkMonitorFragment));
    SystemFragmentRegistry.initialized = true;
  }

  /** 按页面名登记画法。同名后登记的覆盖先登记的。 */
  static registerSystemFragment(fragmentName: string, builder: SystemFragmentBuilder): void {
    SystemFragmentRegistry.fragments.set(fragmentName, builder);
  }

  /** 按页面名取画法。没有则返回空。取之前会先确保默认页已登记。 */
  static getSystemFragmentBuilder(fragmentName: string): SystemFragmentBuilder | undefined {
    SystemFragmentRegistry.initDefaultFragments();
    return SystemFragmentRegistry.fragments.get(fragmentName);
  }

  /** 表里是否已有这个页面名。页面栈用来区分系统页和业务页。 */
  static hasSystemFragment(fragmentName: string): boolean {
    SystemFragmentRegistry.initDefaultFragments();
    return SystemFragmentRegistry.fragments.has(fragmentName);
  }
}

/**
 * 按页面名画出系统全屏页。
 * 全屏层在栈顶是系统页时调用。没有对应画法时不画。
 */
@Builder
export function buildUniversalFragment(fragmentName: string, params: DoKitParams, onClose: FragmentCloseHandler) {
  SystemFragmentRegistry.getSystemFragmentBuilder(fragmentName)?.builder(params, onClose);
}
