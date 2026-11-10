import { CloseRegistrar, DoKit, DoKitParams } from '@didi-hummer/dokit';


/**
 * 对标 Android DemoDoKitView + dk_demo.xml
 */
@Builder
export function DemoDoKitViewBuilder(_params: DoKitParams, _register: CloseRegistrar) {
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
      .width(40)
      .height(40)
      .textAlign(TextAlign.Center)
      .hitTestBehavior(HitTestMode.Default)
      .monopolizeEvents(true)
      .onClick(() => {
        DoKit.removeFloatingView(DemoDoKitView.KIT_NAME);
      })
  }
}

export class DemoDoKitView {
  static readonly KIT_NAME: string = 'DemoDoKitView';
}
