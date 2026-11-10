import { ViewCheckTree } from '../kit/viewcheck/ViewCheckTree';
import { DoKitWindowUtil } from '../util/DoKitWindowUtil';
import { DoKitOverlayEmpty } from './DoKitOverlayEmpty';
import { DOKIT_OVERLAY_TICK, DoKitOverlayBuilder, DoKitOverlayName, DoKitOverlayRecord, DoKitOverlayTable } from './DoKitOverlayTable';


@Component
struct DoKitOverlayRoot {
  /** Manager 传进来的层表。不拷贝，重画时读这一份。 */
  table: DoKitOverlayTable = new DoKitOverlayTable();
  @StorageLink(DOKIT_OVERLAY_TICK) @Watch('reloadOverlay') tick: number = 0;
  @StorageProp(DoKitWindowUtil.TOP_RECT_HEIGHT) topRectHeightPx: number = 0;
  @StorageProp(DoKitWindowUtil.NAV_INSET_PX) navInsetPx: number = 0;
  /** 按叠放序号从小到大。后画的盖住先画的。 */
  @State private layers: DoKitOverlayRecord[] = [];
  @State private stageW: number = 0;
  @State private stageH: number = 0;

  /** 出现时按当前层表画一次。 */
  aboutToAppear(): void {
    this.reloadOverlay();
  }

  /** 层表变了就按序号重画。 */
  reloadOverlay(): void {
    if (this.tick < 0) {
      return;
    }
    this.layers = this.table.copyByIndex();
  }

  /** 包一层铺满的透明容器，这一层的空白处不挡住业务页。 */
  @Builder
  private layerNode(item: DoKitOverlayRecord) {
    Stack() {
      if (item.name === DoKitOverlayName.BALL) {
        // 球要铺满才能量到窗口。这一层本身点透，点击只留给球的图片。
        DoKitOverlayItem({
          layerName: item.name,
          stageW: this.stageW,
          stageH: this.stageH,
          topInset: DoKitWindowUtil.topInsetVp(this.topRectHeightPx),
          bottomInset: DoKitWindowUtil.navInsetVp(this.navInsetPx),
          layerBuilder: item.builder ?? DoKitOverlayEmpty.LAYER
        })
          .width('100%')
          .height('100%')
          .hitTestBehavior(HitTestMode.Transparent)
      } else {
        DoKitOverlayItem({
          layerName: item.name,
          stageW: this.stageW,
          stageH: this.stageH,
          topInset: DoKitWindowUtil.topInsetVp(this.topRectHeightPx),
          bottomInset: DoKitWindowUtil.navInsetVp(this.navInsetPx),
          layerBuilder: item.builder ?? DoKitOverlayEmpty.LAYER
        })
      }
    }
    .width('100%')
    .height('100%')
    .hitTestBehavior(HitTestMode.Transparent)
  }

  /**
   * 按序号从下往上画。身份不含序号：只换叠放时不拆组件。
   * 版本仍在身份里，全屏栈换页会重建。
   */
  build() {
    Stack({ alignContent: Alignment.TopStart }) {
      ForEach(this.layers, (item: DoKitOverlayRecord) => {
        this.layerNode(item)
      }, (item: DoKitOverlayRecord) => `${item.name}#${item.revision}`)
    }
    .id(ViewCheckTree.FLOATING_ID)
    .width('100%')
    .height('100%')
    .backgroundColor(Color.Transparent)
    .hitTestBehavior(HitTestMode.Transparent)
    .onAreaChange((_oldValue: Area, area: Area) => {
      const width = Number(area.width);
      const height = Number(area.height);
      if (width > 1) {
        this.stageW = width;
      }
      if (height > 1) {
        this.stageH = height;
      }
    })
  }
}

@Component
struct DoKitOverlayItem {
  @Prop layerName: string = '';
  @Prop stageW: number = 0;
  @Prop stageH: number = 0;
  @Prop topInset: number = 0;
  @Prop bottomInset: number = 0;
  layerBuilder: DoKitOverlayBuilder = DoKitOverlayEmpty.LAYER;

  /** 用这条层自己的画法画出内容。 */
  build() {
    this.layerBuilder.builder(this.layerName, this.stageW, this.stageH, this.topInset, this.bottomInset)
  }
}


/** OverlayManager 上的根节点。透明命中写在这里，空白点击才能落到业务页。 */
@Builder
function buildDoKitOverlayRoot(table: DoKitOverlayTable) {
  // 命中模式必须写在 WrappedBuilder 的根节点上。写在内部 Stack 时，
  // 自定义组件自己仍是 Default，整屏挡住页面。
  DoKitOverlayRoot({ table: table })
    .width('100%')
    .height('100%')
    .hitTestBehavior(HitTestMode.Transparent)
}

export const DOKIT_OVERLAY_ROOT_BUILDER: WrappedBuilder<[DoKitOverlayTable]> = wrapBuilder(buildDoKitOverlayRoot);



