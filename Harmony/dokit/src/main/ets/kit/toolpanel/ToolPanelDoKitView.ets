import promptAction from '@ohos.promptAction';
import { DoKitConstants } from '../../constant/DoKitConstants';
import { resolveKitIcon } from '../../config/KitIconResolver';
import { InnerKitRegistry } from '../core/InnerKitRegistry';
import { DoKitViewManager } from '../core/DoKitViewManager';
import { DoKitManager } from '../core/DoKitManager';
import { KitGroup, KitGroupId, KitItem } from '../../model/KitModels';

/** 工具面板，对标 Android kit/toolpanel/ToolPanelDoKitView */
@Component
export struct ToolPanelDoKitView {
  kitGroups: KitGroup[] = [];
  onClose: () => void = () => {};

  private onKitClick(item: KitItem): void {
    if (item.customKit !== undefined) {
      const context = DoKitManager.getContext();
      if (context === null) {
        return;
      }
      const hidePanel = item.customKit.onClickWithReturn(context);
      if (hidePanel) {
        DoKitViewManager.detachToolPanel();
      }
      return;
    }
    const innerKit = InnerKitRegistry.get(item.innerKitId);
    if (innerKit !== undefined) {
      const context = DoKitManager.getContext();
      if (context === null) {
        return;
      }
      const hidePanel = innerKit.onClickWithReturn(context);
      if (hidePanel) {
        DoKitViewManager.detachToolPanel();
      }
      return;
    }
    promptAction.showToast({
      message: `${item.name}：${DoKitConstants.DEV_TIP}`,
      duration: 2000
    });
  }

  build() {
    Column() {
      Row() {
        Text('DoKit')
          .fontSize(18)
          .fontWeight(FontWeight.Bold)
          .fontColor('#333333')
          .layoutWeight(1)

        Text('v' + DoKitConstants.VERSION)
          .fontSize(12)
          .fontColor('#999999')
          .margin({ right: 12 })

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
      .padding({ left: 16, right: 8 })
      .backgroundColor(Color.White)

      Scroll() {
        Column() {
          ForEach(this.kitGroups, (group: KitGroup) => {
            Column() {
              Row() {
                Text(group.groupName)
                  .fontSize(14)
                  .fontWeight(FontWeight.Medium)
                  .fontColor('#666666')
                if (group.groupId === KitGroupId.PLATFORM) {
                  Text('(www.dokit.cn)')
                    .fontSize(12)
                    .fontColor('#FF0006')
                    .margin({ left: 8 })
                }
              }
              .width('100%')
              .margin({ top: 16, bottom: 8, left: 4 })

              Grid() {
                ForEach(group.kits.filter((k: KitItem) => k.checked), (item: KitItem) => {
                  GridItem() {
                    Column() {
                      Image(resolveKitIcon(item))
                        .width(34)
                        .height(34)
                        .objectFit(ImageFit.Contain)

                      Text(item.name)
                        .fontSize(11)
                        .fontColor('#333333')
                        .margin({ top: 6 })
                        .maxLines(2)
                        .textAlign(TextAlign.Center)
                        .width('100%')
                    }
                    .width('100%')
                    .padding({ top: 8, bottom: 8 })
                    .onClick(() => {
                      this.onKitClick(item);
                    })
                  }
                }, (item: KitItem) => item.innerKitId)
              }
              .columnsTemplate('1fr 1fr 1fr 1fr')
              .rowsGap(4)
              .columnsGap(4)
              .width('100%')
            }
            .width('100%')
            .alignItems(HorizontalAlign.Start)
          }, (group: KitGroup) => group.groupId)
        }
        .width('100%')
        .padding({ left: 12, right: 12, bottom: 24 })
      }
      .layoutWeight(1)
      .scrollBar(BarState.Off)
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#F5F6F8')
    .hitTestBehavior(HitTestMode.Default)
  }
}
