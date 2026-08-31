/**
 * 崩溃信息，对标 Android CrashInfo
 */
export class CrashInfo {
  message: string = '';
  time: number = 0;

  constructor(message: string, time: number) {
    this.message = message;
    this.time = time;
  }
}
