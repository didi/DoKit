import promptAction from '@ohos.promptAction';
import { DoKitBundle, FragmentCloseHandler } from '../core/FragmentRenderer';
import { DokitThirdLibInfo } from '../../aop/DokitThirdLibInfo';
import { ThirdLibInfoHelper } from './ThirdLibInfoHelper';
import { ThirdLibListItem, ThirdLibSortMode } from './ThirdLibInfoModels';

/** 对标 Android ThirdLibInfoFragment */
export class ThirdLibInfoFragment {
  static readonly CLASS_NAME: string = 'ThirdLibInfoFragment';
}

/**
 * 三方库信息页，对标 Android ThirdLibInfoFragment
 */
@Component
export struct ThirdLibInfoPage {
  onClose: () => void = () => {};
  @State private keyword: string = '';
  @State private sortMode: ThirdLibSortMode = ThirdLibSortMode.NAME;
  @State private items: ThirdLibListItem[] = [];

  aboutToAppear(): void {
    this.refreshList();
    if (DokitThirdLibInfo.isEmpty()) {
      promptAction.showToast({
        message: '检查 dokit.config.json5 中 thirdLibEnable 或是否注入三方库信息',
        duration: 2500
      });
    }
  }

  private refreshList(): void {
    this.items = ThirdLibInfoHelper.collect(this.keyword, this.sortMode);
  }

  private onKeywordChange(value: string): void {
    this.keyword = value;
    this.refreshList();
  }

  private onSortChange(mode: ThirdLibSortMode): void {
    this.sortMode = mode;
    this.refreshList();
  }

  @Builder
  private buildListItem(item: ThirdLibListItem) {
    Column() {
      Row() {
        Text(item.name)
          .fontSize(14)
          .fontColor('#666666')
          .maxLines(2)
          .layoutWeight(1)

        Text(ThirdLibInfoHelper.formatFileSize(item.sizeBytes))
          .fontSize(14)
          .fontColor('#333333')
          .textAlign(TextAlign.End)
          .margin({ left: 12 })
      }
      .width('100%')
      .alignItems(VerticalAlign.Top)

      Divider()
        .color('#EEEEEE')
        .margin({ top: 12 })
    }
    .width('100%')
    .padding({ left: 16, right: 16, top: 12, bottom: 0 })
  }

  build() {
    Column() {
      Row() {
        Text('三方库信息')
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
        Row() {
          Image($r('app.media.dk_search'))
            .width(16)
            .height(16)
            .margin({ right: 8 })

          TextInput({ placeholder: '搜索', text: this.keyword })
            .layoutWeight(1)
            .height(36)
            .fontSize(14)
            .backgroundColor(Color.Transparent)
            .onChange((value: string) => {
              this.onKeywordChange(value);
            })
        }
        .layoutWeight(1)
        .height(36)
        .padding({ left: 12, right: 8 })
        .margin({ right: 8 })
        .backgroundColor(Color.White)
        .borderRadius(4)
        .shadow({ radius: 6, color: '#1A000000', offsetX: 0, offsetY: 2 })

        Row() {
          Row() {
            Radio({ value: 'name', group: 'thirdLibSort' })
              .checked(this.sortMode === ThirdLibSortMode.NAME)
              .height(20)
              .onChange((checked: boolean) => {
                if (checked) {
                  this.onSortChange(ThirdLibSortMode.NAME);
                }
              })
            Text('名称')
              .fontSize(12)
              .fontColor('#666666')
              .margin({ left: 4, right: 8 })
          }

          Row() {
            Radio({ value: 'size', group: 'thirdLibSort' })
              .checked(this.sortMode === ThirdLibSortMode.SIZE)
              .height(20)
              .onChange((checked: boolean) => {
                if (checked) {
                  this.onSortChange(ThirdLibSortMode.SIZE);
                }
              })
            Text('大小')
              .fontSize(12)
              .fontColor('#666666')
              .margin({ left: 4 })
          }
        }
      }
      .width('100%')
      .padding({ left: 16, right: 16, top: 8, bottom: 8 })
      .alignItems(VerticalAlign.Center)

      if (this.items.length === 0) {
        Column() {
          Text('暂无三方库数据')
            .fontSize(14)
            .fontColor('#999999')
        }
        .layoutWeight(1)
        .width('100%')
        .justifyContent(FlexAlign.Center)
        .hitTestBehavior(HitTestMode.Default)
      } else {
        Scroll() {
          Column() {
            ForEach(this.items, (item: ThirdLibListItem, index: number) => {
              this.buildListItem(item)
            }, (item: ThirdLibListItem, index: number) => `${item.name}_${index}`)
          }
          .width('100%')
          .padding({ bottom: 24 })
        }
        .layoutWeight(1)
        .scrollBar(BarState.Off)
        .hitTestBehavior(HitTestMode.Default)
      }
    }
    .width('100%')
    .height('100%')
    .backgroundColor(Color.White)
    .hitTestBehavior(HitTestMode.Default)
  }
}

@Builder
export function renderThirdLibInfoFragment(_bundle: DoKitBundle, onClose: FragmentCloseHandler) {
  ThirdLibInfoPage({ onClose });
}
