/** 对标 Android module/MethodCostTest */
export class MethodCostTest {
  static test(): void {
    MethodCostTest.test1();
  }

  private static test1(): void {
    setTimeout(() => {
      MethodCostTest.test2();
    }, 1000);
  }

  private static test2(): void {
    setTimeout(() => {
      MethodCostTest.test3();
    }, 200);
  }

  private static test3(): void {
    setTimeout(() => {
      MethodCostTest.test4();
    }, 200);
  }

  private static test4(): void {
    setTimeout(() => {
      // end
    }, 200);
  }
}
