import { FrameNode, UIContext } from '@kit.ArkUI';
import { ViewCheckTree } from '../viewcheck/ViewCheckTree';

/**
 * 收集页面上每个可见 FrameNode 的窗口矩形，供覆盖层画红色虚线。
 * 安卓是给 View 背景叠 Drawable；声明式节点不能改背景，所以只读树再画到 Canvas。
 * DoKit 浮层整棵跳过，避免框住关闭条自己。
 */
export class LayoutBorderRect {
  left: number = 0;
  top: number = 0;
  width: number = 0;
  height: number = 0;
}

export class LayoutBorderTree {
  static readonly OVERLAY_ID: string = 'dokit_layout_border';

  static collect(uiContext: UIContext, originXVp: number, originYVp: number): LayoutBorderRect[] {
    const entry = LayoutBorderTree.findEntry(uiContext);
    if (entry === null) {
      return [];
    }
    const root = LayoutBorderTree.findRoot(entry);
    const originX = vp2px(originXVp);
    const originY = vp2px(originYVp);
    const out: LayoutBorderRect[] = [];
    LayoutBorderTree.walk(root, originX, originY, out);
    return out;
  }

  private static findEntry(uiContext: UIContext): FrameNode | null {
    try {
      const overlay = uiContext.getFrameNodeById(LayoutBorderTree.OVERLAY_ID);
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

  private static walk(frame: FrameNode, originX: number, originY: number, out: LayoutBorderRect[]): void {
    if (LayoutBorderTree.shouldSkip(frame) || !LayoutBorderTree.isShown(frame)) {
      return;
    }
    const rect = LayoutBorderTree.readRect(frame, originX, originY);
    if (rect !== null) {
      out.push(rect);
    }
    const count = LayoutBorderTree.childCount(frame);
    for (let i = 0; i < count; i++) {
      const child = LayoutBorderTree.childAt(frame, i);
      if (child !== null) {
        LayoutBorderTree.walk(child, originX, originY, out);
      }
    }
  }

  private static shouldSkip(frame: FrameNode): boolean {
    const nodeId = LayoutBorderTree.readId(frame);
    return nodeId === ViewCheckTree.FLOATING_ID || nodeId === LayoutBorderTree.OVERLAY_ID;
  }

  private static isShown(frame: FrameNode): boolean {
    try {
      return frame.isVisible();
    } catch (_err) {
      return true;
    }
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

  private static readRect(frame: FrameNode, originX: number, originY: number): LayoutBorderRect | null {
    try {
      const position = frame.getPositionToWindow();
      const size = frame.getMeasuredSize();
      const width = size.width;
      const height = size.height;
      if (width <= 0 || height <= 0) {
        return null;
      }
      const rect = new LayoutBorderRect();
      rect.left = vp2px(position.x) - originX;
      rect.top = vp2px(position.y) - originY;
      rect.width = width;
      rect.height = height;
      return rect;
    } catch (_err) {
      return null;
    }
  }
}
