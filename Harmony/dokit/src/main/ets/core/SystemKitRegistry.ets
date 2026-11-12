import { common } from '@kit.AbilityKit';
import { AbstractKit } from '../kit/AbstractKit';
import { SysInfoKit } from '../kit/sysinfo/SysInfoKit';
import { ThirdLibInfoKit } from '../kit/sysinfo/ThirdLibInfoKit';
import { DevelopmentPageKit } from '../kit/sysinfo/DevelopmentPageKit';
import { LocalLangKit } from '../kit/sysinfo/LocalLangKit';
import { CrashCaptureKit } from '../kit/crash/CrashCaptureKit';
import { BlockMonitorKit } from '../kit/blockmonitor/BlockMonitorKit';
import { FrameInfoKit } from '../kit/parameter/frameInfo/FrameInfoKit';
import { CpuKit } from '../kit/parameter/cpu/CpuKit';
import { RamKit } from '../kit/parameter/ram/RamKit';
import { NetworkKit } from '../kit/network/NetworkKit';
import { DataCleanKit } from '../kit/dataclean/DataCleanKit';
import { FileExplorerKit } from '../kit/fileexplorer/FileExplorerKit';
import { WebDoorKit } from '../kit/webdoor/WebDoorKit';
import { ColorPickerKit } from '../kit/colorpick/ColorPickerKit';
import { AlignRulerKit } from '../kit/alignruler/AlignRulerKit';
import { ViewCheckKit } from '../kit/viewcheck/ViewCheckKit';
import { LayoutBorderKit } from '../kit/layoutborder/LayoutBorderKit';

/**
 * 内置工具注册表，对标 Android ServiceLoader.load(AbstractKit)。
 * DoKit 安装时登记一次。工具面板按 dokit_system_kits.json 里的 innerKitId 来取。
 */
export class SystemKitRegistry {
  /** 已登记的内置工具。键是 innerKitId。 */
  private static readonly kits: Map<string, AbstractKit> = new Map();
  /** 是否已经登记过。重复 init 直接返回。 */
  private static initialized: boolean = false;

  /** 登记当前已实现的内置工具。只执行一次。 */
  static init(): void {
    if (SystemKitRegistry.initialized) {
      return;
    }
    SystemKitRegistry.register(new SysInfoKit());
    SystemKitRegistry.register(new ThirdLibInfoKit());
    SystemKitRegistry.register(new DevelopmentPageKit());
    SystemKitRegistry.register(new LocalLangKit());
    SystemKitRegistry.register(new CrashCaptureKit());
    SystemKitRegistry.register(new BlockMonitorKit());
    SystemKitRegistry.register(new FrameInfoKit());
    SystemKitRegistry.register(new CpuKit());
    SystemKitRegistry.register(new RamKit());
    SystemKitRegistry.register(new NetworkKit());
    SystemKitRegistry.register(new DataCleanKit());
    SystemKitRegistry.register(new FileExplorerKit());
    SystemKitRegistry.register(new WebDoorKit());
    SystemKitRegistry.register(new ColorPickerKit());
    SystemKitRegistry.register(new AlignRulerKit());
    SystemKitRegistry.register(new ViewCheckKit());
    SystemKitRegistry.register(new LayoutBorderKit());
    SystemKitRegistry.initialized = true;
  }

  /** 按 innerKitId 登记。id 为空的不放入表。同 id 后登记的覆盖先登记的。 */
  static register(kit: AbstractKit): void {
    const kitId = kit.innerKitId();
    if (kitId.length > 0) {
      SystemKitRegistry.kits.set(kitId, kit);
    }
  }

  /** 按 innerKitId 取内置工具。没有则返回空。 */
  static get(innerKitId: string): AbstractKit | undefined {
    return SystemKitRegistry.kits.get(innerKitId);
  }

  /** 表里是否已有这个 innerKitId。 */
  static has(innerKitId: string): boolean {
    return SystemKitRegistry.kits.has(innerKitId);
  }

  /** 当前已登记的全部内置工具。 */
  static getAll(): AbstractKit[] {
    const result: AbstractKit[] = [];
    SystemKitRegistry.kits.forEach((kit: AbstractKit) => {
      result.push(kit);
    });
    return result;
  }

  /** 安装完成后，逐个调用内置工具的 onAppInit。 */
  static onAppInit(context: common.UIAbilityContext): void {
    SystemKitRegistry.getAll().forEach((kit: AbstractKit) => {
      kit.onAppInit(context);
    });
  }
}
