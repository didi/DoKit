//
//  DoraemonManager.m
//  DoraemonKit
//
//  Created by yixiang on 2017/12/11.
//
#import <UIKit/UIKit.h>
#import "DoraemonManager.h"
#import "DoraemonEntryWindow.h"
#import "DoraemonCacheManager.h"
#import "DoraemonStartPluginProtocol.h"
#import "DoraemonDefine.h"
#import "DoraemonUtil.h"
#import "DoraemonHomeWindow.h"
#import "Doraemoni18NUtil.h"
#import "DoraemonCrashUncaughtExceptionHandler.h"
#import "DoraemonCrashSignalExceptionHandler.h"
#import "DoraemonNSLogManager.h"
#import "DoraemonNSLogViewController.h"
#import "DoraemonNSLogListViewController.h"
#import "DoraemonHomeWindow.h"
#import "DoraemonStatisticsUtil.h"
#import "DoraemonANRManager.h"
#import "DoraemonLargeImageDetectionManager.h"
#import "DoraemonMockManager.h"
#import "DoraemonNetFlowOscillogramWindow.h"
#import "DoraemonNetFlowManager.h"
#import "DoraemonHealthManager.h"

#if DoraemonWithGPS
#import "DoraemonGPSMocker.h"
#endif


#if DoraemonWithLogger
#import "DoraemonCocoaLumberjackLogger.h"
#import "DoraemonCocoaLumberjackViewController.h"
#import "DoraemonCocoaLumberjackListViewController.h"
#endif

#if DoraemonWithWeex
#import "DoraemonWeexLogDataSource.h"
#import "DoraemonWeexInfoDataManager.h"
#endif


#define kTitle        @"title"
#define kDesc         @"desc"
#define kIcon         @"icon"
#define kPluginName   @"pluginName"
#define kAtModule     @"atModule"
#define kBuriedPoint  @"buriedPoint"

@implementation DoraemonManagerPluginTypeModel

@end

typedef void (^DoraemonANRBlock)(NSDictionary *);
typedef void (^DoraemonPerformanceBlock)(NSDictionary *);

@interface DoraemonManager()

@property (nonatomic, strong) DoraemonEntryWindow *entryWindow;

@property (nonatomic, strong) NSMutableArray *startPlugins;

@property (nonatomic, copy) DoraemonANRBlock anrBlock;

@property (nonatomic, copy) DoraemonPerformanceBlock performanceBlock;

@property (nonatomic, assign) BOOL hasInstall;

// 定制位置
@property (nonatomic) CGPoint startingPosition;

@end

@implementation DoraemonManager

+ (nonnull DoraemonManager *)shareInstance{
    static dispatch_once_t once;
    static DoraemonManager *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonManager alloc] init];
    });
    return instance;
}

- (instancetype)init{
    self = [super init];
    if (self) {
        _autoDock = YES;
        _keyBlockDic = [[NSMutableDictionary alloc] init];
    }
    return self;
}

- (void)install{
    //启用默认位置
    CGPoint defaultPosition = DoraemonStartingPosition;
    CGSize size = [UIScreen mainScreen].bounds.size;
    if (size.width > size.height) {
        defaultPosition = DoraemonFullScreenStartingPosition;
    }
    [self installWithStartingPosition:defaultPosition];
}

- (void)installWithPid:(NSString *)pId{
    self.pId = pId;
    [self install];
}

- (void)installWithMockDomain:(NSString *)mockDomain{
    self.mockDomain = mockDomain;
    [self install];
}

- (void)installWithStartingPosition:(CGPoint) position{
    _startingPosition = position;
    [self installWithCustomBlock:^{
        //什么也没发生
    }];
}

- (void)installWithCustomBlock:(void(^)(void))customBlock{
    //保证install只执行一次
    if (_hasInstall) {
        return;
    }
    _hasInstall = YES;
    for (int i=0; i<_startPlugins.count; i++) {
        NSString *pluginName = _startPlugins[i];
        Class pluginClass = NSClassFromString(pluginName);
        id<DoraemonStartPluginProtocol> plugin = [[pluginClass alloc] init];
        if (plugin) {
            [plugin startPluginDidLoad];
        }
    }

    [self initData];
    customBlock();

    [self initEntry:self.startingPosition];
    
    //根据开关判断是否收集Crash日志
    if ([[DoraemonCacheManager sharedInstance] crashSwitch]) {
        [DoraemonCrashUncaughtExceptionHandler registerHandler];
        [DoraemonCrashSignalExceptionHandler registerHandler];
    }
    //根据开关判断是否开启流量监控
    if ([[DoraemonCacheManager sharedInstance] netFlowSwitch]) {
        [[DoraemonNetFlowManager shareInstance] canInterceptNetFlow:YES];
        //[[DoraemonNetFlowOscillogramWindow shareInstance] show];
    }

    //重新启动的时候，把帧率、CPU、内存和流量监控关闭
    [[DoraemonCacheManager sharedInstance] saveFpsSwitch:NO];
    [[DoraemonCacheManager sharedInstance] saveCpuSwitch:NO];
    [[DoraemonCacheManager sharedInstance] saveMemorySwitch:NO];

#if DoraemonWithGPS
    //开启mockGPS功能
    if ([[DoraemonCacheManager sharedInstance] mockGPSSwitch]) {
        CLLocationCoordinate2D coordinate = [[DoraemonCacheManager sharedInstance] mockCoordinate];
        CLLocation *loc = [[CLLocation alloc] initWithLatitude:coordinate.latitude longitude:coordinate.longitude];
        [[DoraemonGPSMocker shareInstance] mockPoint:loc];
    }
#endif

    
    //开启NSLog监控功能
    if ([[DoraemonCacheManager sharedInstance] nsLogSwitch]) {
        [[DoraemonNSLogManager sharedInstance] startNSLogMonitor];
    }
    
#if DoraemonWithLogger
    //开启CocoaLumberjack监控
    if ([[DoraemonCacheManager sharedInstance] loggerSwitch]) {
        [[DoraemonCocoaLumberjackLogger sharedInstance] startMonitor];
    }
#endif
    
    [[DoraemonANRManager sharedInstance] addANRBlock:^(NSDictionary *anrInfo) {
        if (self.anrBlock) {
            self.anrBlock(anrInfo);
        }
    }];
    
    //外部设置大图检测的数值
    if (_bigImageDetectionSize > 0){
        [DoraemonLargeImageDetectionManager shareInstance].minimumDetectionSize = _bigImageDetectionSize;
    }
    
    //统计开源项目使用量 不用于任何恶意行为
    [[DoraemonStatisticsUtil shareInstance] upLoadUserInfo];
    
    //拉取最新的mock数据
    [[DoraemonMockManager sharedInstance] queryMockData:^(int flag) {
        DoKitLog(@"mock get data, flag == %i",flag);
    }];
    
    //Weex工具的初始化
#if DoraemonWithWeex
    [DoraemonWeexLogDataSource shareInstance];
    [DoraemonWeexInfoDataManager shareInstance];
#endif
    
    //开启健康体检
    if ([[DoraemonCacheManager sharedInstance] healthStart]) {
        [[DoraemonHealthManager sharedInstance] startHealthCheck];
    }
    
}


/**
 初始化内置工具数据
 */
- (void)initData{
    #pragma mark - 平台工具
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonMockPlugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonHealthPlugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonFileSyncPlugin];
    
    #pragma mark - 常用工具
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonAppSettingPlugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonAppInfoPlugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonSandboxPlugin];
#if DoraemonWithGPS
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonGPSPlugin];
#endif

    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonH5Plugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonDeleteLocalDataPlugin];
    
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonNSLogPlugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonNSUserDefaultsPlugin];
#if DoraemonWithLogger
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonCocoaLumberjackPlugin];
#endif
    
#if DoraemonWithDatabase
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonDatabasePlugin];
#endif
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonJavaScriptPlugin];
    
    #pragma mark - 性能检测
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonFPSPlugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonCPUPlugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonMemoryPlugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonNetFlowPlugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonCrashPlugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonSubThreadUICheckPlugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonANRPlugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonLargeImageFilter];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonWeakNetworkPlugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonStartTimePlugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonUIProfilePlugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonTimeProfilePlugin];
#if DoraemonWithLoad
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonMethodUseTimePlugin];
#endif
#if DoraemonWithMLeaksFinder
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonMemoryLeakPlugin];
#endif
    
    #pragma mark - 视觉工具
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonColorPickPlugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonViewCheckPlugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonViewAlignPlugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonViewMetricsPlugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonHierarchyPlugin];
    
    #pragma mark - Weex专项工具
    #if DoraemonWithWeex
        [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonWeexLogPlugin];
        [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonWeexStoragePlugin];
        [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonWeexInfoPlugin];
        [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonWeexDevToolPlugin];
    #endif
}

/**
 初始化工具入口
 */
- (void)initEntry:(CGPoint) startingPosition{
    _entryWindow = [[DoraemonEntryWindow alloc] initWithStartPoint:startingPosition];
    [_entryWindow show];
    if(_autoDock){
        [_entryWindow setAutoDock:YES];
    }
}

- (void)addStartPlugin:(NSString *)pluginName{
    if (!_startPlugins) {
        _startPlugins = [[NSMutableArray alloc] init];
    }
    [_startPlugins addObject:pluginName];
}

- (void)addPluginWithPluginType:(DoraemonManagerPluginType)pluginType
{
    DoraemonManagerPluginTypeModel *model = [self getDefaultPluginDataWithPluginType:pluginType];
    [self addPluginWithTitle:model.title icon:model.icon desc:model.desc pluginName:model.pluginName atModule:model.atModule buriedPoint:model.buriedPoint];
}

// out 1
- (void)addPluginWithTitle:(NSString *)title icon:(NSString *)iconName desc:(NSString *)desc pluginName:(NSString *)entryName atModule:(NSString *)moduleName{
    [self addPluginWithTitle:title icon:iconName desc:desc pluginName:entryName atModule:moduleName buriedPoint:@"dokit_sdk_business_ck"];
}

- (void)addPluginWithTitle:(NSString *)title icon:(NSString *)iconName desc:(NSString *)desc pluginName:(NSString *)entryName atModule:(NSString *)moduleName buriedPoint:(NSString *)buriedPoint{
    
    NSMutableDictionary *pluginDic = [self foundGroupWithModule:moduleName];
    pluginDic[@"key"] = [NSString stringWithFormat:@"%@-%@-%@-%@",moduleName,title,iconName,desc];
    pluginDic[@"name"] = title;
    pluginDic[@"icon"] = iconName;
    pluginDic[@"desc"] = desc;
    pluginDic[@"pluginName"] = entryName;
    pluginDic[@"buriedPoint"] = buriedPoint;
    pluginDic[@"show"] = @1;
}

// out 2
- (void)addPluginWithTitle:(NSString *)title icon:(NSString *)iconName desc:(NSString *)desc pluginName:(NSString *)entryName atModule:(NSString *)moduleName handle:(void (^)(NSDictionary *))handleBlock
{
    NSMutableDictionary *pluginDic = [self foundGroupWithModule:moduleName];
    pluginDic[@"key"] = [NSString stringWithFormat:@"%@-%@-%@-%@",moduleName,title,iconName,desc];
    pluginDic[@"name"] = title;
    pluginDic[@"icon"] = iconName;
    pluginDic[@"desc"] = desc;
    pluginDic[@"pluginName"] = entryName;
    [_keyBlockDic setValue:[handleBlock copy] forKey:pluginDic[@"key"]];
    pluginDic[@"buriedPoint"] = @"dokit_sdk_business_ck";
    pluginDic[@"show"] = @1;

}

- (void)addPluginWithTitle:(NSString *)title image:(UIImage *)image desc:(NSString *)desc pluginName:(NSString *)entryName atModule:(NSString *)moduleName handle:(void (^)(NSDictionary * _Nonnull))handleBlock {
    NSMutableDictionary *pluginDic = [self foundGroupWithModule:moduleName];
    pluginDic[@"key"] = [NSString stringWithFormat:@"%@-%@-%@",moduleName,title,desc];
    pluginDic[@"name"] = title;
    pluginDic[@"image"] = image;
    pluginDic[@"desc"] = desc;
    pluginDic[@"pluginName"] = entryName;
    if (handleBlock) {
        [_keyBlockDic setValue:[handleBlock copy] forKey:pluginDic[@"key"]];
    }
    pluginDic[@"buriedPoint"] = @"dokit_sdk_business_ck";
    pluginDic[@"show"] = @1;
}

- (NSMutableDictionary *)foundGroupWithModule:(NSString *)module
{
    NSMutableDictionary *pluginDic = [NSMutableDictionary dictionary];
    pluginDic[@"moduleName"] = module;
    __block BOOL hasModule = NO;
    [self.dataArray enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        NSDictionary *moduleDic = obj;
        NSString *moduleName = moduleDic[@"moduleName"];
        if ([moduleName isEqualToString:module]) {
            hasModule = YES;
            NSMutableArray *pluginArray = moduleDic[@"pluginArray"];
            if (pluginArray) {
                [pluginArray addObject:pluginDic];
            }
            [moduleDic setValue:pluginArray forKey:@"pluginArray"];
            *stop = YES;
        }
    }];
    if (!hasModule) {
        NSMutableArray *pluginArray = [[NSMutableArray alloc] initWithObjects:pluginDic, nil];
        [self registerPluginArray:pluginArray withModule:module];
    }
    return pluginDic;
}
- (void)removePluginWithPluginType:(DoraemonManagerPluginType)pluginType
{
    DoraemonManagerPluginTypeModel *model = [self getDefaultPluginDataWithPluginType:pluginType];
    [self removePluginWithPluginName:model.pluginName atModule:model.atModule];
}

- (void)removePluginWithPluginName:(NSString *)pluginName atModule:(NSString *)moduleName{
    [self unregisterPlugin:pluginName withModule:moduleName];
}

- (void)registerPluginArray:(NSMutableArray*)array withModule:(NSString*)moduleName{
    if (!_dataArray){
        _dataArray = [[NSMutableArray alloc]init];
    }
    NSMutableDictionary *dic = [[NSMutableDictionary alloc] init];
    [dic setValue:moduleName forKey:@"moduleName"];
    [dic setValue:array forKey:@"pluginArray"];
    [_dataArray addObject:dic];
}

- (void)unregisterPlugin:(NSString*)pluginName withModule:(NSString*)moduleName{
    if (!_dataArray){
        return;
    }
    id object;
    for (object in _dataArray) {
        NSString *tempModuleName = [((NSMutableDictionary *)object) valueForKey:@"moduleName"];
        if ([tempModuleName isEqualToString:moduleName]) {
            NSMutableArray *tempPluginArray = [((NSMutableDictionary *)object) valueForKey:@"pluginArray"];
            id pluginObject;
            for (pluginObject in tempPluginArray) {
                NSString *tempPluginName = [((NSMutableDictionary *)pluginObject) valueForKey:@"pluginName"];
                if ([tempPluginName isEqualToString:pluginName]) {
                    [tempPluginArray removeObject:pluginObject];
                    return;
                }
            }
        }
    }
}

- (BOOL)isShowDoraemon{
    if (!_entryWindow) {
        return NO;
    }
    return !_entryWindow.hidden;
}

- (void)showDoraemon{
    if (_entryWindow.hidden) {
        _entryWindow.hidden = NO;
    }
}

- (void)hiddenDoraemon{
    if (!_entryWindow.hidden) {
        _entryWindow.hidden = YES;
     }
}


- (void)addH5DoorBlock:(void(^)(NSString *h5Url))block{
    self.h5DoorBlock = block;
}

- (void)addANRBlock:(void(^)(NSDictionary *anrDic))block{
    self.anrBlock = block;
}

- (void)addPerformanceBlock:(void(^)(NSDictionary *performanceDic))block{
    self.performanceBlock = block;
}

- (void)addWebpHandleBlock:(UIImage *(^)(NSString *filePath))block{
    self.webpHandleBlock = block;
}

- (void)hiddenHomeWindow{
    [[DoraemonHomeWindow shareInstance] hide];
}

#pragma mark - default data
- (DoraemonManagerPluginTypeModel *)getDefaultPluginDataWithPluginType:(DoraemonManagerPluginType)pluginType
{
    NSArray *dataArray = @{
                           @(DoraemonManagerPluginType_DoraemonWeexLogPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"日志")},
                                   @{kDesc:@"Weex log"},
                                   @{kIcon:@"doraemon_log"},
                                   @{kPluginName:@"DoraemonWeexLogPlugin"},
                                   @{kAtModule:@"Weex"},
                                   @{kBuriedPoint:@"dokit_sdk_weex_ck_log"}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonWeexStoragePlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"缓存")},
                                   @{kDesc:@"weex storage"},
                                   @{kIcon:@"doraemon_file"},
                                   @{kPluginName:@"DoraemonWeexStoragePlugin"},
                                   @{kAtModule:@"Weex"},
                                   @{kBuriedPoint:@"dokit_sdk_weex_ck_storage"}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonWeexInfoPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"信息")},
                                   @{kDesc:@"weex info"},
                                   @{kIcon:@"doraemon_app_info"},
                                   @{kPluginName:@"DoraemonWeexInfoPlugin"},
                                   @{kAtModule:@"Weex"},
                                   @{kBuriedPoint:@"dokit_sdk_weex_ck_vessel"}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonWeexDevToolPlugin) : @[
                                   @{kTitle:@"DevTool"},
                                   @{kDesc:@"weex devtool"},
                                   @{kIcon:@"doraemon_default"},
                                   @{kPluginName:@"DoraemonWeexDevTooloPlugin"},
                                   @{kAtModule:@"Weex"},
                                   @{kBuriedPoint:@"dokit_sdk_weex_ck_devtool"}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonAppSettingPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"应用设置")},
                                   @{kDesc:DoraemonLocalizedString(@"应用设置")},
                                   @{kIcon:@"doraemon_setting"},
                                   @{kPluginName:@"DoraemonAppSettingPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"常用工具")},
                                   @{kBuriedPoint:@"dokit_sdk_comm_ck_setting"}
                                    ],
                           @(DoraemonManagerPluginType_DoraemonAppInfoPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"App信息")},
                                   @{kDesc:DoraemonLocalizedString(@"App信息")},
                                   @{kIcon:@"doraemon_app_info"},
                                   @{kPluginName:@"DoraemonAppInfoPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"常用工具")},
                                   @{kBuriedPoint:@"dokit_sdk_comm_ck_appinfo"}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonSandboxPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"沙盒浏览器")},
                                   @{kDesc:DoraemonLocalizedString(@"沙盒浏览器")},
                                   @{kIcon:@"doraemon_file"},
                                   @{kPluginName:@"DoraemonSandboxPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"常用工具")},
                                   @{kBuriedPoint:@"dokit_sdk_comm_ck_sandbox"}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonGPSPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"Mock GPS")},
                                   @{kDesc:DoraemonLocalizedString(@"Mock GPS")},
                                   @{kIcon:@"doraemon_mock_gps"},
                                   @{kPluginName:@"DoraemonGPSPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"常用工具")},
                                   @{kBuriedPoint:@"dokit_sdk_comm_ck_gps"}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonH5Plugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"H5任意门")},
                                   @{kDesc:DoraemonLocalizedString(@"H5任意门")},
                                   @{kIcon:@"doraemon_h5"},
                                   @{kPluginName:@"DoraemonH5Plugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"常用工具")},
                                   @{kBuriedPoint:@"dokit_sdk_comm_ck_h5"}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonDeleteLocalDataPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"清理缓存")},
                                   @{kDesc:DoraemonLocalizedString(@"清理缓存")},
                                   @{kIcon:@"doraemon_qingchu"},
                                   @{kPluginName:@"DoraemonDeleteLocalDataPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"常用工具")},
                                   @{kBuriedPoint:@"dokit_sdk_comm_ck_cache"}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonNSLogPlugin) : @[
                                   @{kTitle:@"NSLog"},
                                   @{kDesc:@"NSLog"},
                                   @{kIcon:@"doraemon_nslog"},
                                   @{kPluginName:@"DoraemonNSLogPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"常用工具")},
                                   @{kBuriedPoint:@"dokit_sdk_comm_ck_log"}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonCocoaLumberjackPlugin) : @[
                                   @{kTitle:@"Lumberjack"},
                                   @{kDesc:DoraemonLocalizedString(@"Lumberjack")},
                                   @{kIcon:@"doraemon_log"},
                                   @{kPluginName:@"DoraemonCocoaLumberjackPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"常用工具")},
                                   @{kBuriedPoint:@"dokit_sdk_comm_ck_lumberjack"}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonDatabasePlugin) : @[
                                   @{kTitle:@"DBView"},
                                   @{kDesc:DoraemonLocalizedString(@"数据库预览")},
                                   @{kIcon:@"doraemon_database"},
                                   @{kPluginName:@"DoraemonDatabasePlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"常用工具")},
                                   @{kBuriedPoint:@"dokit_sdk_comm_ck_dbview"}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonNSUserDefaultsPlugin) : @[
                                   @{kTitle:@"UserDefaults"},
                                   @{kDesc:@"UserDefaults"},
                                   @{kIcon:@"doraemon_database"},
                                   @{kPluginName:@"DoraemonNSUserDefaultsPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"常用工具")},
                                   @{kBuriedPoint:@"dokit_sdk_comm_ck_userdefault"}
                           ],
                           @(DoraemonManagerPluginType_DoraemonJavaScriptPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"JS脚本")},
                                   @{kDesc:DoraemonLocalizedString(@"JS脚本")},
                                   @{kIcon:@"doraemon_js"},
                                   @{kPluginName:@"DoraemonJavaScriptPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"常用工具")},
                                   @{kBuriedPoint:@"dokit_sdk_comm_ck_js"}
                           ],
                           
                           // 性能检测
                           @(DoraemonManagerPluginType_DoraemonFPSPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"帧率")},
                                   @{kDesc:DoraemonLocalizedString(@"帧率")},
                                   @{kIcon:@"doraemon_fps"},
                                   @{kPluginName:@"DoraemonFPSPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"性能检测")},
                                   @{kBuriedPoint:@"dokit_sdk_performance_ck_fps"}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonCPUPlugin) : @[
                                   @{kTitle:@"CPU"},
                                   @{kDesc:DoraemonLocalizedString(@"CPU")},
                                   @{kIcon:@"doraemon_cpu"},
                                   @{kPluginName:@"DoraemonCPUPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"性能检测")},
                                   @{kBuriedPoint:@"dokit_sdk_performance_ck_cpu"}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonMemoryPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"内存")},
                                   @{kDesc:DoraemonLocalizedString(@"内存")},
                                   @{kIcon:@"doraemon_memory"},
                                   @{kPluginName:@"DoraemonMemoryPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"性能检测")},
                                   @{kBuriedPoint:@"dokit_sdk_performance_ck_arm"}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonNetFlowPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"网络")},
                                   @{kDesc:DoraemonLocalizedString(@"网络监控")},
                                   @{kIcon:@"doraemon_net"},
                                   @{kPluginName:@"DoraemonNetFlowPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"性能检测")},
                                   @{kBuriedPoint:@"dokit_sdk_performance_ck_network"}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonCrashPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"Crash")},
                                   @{kDesc:DoraemonLocalizedString(@"Crash")},
                                   @{kIcon:@"doraemon_crash"},
                                   @{kPluginName:@"DoraemonCrashPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"性能检测")},
                                   @{kBuriedPoint:@"dokit_sdk_comm_ck_crash"}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonSubThreadUICheckPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"子线程UI")},
                                   @{kDesc:DoraemonLocalizedString(@"子线程UI")},
                                   @{kIcon:@"doraemon_ui"},
                                   @{kPluginName:@"DoraemonSubThreadUICheckPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"性能检测")},
                                   @{kBuriedPoint:@"dokit_sdk_comm_ck_child_thread"}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonANRPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"卡顿")},
                                   @{kDesc:DoraemonLocalizedString(@"卡顿")},
                                   @{kIcon:@"doraemon_kadun"},
                                   @{kPluginName:@"DoraemonANRPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"性能检测")},
                                   @{kBuriedPoint:@"dokit_sdk_performance_ck_block"}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonMethodUseTimePlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"Load耗时")},
                                   @{kDesc:DoraemonLocalizedString(@"Load耗时")},
                                   @{kIcon:@"doraemon_method_use_time"},
                                   @{kPluginName:@"DoraemonMethodUseTimePlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"性能检测")},
                                   @{kBuriedPoint:@"dokit_sdk_performance_ck_load"}
                                   ],
                           
                           @(DoraemonManagerPluginType_DoraemonLargeImageFilter) : @[
                                   @{kTitle:DoraemonLocalizedString(@"大图检测")},
                                   @{kDesc:DoraemonLocalizedString(@"大图检测")},
                                   @{kIcon:@"doraemon_net"},
                                   @{kPluginName:@"DoraemonLargeImagePlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"性能检测")},
                                   @{kBuriedPoint:@"dokit_sdk_performance_ck_img"}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonStartTimePlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"启动耗时")},
                                   @{kDesc:DoraemonLocalizedString(@"启动耗时")},
                                   @{kIcon:@"doraemon_app_start_time"},
                                   @{kPluginName:@"DoraemonStartTimePlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"性能检测")},
                                   @{kBuriedPoint:@"dokit_sdk_performance_ck_appstart_coast"}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonMemoryLeakPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"内存泄漏")},
                                   @{kDesc:DoraemonLocalizedString(@"内存泄漏统计")},
                                   @{kIcon:@"doraemon_memory_leak"},
                                   @{kPluginName:@"DoraemonMLeaksFinderPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"性能检测")},
                                   @{kBuriedPoint:@"dokit_sdk_performance_ck_leak"}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonUIProfilePlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"UI层级")},
                                   @{kDesc:DoraemonLocalizedString(@"UI层级s")},
                                   @{kIcon:@"doraemon_view_level"},
                                   @{kPluginName:@"DoraemonUIProfilePlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"性能检测")},
                                   @{kBuriedPoint:@"dokit_sdk_ui_ck_hierarchy"}
                           ],
                           @(DoraemonManagerPluginType_DoraemonTimeProfilePlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"函数耗时")},
                                   @{kDesc:DoraemonLocalizedString(@"函数耗时统计")},
                                   @{kIcon:@"doraemon_time_profiler"},
                                   @{kPluginName:@"DoraemonTimeProfilerPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"性能检测")},
                                   @{kBuriedPoint:@"dokit_sdk_performance_ck_method_coast"}
                           ],
                           @(DoraemonManagerPluginType_DoraemonWeakNetworkPlugin) : @[
                                     @{kTitle:DoraemonLocalizedString(@"模拟弱网")},
                                     @{kDesc:DoraemonLocalizedString(@"模拟弱网测试")},
                                     @{kIcon:@"doraemon_weaknet"},
                                     @{kPluginName:@"DoraemonWeakNetworkPlugin"},
                                     @{kAtModule:DoraemonLocalizedString(@"性能检测")},
                                     @{kBuriedPoint:@"dokit_sdk_comm_ck_weaknetwork"}
                             ],
                           // 视觉工具
                           @(DoraemonManagerPluginType_DoraemonColorPickPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"取色器")},
                                   @{kDesc:DoraemonLocalizedString(@"取色器")},
                                   @{kIcon:@"doraemon_straw"},
                                   @{kPluginName:@"DoraemonColorPickPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"视觉工具")},
                                   @{kBuriedPoint:@"dokit_sdk_ui_ck_color_pick"}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonViewCheckPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"组件检查")},
                                   @{kDesc:DoraemonLocalizedString(@"组件检查")},
                                   @{kIcon:@"doraemon_view_check"},
                                   @{kPluginName:@"DoraemonViewCheckPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"视觉工具")},
                                   @{kBuriedPoint:@"dokit_sdk_ui_ck_widget"}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonViewAlignPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"对齐标尺")},
                                   @{kDesc:DoraemonLocalizedString(@"对齐标尺")},
                                   @{kIcon:@"doraemon_align"},
                                   @{kPluginName:@"DoraemonViewAlignPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"视觉工具")},
                                   @{kBuriedPoint:@"dokit_sdk_ui_ck_aligin_scaleplate"}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonViewMetricsPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"布局边框")},
                                   @{kDesc:DoraemonLocalizedString(@"布局边框")},
                                   @{kIcon:@"doraemon_viewmetrics"},
                                   @{kPluginName:@"DoraemonViewMetricsPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"视觉工具")},
                                   @{kBuriedPoint:@"dokit_sdk_ui_ck_border"}
                                   ],
                          @(DoraemonManagerPluginType_DoraemonHierarchyPlugin) : @[
                                           @{kTitle:DoraemonLocalizedString(@"UI结构")},
                                           @{kDesc:DoraemonLocalizedString(@"显示UI结构")},
                                           @{kIcon:@"doraemon_view_level"},
                                           @{kPluginName:@"DoraemonHierarchyPlugin"},
                                           @{kAtModule:DoraemonLocalizedString(@"视觉工具")},
                                           @{kBuriedPoint:@"dokit_sdk_ui_ck_widget_3d"}
                                   ],
                           // 平台工具
                           @(DoraemonManagerPluginType_DoraemonMockPlugin) : @[
                                @{kTitle:DoraemonLocalizedString(@"Mock数据")},
                                   @{kDesc:DoraemonLocalizedString(@"Mock数据")},
                                   @{kIcon:@"doraemon_mock"},
                                   @{kPluginName:@"DoraemonMockPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"平台工具")},
                                   @{kBuriedPoint:@"dokit_sdk_platform_ck_mock"}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonHealthPlugin) : @[
                               @{kTitle:DoraemonLocalizedString(@"健康体检")},
                                  @{kDesc:DoraemonLocalizedString(@"健康体检中心")},
                                  @{kIcon:@"doraemon_health"},
                                  @{kPluginName:@"DoraemonHealthPlugin"},
                                  @{kAtModule:DoraemonLocalizedString(@"平台工具")},
                                  @{kBuriedPoint:@"dokit_sdk_platform_ck_health"}
                                  ],
                           @(DoraemonManagerPluginType_DoraemonFileSyncPlugin) : @[
                                @{kTitle:DoraemonLocalizedString(@"文件同步")},
                                    @{kDesc:DoraemonLocalizedString(@"文件同步")},
                                    @{kIcon:@"doraemon_file_sync"},
                                    @{kPluginName:@"DoraemonFileSyncPlugin"},
                                    @{kAtModule:DoraemonLocalizedString(@"平台工具")},
                                    @{kBuriedPoint:@"dokit_sdk_platform_ck_filesync"}
                                    ]
                           }[@(pluginType)];
    
    DoraemonManagerPluginTypeModel *model = [DoraemonManagerPluginTypeModel new];
    model.title = dataArray[0][kTitle];
    model.desc = dataArray[1][kDesc];
    model.icon = dataArray[2][kIcon];
    model.pluginName = dataArray[3][kPluginName];
    model.atModule = dataArray[4][kAtModule];
    model.buriedPoint = dataArray[5][kBuriedPoint];
    
    return model;
}

- (void)setStartClass:(NSString *)startClass {
    [[DoraemonCacheManager sharedInstance] saveStartClass:startClass];
}

- (NSString *)startClass{
    return [[DoraemonCacheManager sharedInstance] startClass];
}

- (void)configEntryBtnBlingWithText:(NSString *)text backColor:(UIColor *)backColor {
    [self.entryWindow configEntryBtnBlingWithText:text backColor:backColor];
}

@end
