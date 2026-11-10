import { CloseHandler, CloseRegistrar, DoKit, DoKitParams } from '@didi-hummer/dokit';

/** 浮窗1 的悬浮卡片 */
export class FloatWindow1View {
  static readonly CLASS_NAME: string = 'FloatWindow1';
}

@Component
struct FloatWindow1Card {
  register: CloseRegistrar = (_handler: CloseHandler): void => {
  };
  private readonly onFloatClose: CloseHandler = (): void => {
    console.log('2222');
  };

  aboutToAppear(): void {
    this.register(this.onFloatClose);
  }

  build() {
    Stack({ alignContent: Alignment.TopEnd }) {
      Column() {
        Text('浮窗1')
          .fontSize(16)
          .fontWeight(FontWeight.Medium)
          .fontColor('#111111')
        Text('这是一个悬浮窗')
          .fontSize(13)
          .fontColor('#666666')
          .margin({ top: 6 })
      }
      .width(220)
      .height(88)
      .justifyContent(FlexAlign.Center)
      .backgroundColor(Color.White)
      .borderRadius(8)
      .shadow({ radius: 8, color: '#33000000', offsetX: 0, offsetY: 2 })

      Text('✕')
        .fontSize(18)
        .fontColor('#666666')
        .width(40)
        .height(40)
        .textAlign(TextAlign.Center)
        .hitTestBehavior(HitTestMode.Default)
        .monopolizeEvents(true)
        .onClick(() => {
          DoKit.removeFloatingView(FloatWindow1View.CLASS_NAME);
        })
    }
  }
}

@Builder
export function FloatWindow1Builder(_params: DoKitParams, register: CloseRegistrar) {
  FloatWindow1Card({ register: register })
}
