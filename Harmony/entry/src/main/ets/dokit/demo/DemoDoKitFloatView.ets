import { CustomKitCloseHandler } from '@didi/dokit';

/** 对标 Android DemoDoKitView */
@Builder
export function buildDemoDoKitFloatView(onClose: CustomKitCloseHandler) {
  Column() {
    Text('Demo 自定义悬浮窗')
      .fontSize(14)
      .fontColor('#333333')
      .margin({ bottom: 8 })

    Text('对标 Android DemoDoKitView')
      .fontSize(12)
      .fontColor('#999999')
      .margin({ bottom: 12 })

    Button('关闭')
      .fontSize(14)
      .onClick(() => {
        onClose();
      })
  }
  .padding(16)
  .backgroundColor(Color.White)
  .borderRadius(8)
  .shadow({ radius: 8, color: '#33000000', offsetX: 0, offsetY: 2 })
  .position({ x: 48, y: 180 })
}
