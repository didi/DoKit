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

#if DoraemonWithLogger
#import "DoraemonCocoaLumberjackLogger.h"
#import "DoraemonCocoaLumberjackViewController.h"
#import "DoraemonCocoaLumberjackListViewController.h"
#endif

typedef void (^DoraemonANRBlock)(NSDictionary *);
typedef void (^DoraemonPerformanceBlock)(NSDictionary *);

@interface DoraemonManager()

@property (nonatomic, strong) DoraemonEntryView *entryView;

@property (nonatomic, strong) NSMutableArray *startPlugins;

@property (nonatomic, copy) DoraemonANRBlock anrBlock;

@property (nonatomic, copy) DoraemonPerformanceBlock performanceBlock;

@end

@implementation DoraemonManager

+ (DoraemonManager *)shareInstance{
    static dispatch_once_t once;
    static DoraemonManager *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonManager alloc] init];
    });
    return instance;
}

- (void)install{
    for (int i=0; i<_startPlugins.count; i++) {
        NSString *pluginName = _startPlugins[i];
        Class pluginClass = NSClassFromString(pluginName);
        id<DoraemonStartPluginProtocol> plugin = [[pluginClass alloc] init];
        if (plugin) {
            [plugin pluginDidLoad];
        }
    }
    
    [self initData];
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
    
    //监听DoraemonStateBar点击事件
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(quickOpenLogVC:) name:DoraemonQuickOpenLogVCNotification object:nil];
    
    //统计开源项目使用量 不用于任何恶意行为
    [DoraemonStatisticsUtil upLoadUserInfo];

}


/**
 初始化内置工具数据
 */
- (void)initData{
    
    [self addPluginWithTitle:DoraemonLocalizedString(@"App信息") icon:@"doraemon_app_info" desc:DoraemonLocalizedString(@"App的一些基本信息") pluginName:@"DoraemonAppInfoPlugin" atModule:DoraemonLocalizedString(@"常用工具")];
    [self addPluginWithTitle:DoraemonLocalizedString(@"沙盒浏览") icon:@"doraemon_file" desc:DoraemonLocalizedString(@"沙盒浏览") pluginName:@"DoraemonSandboxPlugin" atModule:DoraemonLocalizedString(@"常用工具")];
    [self addPluginWithTitle:@"MockGPS" icon:@"doraemon_mock_gps" desc:@"mock GPS" pluginName:@"DoraemonGPSPlugin" atModule:DoraemonLocalizedString(@"常用工具")];
    [self addPluginWithTitle:DoraemonLocalizedString(@"H5任意门") icon:@"doraemon_h5" desc:DoraemonLocalizedString(@"H5通用跳转") pluginName:@"DoraemonH5Plugin" atModule:DoraemonLocalizedString(@"常用工具")];
    [self addPluginWithTitle:DoraemonLocalizedString(@"Crash查看") icon:@"doraemon_crash" desc:DoraemonLocalizedString(@"Crash本地查看") pluginName:@"DoraemonCrashPlugin" atModule:DoraemonLocalizedString(@"常用工具")];
    [self addPluginWithTitle:DoraemonLocalizedString(@"子线程UI") icon:@"doraemon_ui" desc:DoraemonLocalizedString(@"非主线程UI渲染检查") pluginName:@"DoraemonSubThreadUICheckPlugin" atModule:DoraemonLocalizedString(@"常用工具")];
    [self addPluginWithTitle:DoraemonLocalizedString(@"清除本地数据") icon:@"doraemon_qingchu" desc:DoraemonLocalizedString(@"清除本地数据") pluginName:@"DoraemonDeleteLocalDataPlugin" atModule:DoraemonLocalizedString(@"常用工具")];
    [self addPluginWithTitle:DoraemonLocalizedString(@"NSLog") icon:@"doraemon_nslog" desc:DoraemonLocalizedString(@"NSLog") pluginName:@"DoraemonNSLogPlugin" atModule:DoraemonLocalizedString(@"常用工具")];
#if DoraemonWithLogger
    [self addPluginWithTitle:@"Lumberjack" icon:@"doraemon_log" desc:DoraemonLocalizedString(@"日志显示") pluginName:@"DoraemonCocoaLumberjackPlugin" atModule:DoraemonLocalizedString(@"常用工具")];
#endif
    
    [self addPluginWithTitle:DoraemonLocalizedString(@"帧率") icon:@"doraemon_fps" desc:DoraemonLocalizedString(@"帧率监控") pluginName:@"DoraemonFPSPlugin" atModule:DoraemonLocalizedString(@"性能检测")];
    [self addPluginWithTitle:@"CPU" icon:@"doraemon_cpu" desc:DoraemonLocalizedString(@"CPU监控") pluginName:@"DoraemonCPUPlugin" atModule:DoraemonLocalizedString(@"性能检测")];
    [self addPluginWithTitle:DoraemonLocalizedString(@"内存") icon:@"doraemon_memory" desc:DoraemonLocalizedString(@"内存监控") pluginName:@"DoraemonMemoryPlugin" atModule:DoraemonLocalizedString(@"性能检测")];
    [self addPluginWithTitle:DoraemonLocalizedString(@"流量") icon:@"doraemon_net" desc:DoraemonLocalizedString(@"流量监控") pluginName:@"DoraemonNetFlowPlugin" atModule:DoraemonLocalizedString(@"性能检测")];
    [self addPluginWithTitle:DoraemonLocalizedString(@"卡顿") icon:@"doraemon_kadun" desc:DoraemonLocalizedString(@"卡顿检测") pluginName:@"DoraemonANRPlugin" atModule:DoraemonLocalizedString(@"性能检测")];
    [self addPluginWithTitle:@"自定义" icon:@"doraemon_default" desc:DoraemonLocalizedString(@"性能数据保存到本地") pluginName:@"DoraemonAllTestPlugin" atModule:DoraemonLocalizedString(@"性能检测")];
    
    [self addPluginWithTitle:DoraemonLocalizedString(@"颜色吸管") icon:@"doraemon_straw" desc:DoraemonLocalizedString(@"颜色拾取器") pluginName:@"DoraemonColorPickPlugin" atModule:DoraemonLocalizedString(@"视觉工具")];
    [self addPluginWithTitle:DoraemonLocalizedString(@"组件检查") icon:@"doraemon_view_check" desc:DoraemonLocalizedString(@"View查看器") pluginName:@"DoraemonViewCheckPlugin" atModule:DoraemonLocalizedString(@"视觉工具")];
    [self addPluginWithTitle:DoraemonLocalizedString(@"对齐标尺") icon:@"doraemon_align" desc:DoraemonLocalizedString(@"查看组件是否对齐") pluginName:@"DoraemonViewAlignPlugin" atModule:DoraemonLocalizedString(@"视觉工具")];
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

- (void)addPluginWithTitle:(NSString *)title icon:(NSString *)iconName desc:(NSString *)desc pluginName:(NSString *)pluginName atModule:(NSString *)moduleName{
    NSMutableDictionary *pluginDic = [[NSMutableDictionary alloc] init];
    [pluginDic setValue:title forKey:@"name"];
    [pluginDic setValue:iconName forKey:@"icon"];
    [pluginDic setValue:desc forKey:@"desc"];
    [pluginDic setValue:pluginName forKey:@"pluginName"];
    
    BOOL hasModule = NO;
    for (int i=0; i<_dataArray.count; i++) {
        NSDictionary *moduleDic = _dataArray[i];
        NSString *tempModuleName = moduleDic[@"moduleName"];
        if ([tempModuleName isEqualToString:moduleName]) {
            hasModule = YES;
            NSMutableArray *pluginArray = moduleDic[@"pluginArray"];
            if (pluginArray) {
                [pluginArray addObject:pluginDic];
            }
            [moduleDic setValue:pluginArray forKey:@"pluginArray"];
        }
    }
    if (!hasModule) {
        NSMutableArray *pluginArray = [[NSMutableArray alloc] initWithObjects:pluginDic, nil];
        [self registerPluginArray:pluginArray withModule:moduleName];
    }
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

- (void)hiddenDoraemon{
    _entryView.hidden = YES;
}

- (void)addH5DoorBlock:(void(^)(NSString *h5Url))block{
    self.h5DoorBlock = block;
}

- (void)addANRBlock:(void(^)(NSDictionary *anrDic))block{
    self.anrBlock = block;
}

- (void)addperformanceBlock:(void(^)(NSDictionary *performanceDic))block{
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


@end
