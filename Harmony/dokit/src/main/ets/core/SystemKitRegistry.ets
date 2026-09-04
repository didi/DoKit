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
import { DataCleanKit } from '../kit/dataclean/DataCleanKit';
import { FileExplorerKit } from '../kit/fileexplorer/FileExplorerKit';
import { WebDoorKit } from '../kit/webdoor/WebDoorKit';

/**
 * 内置 Kit 注册表，对标 Android ServiceLoader.load(AbstractKit)
 */
export class SystemKitRegistry {
  private static readonly kits: Map<string, AbstractKit> = new Map();
  private static initialized: boolean = false;

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
    SystemKitRegistry.register(new DataCleanKit());
    SystemKitRegistry.register(new FileExplorerKit());
    SystemKitRegistry.register(new WebDoorKit());
    SystemKitRegistry.initialized = true;
  }

  static register(kit: AbstractKit): void {
    const kitId = kit.innerKitId();
    if (kitId.length > 0) {
      SystemKitRegistry.kits.set(kitId, kit);
    }
  }

  static get(innerKitId: string): AbstractKit | undefined {
    return SystemKitRegistry.kits.get(innerKitId);
  }

  static has(innerKitId: string): boolean {
    return SystemKitRegistry.kits.has(innerKitId);
  }

  static getAll(): AbstractKit[] {
    const result: AbstractKit[] = [];
    SystemKitRegistry.kits.forEach((kit: AbstractKit) => {
      result.push(kit);
    });
    return result;
  }

  static onAppInit(context: common.UIAbilityContext): void {
    SystemKitRegistry.getAll().forEach((kit: AbstractKit) => {
      kit.onAppInit(context);
    });
  }
}
