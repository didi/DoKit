import { DoKit, DoKitParams, FragmentCloseHandler } from '@didi-hummer/dokit';

/** 多页面 kit 的全屏页 A */
export class MultiPageA {
  static readonly CLASS_NAME: string = 'MultiPageA';
}

/** 多页面 kit 的全屏页 B */
export class MultiPageB {
  static readonly CLASS_NAME: string = 'MultiPageB';
}

/** 多页面 kit 的全屏页 C */
export class MultiPageC {
  static readonly CLASS_NAME: string = 'MultiPageC';
}

@Builder
function pageTitle(backLabel: string, title: string, accent: string) {
  Row() {
    Text(backLabel)
      .fontSize(16)
      .fontColor(accent)
      .padding({ left: 4, right: 8, top: 8, bottom: 8 })
      .onClick(() => {
        DoKit.popFullScreen();
      })

    Text(title)
      .fontSize(18)
      .fontWeight(FontWeight.Medium)
      .fontColor('#111111')
      .layoutWeight(1)
      .textAlign(TextAlign.Center)

    Text(' ')
      .width(64)
  }
  .width('100%')
  .height(56)
  .padding({ left: 8, right: 8 })
  .backgroundColor(Color.White)
}

@Builder
function pageCopy(path: string, detail: string, accent: string) {
  Column() {
    Text(path)
      .fontSize(28)
      .fontWeight(FontWeight.Bold)
      .fontColor(accent)

    Text(detail)
      .fontSize(15)
      .fontColor('#333333')
      .lineHeight(24)
      .margin({ top: 16 })
  }
  .width('100%')
  .alignItems(HorizontalAlign.Start)
}

@Builder
export function buildMultiPageA(_params: DoKitParams, _onClose: FragmentCloseHandler) {
  Column() {
    pageTitle('关闭', '页面 A', '#337CC4')
    Column() {
      pageCopy('A', '这是全屏根页。打开 B，再从 B 打开 C。返回顺序是 C → B → A。', '#337CC4')
      Text('打开 B')
        .width('100%')
        .height(48)
        .margin({ top: 32 })
        .fontSize(16)
        .fontColor(Color.White)
        .textAlign(TextAlign.Center)
        .borderRadius(24)
        .backgroundColor('#337CC4')
        .onClick(() => {
          DoKit.pushFullScreen(MultiPageB.CLASS_NAME, wrapBuilder(buildMultiPageB));
        })
    }
    .width('100%')
    .layoutWeight(1)
    .padding(24)
    .alignItems(HorizontalAlign.Start)
  }
  .width('100%')
  .height('100%')
  .backgroundColor('#F5F6F8')
}

@Builder
export function buildMultiPageB(_params: DoKitParams, _onClose: FragmentCloseHandler) {
  Column() {
    pageTitle('返回 A', '页面 B', '#1F8A4C')
    Column() {
      pageCopy('A / B', '从 A 压栈打开。可以继续打开 C，也可以返回 A。', '#1F8A4C')
      Text('打开 C')
        .width('100%')
        .height(48)
        .margin({ top: 32 })
        .fontSize(16)
        .fontColor(Color.White)
        .textAlign(TextAlign.Center)
        .borderRadius(24)
        .backgroundColor('#1F8A4C')
        .onClick(() => {
          DoKit.pushFullScreen(MultiPageC.CLASS_NAME, wrapBuilder(buildMultiPageC));
        })
    }
    .width('100%')
    .layoutWeight(1)
    .padding(24)
    .alignItems(HorizontalAlign.Start)
  }
  .width('100%')
  .height('100%')
  .backgroundColor('#F5F6F8')
}

@Builder
export function buildMultiPageC(_params: DoKitParams, _onClose: FragmentCloseHandler) {
  Column() {
    Row() {
      Text('返回 B')
        .width(72)
        .fontSize(16)
        .fontColor('#C56A12')
        .textAlign(TextAlign.Start)
        .padding({ top: 8, bottom: 8 })
        .onClick(() => {
          DoKit.popFullScreen();
        })

      Text('页面 C')
        .fontSize(18)
        .fontWeight(FontWeight.Medium)
        .fontColor('#111111')
        .layoutWeight(1)
        .textAlign(TextAlign.Center)

      Text('关闭')
        .width(72)
        .fontSize(16)
        .fontColor('#C56A12')
        .textAlign(TextAlign.End)
        .padding({ top: 8, bottom: 8 })
        .onClick(() => {
          DoKit.closeFullScreen();
        })
    }
    .width('100%')
    .height(56)
    .padding({ left: 12, right: 12 })
    .backgroundColor(Color.White)

    Column() {
      pageCopy('A / B / C', '从 B 压栈打开。返回后回到 B。点「清空 B」后，B 从栈里去掉，再点左上角返回会回到 A。右上角关闭会直接退出。', '#C56A12')
      Text('清空 B')
        .width('100%')
        .height(48)
        .margin({ top: 32 })
        .fontSize(16)
        .fontColor(Color.White)
        .textAlign(TextAlign.Center)
        .borderRadius(24)
        .backgroundColor('#C56A12')
        .onClick(() => {
          DoKit.popFullScreenPage(MultiPageB.CLASS_NAME);
        })
    }
    .width('100%')
    .layoutWeight(1)
    .padding(24)
    .alignItems(HorizontalAlign.Start)
  }
  .width('100%')
  .height('100%')
  .backgroundColor('#F5F6F8')
}
