import { CustomKitCloseHandler } from '@didi/dokit';

/** 对标 Android CustomDokitFragment */
@Builder
export function buildCustomDokitFragment(onClose: CustomKitCloseHandler) {
  Column() {
    Row() {
      Text('自定义全屏 Kit')
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
          onClose();
        })
    }
    .width('100%')
    .height(56)
    .padding({ left: 8, right: 8 })
    .backgroundColor(Color.White)

    Column() {
      Text('对标 Android CustomDokitFragment')
        .fontSize(14)
        .fontColor('#666666')
        .margin({ top: 24 })
    }
    .layoutWeight(1)
    .width('100%')
  }
  .width('100%')
  .height('100%')
  .backgroundColor('#F5F6F8')
}
