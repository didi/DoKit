//
//  DoraemonManager.h
//  DoraemonKit
//
//  Created by yixiang on 2017/12/11.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>


NS_ASSUME_NONNULL_BEGIN
typedef void (^DoraemonH5DoorBlock)(NSString *);

typedef NS_ENUM(NSUInteger, DoraemonManagerPluginType) {
    #pragma mark - weex专项工具
    // 日志
    DoraemonManagerPluginType_DoraemonWeexLogPlugin,
    // 缓存
    DoraemonManagerPluginType_DoraemonWeexStoragePlugin,
    // 信息
    DoraemonManagerPluginType_DoraemonWeexInfoPlugin,
    // DevTool
    DoraemonManagerPluginType_DoraemonWeexDevToolPlugin,
    #pragma mark - 常用工具
    // App设置
    DoraemonManagerPluginType_DoraemonAppSettingPlugin,
    // App信息
    DoraemonManagerPluginType_DoraemonAppInfoPlugin,
    // 沙盒浏览
    DoraemonManagerPluginType_DoraemonSandboxPlugin,
    // MockGPS
    DoraemonManagerPluginType_DoraemonGPSPlugin,
    // H5任意门
    DoraemonManagerPluginType_DoraemonH5Plugin,
    // Crash查看
    DoraemonManagerPluginType_DoraemonCrashPlugin,
    // 子线程UI
    DoraemonManagerPluginType_DoraemonSubThreadUICheckPlugin,
    // 清除本地数据
    DoraemonManagerPluginType_DoraemonDeleteLocalDataPlugin,
    // NSLog
    DoraemonManagerPluginType_DoraemonNSLogPlugin,
    // 日志显示
    DoraemonManagerPluginType_DoraemonCocoaLumberjackPlugin,
    // 数据库工具
    DoraemonManagerPluginType_DoraemonDatabasePlugin,
    // NSUserDefaults工具
    DoraemonManagerPluginType_DoraemonNSUserDefaultsPlugin,
    
    #pragma mark - 性能检测
    // 帧率监控
    DoraemonManagerPluginType_DoraemonFPSPlugin,
    // CPU监控
    DoraemonManagerPluginType_DoraemonCPUPlugin,
    // 内存监控
    DoraemonManagerPluginType_DoraemonMemoryPlugin,
    // 流量监控
    DoraemonManagerPluginType_DoraemonNetFlowPlugin,
    // 卡顿检测
    DoraemonManagerPluginType_DoraemonANRPlugin,
    // Load耗时
    DoraemonManagerPluginType_DoraemonMethodUseTimePlugin,
    // 大图检测
    DoraemonManagerPluginType_DoraemonLargeImageFilter,
    // 启动耗时
    DoraemonManagerPluginType_DoraemonStartTimePlugin,
    // 内存泄漏
    DoraemonManagerPluginType_DoraemonMemoryLeakPlugin,
    // UI层级检查
    DoraemonManagerPluginType_DoraemonUIProfilePlugin,
    // UI结构调整
    DoraemonManagerPluginType_DoraemonHierarchyPlugin,
    // 函数耗时
    DoraemonManagerPluginType_DoraemonTimeProfilePlugin,
    // 模拟弱网
    DoraemonManagerPluginType_DoraemonWeakNetworkPlugin,
    
    #pragma mark - 视觉工具
    // 颜色吸管
    DoraemonManagerPluginType_DoraemonColorPickPlugin,
    // 组件检查
    DoraemonManagerPluginType_DoraemonViewCheckPlugin,
    // 对齐标尺
    DoraemonManagerPluginType_DoraemonViewAlignPlugin,
    // 元素边框线
    DoraemonManagerPluginType_DoraemonViewMetricsPlugin,
    
    #pragma mark - 平台工具
    // Mock 数据
    DoraemonManagerPluginType_DoraemonMockPlugin,
    DoraemonManagerPluginType_DoraemonHealthPlugin
};

@interface DoraemonManagerPluginTypeModel : NSObject

@property(nonatomic, copy) NSString *title;
@property(nonatomic, copy) NSString *desc;
@property(nonatomic, copy) NSString *icon;
@property(nonatomic, copy) NSString *pluginName;
@property(nonatomic, copy) NSString *atModule;
@property(nonatomic, copy) NSString *buriedPoint;

@end

@interface DoraemonManager : NSObject

+ (nonnull DoraemonManager *)shareInstance;

@property (nonatomic, copy) NSString *appKey __attribute__((deprecated("此属性已被弃用，替换方式请参考最新 https://www.dokit.cn/ 的使用手册")));

@property (nonatomic, copy) NSString *pId; //产品id 平台端的工具必须填写

- (void)install;
//带有平台端功能的s初始化方式
- (void)installWithPid:(NSString *)pId;

// 定制起始位置 | 适用正好挡住关键位置
- (void)installWithStartingPosition:(CGPoint) position;

- (void)installWithCustomBlock:(void(^)(void))customBlock;

@property (nonatomic,strong) NSMutableArray *dataArray;

@property (nonatomic, copy) DoraemonH5DoorBlock h5DoorBlock;

- (void)addPluginWithTitle:(NSString *)title icon:(NSString *)iconName desc:(NSString *)desc pluginName:(NSString *)entryName atModule:(NSString *)moduleName;
- (void)addPluginWithTitle:(NSString *)title icon:(NSString *)iconName desc:(NSString *)desc pluginName:(NSString *)entryName atModule:(NSString *)moduleName handle:(void(^)(NSDictionary *itemData))handleBlock;


- (void)removePluginWithPluginType:(DoraemonManagerPluginType)pluginType;

- (void)removePluginWithPluginName:(NSString *)pluginName atModule:(NSString *)moduleName;

- (void)addStartPlugin:(NSString *)pluginName;

- (void)addH5DoorBlock:(void(^)(NSString *h5Url))block;

- (void)addANRBlock:(void(^)(NSDictionary *anrDic))block;

- (void)addPerformanceBlock:(void(^)(NSDictionary *performanceDic))block;

- (BOOL)isShowDoraemon;

- (void)showDoraemon;

- (void)hiddenDoraemon;

- (void)hiddenHomeWindow;

@property (nonatomic, assign) int64_t bigImageDetectionSize; // 外部设置大图检测的监控数值  比如监控所有图片大于50K的图片 那么这个值就设置为 50 * 1024；

@property (nonatomic, copy) NSString *startClass; //如果你的启动代理不是默认的AppDelegate,需要传入才能获取正确的启动时间

@end
NS_ASSUME_NONNULL_END
