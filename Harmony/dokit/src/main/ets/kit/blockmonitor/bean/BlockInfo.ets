/**
 * 卡顿信息，对标 Android blockmonitor/bean/BlockInfo
 */
export class BlockInfo {
  static readonly SEPARATOR: string = '\r\n';

  timeCost: number = 0;
  threadTimeCost: number = 0;
  time: number = 0;
  timeStart: string = '';
  timeEnd: string = '';
  threadStackEntries: string[] = [];
  concernStackString: string = '';

  private timeText: string = '';
  private stackText: string = '';

  static newInstance(): BlockInfo {
    return new BlockInfo();
  }

  setThreadStackEntries(entries: string[]): BlockInfo {
    this.threadStackEntries = entries;
    return this;
  }

  setMainThreadTimeCost(realTimeStart: number, realTimeEnd: number,
    threadTimeStart: number, threadTimeEnd: number): BlockInfo {
    this.timeCost = realTimeEnd - realTimeStart;
    this.threadTimeCost = threadTimeEnd - threadTimeStart;
    this.timeStart = BlockInfo.formatTime(realTimeStart);
    this.timeEnd = BlockInfo.formatTime(realTimeEnd);
    return this;
  }

  flushString(): BlockInfo {
    this.timeText = `time = ${this.timeCost}${BlockInfo.SEPARATOR}` +
      `thread-time = ${this.threadTimeCost}${BlockInfo.SEPARATOR}` +
      `time-start = ${this.timeStart}${BlockInfo.SEPARATOR}` +
      `time-end = ${this.timeEnd}${BlockInfo.SEPARATOR}`;

    let stackBody = '';
    for (let i = 0; i < this.threadStackEntries.length; i++) {
      stackBody += this.threadStackEntries[i];
      stackBody += BlockInfo.SEPARATOR;
    }
    this.stackText = `stack = ${stackBody}${BlockInfo.SEPARATOR}`;
    return this;
  }

  toString(): string {
    return `${this.timeText}\n${this.stackText}`;
  }

  static formatTime(timestamp: number): string {
    const date = new Date(timestamp);
    const pad2 = (value: number): string => {
      return value < 10 ? `0${value}` : `${value}`;
    };
    return `${pad2(date.getMonth() + 1)}-${pad2(date.getDate())} ` +
      `${pad2(date.getHours())}:${pad2(date.getMinutes())}:${pad2(date.getSeconds())}.` +
      `${pad2(Math.floor(date.getMilliseconds() / 10))}`;
  }
}
