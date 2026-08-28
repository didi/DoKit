import { common } from '@kit.AbilityKit';
import { AbstractKit } from '../AbstractKit';
import { SysInfoKit } from '../sysinfo/SysInfoKit';
import { ThirdLibInfoKit } from '../sysinfo/ThirdLibInfoKit';
import { DevelopmentPageKit } from '../sysinfo/DevelopmentPageKit';
import { DataCleanKit } from '../dataclean/DataCleanKit';
import { FileExplorerKit } from '../fileexplorer/FileExplorerKit';
import { WebDoorKit } from '../webdoor/WebDoorKit';

/**
 * 内置 Kit 注册表，对标 Android ServiceLoader.load(AbstractKit)
 */
export class InnerKitRegistry {
  private static readonly kits: Map<string, AbstractKit> = new Map();
  private static initialized: boolean = false;

  static init(): void {
    if (InnerKitRegistry.initialized) {
      return;
    }
    InnerKitRegistry.register(new SysInfoKit());
    InnerKitRegistry.register(new ThirdLibInfoKit());
    InnerKitRegistry.register(new DevelopmentPageKit());
    InnerKitRegistry.register(new DataCleanKit());
    InnerKitRegistry.register(new FileExplorerKit());
    InnerKitRegistry.register(new WebDoorKit());
    InnerKitRegistry.initialized = true;
  }

  static register(kit: AbstractKit): void {
    const kitId = kit.innerKitId();
    if (kitId.length > 0) {
      InnerKitRegistry.kits.set(kitId, kit);
    }
  }

  static get(innerKitId: string): AbstractKit | undefined {
    return InnerKitRegistry.kits.get(innerKitId);
  }

  static has(innerKitId: string): boolean {
    return InnerKitRegistry.kits.has(innerKitId);
  }

  static getAll(): AbstractKit[] {
    const result: AbstractKit[] = [];
    InnerKitRegistry.kits.forEach((kit: AbstractKit) => {
      result.push(kit);
    });
    return result;
  }

  static onAppInit(context: common.UIAbilityContext): void {
    InnerKitRegistry.getAll().forEach((kit: AbstractKit) => {
      kit.onAppInit(context);
    });
  }
}
