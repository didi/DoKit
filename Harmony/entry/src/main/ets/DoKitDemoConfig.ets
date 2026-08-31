/**
 * Demo 常量配置，对标 Android App.kt 中的注释与初始化参数
 */
export class DoKitDemoConfig {
  /** 测试环境 productId */
  static readonly PRODUCT_ID_TEST: string = '277016abcc33bff1e6a4f1afdf14b8e1';
  /** 线上环境 productId */
  static readonly PRODUCT_ID_PROD: string = '749a0600b5e48dd77cf8ee680be7b1b7';
  /** 文件同步助手 HTTP 端口，对标 Android fileManagerHttpPort(9001) */
  static readonly FILE_MANAGER_HTTP_PORT: number = 9001;
  /** 一机多控 WebSocket 端口，对标 Android mcWSPort(5555) */
  static readonly MC_WS_PORT: number = 5555;
  /** 加密数据库密码，对标 Android databasePass */
  static readonly DATABASE_PASS: Map<string, string> = new Map([
    ['Person.db', 'a_password']
  ]);
}
