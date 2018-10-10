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
#import "DoraemonANRManager.h"
#import "DoraemonAllTestManager.h"
#import "DoraemonUtil.h"

#if DoraemonWithLogger
#import "DoraemonLoggerConsoleWindow.h"
#endif

typedef void (^DoraemonH5DoorBlock)(NSString *);
typedef void (^DoraemonANRBlock)(NSDictionary *);
typedef void (^DoraemonPerformanceBlock)(NSDictionary *);

@interface DoraemonManager()

@property (nonatomic, strong) DoraemonEntryView *entryView;

@property (nonatomic, strong) NSMutableArray *startPlugins;

@property (nonatomic, copy) DoraemonH5DoorBlock h5DoorBlock;

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
    
    //保存日志显示状态
#if DoraemonWithLogger
    if ([[DoraemonCacheManager sharedInstance] loggerSwitch]) {
        [[DoraemonLoggerConsoleWindow shareInstance] show];
    }
#endif

    
    //重新启动的时候，把帧率、CPU、内存和流量监控关闭
    [[DoraemonCacheManager sharedInstance] saveFpsSwitch:NO];
    [[DoraemonCacheManager sharedInstance] saveCpuSwitch:NO];
    [[DoraemonCacheManager sharedInstance] saveMemorySwitch:NO];
    [[DoraemonCacheManager sharedInstance] saveNetFlowSwitch:NO];
    [[DoraemonCacheManager sharedInstance] saveNetFlowShowOscillogramSwitch:NO];
    [[DoraemonCacheManager sharedInstance] saveMockGPSSwitch:NO];
    
    //监听h5Plugin点击回调
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(h5DoorPluginClick:) name:DoraemonH5DoorPluginNotification object:nil];
    
    [[DoraemonANRManager sharedInstance] addAnrBlock:^(NSDictionary *upLoadData) {
        if (self.anrBlock) {
            self.anrBlock(upLoadData);
        }
        //默认实现 保存到沙盒中
        NSString *testTime = upLoadData[@"testTime"];
        NSString *data = [DoraemonUtil dictToJsonStr:upLoadData];
        [DoraemonUtil saveAnrDataInFile:testTime data:data];
    }];
    
    [[DoraemonAllTestManager shareInstance] addPerformanceBlock:^(NSDictionary *upLoadData) {
        if (self.performanceBlock) {
            self.performanceBlock(upLoadData);
        }
        //默认实现 保存到沙盒中
        NSString *testTime = upLoadData[@"testTime"];
        NSString *data = [DoraemonUtil dictToJsonStr:upLoadData];
        [DoraemonUtil savePerformanceDataInFile:testTime data:data];
    }];
}


/**
 初始化内置工具数据
 */
- (void)initData{
    
    [self addPluginWithTitle:@"App信息" icon:@"doraemon_app" desc:@"App的一些基本信息" pluginName:@"DoraemonAppInfoPlugin" atModule:@"常用工具集"];
    [self addPluginWithTitle:@"沙盒浏览" icon:@"file" desc:@"沙盒浏览" pluginName:@"DoraemonSandboxPlugin" atModule:@"常用工具集"];
    [self addPluginWithTitle:@"MockGPS" icon:@"doraemon_gps" desc:@"mock GPS" pluginName:@"DoraemonGPSPlugin" atModule:@"常用工具集"];
    [self addPluginWithTitle:@"H5任意门" icon:@"doraemon_h5" desc:@"H5通用跳转" pluginName:@"DoraemonH5Plugin" atModule:@"常用工具集"];
    [self addPluginWithTitle:@"子线程UI" icon:@"doraemon_ui" desc:@"非主线程UI渲染检查" pluginName:@"DoraemonSubThreadUICheckPlugin" atModule:@"常用工具集"];
#if DoraemonWithLogger
    [self addPluginWithTitle:@"日志显示" icon:@"logger" desc:@"日志显示" pluginName:@"DoraemonLoggerPlugin" atModule:@"常用工具集"];
#endif
    
    [self addPluginWithTitle:@"帧率" icon:@"doraemon_fps" desc:@"帧率监控" pluginName:@"DoraemonFPSPlugin" atModule:@"性能监控"];
    [self addPluginWithTitle:@"CPU" icon:@"doraemon_cpu" desc:@"CPU监控" pluginName:@"DoraemonCPUPlugin" atModule:@"性能监控"];
    [self addPluginWithTitle:@"内存" icon:@"doraemon_memory" desc:@"内存监控" pluginName:@"DoraemonMemoryPlugin" atModule:@"性能监控"];
    [self addPluginWithTitle:@"流量" icon:@"doraemon_flow" desc:@"流量监控" pluginName:@"DoraemonNetFlowPlugin" atModule:@"性能监控"];
    [self addPluginWithTitle:@"自定义" icon:@"doraemon_alltest" desc:@"自定义你要选择测试的性能项" pluginName:@"DoraemonAllTestPlugin" atModule:@"性能监控"];
    
    [self addPluginWithTitle:@"颜色吸管" icon:@"doraemon_straw" desc:@"颜色拾取器" pluginName:@"DoraemonColorPickPlugin" atModule:@"视觉工具"];
    [self addPluginWithTitle:@"组件检查" icon:@"doraemon_finger" desc:@"View查看器" pluginName:@"DoraemonViewCheckPlugin" atModule:@"视觉工具"];
    [self addPluginWithTitle:@"对齐标尺" icon:@"doraemon_align" desc:@"查看组件是否对齐" pluginName:@"DoraemonViewAlignPlugin" atModule:@"视觉工具"];
    
    [self addPluginWithTitle:@"关闭" icon:@"doraemon_close" desc:@"隐藏Doraemon" pluginName:@"DoraemonClosePlugin" atModule:@"其他"];
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

- (void)h5DoorPluginClick:(NSNotification *)noti{
    NSDictionary *userInfo = noti.userInfo;
    NSString *h5Url = userInfo[@"h5Url"];
    if (h5Url.length>0 && self.h5DoorBlock) {
        self.h5DoorBlock(h5Url);
    }
}

- (void)hiddenHomeWindow{
    [[DoraemonHomeWindow shareInstance] hide];
}


@end
