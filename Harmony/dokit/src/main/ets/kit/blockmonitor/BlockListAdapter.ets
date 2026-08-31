import { BlockInfo } from './bean/BlockInfo';

/**
 * 卡顿列表适配辅助，对标 Android BlockListAdapter
 */
export class BlockListAdapter {
  static formatTitle(info: BlockInfo, position: number, totalCount: number): string {
    const index = `${totalCount - position}. `;
    return `${index}${info.concernStackString} 卡顿 ${info.timeCost} ms`;
  }

  static formatTime(timestamp: number): string {
    const date = new Date(timestamp);
    const pad2 = (value: number): string => {
      return value < 10 ? `0${value}` : `${value}`;
    };
    return `${date.getFullYear()}-${pad2(date.getMonth() + 1)}-${pad2(date.getDate())} ` +
      `${pad2(date.getHours())}:${pad2(date.getMinutes())}:${pad2(date.getSeconds())}`;
  }
}
