/** 浮层列表刷新记号。顺序或成员变了才加一。 */
export const DOKIT_OVERLAY_TICK: string = 'dokitOverlayTick';

/** 一层浮层的绘制入口。参数：名字、宽、高、顶部避让、底部避让。 */
export type DoKitOverlayBuilder = WrappedBuilder<[string, number, number, number, number]>;

/** 框架层名字。浮窗用自己的名字，不写在这里。 */
export class DoKitOverlayName {
  /** 悬浮球。 */
  static readonly BALL: string = 'dokit.ball';
  /** 工具面板。 */
  static readonly PANEL: string = 'dokit.panel';
  /** 全屏功能页。同一时刻只有这一层，画的是页面栈顶。 */
  static readonly PAGE: string = 'dokit.page';
}

/** 一层浮层的记录。层表里改原对象，交给界面时再拷一份。index 越大越靠上。 */
export class DoKitOverlayRecord {
  /** 层名字。 */
  name: string = '';
  /** 叠放序号。数字大的后画，压在上面。 */
  index: number = 0;
  /** 内容版本。变了 ForEach 才会拆掉旧节点重建。 */
  revision: number = 0;
  /** 要画在全屏页上面。 */
  abovePage: boolean = false;
  /** 要画在工具面板上面。 */
  abovePanel: boolean = false;
  /** 这一层怎么画。 */
  builder: DoKitOverlayBuilder | null = null;

  /** 拷一份当前字段。层表继续改原对象，不影响已经交给界面的这份。 */
  copy(): DoKitOverlayRecord {
    const copied = new DoKitOverlayRecord();
    copied.name = this.name;
    copied.index = this.index;
    copied.revision = this.revision;
    copied.abovePage = this.abovePage;
    copied.abovePanel = this.abovePanel;
    copied.builder = this.builder;
    return copied;
  }
}

/**
 * 浮层顺序表。后挂上的 index 更大，画在上面。
 * 全屏页和工具面板没有第二个窗口，带标记的记录会在 requestRedraw 时挪到它们之上。
 */
export class DoKitOverlayTable {
  /** 当前叠着的层。 */
  private readonly items: DoKitOverlayRecord[] = [];
  /** 上次已经通知给界面的摘要。一样就不刷新。 */
  private lastSignature: string = '';

  /** 没有这条就按标记插进去。已经有则只更新画法和盖住标记，序号和版本不动。 */
  addOverlayOrUpdateDraw(name: string, builder: DoKitOverlayBuilder, abovePage: boolean, abovePanel: boolean): void {
    const found = this.find(name);
    if (found !== undefined) {
      found.builder = builder;
      found.abovePage = abovePage;
      found.abovePanel = abovePanel;
      return;
    }
    const item = new DoKitOverlayRecord();
    item.name = name;
    item.builder = builder;
    item.abovePage = abovePage;
    item.abovePanel = abovePanel;
    // 新层插在该盖住的锚点下面，或排到当前最上面
    item.index = this.initialIndex(abovePage, abovePanel);
    item.revision = 1;
    this.items.push(item);
  }

  /**
   * 层还在，但里面的内容换了。版本加一，ForEach 才会拆掉这一层重画。
   * 全屏栈只换栈顶时层不会增减，不调用这个的话界面一直停在旧页。
   */
  touch(name: string): void {
    const found = this.find(name);
    if (found === undefined) {
      return;
    }
    found.revision++;
  }

  /** 按名字拿掉一层。没有这个名字则什么都不做。 */
  detach(name: string): void {
    for (let i = 0; i < this.items.length; i++) {
      const item = this.items[i];
      if (item !== undefined && item.name === name) {
        this.items.splice(i, 1);
        return;
      }
    }
  }

  /** 去掉不在这份名单里的浮窗。球、面板、全屏页保留。 */
  removeUnlistedFloats(names: string[]): void {
    const kept: DoKitOverlayRecord[] = [];
    for (let i = 0; i < this.items.length; i++) {
      const item = this.items[i];
      if (item === undefined) {
        continue;
      }
      if (this.isReserved(item.name) || this.containsName(names, item.name)) {
        kept.push(item);
      }
    }
    // 清空后放回同一批对象，序号和版本都还在
    this.items.length = 0;
    for (let i = 0; i < kept.length; i++) {
      const item = kept[i];
      if (item !== undefined) {
        this.items.push(item);
      }
    }
  }

  /** 先按盖住标记调整 index。层表和上次不一样，才通知界面重画。 */
  requestRedraw(): void {
    this.arrangeIndex();
    const signature = this.signature();
    // 名字、序号、版本都没变，界面不用重画
    if (signature === this.lastSignature) {
      return;
    }
    this.lastSignature = signature;
    const tick = (AppStorage.get<number>(DOKIT_OVERLAY_TICK) ?? 0) + 1;
    AppStorage.setOrCreate(DOKIT_OVERLAY_TICK, tick);
  }

  /** 按序号从小到大拷出一份，给界面先画下层、再画上层。 */
  copyByIndex(): DoKitOverlayRecord[] {
    const result: DoKitOverlayRecord[] = [];
    for (let i = 0; i < this.items.length; i++) {
      const item = this.items[i];
      if (item === undefined || item.builder === null) {
        continue;
      }
      result.push(item.copy());
    }
    this.sortByIndex(result);
    return result;
  }

  /** 清空层表和上次的摘要。 */
  clear(): void {
    this.items.length = 0;
    this.lastSignature = '';
  }

  /** 重画前调整 index：先盖过全屏页，再盖过工具面板，最后让球回到最上面。 */
  private arrangeIndex(): void {
    this.raiseIndexAbove(DoKitOverlayName.PAGE, true);
    this.raiseIndexAbove(DoKitOverlayName.PANEL, false);
    this.raiseBall();
  }

  /**
   * 把该盖住锚点、index 却还不超过锚点的层，把 index 调到锚点上面。
   * 按原来的 index 从低到高排，紧挨着锚点往上编号，原来谁上谁下的顺序还在。
   * usePageFlag 为 true 看 abovePage（锚点是全屏页），为 false 看 abovePanel（锚点是工具面板）。
   */
  private raiseIndexAbove(anchorName: string, usePageFlag: boolean): void {
    const anchor = this.find(anchorName);
    if (anchor === undefined) {
      return;
    }
    // 还在锚点下面、又标了要盖住它的层。锚点自己和已经在上面的不动
    const below: DoKitOverlayRecord[] = [];
    for (let i = 0; i < this.items.length; i++) {
      const item = this.items[i];
      if (item === undefined || item.name === anchorName || item.index > anchor.index) {
        continue;
      }
      const cover = usePageFlag ? item.abovePage : item.abovePanel;
      if (cover) {
        below.push(item);
      }
    }
    this.sortByIndex(below);
    let index = anchor.index;
    for (let i = 0; i < below.length; i++) {
      const item = below[i];
      if (item !== undefined) {
        index++;
        item.index = index;
      }
    }
  }

  /**
   * 新层的序号。
   * 可以盖住现有的全屏页和工具面板时，排到最上面。
   * 必须躲到某个锚点下面时，插到更靠下的那个锚点原来的位置。
   */
  private initialIndex(abovePage: boolean, abovePanel: boolean): number {
    const under = this.underAnchorIndex(abovePage, abovePanel);
    if (under === undefined) {
      return this.maxIndex() + 1;
    }
    // 锚点和它上面的层让出一位，新层占锚点原来的位置，所以严格在锚点下面
    for (let i = 0; i < this.items.length; i++) {
      const item = this.items[i];
      if (item !== undefined && item.index >= under) {
        item.index += 1;
      }
    }
    return under;
  }

  /** 新层必须待在哪个锚点下面。两个都要躲时取更靠下的那个。可以盖住现有锚点时返回空。 */
  private underAnchorIndex(abovePage: boolean, abovePanel: boolean): number | undefined {
    let under: number | undefined = undefined;
    const page = this.find(DoKitOverlayName.PAGE);
    if (page !== undefined && !abovePage) {
      under = page.index;
    }
    const panel = this.find(DoKitOverlayName.PANEL);
    if (panel !== undefined && !abovePanel) {
      if (under === undefined || panel.index < under) {
        under = panel.index;
      }
    }
    return under;
  }

  /** 没有全屏页和面板时，悬浮球回到最上面。 */
  private raiseBall(): void {
    if (this.find(DoKitOverlayName.PAGE) !== undefined ||
      this.find(DoKitOverlayName.PANEL) !== undefined) {
      return;
    }
    const ball = this.find(DoKitOverlayName.BALL);
    if (ball === undefined) {
      return;
    }
    const max = this.maxIndex();
    // 已经在最上面就不动，避免每次重画序号都加一
    if (ball.index < max) {
      ball.index = max + 1;
    }
  }

  /** 按序号从小到大排。界面按这个顺序先画下面的。 */
  private sortByIndex(list: DoKitOverlayRecord[]): void {
    for (let i = 1; i < list.length; i++) {
      const current = list[i];
      if (current === undefined) {
        continue;
      }
      let j = i - 1;
      while (j >= 0) {
        const prev = list[j];
        if (prev === undefined || prev.index <= current.index) {
          break;
        }
        list[j + 1] = prev;
        j--;
      }
      list[j + 1] = current;
    }
  }

  /** 当前最大的叠放序号。表是空的时返回 0。 */
  private maxIndex(): number {
    let max = 0;
    for (let i = 0; i < this.items.length; i++) {
      const item = this.items[i];
      if (item !== undefined && item.index > max) {
        max = item.index;
      }
    }
    return max;
  }

  /** 按名字找那一条。没有则返回空。 */
  private find(name: string): DoKitOverlayRecord | undefined {
    for (let i = 0; i < this.items.length; i++) {
      const item = this.items[i];
      if (item !== undefined && item.name === name) {
        return item;
      }
    }
    return undefined;
  }

  /** 层表摘要：名字、序号、版本。这三项变了界面才重画。 */
  private signature(): string {
    const parts: string[] = [];
    for (let i = 0; i < this.items.length; i++) {
      const item = this.items[i];
      if (item === undefined) {
        continue;
      }
      parts.push(`${item.name}@${item.index}#${item.revision}`);
    }
    return parts.join('|');
  }

  /** 球、面板、全屏页。对齐浮窗时不会被删掉。 */
  private isReserved(name: string): boolean {
    return name === DoKitOverlayName.BALL || name === DoKitOverlayName.PANEL || name === DoKitOverlayName.PAGE;
  }

  /** 这个名字是否还在当前浮窗名单里。 */
  private containsName(names: string[], name: string): boolean {
    for (let i = 0; i < names.length; i++) {
      if (names[i] === name) {
        return true;
      }
    }
    return false;
  }
}
