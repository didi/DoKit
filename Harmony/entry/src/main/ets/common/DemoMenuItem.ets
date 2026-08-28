@Component
export struct DemoMenuItem {
  @Prop title: string = '';
  onTap: () => void = () => {};

  build() {
    Row() {
      Text(this.title)
        .fontSize(15)
        .fontColor('#333333')
        .layoutWeight(1)

      Text('>')
        .fontSize(16)
        .fontColor('#CCCCCC')
    }
    .width('100%')
    .height(52)
    .padding({ left: 16, right: 16 })
    .backgroundColor(Color.White)
    .borderRadius(8)
    .margin({ left: 14, right: 14, bottom: 8 })
    .onClick(() => {
      this.onTap();
    })
  }
}

@Component
export struct DemoSectionTitle {
  @Prop title: string = '';

  build() {
    Text(this.title)
      .fontSize(14)
      .fontColor('#999999')
      .width('100%')
      .margin({ left: 28, top: 16, bottom: 8 })
  }
}
