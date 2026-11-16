/** 悬浮球外观。位置和触摸由浮层传入，这里只画图片。 */
@Component
export struct DoKitBallViewComponent {
  /** 图片左上角横坐标，单位 vp。 */
  @Prop x: number = 0;
  /** 图片左上角纵坐标，单位 vp。 */
  @Prop y: number = 0;
  /** 图片边长，单位 vp。和浮层计算边界用的是同一个值。 */
  @Prop ballSize: number = 58;
  /** 位置就绪后才显示，避免先在左上角闪一下。 */
  @Prop placed: boolean = false;
  /** 图片上的触摸。拖动、吸边和短按由浮层处理。 */
  onBallTouch: (event: TouchEvent) => void = () => {};

  build() {
    Image($r('app.media.dk_doraemon'))
      .width(this.ballSize)
      .height(this.ballSize)
      .objectFit(ImageFit.Contain)
      .position({ x: this.x, y: this.y })
      .visibility(this.placed ? Visibility.Visible : Visibility.None)
      .hitTestBehavior(HitTestMode.Default)
      .monopolizeEvents(true)
      .onTouch((event: TouchEvent) => {
        this.onBallTouch(event);
      })
  }
}
