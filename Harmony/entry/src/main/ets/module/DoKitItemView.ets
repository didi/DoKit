/** 对标 Android module/DoKitItemView */
@Component
export struct DoKitItemView {
  @Prop itemText: string = '';
  onTap: () => void = () => {};

  build() {
    Row() {
      Text(this.itemText)
        .fontSize(15)
        .fontColor('#333333')
        .layoutWeight(1)

      Text('>')
        .fontSize(16)
        .fontColor('#CCCCCC')
    }
    .width('100%')
    .height(60)
    .padding({ left: 16, right: 16 })
    .backgroundColor(Color.White)
    .borderRadius(8)
    .margin({ left: 14, right: 14, bottom: 8 })
    .onClick(() => {
      this.onTap();
    })
  }
}

/** 对标 Android activity_dokit_main.xml 分组标题 */
@Component
export struct DoKitSectionTitle {
  @Prop title: string = '';

  build() {
    Text(this.title)
      .fontSize(14)
      .fontColor('#999999')
      .width('100%')
      .margin({ left: 28, top: 16, bottom: 8 })
  }
}
