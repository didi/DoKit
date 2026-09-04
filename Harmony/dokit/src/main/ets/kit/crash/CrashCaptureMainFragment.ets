import promptAction from '@ohos.promptAction';
import { CrashCaptureConfig } from '../../config/CrashCaptureConfig';
import { BundleKey } from '../../constant/BundleKey';
import { DoKitFileUtil } from '../../util/DoKitFileUtil';
import { DoKitBundle, FragmentCloseHandler } from '../../core/FragmentRenderer';
import { DoKitLauncher } from '../../core/DoKitLauncher';
import { SettingItem } from '../../core/SettingItem';
import { CrashCaptureManager } from './CrashCaptureManager';
import { FileExplorerFragment } from '../fileexplorer/FileExplorerPage';

/** 对标 Android CrashCaptureMainFragment */
export class CrashCaptureMainFragmentMeta {
  static readonly CLASS_NAME: string = 'CrashCaptureMainFragment';
}

/** 设置项文案，对标 Android strings.xml */
class CrashCaptureStrings {
  static readonly SWITCH: string = '崩溃捕获开关';
  static readonly LOOK: string = '查看崩溃记录';
  static readonly CLEAN: string = '清理崩溃日志';
}

/**
 * Crash 捕获主页，对标 Android CrashCaptureMainFragment
 */
@Component
export struct CrashCaptureMainFragment {
  onClose: () => void = () => {};
  @State private captureOpen: boolean = CrashCaptureConfig.isCrashCaptureOpen();
  @State private cleanDataSizeText: string = '';

  aboutToAppear(): void {
    this.refreshCleanDataSize();
  }

  private refreshCleanDataSize(): void {
    const size = CrashCaptureManager.getInstance().getCrashCacheSize();
    this.cleanDataSizeText = DoKitFileUtil.formatFileSize(size);
  }

  private onSwitchChange(isOn: boolean): void {
    this.captureOpen = isOn;
    CrashCaptureConfig.setCrashCaptureOpen(isOn);
    if (isOn) {
      CrashCaptureManager.getInstance().start();
    } else {
      CrashCaptureManager.getInstance().stop();
    }
  }

  private openCrashHistory(): void {
    const crashDir = CrashCaptureManager.getInstance().getCrashCacheDir();
    const bundle: DoKitBundle = {};
    bundle[BundleKey.DIR_KEY] = crashDir;
    DoKitLauncher.launchFullScreen(FileExplorerFragment.CLASS_NAME, bundle, true);
  }

  private cleanCrashHistory(): void {
    CrashCaptureManager.getInstance().clearCacheHistory();
    this.refreshCleanDataSize();
    promptAction.showToast({
      message: CrashCaptureStrings.CLEAN,
      duration: 2000
    });
  }

  @Builder
  private buildSwitchRow() {
    Row() {
      Text(CrashCaptureStrings.SWITCH)
        .fontSize(16)
        .fontColor('#333333')
        .layoutWeight(1)

      Toggle({ type: ToggleType.Switch, isOn: this.captureOpen })
        .selectedColor('#0070BB')
        .onChange((isOn: boolean) => {
          this.onSwitchChange(isOn);
        })
    }
    .width('100%')
    .height(52)
    .padding({ left: 16, right: 16 })
    .backgroundColor(Color.White)
  }

  @Builder
  private buildClickRow(item: SettingItem, onClick: () => void) {
    Row() {
      Text(item.desc)
        .fontSize(16)
        .fontColor('#333333')
        .layoutWeight(1)

      if (item.rightDesc.length > 0) {
        Text(item.rightDesc)
          .fontSize(14)
          .fontColor('#999999')
          .margin({ right: 8 })
      } else {
        Text('›')
          .fontSize(20)
          .fontColor('#CCCCCC')
      }
    }
    .width('100%')
    .height(52)
    .padding({ left: 16, right: 16 })
    .backgroundColor(Color.White)
    .onClick(() => {
      onClick();
    })
  }

  build() {
    Column() {
      Row() {
        Text('Crash')
          .fontSize(18)
          .fontWeight(FontWeight.Medium)
          .fontColor('#111111')
          .layoutWeight(1)
          .textAlign(TextAlign.Center)

        Text('✕')
          .fontSize(20)
          .fontColor('#666666')
          .padding(8)
          .onClick(() => {
            this.onClose();
          })
      }
      .width('100%')
      .height(56)
      .padding({ left: 8, right: 8 })
      .backgroundColor(Color.White)

      this.buildSwitchRow()
      Divider().color('#EEEEEE').margin({ left: 16 })

      this.buildClickRow(new SettingItem(CrashCaptureStrings.LOOK), () => {
        this.openCrashHistory();
      })
      Divider().color('#EEEEEE').margin({ left: 16 })

      this.buildClickRow(new SettingItem(CrashCaptureStrings.CLEAN, false, false, this.cleanDataSizeText), () => {
        this.cleanCrashHistory();
      })
    }
    .width('100%')
    .height('100%')
    .backgroundColor(Color.White)
  }
}

@Builder
export function renderCrashCaptureMainFragment(_bundle: DoKitBundle, onClose: FragmentCloseHandler) {
  CrashCaptureMainFragment({ onClose });
}
