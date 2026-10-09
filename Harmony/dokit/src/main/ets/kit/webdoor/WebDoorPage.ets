import promptAction from '@ohos.promptAction';
import type { BusinessError } from '@ohos.base';
import { common } from '@kit.AbilityKit';
import scanBarcode from '@hms.core.scan.scanBarcode';
import scanCore from '@hms.core.scan.scanCore';
import { DoKitBundle, FragmentCloseHandler } from '../../core/FragmentRenderer';
import { WebDoorHistoryStore } from './WebDoorHistoryStore';
import { WebDoorManager } from './WebDoorManager';

/** 对标 Android WebDoorFragment */
export class WebDoorFragment {
  static readonly CLASS_NAME: string = 'WebDoorFragment';
}

/**
 * H5任意门输入页，对标 Android WebDoorFragment（含扫码）
 */
@Component
export struct WebDoorPage {
  onClose: () => void = () => {};
  @State private inputUrl: string = '';
  @State private historyItems: string[] = [];
  @State private canJump: boolean = false;

  aboutToAppear(): void {
    this.historyItems = WebDoorHistoryStore.load();
    this.canJump = this.inputUrl.trim().length > 0;
  }

  private refreshHistory(): void {
    this.historyItems = WebDoorHistoryStore.load();
  }

  private clearHistory(): void {
    WebDoorManager.clearHistory();
    this.historyItems = [];
  }

  /**
   * 对标 Android WebDoorFragment.doSearch：
   * saveHistory + webDoorCallback.overrideUrlLoading。
   * 安卓 callback 会新开 Activity 盖住任意门；鸿蒙宿主是 Navigation.push，
   * 有宿主 callback 时须先关 Overlay，否则目标页被挡住表现为无法跳转。
   */
  private openTargetUrl(): void {
    const url = this.inputUrl.trim();
    if (url.length === 0) {
      promptAction.showToast({ message: '链接不能为空', duration: 1500 });
      return;
    }
    if (WebDoorManager.hasHostCallback()) {
      this.onClose();
      setTimeout(() => {
        if (!WebDoorManager.openUrl(url)) {
          promptAction.showToast({ message: 'H5链接有误', duration: 1500 });
        }
      }, 0);
      return;
    }
    const opened = WebDoorManager.openUrl(url);
    if (!opened) {
      promptAction.showToast({ message: 'H5链接有误', duration: 1500 });
      return;
    }
    this.refreshHistory();
  }

  private chooseHistory(url: string): void {
    this.inputUrl = url;
    this.canJump = true;
    this.openTargetUrl();
  }

  /** 对标 Android WebDoorFragment.qrCode → onActivityResult → doSearch */
  private async clickScan(): Promise<void> {
    try {
      const context = getContext(this) as common.Context;
      const result = await scanBarcode.startScanForResult(context, {
        scanTypes: [scanCore.ScanType.QR_CODE]
      });
      if (result.originalValue.length === 0) {
        return;
      }
      this.inputUrl = result.originalValue.trim();
      this.canJump = this.inputUrl.length > 0;
      // 扫码 Ability 返回后稍延后跳转，避免 Overlay 尚未恢复
      setTimeout(() => {
        this.openTargetUrl();
      }, 100);
    } catch (error) {
      const businessError = error as BusinessError;
      if (businessError.code === 1000500002) {
        return;
      }
      promptAction.showToast({ message: '扫码失败', duration: 1500 });
    }
  }

  @Builder
  private buildHistoryRow(url: string) {
    Row() {
      Image($r('app.media.dk_web_door_history_item_magnifier'))
        .width(18)
        .height(18)
        .objectFit(ImageFit.Contain)
        .margin({ right: 10 })

      Text(url)
        .fontSize(14)
        .fontColor('#333333')
        .maxLines(1)
        .textOverflow({ overflow: TextOverflow.Ellipsis })
        .layoutWeight(1)
    }
    .width('100%')
    .height(44)
    .padding({ left: 16, right: 16 })
    .onClick(() => {
      this.chooseHistory(url);
    })
  }

  build() {
    Column() {
      Row() {
        Text('H5任意门')
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

      Stack({ alignContent: Alignment.BottomEnd }) {
        TextArea({ text: this.inputUrl, placeholder: '输入地址，点击按钮跳转' })
          .width('100%')
          .height(180)
          .fontSize(14)
          .fontColor('#333333')
          .backgroundColor(Color.White)
          .padding({ left: 12, right: 48, top: 12, bottom: 40 })
          .onChange((value: string) => {
            this.inputUrl = value;
            this.canJump = value.trim().length > 0;
          })

        Image($r('app.media.dk_web_door_history_qrcode'))
          .width(28)
          .height(28)
          .objectFit(ImageFit.Contain)
          .margin({ right: 12, bottom: 12 })
          .onClick(() => {
            this.clickScan();
          })
      }
      .width('100%')
      .margin({ top: 12, left: 16, right: 16 })

      Button('点击跳转')
        .width('100%')
        .height(44)
        .fontSize(16)
        .fontColor(Color.White)
        .backgroundColor('#337CC4')
        .margin({ top: 16, left: 16, right: 16 })
        .enabled(this.canJump)
        .opacity(this.canJump ? 1 : 0.5)
        .onClick(() => {
          this.openTargetUrl();
        })

      // 历史紧贴按钮下方，不拉满剩余高度
      Scroll() {
        Column() {
          ForEach(this.historyItems, (item: string) => {
            this.buildHistoryRow(item)
            Divider().color('#EEEEEE')
          }, (item: string) => item)

          if (this.historyItems.length > 0) {
            Text('清空搜索历史')
              .fontSize(13)
              .fontColor('#999999')
              .margin({ top: 16, bottom: 24 })
              .onClick(() => {
                this.clearHistory();
              })
          }
        }
        .width('100%')
        .padding({ top: 12 })
      }
      .width('100%')
      .scrollBar(BarState.Off)
      .margin({ top: 8 })
      .constraintSize({ maxHeight: '40%' })
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#F5F6F8')
    .justifyContent(FlexAlign.Start)
  }
}

@Builder
export function renderWebDoorFragment(_bundle: DoKitBundle, onClose: FragmentCloseHandler) {
  WebDoorPage({ onClose });
}
