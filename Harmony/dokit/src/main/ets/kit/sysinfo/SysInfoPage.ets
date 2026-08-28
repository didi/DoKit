import { Want } from '@kit.AbilityKit';
import { DoKitManager } from '../core/DoKitManager';
import { SysInfoCollector } from './SysInfoCollector';
import { SysInfoListItem } from './SysInfoModels';

/**
 * App 信息页，对标 Android SysInfoFragment
 */
@Component
export struct SysInfoPage {
  onClose: () => void = () => {};
  @State private items: SysInfoListItem[] = [];

  aboutToAppear(): void {
    this.items = SysInfoCollector.collect();
  }

  private openAppSettings(): void {
    const context = DoKitManager.getContext();
    if (context === null) {
      return;
    }
    const want: Want = {
      bundleName: 'com.huawei.hmos.settings',
      abilityName: 'com.huawei.hmos.settings.MainAbility',
      uri: 'application_info_entry',
      parameters: {
        pushParams: context.applicationInfo.name
      }
    };
    context.startAbility(want).catch((_err: Error) => {
      // 部分设备 settings bundle 不同，忽略失败
    });
  }

  private onItemClick(item: SysInfoListItem): void {
    if (item.isPermission) {
      this.openAppSettings();
    }
  }

  @Builder
  private buildTitleItem(item: SysInfoListItem) {
    Text(item.name)
      .fontSize(14)
      .fontColor('#999999')
      .width('100%')
      .height(56)
      .padding({ left: 16, right: 16 })
      .backgroundColor('#F5F6F8')
  }

  @Builder
  private buildInfoItem(item: SysInfoListItem) {
    Column() {
      Row() {
        Text(item.name)
          .fontSize(14)
          .fontColor('#666666')
          .maxLines(2)
          .layoutWeight(1)

        Text(item.value)
          .fontSize(14)
          .fontColor('#333333')
          .maxLines(3)
          .textAlign(TextAlign.End)
          .margin({ left: 12 })
          .layoutWeight(1)
      }
      .width('100%')
      .alignItems(VerticalAlign.Top)

      Divider()
        .color('#EEEEEE')
        .margin({ top: 12 })
    }
    .width('100%')
    .padding({ left: 16, right: 16, top: 12, bottom: 0 })
    .onClick(() => {
      this.onItemClick(item);
    })
  }

  build() {
    Column() {
      Row() {
        Text('App信息')
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

      Scroll() {
        Column() {
          ForEach(this.items, (item: SysInfoListItem, index: number) => {
            if (item.kind === 'title') {
              this.buildTitleItem(item)
            } else {
              this.buildInfoItem(item)
            }
          }, (item: SysInfoListItem, index: number) => `${item.kind}_${item.name}_${index}`)
        }
        .width('100%')
        .padding({ bottom: 24 })
      }
      .layoutWeight(1)
      .scrollBar(BarState.Off)
    }
    .width('100%')
    .height('100%')
    .backgroundColor(Color.White)
  }
}
