import { BlockInfo } from './bean/BlockInfo';
import { BlockListAdapter } from './BlockListAdapter';
import { BlockMonitorManager } from './core/BlockMonitorManager';
import { OnBlockInfoUpdateListener } from './core/OnBlockInfoUpdateListener';

/**
 * 卡顿检测日志列表，对标 Android BlockListFragment
 */
@Component
export struct BlockListFragment {
  onBack: () => void = () => {};
  @State private blockItems: BlockInfo[] = [];
  @State private showDetail: boolean = false;
  @State private detailText: string = '';
  private blockUpdateListener: OnBlockInfoUpdateListener = (blockInfo: BlockInfo) => {
    this.onBlockInfoUpdate(blockInfo);
  };

  aboutToAppear(): void {
    this.load();
    BlockMonitorManager.getInstance().setOnBlockInfoUpdateListener(this.blockUpdateListener);
  }

  aboutToDisappear(): void {
    BlockMonitorManager.getInstance().setOnBlockInfoUpdateListener(null);
  }

  private load(): void {
    const infos = BlockMonitorManager.getInstance().getBlockInfoList();
    infos.sort((lhs: BlockInfo, rhs: BlockInfo) => {
      return rhs.time - lhs.time;
    });
    this.blockItems = infos;
  }

  private onBlockInfoUpdate(blockInfo: BlockInfo): void {
    const items = [blockInfo, ...this.blockItems];
    this.blockItems = items;
  }

  private openDetail(info: BlockInfo): void {
    this.detailText = info.toString();
    this.showDetail = true;
  }

  private handleBack(): void {
    if (this.showDetail) {
      this.showDetail = false;
      this.detailText = '';
      return;
    }
    this.onBack();
  }

  @Builder
  private buildListItem(info: BlockInfo, index: number) {
    Column() {
      Text(BlockListAdapter.formatTitle(info, index, this.blockItems.length))
        .fontSize(14)
        .fontColor('#333333')
        .width('100%')
      Text(BlockListAdapter.formatTime(info.time))
        .fontSize(12)
        .fontColor('#999999')
        .width('100%')
        .margin({ top: 4 })
    }
    .width('100%')
    .padding({ left: 16, right: 16, top: 12, bottom: 12 })
    .backgroundColor(Color.White)
    .onClick(() => {
      this.openDetail(info);
    })
  }

  build() {
    Column() {
      Row() {
        Text(this.showDetail ? '卡顿详情' : '卡顿列表')
          .fontSize(18)
          .fontWeight(FontWeight.Medium)
          .fontColor('#111111')
          .layoutWeight(1)
          .textAlign(TextAlign.Center)

        if (this.showDetail) {
          Text('←')
            .fontSize(20)
            .fontColor('#666666')
            .padding(8)
            .onClick(() => {
              this.handleBack();
            })
        } else {
          Text('✕')
            .fontSize(20)
            .fontColor('#666666')
            .padding(8)
            .onClick(() => {
              this.onBack();
            })
        }
      }
      .width('100%')
      .height(56)
      .padding({ left: 8, right: 8 })
      .backgroundColor(Color.White)

      if (this.showDetail) {
        Scroll() {
          Text(this.detailText)
            .fontSize(12)
            .fontColor('#333333')
            .width('100%')
            .padding(16)
        }
        .layoutWeight(1)
        .scrollBar(BarState.Off)
      } else {
        if (this.blockItems.length === 0) {
          Column() {
            Text('暂无卡顿记录')
              .fontSize(14)
              .fontColor('#999999')
          }
          .width('100%')
          .layoutWeight(1)
          .justifyContent(FlexAlign.Center)
        } else {
          List() {
            ForEach(this.blockItems, (info: BlockInfo, index: number) => {
              ListItem() {
                this.buildListItem(info, index)
              }
            }, (info: BlockInfo, index: number) => `${info.time}_${index}`)
          }
          .layoutWeight(1)
          .divider({ strokeWidth: 1, color: '#EEEEEE' })
        }
      }
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#F5F6F8')
  }
}
