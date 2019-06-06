//
//  DoraemonManager.m
//  DoraemonKit
//
//  Created by yixiang on 2017/12/11.
//

#import "DoraemonManager.h"
#import "DoraemonEntryView.h"
#import "DoraemonCacheManager.h"
#import "DoraemonStartPluginProtocol.h"
#import "DoraemonDefine.h"
#import "DoraemonHomeWindow.h"
#import "Doraemoni18NUtil.h"
#import "DoraemonCrashUncaughtExceptionHandler.h"
#import "DoraemonCrashSignalExceptionHandler.h"
#import "DoraemonNSLogManager.h"
#import "DoraemonStateBar.h"
#import "DoraemonNSLogViewController.h"
#import "DoraemonNSLogListViewController.h"
#import "DoraemonUtil.h"
#import "DoraemonAllTestManager.h"
#import "DoraemonStatisticsUtil.h"
#import "DoraemonANRManager.h"

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

@implementation DoraemonManagerPluginTypeModel

@end

typedef void (^DoraemonANRBlock)(NSDictionary *);
typedef void (^DoraemonPerformanceBlock)(NSDictionary *);

@interface DoraemonManager()

@property (nonatomic, strong) DoraemonEntryView *entryView;

@property (nonatomic, strong) NSMutableArray *startPlugins;

@property (nonatomic, copy) DoraemonANRBlock anrBlock;

@property (nonatomic, copy) DoraemonPerformanceBlock performanceBlock;

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

- (void)install{
    [self installWithCustomBlock:^{
        //什么也没发生
    }];
}

- (void)installWithCustomBlock:(void(^)())customBlock{
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

    [self initEntry];
    
    //根据开关判断是否收集Crash日志
    if ([[DoraemonCacheManager sharedInstance] crashSwitch]) {
        [DoraemonCrashUncaughtExceptionHandler registerHandler];
        [DoraemonCrashSignalExceptionHandler registerHandler];
    }

    //重新启动的时候，把帧率、CPU、内存和流量监控关闭
    [[DoraemonCacheManager sharedInstance] saveFpsSwitch:NO];
    [[DoraemonCacheManager sharedInstance] saveCpuSwitch:NO];
    [[DoraemonCacheManager sharedInstance] saveMemorySwitch:NO];
    [[DoraemonCacheManager sharedInstance] saveNetFlowSwitch:NO];
    [[DoraemonCacheManager sharedInstance] saveMockGPSSwitch:NO];
    
    //开启NSLog监控功能
    if ([[DoraemonCacheManager sharedInstance] nsLogSwitch]) {
        [[DoraemonNSLogManager sharedInstance] startNSLogMonitor];
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1. * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [[DoraemonStateBar shareInstance] show];
        });
    }
    
#if DoraemonWithLogger
    //开启CocoaLumberjack监控
    if ([[DoraemonCacheManager sharedInstance] loggerSwitch]) {
        [DoraemonCocoaLumberjackLogger sharedInstance];
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1. * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [[DoraemonStateBar shareInstance] show];
        });
    }
#endif
    
    [[DoraemonAllTestManager shareInstance] addPerformanceBlock:^(NSDictionary *upLoadData) {
        if (self.performanceBlock) {
            self.performanceBlock(upLoadData);
        }
        //默认实现 保存到沙盒中
        NSString *testTime = [DoraemonUtil dateFormatTimeInterval:[upLoadData[@"testTime"] floatValue]];
        
        NSString *data = [DoraemonUtil dictToJsonStr:upLoadData];
        [DoraemonUtil savePerformanceDataInFile:testTime data:data];
    }];
    
    [[DoraemonANRManager sharedInstance] addANRBlock:^(NSDictionary *anrInfo) {
        if (self.anrBlock) {
            self.anrBlock(anrInfo);
        }
    }];
    
    //监听DoraemonStateBar点击事件
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(quickOpenLogVC:) name:DoraemonQuickOpenLogVCNotification object:nil];
    
    //统计开源项目使用量 不用于任何恶意行为
    [[DoraemonStatisticsUtil shareInstance] upLoadUserInfo];
    
    //Weex工具的初始化
#if DoraemonWithWeex
    [DoraemonWeexLogDataSource shareInstance];
    [DoraemonWeexInfoDataManager shareInstance];
#endif

}


/**
 初始化内置工具数据
 */
- (void)initData{
    #pragma mark - Weex专项工具
#if DoraemonWithWeex
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonWeexLogPlugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonWeexStoragePlugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonWeexInfoPlugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonWeexDevToolPlugin];
#endif
    #pragma mark - 常用工具
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonAppInfoPlugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonSandboxPlugin];
#if DoraemonWithGPS
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonGPSPlugin];
#endif
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonH5Plugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonCrashPlugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonSubThreadUICheckPlugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonDeleteLocalDataPlugin];
    
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonNSLogPlugin];
#if DoraemonWithLogger
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonCocoaLumberjackPlugin];
#endif
    
    #pragma mark - 性能检测
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonFPSPlugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonCPUPlugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonMemoryPlugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonNetFlowPlugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonANRPlugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonAllTestPlugin];
#if DoraemonWithLoad
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonMethodUseTimePlugin];
#endif
    
    #pragma mark - 视觉工具
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonColorPickPlugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonViewCheckPlugin];

    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonViewAlignPlugin];
    [self addPluginWithPluginType:DoraemonManagerPluginType_DoraemonViewMetricsPlugin];
}

/**
 初始化工具入口
 */
- (void)initEntry{
    _entryView = [[DoraemonEntryView alloc] init];
    [_entryView makeKeyAndVisible];
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
    [self addPluginWithTitle:DoraemonLocalizedString(model.title) icon:model.icon desc:DoraemonLocalizedString(model.desc) pluginName:model.pluginName atModule:DoraemonLocalizedString(model.atModule)];
}

- (void)addPluginWithTitle:(NSString *)title icon:(NSString *)iconName desc:(NSString *)desc pluginName:(NSString *)entryName atModule:(NSString *)moduleName{
    
    NSMutableDictionary *pluginDic = [self foundGroupWithModule:moduleName];
    pluginDic[@"name"] = title;
    pluginDic[@"icon"] = iconName;
    pluginDic[@"desc"] = desc;
    pluginDic[@"pluginName"] = entryName;
}

- (void)addPluginWithTitle:(NSString *)title icon:(NSString *)iconName desc:(NSString *)desc pluginName:(NSString *)entryName atModule:(NSString *)moduleName handle:(void (^)(NSDictionary *))handleBlock
{
    NSMutableDictionary *pluginDic = [self foundGroupWithModule:moduleName];
    pluginDic[@"name"] = title;
    pluginDic[@"icon"] = iconName;
    pluginDic[@"desc"] = desc;
    pluginDic[@"pluginName"] = entryName;
    pluginDic[@"handleBlock"] = [handleBlock copy];

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

- (void)showDoraemon{
    if (_entryView.hidden) {
        _entryView.hidden = NO;
    }
}

- (void)hiddenDoraemon{
    if (!_entryView.hidden) {
        _entryView.hidden = YES;
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

- (void)quickOpenLogVC:(NSNotification *)noti{
    NSDictionary *userInfo = noti.userInfo;
    NSInteger from = [userInfo[@"from"] integerValue];
    if (from == DoraemonStateBarFromNSLog) {//快速打开NSLog list页面
        DoraemonNSLogViewController *vc = [[DoraemonNSLogViewController alloc] init];
        [DoraemonUtil openPlugin:vc];
        DoraemonNSLogListViewController *vcList = [[DoraemonNSLogListViewController alloc] init];
        [vc.navigationController pushViewController:vcList animated:NO];
    }else{//快速打开CocoaLumberjack list页面
#if DoraemonWithLogger
        DoraemonCocoaLumberjackViewController *vc = [[DoraemonCocoaLumberjackViewController alloc] init];
        [DoraemonUtil openPlugin:vc];
        DoraemonCocoaLumberjackListViewController *vcList = [[DoraemonCocoaLumberjackListViewController alloc] init];
        [vc.navigationController pushViewController:vcList animated:NO];
#endif
    }
}

- (void)hiddenHomeWindow{
    [[DoraemonHomeWindow shareInstance] hide];
}

#pragma mark - default data
- (DoraemonManagerPluginTypeModel *)getDefaultPluginDataWithPluginType:(DoraemonManagerPluginType)pluginType
{
    NSArray *dataArray = @{
                           @(DoraemonManagerPluginType_DoraemonWeexLogPlugin) : @[
                                   @{kTitle:@"日志"},
                                   @{kDesc:@"Weex日志显示"},
                                   @{kIcon:@"doraemon_log"},
                                   @{kPluginName:@"DoraemonWeexLogPlugin"},
                                   @{kAtModule:@"Weex专区"}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonWeexStoragePlugin) : @[
                                   @{kTitle:@"缓存"},
                                   @{kDesc:@"weex storage 查看"},
                                   @{kIcon:@"doraemon_file"},
                                   @{kPluginName:@"DoraemonWeexStoragePlugin"},
                                   @{kAtModule:@"Weex专区"}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonWeexInfoPlugin) : @[
                                   @{kTitle:@"信息"},
                                   @{kDesc:@"weex 信息查看"},
                                   @{kIcon:@"doraemon_app_info"},
                                   @{kPluginName:@"DoraemonWeexInfoPlugin"},
                                   @{kAtModule:@"Weex专区"}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonWeexDevToolPlugin) : @[
                                   @{kTitle:@"DevTool"},
                                   @{kDesc:@"weex devtool"},
                                   @{kIcon:@"doraemon_default"},
                                   @{kPluginName:@"DoraemonWeexDevTooloPlugin"},
                                   @{kAtModule:@"Weex专区"}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonAppInfoPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"App信息")},
                                   @{kDesc:DoraemonLocalizedString(@"App的一些基本信息")},
                                   @{kIcon:@"doraemon_app_info"},
                                   @{kPluginName:@"DoraemonAppInfoPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"常用工具")}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonSandboxPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"沙盒浏览")},
                                   @{kDesc:DoraemonLocalizedString(@"沙盒浏览")},
                                   @{kIcon:@"doraemon_file"},
                                   @{kPluginName:@"DoraemonSandboxPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"常用工具")}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonGPSPlugin) : @[
                                   @{kTitle:@"MockGPS"},
                                   @{kDesc:@"MockGPS"},
                                   @{kIcon:@"doraemon_mock_gps"},
                                   @{kPluginName:@"DoraemonGPSPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"常用工具")}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonH5Plugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"H5任意门")},
                                   @{kDesc:DoraemonLocalizedString(@"H5通用跳转")},
                                   @{kIcon:@"doraemon_h5"},
                                   @{kPluginName:@"DoraemonH5Plugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"常用工具")}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonCrashPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"Crash查看")},
                                   @{kDesc:DoraemonLocalizedString(@"Crash本地查看")},
                                   @{kIcon:@"doraemon_crash"},
                                   @{kPluginName:@"DoraemonCrashPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"常用工具")}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonSubThreadUICheckPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"子线程UI")},
                                   @{kDesc:DoraemonLocalizedString(@"非主线程UI渲染检查")},
                                   @{kIcon:@"doraemon_ui"},
                                   @{kPluginName:@"DoraemonSubThreadUICheckPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"常用工具")}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonDeleteLocalDataPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"清除本地数据")},
                                   @{kDesc:DoraemonLocalizedString(@"清除本地数据")},
                                   @{kIcon:@"doraemon_qingchu"},
                                   @{kPluginName:@"DoraemonDeleteLocalDataPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"常用工具")}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonNSLogPlugin) : @[
                                   @{kTitle:@"NSLog"},
                                   @{kDesc:@"NSLog"},
                                   @{kIcon:@"doraemon_nslog"},
                                   @{kPluginName:@"DoraemonNSLogPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"常用工具")}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonCocoaLumberjackPlugin) : @[
                                   @{kTitle:@"Lumberjack"},
                                   @{kDesc:DoraemonLocalizedString(@"日志显示")},
                                   @{kIcon:@"doraemon_log"},
                                   @{kPluginName:@"DoraemonCocoaLumberjackPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"常用工具")}
                                   ],
                           
                           // 性能检测
                           @(DoraemonManagerPluginType_DoraemonFPSPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"帧率")},
                                   @{kDesc:DoraemonLocalizedString(@"帧率监控")},
                                   @{kIcon:@"doraemon_fps"},
                                   @{kPluginName:@"DoraemonFPSPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"性能检测")}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonCPUPlugin) : @[
                                   @{kTitle:@"CPU"},
                                   @{kDesc:DoraemonLocalizedString(@"CPU监控")},
                                   @{kIcon:@"doraemon_cpu"},
                                   @{kPluginName:@"DoraemonCPUPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"性能检测")}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonMemoryPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"内存")},
                                   @{kDesc:DoraemonLocalizedString(@"内存监控")},
                                   @{kIcon:@"doraemon_memory"},
                                   @{kPluginName:@"DoraemonMemoryPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"性能检测")}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonNetFlowPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"流量")},
                                   @{kDesc:DoraemonLocalizedString(@"流量监控")},
                                   @{kIcon:@"doraemon_net"},
                                   @{kPluginName:@"DoraemonNetFlowPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"性能检测")}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonANRPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"卡顿")},
                                   @{kDesc:DoraemonLocalizedString(@"卡顿监控")},
                                   @{kIcon:@"doraemon_kadun"},
                                   @{kPluginName:@"DoraemonANRPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"性能检测")}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonAllTestPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"自定义")},
                                   @{kDesc:DoraemonLocalizedString(@"性能数据保存到本地")},
                                   @{kIcon:@"doraemon_default"},
                                   @{kPluginName:@"DoraemonAllTestPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"性能检测")}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonMethodUseTimePlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"Load耗时")},
                                   @{kDesc:DoraemonLocalizedString(@"Load方法消耗时间")},
                                   @{kIcon:@"doraemon_method_use_time"},
                                   @{kPluginName:@"DoraemonMethodUseTimePlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"性能检测")}
                                   ],
                           
                           // 视觉工具
                           @(DoraemonManagerPluginType_DoraemonColorPickPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"颜色吸管")},
                                   @{kDesc:DoraemonLocalizedString(@"颜色拾取器")},
                                   @{kIcon:@"doraemon_straw"},
                                   @{kPluginName:@"DoraemonColorPickPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"视觉工具")}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonViewCheckPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"组件检查")},
                                   @{kDesc:DoraemonLocalizedString(@"View查看器")},
                                   @{kIcon:@"doraemon_view_check"},
                                   @{kPluginName:@"DoraemonViewCheckPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"视觉工具")}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonViewAlignPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"对齐标尺")},
                                   @{kDesc:DoraemonLocalizedString(@"查看组件是否对齐")},
                                   @{kIcon:@"doraemon_align"},
                                   @{kPluginName:@"DoraemonViewAlignPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"视觉工具")}
                                   ],
                           @(DoraemonManagerPluginType_DoraemonViewMetricsPlugin) : @[
                                   @{kTitle:DoraemonLocalizedString(@"元素边框线")},
                                   @{kDesc:DoraemonLocalizedString(@"显示元素边框线")},
                                   @{kIcon:@"doraemon_viewmetrics"},
                                   @{kPluginName:@"DoraemonViewMetricsPlugin"},
                                   @{kAtModule:DoraemonLocalizedString(@"视觉工具")}
                                   ]
                           }[@(pluginType)];
    
    DoraemonManagerPluginTypeModel *model = [DoraemonManagerPluginTypeModel new];
    model.title = dataArray[0][kTitle];
    model.desc = dataArray[1][kDesc];
    model.icon = dataArray[2][kIcon];
    model.pluginName = dataArray[3][kPluginName];
    model.atModule = dataArray[4][kAtModule];
    
    return model;
}

@end
