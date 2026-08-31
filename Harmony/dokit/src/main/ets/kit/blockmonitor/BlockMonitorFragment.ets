import { BlockMonitorManager } from './core/BlockMonitorManager';
import { BlockListFragment } from './BlockListFragment';
import { DoKitBundle, FragmentCloseHandler } from '../core/FragmentRenderer';
import { SettingItem } from '../core/SettingItem';

/** 对标 Android BlockMonitorFragment */
export class BlockMonitorFragmentMeta {
  static readonly CLASS_NAME: string = 'BlockMonitorFragment';
}

/** 对标 Android BlockMonitorFragment.KEY_JUMP_TO_LIST */
export class BlockMonitorFragmentKeys {
  static readonly KEY_JUMP_TO_LIST: string = 'KEY_JUMP_TO_LIST';
}

class BlockMonitorStrings {
  static readonly SWITCH: string = '卡顿检测开关';
  static readonly GOTO_LIST: string = '查看卡顿记录';
  static readonly MOCK: string = '模拟卡顿';
}

/**
 * 卡顿检测首页，对标 Android BlockMonitorFragment
 */
@Component
export struct BlockMonitorFragment {
  onClose: () => void = () => {};
  jumpToList: boolean = false;
  @State private monitorOpen: boolean = BlockMonitorManager.getInstance().isRunning();
  @State private showList: boolean = false;

  aboutToAppear(): void {
    if (this.jumpToList) {
      this.showList = true;
    }
  }

  private onSwitchChange(isOn: boolean): void {
    this.monitorOpen = isOn;
    if (isOn) {
      BlockMonitorManager.getInstance().start();
    } else {
      BlockMonitorManager.getInstance().stop();
    }
  }

  private openBlockList(): void {
    this.showList = true;
  }

  private mockBlock(): void {
    setTimeout(() => {
      const start = Date.now();
      while (Date.now() - start < 2000) {
        // 模拟主线程阻塞，对标 Android Thread.sleep(2000)
      }
    }, 1000);
  }

  @Builder
  private buildClickRow(item: SettingItem, onClick: () => void) {
    Row() {
      Text(item.desc)
        .fontSize(16)
        .fontColor('#333333')
        .layoutWeight(1)
      Text('›')
        .fontSize(20)
        .fontColor('#CCCCCC')
    }
    .width('100%')
    .height(52)
    .padding({ left: 16, right: 16 })
    .backgroundColor(Color.White)
    .onClick(() => {
      onClick();
    })
  }

  build() {
    if (this.showList) {
      BlockListFragment({
        onBack: () => {
          this.showList = false;
        }
      })
    } else {
      Column() {
        Row() {
          Text('卡顿')
            .fontSize(18)
            .fontWeight(FontWeight.Medium)
            .fontColor('#111111')
            .layoutWeight(1)
            .textAlign(TextAlign.Center)

          Text('✕')
            .fontSize(20)
            .fontColor('#666666')
            .padding(8)
            .onClick(() => {
              this.onClose();
            })
        }
        .width('100%')
        .height(56)
        .padding({ left: 8, right: 8 })
        .backgroundColor(Color.White)

        Row() {
          Text(BlockMonitorStrings.SWITCH)
            .fontSize(16)
            .fontColor('#333333')
            .layoutWeight(1)

          Toggle({ type: ToggleType.Switch, isOn: this.monitorOpen })
            .selectedColor('#0070BB')
            .onChange((isOn: boolean) => {
              this.onSwitchChange(isOn);
            })
        }
        .width('100%')
        .height(52)
        .padding({ left: 16, right: 16 })
        .backgroundColor(Color.White)

        Divider().color('#EEEEEE').margin({ left: 16 })

        this.buildClickRow(new SettingItem(BlockMonitorStrings.GOTO_LIST), () => {
          this.openBlockList();
        })

        Divider().color('#EEEEEE').margin({ left: 16 })

        this.buildClickRow(new SettingItem(BlockMonitorStrings.MOCK), () => {
          this.mockBlock();
        })
      }
      .width('100%')
      .height('100%')
      .backgroundColor(Color.White)
    }
  }
}

@Builder
export function renderBlockMonitorFragment(bundle: DoKitBundle, onClose: FragmentCloseHandler) {
  BlockMonitorFragment({
    jumpToList: bundle[BlockMonitorFragmentKeys.KEY_JUMP_TO_LIST] === true,
    onClose
  });
}
