import promptAction from '@ohos.promptAction';
import { WebDoorHistoryStore } from './WebDoorHistoryStore';
import { WebDoorManager } from './WebDoorManager';

/**
 * H5任意门输入页，对标 Android WebDoorFragment + harmonydriver H5AnyDoorPage
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

  private openTargetUrl(): void {
    const url = this.inputUrl.trim();
    if (url.length === 0) {
      promptAction.showToast({ message: '链接不能为空', duration: 1500 });
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
          .padding({ left: 12, right: 12, top: 12, bottom: 40 })
          .onChange((value: string) => {
            this.inputUrl = value;
            this.canJump = value.trim().length > 0;
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
      .layoutWeight(1)
      .scrollBar(BarState.Off)
      .margin({ top: 8 })
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#F5F6F8')
  }
}
