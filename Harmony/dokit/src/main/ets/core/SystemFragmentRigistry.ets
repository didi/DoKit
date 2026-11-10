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

/** 系统全屏页画法表。进出栈在 DoKitPageStack。 */
export class SystemFragmentRegistry {
  private static readonly fragments: Map<string, SystemFragmentBuilder> = new Map();
  private static initialized: boolean = false;

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

  static registerSystemFragment(fragmentName: string, builder: SystemFragmentBuilder): void {
    SystemFragmentRegistry.fragments.set(fragmentName, builder);
  }

  static getSystemFragmentBuilder(fragmentName: string): SystemFragmentBuilder | undefined {
    SystemFragmentRegistry.initDefaultFragments();
    return SystemFragmentRegistry.fragments.get(fragmentName);
  }

  static hasSystemFragment(fragmentName: string): boolean {
    SystemFragmentRegistry.initDefaultFragments();
    return SystemFragmentRegistry.fragments.has(fragmentName);
  }
}

@Builder
export function buildUniversalFragment(fragmentName: string, params: DoKitParams, onClose: FragmentCloseHandler) {
  SystemFragmentRegistry.getSystemFragmentBuilder(fragmentName)?.builder(params, onClose);
}
