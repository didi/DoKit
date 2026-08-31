import promptAction from '@ohos.promptAction';
import { DoKit, DoKitConstants, DoKitFloatingWindow, DoKitOverlayState } from '@didi/dokit';
import { buildEntryCustomFloating, buildEntryCustomFullScreen } from '../dokit/DemoCustomKitRenderers';
import { DoKitItemView, DoKitSectionTitle } from '../module/DoKitItemView';
import { DemoMenuSection, getDemoMenuSections } from '../module/DemoMenuData';
import { CrashTest } from '../module/CrashTest';
import { MethodCostTest } from '../module/MethodCostTest';

/**
 * Demo 主页 UI 入口，对标 Android MainDoKitActivity + activity_dokit_main.xml
 */
@Entry
@Component
struct Index {
  @State menuSections: DemoMenuSection[] = getDemoMenuSections();
  @StorageLink(DoKitOverlayState.ICON_VISIBLE_KEY) isFloatIconShow: boolean = true;

  private onItemClick(title: string): void {
    switch (title) {
      case '显示/隐藏快捷入口':
        if (this.isFloatIconShow) {
          DoKit.hide();
          promptAction.showToast({ message: '已隐藏快捷入口', duration: 1500 });
        } else {
          DoKit.show();
          promptAction.showToast({ message: '已显示快捷入口', duration: 1500 });
        }
        return;
      case '打开工具窗口':
        DoKit.showToolPanel();
        return;
      case '模拟耗时函数调用':
        MethodCostTest.test();
        promptAction.showToast({ message: '已触发耗时函数调用', duration: 1500 });
        return;
      case '崩溃模拟':
        CrashTest.test();
        return;
      default:
        promptAction.showToast({
          message: `${title}：${DoKitConstants.DEV_TIP}`,
          duration: 2000
        });
    }
  }

  build() {
    Stack() {
      Scroll() {
        Column() {
          Stack({ alignContent: Alignment.Top }) {
            Column()
              .width('100%')
              .height(80)
              .backgroundColor('#337CC4')

            Column() {
              Image($r('app.media.dk_dokit_big'))
                .width(65)
                .height(65)
                .objectFit(ImageFit.Contain)
                .margin({ top: 15 })

              Text('一款面向泛前端产品研发全生命周期的效率平台')
                .fontSize(12)
                .fontColor('#333333')
                .margin({ top: 12, bottom: 20 })
                .textAlign(TextAlign.Center)
                .padding({ left: 12, right: 12 })
            }
            .width('100%')
            .height(140)
            .backgroundColor(Color.White)
            .borderRadius(8)
            .margin({ left: 18, right: 18, top: 10 })
            .shadow({ radius: 12, color: '#1A000000', offsetX: 0, offsetY: 4 })
          }
          .width('100%')
          .height(160)

          ForEach(this.menuSections, (section: DemoMenuSection) => {
            Column() {
              DoKitSectionTitle({ title: section.title })
              if (section.title === '工具入口') {
                Row() {
                  Text('快捷入口状态：')
                    .fontSize(13)
                    .fontColor('#666666')
                  Text(this.isFloatIconShow ? '显示中' : '已隐藏')
                    .fontSize(13)
                    .fontColor(this.isFloatIconShow ? '#337CC4' : '#999999')
                    .fontWeight(FontWeight.Medium)
                }
                .width('100%')
                .padding({ left: 28, right: 28, bottom: 4 })
              }
              ForEach(section.items, (item: string) => {
                DoKitItemView({
                  itemText: item,
                  onTap: () => {
                    this.onItemClick(item);
                  }
                })
              }, (item: string) => item)
            }
            .width('100%')
          }, (section: DemoMenuSection) => section.title)

          Text('v' + DoKitConstants.VERSION)
            .fontSize(14)
            .fontColor('#999999')
            .margin({ top: 26, bottom: 40 })
        }
        .width('100%')
      }
      .width('100%')
      .height('100%')
      .backgroundColor('#F5F6F8')
      .scrollBar(BarState.Off)

      DoKitFloatingWindow({
        customFullScreenPage: buildEntryCustomFullScreen,
        customFloatingPage: buildEntryCustomFloating
      })
    }
    .width('100%')
    .height('100%')
  }
}
