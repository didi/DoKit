import { hilog } from '@kit.PerformanceAnalysisKit';

const DOMAIN = 0xD0F2;
const TAG = 'ViewSetupHelper';

/**
 * 对标 Android dokit/ViewSetupHelper + layout_demo_custom.xml
 */
@Component
export struct ViewSetupHelper {
  /** 是否展示「跳转全屏 Fragment」按钮，对标 setupButton(test1) */
  showFullScreenLauncher: boolean = false;
  onLaunchFullScreen: () => void = () => {};

  @State private toggleChecked: boolean = false;

  build() {
    Column() {
      if (this.showFullScreenLauncher) {
        Button('TestSimpleDokitFragment')
          .fontSize(14)
          .margin({ bottom: 8 })
          .onClick(() => {
            this.onLaunchFullScreen();
          })
      }

      Toggle({ type: ToggleType.Checkbox, isOn: this.toggleChecked })
        .selectedColor('#337CC4')
        .margin({ bottom: 8 })
        .onChange((isOn: boolean) => {
          this.toggleChecked = isOn;
          hilog.info(DOMAIN, TAG, 'TB %{public}s', isOn ? 'true' : 'false');
        })

      TextInput({ placeholder: '测试一下键盘焦点' })
        .width('100%')
        .height(40)
        .margin({ top: 4 })
    }
    .alignItems(HorizontalAlign.Start)
    .padding(20)
  }
}
