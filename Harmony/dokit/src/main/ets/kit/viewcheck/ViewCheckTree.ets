import { FrameNode, LengthMetrics, LengthUnit, UIContext } from '@kit.ArkUI';

/**
 * 用 FrameNode 按安卓的方式命中控件。
 * 从控件检查浮层的 id 找到节点，沿父节点走到页面根，子节点从后往前遍历。
 * 位置是窗口 vp，测量尺寸是 px，命中前统一成 px。DoKit 浮层整棵跳过。
 */
export class ViewCheckNode {
  typeName: string = '';
  componentId: string = '';
  left: number = 0;
  top: number = 0;
  right: number = 0;
  bottom: number = 0;
  padding: number[] = [];
  margin: number[] = [];
  background: string = '';
  textColor: string = '';
  textSizePx: number = -1;
  pageName: string = '';
  navName: string = '';
}

export class ViewCheckTree {
  static readonly FLOATING_ID: string = 'dokit_floating_window';
  static readonly OVERLAY_ID: string = 'dokit_view_check';

  static find(uiContext: UIContext, centerXVp: number, centerYVp: number, originXVp: number,
    originYVp: number): ViewCheckNode[] {
    const entry = ViewCheckTree.findEntry(uiContext);
    if (entry === null) {
      return [];
    }
    const root = ViewCheckTree.findRoot(entry);
    const hitX = vp2px(originXVp + centerXVp);
    const hitY = vp2px(originYVp + centerYVp);
    const hits: ViewCheckNode[] = [];
    ViewCheckTree.collect(uiContext, root, hitX, hitY, hits);
    return hits;
  }

  static describeExtra(node: ViewCheckNode): string[] {
    const lines: string[] = [];
    if (ViewCheckTree.isPaintedColor(node.background)) {
      lines.push(`背景颜色：${node.background}`);
    }
    if (ViewCheckTree.anySide(node.padding)) {
      lines.push(`内边距：${node.padding[0]}, ${node.padding[1]}, ${node.padding[2]}, ${node.padding[3]}`);
    }
    if (ViewCheckTree.anySide(node.margin)) {
      lines.push(`外边距：${node.margin[0]}, ${node.margin[1]}, ${node.margin[2]}, ${node.margin[3]}`);
    }
    if (ViewCheckTree.isTextNode(node.typeName)) {
      if (node.textColor.length > 0) {
        lines.push(`文字颜色：${node.textColor}`);
      }
      if (node.textSizePx >= 0) {
        lines.push(`文字大小：${node.textSizePx}`);
      }
    }
    return lines;
  }

  static findVisiblePage(nodes: ViewCheckNode[]): string {
    const names: string[] = [];
    for (let i = 0; i < nodes.length; i++) {
      const node = nodes[i];
      let label = node.navName;
      if (label.length === 0 && (node.typeName === 'NavDestination' || node.typeName === 'NavBar')) {
        label = node.componentId.length > 0 ? `${node.typeName}#${node.componentId}` : node.typeName;
      }
      if (label.length === 0 || names.indexOf(label) >= 0) {
        continue;
      }
      names.push(label);
    }
    return names.join(';');
  }

  private static findEntry(uiContext: UIContext): FrameNode | null {
    try {
      const overlay = uiContext.getFrameNodeById(ViewCheckTree.OVERLAY_ID);
      if (overlay !== null) {
        return overlay;
      }
      return uiContext.getFrameNodeById(ViewCheckTree.FLOATING_ID);
    } catch (_err) {
      return null;
    }
  }

  private static findRoot(entry: FrameNode): FrameNode {
    let current = entry;
    for (let guard = 0; guard < 64; guard++) {
      const parent = current.getParent();
      if (parent === null) {
        return current;
      }
      current = parent;
    }
    return current;
  }

  private static collect(uiContext: UIContext, frame: FrameNode, x: number, y: number,
    out: ViewCheckNode[]): void {
    if (ViewCheckTree.shouldSkip(frame)) {
      return;
    }
    const count = ViewCheckTree.childCount(frame);
    for (let i = count - 1; i >= 0; i--) {
      const child = ViewCheckTree.childAt(frame, i);
      if (child !== null) {
        ViewCheckTree.collect(uiContext, child, x, y, out);
      }
    }
    const rect = ViewCheckTree.readRect(frame);
    if (rect === null) {
      return;
    }
    if (rect[0] < x && x < rect[2] && rect[1] < y && y < rect[3]) {
      out.push(ViewCheckTree.snapshot(uiContext, frame, rect));
    }
  }

  private static shouldSkip(frame: FrameNode): boolean {
    const nodeId = ViewCheckTree.readId(frame);
    return nodeId === ViewCheckTree.FLOATING_ID || nodeId === ViewCheckTree.OVERLAY_ID;
  }

  private static readId(frame: FrameNode): string {
    try {
      return frame.getId();
    } catch (_err) {
      return '';
    }
  }

  private static childCount(frame: FrameNode): number {
    try {
      return frame.getChildrenCount();
    } catch (_err) {
      return 0;
    }
  }

  private static childAt(frame: FrameNode, index: number): FrameNode | null {
    try {
      return frame.getChild(index);
    } catch (_err) {
      return null;
    }
  }

  private static readRect(frame: FrameNode): number[] | null {
    try {
      const position = frame.getPositionToWindow();
      const size = frame.getMeasuredSize();
      const left = vp2px(position.x);
      const top = vp2px(position.y);
      const right = left + size.width;
      const bottom = top + size.height;
      if (right <= left || bottom <= top) {
        return null;
      }
      return [left, top, right, bottom];
    } catch (_err) {
      return null;
    }
  }

  private static snapshot(uiContext: UIContext, frame: FrameNode, rect: number[]): ViewCheckNode {
    const node = new ViewCheckNode();
    node.left = rect[0];
    node.top = rect[1];
    node.right = rect[2];
    node.bottom = rect[3];
    node.componentId = ViewCheckTree.readId(frame);
    node.typeName = ViewCheckTree.readType(frame);
    node.padding = ViewCheckTree.readEdges(frame, true);
    node.margin = ViewCheckTree.readEdges(frame, false);
    ViewCheckTree.readInspector(frame, node);
    ViewCheckTree.readPage(uiContext, frame, node);
    if (node.typeName.length === 0) {
      node.typeName = 'View';
    }
    return node;
  }

  private static readType(frame: FrameNode): string {
    try {
      const typeName = frame.getNodeType();
      if (typeName === '__Common__') {
        return '';
      }
      return typeName;
    } catch (_err) {
      return '';
    }
  }

  private static readEdges(frame: FrameNode, padding: boolean): number[] {
    try {
      const edges = padding ? frame.getUserConfigPadding() : frame.getUserConfigMargin();
      return [
        ViewCheckTree.metricsToPx(edges.left),
        ViewCheckTree.metricsToPx(edges.top),
        ViewCheckTree.metricsToPx(edges.right),
        ViewCheckTree.metricsToPx(edges.bottom)
      ];
    } catch (_err) {
      return [];
    }
  }

  private static metricsToPx(metrics: LengthMetrics): number {
    if (metrics === undefined || metrics === null) {
      return 0;
    }
    if (metrics.unit === LengthUnit.PX) {
      return Math.round(metrics.value);
    }
    if (metrics.unit === LengthUnit.PERCENT) {
      return 0;
    }
    return Math.round(vp2px(metrics.value));
  }

  private static readInspector(frame: FrameNode, node: ViewCheckNode): void {
    try {
      const info = frame.getInspectorInfo();
      const record = ViewCheckTree.asRecord(info);
      if (record === null) {
        return;
      }
      if (node.typeName.length === 0) {
        const typeName = ViewCheckTree.textOf(record['$type']);
        node.typeName = typeName.length > 0 ? typeName : ViewCheckTree.textOf(record['type']);
      }
      const attrs = ViewCheckTree.readAttrs(record['$attrs'] !== undefined ? record['$attrs'] : record['attrs']);
      const background = ViewCheckTree.attrOf(attrs, 'backgroundColor', 'background');
      if (background.length > 0) {
        node.background = background;
      }
      if (!ViewCheckTree.isTextNode(node.typeName)) {
        return;
      }
      node.textColor = ViewCheckTree.attrOf(attrs, 'fontColor', 'textColor');
      const fontSize = ViewCheckTree.attrOf(attrs, 'fontSize', 'textSize');
      if (fontSize.length > 0) {
        node.textSizePx = ViewCheckTree.lengthToPx(fontSize);
      }
    } catch (_err) {
    }
  }

  private static readPage(uiContext: UIContext, frame: FrameNode, node: ViewCheckNode): void {
    try {
      const info = uiContext.getPageInfoByUniqueId(frame.getUniqueId());
      const router = info.routerPageInfo;
      if (router !== undefined && router !== null) {
        node.pageName = router.name.length > 0 ? router.name : router.path;
      }
      const nav = info.navDestinationInfo;
      if (nav !== undefined && nav !== null && typeof nav.name === 'string') {
        const navName = nav.name as string;
        if (navName.length > 0) {
          node.navName = navName;
        }
      }
    } catch (_err) {
    }
  }

  private static readAttrs(raw: Object | undefined): Record<string, string> {
    const attrs: Record<string, string> = {};
    const record = ViewCheckTree.asRecord(raw);
    if (record === null) {
      return attrs;
    }
    const keys = Object.keys(record);
    for (let i = 0; i < keys.length; i++) {
      const key = keys[i];
      const text = ViewCheckTree.textOf(record[key]);
      if (text.length > 0) {
        attrs[key] = text;
      }
    }
    return attrs;
  }

  private static asRecord(raw: Object | undefined): Record<string, Object> | null {
    if (raw === undefined || raw === null) {
      return null;
    }
    if (typeof raw === 'string') {
      const text = raw as string;
      if (text.length === 0) {
        return null;
      }
      try {
        return ViewCheckTree.asRecord(JSON.parse(text) as Object);
      } catch (_err) {
        return null;
      }
    }
    if (typeof raw !== 'object' || Array.isArray(raw)) {
      return null;
    }
    return raw as Record<string, Object>;
  }

  private static attrOf(attrs: Record<string, string>, key: string, extra?: string): string {
    const primary = attrs[key];
    if (primary !== undefined && primary.length > 0) {
      return primary;
    }
    if (extra === undefined) {
      return '';
    }
    const secondary = attrs[extra];
    if (secondary !== undefined && secondary.length > 0) {
      return secondary;
    }
    return '';
  }

  private static lengthToPx(token: string): number {
    const matched = token.trim().match(/^(-?\d+(?:\.\d+)?)(vp|px|fp|lpx)?$/);
    if (matched === null) {
      return 0;
    }
    const value = Number(matched[1]);
    const unit = matched[2] ?? 'vp';
    if (unit === 'px') {
      return Math.round(value);
    }
    return Math.round(vp2px(value));
  }

  private static anySide(sides: number[]): boolean {
    if (sides.length < 4) {
      return false;
    }
    for (let i = 0; i < sides.length; i++) {
      if (sides[i] !== 0) {
        return true;
      }
    }
    return false;
  }

  private static isTextNode(typeName: string): boolean {
    return typeName === 'Text' || typeName === 'Span' || typeName === 'TextInput' ||
      typeName === 'TextArea' || typeName === 'Search' || typeName === 'Button' ||
      typeName === 'RichEditor';
  }

  private static isPaintedColor(color: string): boolean {
    const value = color.trim().toLowerCase();
    if (value.length === 0 || value === 'transparent' || value === '#00000000' || value === '#00ffffff') {
      return false;
    }
    return true;
  }

  private static textOf(value: Object | undefined): string {
    if (value === undefined || value === null) {
      return '';
    }
    if (typeof value === 'string') {
      return value as string;
    }
    if (typeof value === 'number' || typeof value === 'boolean') {
      return `${value}`;
    }
    return '';
  }
}
