import { CustomKitCloseHandler, DoKit, DoKitBundle } from '@didi-hummer/dokit';
import { CloseHandler, DoKitParams } from '@didi-hummer/dokit/src/main/ets/core/CustomViewRegistry';


/**
 * 对标 Android DemoDoKitView + dk_demo.xml
 */
@Builder
export function DemoDoKitViewBuilder(params?: DoKitParams, handler?: CloseHandler) {
  DemoDoKitView({ params, handler })
}

@Component
export struct DemoDoKitView {
  static readonly KIT_NAME: string = 'DemoDoKitView';
  protected params?: DoKitParams;
  protected handler?: CloseHandler;

  build() {
    Stack({ alignContent: Alignment.TopEnd }) {
      Column() {
        Text('我是demo浮标')
          .fontSize(14)
          .fontColor('#333333')
      }
      .width(280)
      .height(72)
      .justifyContent(FlexAlign.Center)
      .backgroundColor('#F0F6FF')
      .borderRadius(8)
      .border({ width: 1, color: '#D0E3FF' })

      Text('X')
        .fontSize(20)
        .fontColor('#666666')
        .margin({ top: 8, right: 8 })
        .onClick(() => {
          if (this.handler) {
            this.handler();
          }
        })
    }
  }
}
