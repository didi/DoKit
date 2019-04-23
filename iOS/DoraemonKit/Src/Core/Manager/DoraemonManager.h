//
//  DoraemonManager.h
//  DoraemonKit
//
//  Created by yixiang on 2017/12/11.
//

#import <Foundation/Foundation.h>

typedef void (^DoraemonH5DoorBlock)(NSString *);

typedef NS_ENUM(NSUInteger, DoraemonManagerPluginType) {
    #pragma mark - 常用工具
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
    // 自定义 性能数据保存到本地
    DoraemonManagerPluginType_DoraemonAllTestPlugin,
    // Load耗时
    DoraemonManagerPluginType_DoraemonMethodUseTimePlugin,
    
    #pragma mark - 视觉工具
    // 颜色吸管
    DoraemonManagerPluginType_DoraemonColorPickPlugin,
    // 组件检查
    DoraemonManagerPluginType_DoraemonViewCheckPlugin,
    // 对齐标尺
    DoraemonManagerPluginType_DoraemonViewAlignPlugin,
    // 元素边框线
    DoraemonManagerPluginType_DoraemonViewMetricsPlugin
};

@interface DoraemonManagerPluginTypeModel : NSObject

@property(nonatomic, copy) NSString *title;
@property(nonatomic, copy) NSString *desc;
@property(nonatomic, copy) NSString *icon;
@property(nonatomic, copy) NSString *pluginName;
@property(nonatomic, copy) NSString *atModule;

@end

@interface DoraemonManager : NSObject

+ (nonnull DoraemonManager *)shareInstance;

- (void)install;

- (void)installWithCustomBlock:(void(^)())customBlock;

@property (nonatomic,strong) NSMutableArray *dataArray;

@property (nonatomic, copy) DoraemonH5DoorBlock h5DoorBlock;

- (void)addPluginWithTitle:(NSString *)title icon:(NSString *)iconName desc:(NSString *)desc pluginName:(NSString *)entryName atModule:(NSString *)moduleName;

- (void)removePluginWithPluginType:(DoraemonManagerPluginType)pluginType;
// 推荐使用 removePluginWithPluginType 方法
- (void)removePluginWithPluginName:(NSString *)pluginName atModule:(NSString *)moduleName;

- (void)addStartPlugin:(NSString *)pluginName;

- (void)addH5DoorBlock:(void(^)(NSString *h5Url))block;

- (void)addANRBlock:(void(^)(NSDictionary *anrDic))block;

- (void)addperformanceBlock:(void(^)(NSDictionary *performanceDic))block;

- (void)hiddenDoraemon;

- (void)hiddenHomeWindow;

@end
