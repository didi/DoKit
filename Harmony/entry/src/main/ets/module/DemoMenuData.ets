export interface DemoMenuSection {
  title: string;
  items: string[];
}

/**
 * Demo 主页菜单，对标 Android activity_dokit_main.xml
 */
export function getDemoMenuSections(): DemoMenuSection[] {
  return [
    {
      title: '工具入口',
      items: ['显示/隐藏快捷入口', '打开工具窗口']
    },
    {
      title: '平台工具',
      items: [
        '数据Mock测试',
        'OkHttp 模拟请求',
        'UrlConnection 模拟请求',
        'retrofit 模拟请求',
        '一机多控测试',
        '自动化测试'
      ]
    },
    {
      title: '常用工具',
      items: ['日志测试', '系统:WebView', 'X5:WebView']
    },
    {
      title: 'LBS',
      items: ['位置模拟', '路径模拟']
    },
    {
      title: '性能工具',
      items: [
        '模拟内存泄漏',
        '模拟耗时函数调用',
        '崩溃模拟',
        '数据库测试',
        '文件上传模拟',
        '文件下载模拟',
        '大图检测模拟'
      ]
    },
    {
      title: '视觉工具',
      items: ['取色器测试', '标尺对齐测试']
    },
    {
      title: '其他工具',
      items: ['旧版页面入口']
    }
  ];
}
