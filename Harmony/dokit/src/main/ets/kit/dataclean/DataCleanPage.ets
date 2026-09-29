import promptAction from '@ohos.promptAction';
import { DoKitBundle, FragmentCloseHandler } from '../../core/FragmentRenderer';
import { DataCleanDirItem, DataCleanOutcome, DataCleanResult } from './DataCleanModels';
import { DataCleanUtil } from '../../util/DataCleanUtil';

/** 对标 Android DataCleanFragment */
export class DataCleanFragment {
  static readonly CLASS_NAME: string = 'DataCleanFragment';
}

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

  private setDirItemChecked(index: number, checked: boolean): void {
    const items: DataCleanDirItem[] = [...this.dirItems];
    const item = items[index];
    if (item.isCheckAll) {
      for (let i = 0; i < items.length; i++) {
        items[i] = new DataCleanDirItem(items[i].name, checked, items[i].isCheckAll);
      }
    } else {
      items[index] = new DataCleanDirItem(item.name, checked, false);
    }
    this.dirItems = items;
  }

  private showCleanConfirmDialog(): void {
    if (!DataCleanUtil.hasCheckedDir(this.dirItems)) {
      promptAction.showToast({ message: '请先选择要清理的目录', duration: 2000 });
      return;
    }
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
    const outcome: DataCleanOutcome = DataCleanUtil.cleanSelectedDirs(context, this.dirItems);
    this.reloadPageData();
    this.showCleanResult(outcome);
  }

  private showCleanResult(outcome: DataCleanOutcome): void {
    if (outcome.result === DataCleanResult.NONE_SELECTED) {
      promptAction.showToast({ message: '请先选择要清理的目录', duration: 2000 });
      return;
    }
    if (outcome.result === DataCleanResult.PARTIAL_FAILED) {
      const failed = outcome.failedNames.join('、');
      const message = failed.length > 0 ? `部分目录清理失败：${failed}` : '部分目录清理失败';
      promptAction.showToast({ message, duration: 2000 });
      return;
    }
    promptAction.showToast({ message: '清理完成', duration: 2000 });
  }

  @Builder
  private buildDirRow(item: DataCleanDirItem, index: number) {
    DataCleanDirRow({
      name: item.name,
      checked: item.checked,
      onCheckedChange: (checked: boolean) => {
        this.setDirItemChecked(index, checked);
      }
    })
  }

  build() {
    Column() {
      Row() {
        Image($r('app.media.dk_close_icon_big'))
          .height(18)
          .width(18)
          .objectFit(ImageFit.Fill)
          .onClick(() => {
            this.onClose();
          })

        Text('清理缓存')
          .fontSize(18)
          .fontWeight(FontWeight.Medium)
          .fontColor('#111111')
          .layoutWeight(1)
          .textAlign(TextAlign.Start)

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

      Column() {
        Scroll() {
          Column() {
            ForEach(this.dirItems, (item: DataCleanDirItem, index: number) => {
              this.buildDirRow(item, index)
            }, (item: DataCleanDirItem, index: number) => `${item.name}_${index}_${item.checked ? '1' : '0'}`)
          }
          .width('100%')
        }
        .scrollBar(BarState.Off)
      }
      .layoutWeight(1)

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

/** 对齐 Android 目录行：整行可点，Toggle 只展示状态 */
@Component
struct DataCleanDirRow {
  @Prop name: string = '';
  @Prop @Watch('onCheckedPropChange') checked: boolean = false;
  onCheckedChange: (checked: boolean) => void = (_checked: boolean) => {};
  @State private switchOn: boolean = false;

  aboutToAppear(): void {
    this.switchOn = this.checked;
  }

  onCheckedPropChange(): void {
    this.switchOn = this.checked;
  }

  private emitChecked(checked: boolean): void {
    if (this.switchOn !== checked) {
      this.switchOn = checked;
    }
    this.onCheckedChange(checked);
  }

  build() {
    Row() {
      Text(this.name)
        .fontSize(14)
        .fontColor('#333333')
        .layoutWeight(1)

      Toggle({ type: ToggleType.Switch, isOn: this.switchOn })
        .selectedColor('#0070BB')
        .hitTestBehavior(HitTestMode.None)
        .onChange((isOn: boolean) => {
          this.emitChecked(isOn);
        })
    }
    .width('100%')
    .padding({ left: 10, right: 10, top: 10, bottom: 10 })
    .backgroundColor(Color.White)
    .onClick(() => {
      this.emitChecked(!this.switchOn);
    })
  }
}

@Builder
export function renderDataCleanFragment(_bundle: DoKitBundle, onClose: FragmentCloseHandler) {
  DataCleanPage({ onClose });
}
