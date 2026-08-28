import promptAction from '@ohos.promptAction';
import { DataCleanDirItem } from './DataCleanModels';
import { DataCleanUtil } from './DataCleanUtil';

/**
 * 清理缓存页，对标 Android DataCleanFragment
 */
@Component
export struct DataCleanPage {
  onClose: () => void = () => {};
  @State private dataSizeText: string = '';
  @State private dirItems: DataCleanDirItem[] = [];

  aboutToAppear(): void {
    this.reloadPageData();
  }

  private reloadPageData(): void {
    const context = DataCleanUtil.getContextOrNull();
    if (context === null) {
      return;
    }
    this.dataSizeText = DataCleanUtil.getApplicationDataSizeStr(context);
    this.dirItems = DataCleanUtil.buildDirItems(context);
  }

  private onDirItemClick(index: number): void {
    const items: DataCleanDirItem[] = [...this.dirItems];
    const item = items[index];
    if (item.isCheckAll) {
      const targetChecked = !item.checked;
      for (let i = 0; i < items.length; i++) {
        items[i] = new DataCleanDirItem(items[i].name, targetChecked, items[i].isCheckAll);
      }
    } else {
      items[index] = new DataCleanDirItem(item.name, !item.checked, false);
    }
    this.dirItems = items;
  }

  private showCleanConfirmDialog(): void {
    promptAction.showDialog({
      title: '提示',
      message: '确认要删除本地数据',
      buttons: [
        { text: '取消', color: '#666666' },
        { text: '确认', color: '#0070BB' }
      ]
    }).then((result: promptAction.ShowDialogSuccessResponse) => {
      if (result.index === 1) {
        this.cleanCache();
      }
    }).catch((_err: Error) => {
      // ignore dialog dismiss
    });
  }

  private cleanCache(): void {
    const context = DataCleanUtil.getContextOrNull();
    if (context === null) {
      return;
    }
    DataCleanUtil.cleanSelectedDirs(context, this.dirItems);
    this.dataSizeText = DataCleanUtil.getApplicationDataSizeStr(context);
  }

  @Builder
  private buildDirRow(item: DataCleanDirItem, index: number) {
    Row() {
      Text(item.name)
        .fontSize(14)
        .fontColor('#333333')
        .layoutWeight(1)

      Toggle({ type: ToggleType.Switch, isOn: item.checked })
        .enabled(false)
        .selectedColor('#0070BB')
    }
    .width('100%')
    .padding({ left: 10, right: 10, top: 10, bottom: 10 })
    .backgroundColor(Color.White)
    .onClick(() => {
      this.onDirItemClick(index);
    })
  }

  build() {
    Column() {
      Row() {
        Text('清理缓存')
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

      Row() {
        Text('清理缓存')
          .fontSize(16)
          .fontColor('#333333')
          .layoutWeight(1)

        Text(this.dataSizeText)
          .fontSize(16)
          .fontColor('#333333')
          .textAlign(TextAlign.End)
      }
      .width('100%')
      .height(52)
      .padding({ left: 16, right: 16 })
      .backgroundColor(Color.White)

      Divider()
        .color('#EEEEEE')
        .margin({ left: 16, right: 16 })

      Scroll() {
        Column() {
          ForEach(this.dirItems, (item: DataCleanDirItem, index: number) => {
            this.buildDirRow(item, index)
          }, (item: DataCleanDirItem, index: number) => `${item.name}_${index}`)
        }
        .width('100%')
      }
      .layoutWeight(1)
      .scrollBar(BarState.Off)

      Button('清理缓存')
        .width('100%')
        .height(40)
        .fontSize(18)
        .fontColor(Color.White)
        .backgroundColor('#0070BB')
        .borderRadius(0)
        .onClick(() => {
          this.showCleanConfirmDialog();
        })
    }
    .width('100%')
    .height('100%')
    .backgroundColor(Color.White)
  }
}
