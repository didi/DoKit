/** 对标 Android module/CrashTest */
export class CrashTest {
  static test(): void {
    const value: string | null = CrashTest.testCrash();
    if (value === null) {
      throw new Error('CrashTest');
    }
  }

  private static testCrash(): string | null {
    return null;
  }
}
