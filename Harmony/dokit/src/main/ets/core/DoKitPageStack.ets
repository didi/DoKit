import { BundleKey } from '../constant/BundleKey';
import { FragmentIndex } from '../constant/FragmentIndex';
import { DoKitOverlayState } from '../model/DoKitOverlayState';
import { DoKitParams } from './CustomViewRegistry';
import { SystemFragmentRegistry } from './SystemFragmentRigistry';

/** 按名字删页之后的结果。 */
export class DoKitPageRemoveResult {
  /** 栈里有没有删掉至少一页。 */
  removed: boolean = false;
  /** 删完以后栈是不是空的。 */
  empty: boolean = false;
  /** 栈顶是不是换了。只删下面的页时为 false。 */
  topChanged: boolean = false;
}

/** 全屏栈里的一页。 */
export class DoKitPageRecord {
  /** 页面注册名。用来找 Builder。 */
  name: string;
  /** 打开这一页时带进来的参数。 */
  params: DoKitParams;
  /** true：系统页，查系统表。false：自定义页，查自定义表。 */
  isSystem: boolean;

  constructor(name: string, params: DoKitParams = {}, isSystem: boolean = false) {
    this.name = name;
    this.params = params;
    this.isSystem = isSystem;
  }
}

/**
 * 全屏页面栈。
 * open 清空后打开根页，push 压入下级页，pop 退一层。
 * 栈变了会加版本号，并通知浮层重新对齐。
 */
export class DoKitPageStack {
  /** 从下到上的页面记录。最后一条是当前页。 */
  private static records: DoKitPageRecord[] = [];
  /** 栈内容变过几次。全屏容器靠它判断要不要拆掉旧页重画。 */
  private static revision: number = 0;
  /** 和 DoKitViewManager 共用的浮层状态。用来在栈变化时对齐浮层。 */
  private static overlayState: DoKitOverlayState | null = null;

  /** 挂上浮层状态。和 ViewManager 里的是同一个实例。 */
  static bindOverlayState(state: DoKitOverlayState): void {
    DoKitPageStack.overlayState = state;
  }

  /** 当前栈版本。 */
  static getRevision(): number {
    return DoKitPageStack.revision;
  }

  /** 清空栈，只留这一页当根页。同时关掉面板开关，页关掉后面板不会自己打开。 */
  static open(targetName: string, params: DoKitParams | undefined, isSystemFragment: boolean): void {
    DoKitPageStack.records = [DoKitPageStack.pageRecord(targetName, params, isSystemFragment)];
    DoKitPageStack.closePanelWithoutSync();
    DoKitPageStack.notify();
  }

  /** 在当前页上面再压一页。同时关掉面板开关，页关掉后面板不会自己打开。 */
  static push(targetName: string, params: DoKitParams | undefined, isSystemFragment: boolean): void {
    DoKitPageStack.records.push(DoKitPageStack.pageRecord(targetName, params, isSystemFragment));
    DoKitPageStack.closePanelWithoutSync();
    DoKitPageStack.notify();
  }

  /**
   * 退掉栈顶那一页。
   * 栈本来就是空的，直接返回 false。
   * 返回 true 表示退完以后还有页；false 表示已经退空，全屏层该卸掉。
   */
  static pop(): boolean {
    if (DoKitPageStack.records.length === 0) {
      return false;
    }
    DoKitPageStack.records.pop();
    DoKitPageStack.notify();
    return DoKitPageStack.records.length > 0;
  }

  /**
   * 删掉栈里所有同名页，其余页面先后顺序不变。
   * 栈顶变了才通知界面，包括删空。只删下面的页时，当前页不用重画。
   */
  static removeByName(name: string): DoKitPageRemoveResult {
    const result = new DoKitPageRemoveResult();
    // 记下删之前的栈顶，后面用来判断顶有没有换
    const oldTop = DoKitPageStack.peek();
    const kept: DoKitPageRecord[] = [];
    for (let i = 0; i < DoKitPageStack.records.length; i++) {
      const item = DoKitPageStack.records[i];
      if (item === undefined) {
        continue;
      }
      // 名字对上的丢掉
      if (item.name === name) {
        result.removed = true;
        continue;
      }
      kept.push(item);
    }
    // 一个都没删到，栈不用动，也不通知
    if (!result.removed) {
      return result;
    }
    DoKitPageStack.records = kept;
    result.empty = kept.length === 0;
    // 新栈顶和旧栈顶不是同一条，顶就换了
    result.topChanged = DoKitPageStack.peek() !== oldTop;
    if (result.topChanged) {
      DoKitPageStack.notify();
    }
    return result;
  }

  /** 清空整个栈。本来就是空的就什么都不做。 */
  static clear(): void {
    if (DoKitPageStack.records.length === 0) {
      return;
    }
    DoKitPageStack.records = [];
    DoKitPageStack.notify();
  }

  /** 看一眼栈顶。栈空时返回 undefined。 */
  static peek(): DoKitPageRecord | undefined {
    if (DoKitPageStack.records.length === 0) {
      return undefined;
    }
    return DoKitPageStack.records[DoKitPageStack.records.length - 1];
  }

  /** 栈里现在有几页。 */
  static size(): number {
    return DoKitPageStack.records.length;
  }

  /** 栈是不是空的。 */
  static isEmpty(): boolean {
    return DoKitPageStack.records.length === 0;
  }

  /**
   * 组装一条进栈记录。
   * 按页面名先查系统表，有就记成系统页，没有再记成业务页。
   * 参数里写上对应标记和页面名字，画的时候按这个标记查表。
   */
  private static pageRecord(targetName: string, params: DoKitParams | undefined,
    _isSystemFragment: boolean): DoKitPageRecord {
    const isSystemFragment = SystemFragmentRegistry.hasSystemFragment(targetName);
    const pageParams: DoKitParams = params ?? {};
    pageParams[BundleKey.FRAGMENT_INDEX] =
      isSystemFragment ? FragmentIndex.FRAGMENT_SYSTEM : FragmentIndex.FRAGMENT_CUSTOM;
    pageParams[isSystemFragment ? BundleKey.SYSTEM_FRAGMENT_CLASS : BundleKey.CUSTOM_FRAGMENT_CLASS] = targetName;
    return new DoKitPageRecord(targetName, pageParams, isSystemFragment);
  }

  /** 进栈前关掉面板开关。不在这里刷新，notify 会一起对齐。 */
  private static closePanelWithoutSync(): void {
    if (DoKitPageStack.overlayState !== null) {
      DoKitPageStack.overlayState.dismissPanel();
    }
  }

  /** 栈变了：版本加一，让浮层按新栈重新对齐。 */
  private static notify(): void {
    DoKitPageStack.revision++;
    if (DoKitPageStack.overlayState !== null) {
      DoKitPageStack.overlayState.requestFrameSync();
    }
  }
}
