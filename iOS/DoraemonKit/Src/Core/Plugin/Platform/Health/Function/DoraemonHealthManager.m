//
//  DoraemonHealthManager.m
//  DoraemonKit
//
//  Created by didi on 2020/1/2.
//

#import "DoraemonHealthManager.h"
#import "DoraemonFPSUtil.h"
#import "DoraemonCPUUtil.h"
#import "DoraemonMemoryUtil.h"
#import "DoraemonNetworkUtil.h"
#import "DoraemonUtil.h"
#import "DoraemonAppInfoUtil.h"
#import "DoraemonDefine.h"
#import "DoraemonManager.h"
#import "DoraemonCacheManager.h"
#import "DoraemonANRManager.h"
#import "UIViewController+Doraemon.h"
#import "DoraemonUIProfileManager.h"
#import <UIKit/UIKit.h>
#import "DoraemonUtil.h"
#import "DoraemonHealthCountdownWindow.h"
#import "DoraemonBaseViewController.h"
#import "DoraemonToastUtil.h"

#if __has_include("DoraemonMethodUseTimeManager.h")
#import "DoraemonMethodUseTimeManager.h"
#endif



@interface DoraemonHealthManager()
//每秒运行一次
@property (nonatomic, strong) NSTimer *secondTimer;
@property (nonatomic, strong) DoraemonFPSUtil *fpsUtil;
@property (nonatomic, assign) BOOL firstEnter;

@property (nonatomic, strong) NSMutableArray *cpuPageArray;
@property (nonatomic, strong) NSMutableArray *cpuArray;
@property (nonatomic, strong) NSMutableArray *memoryPageArray;
@property (nonatomic, strong) NSMutableArray *memoryArray;
@property (nonatomic, strong) NSMutableArray *fpsPageArray;
@property (nonatomic, strong) NSMutableArray *fpsArray;
@property (nonatomic, strong) NSMutableArray *networkPageArray;
@property (nonatomic, strong) NSMutableArray *networkArray;
@property (nonatomic, strong) NSMutableArray *blockArray;
@property (nonatomic, strong) NSMutableArray *subThreadUIArray;
@property (nonatomic, strong) NSMutableArray *uiLevelArray;
@property (nonatomic, strong) NSMutableArray *leakArray;
@property (nonatomic, strong) NSMutableDictionary *pageEnterMap;
@property (nonatomic, strong) NSMutableArray *pageLoadArray;
@property (nonatomic, strong) NSMutableArray *bigFileArray;
@property (nonatomic, copy) NSString *h5UrlString;

@end

@implementation DoraemonHealthManager{
    dispatch_semaphore_t semaphore;
}

+ (instancetype)sharedInstance {
    static id instance = nil;
    static dispatch_once_t onceToken;
    
    dispatch_once(&onceToken, ^{
        instance = [[self alloc] init];
    });
    return instance;
}

- (instancetype)init{
    self = [super init];
    if (self) {
        _start = [[DoraemonCacheManager sharedInstance] healthStart];
        _cpuPageArray = [[NSMutableArray alloc] init];
        _cpuArray = [[NSMutableArray alloc] init];
        _memoryPageArray = [[NSMutableArray alloc] init];
        _memoryArray = [[NSMutableArray alloc] init];
        _fpsPageArray = [[NSMutableArray alloc] init];
        _fpsArray = [[NSMutableArray alloc] init];
        _networkPageArray = [[NSMutableArray alloc] init];
        _networkArray = [[NSMutableArray alloc] init];
        _blockArray = [[NSMutableArray alloc] init];
        _subThreadUIArray = [[NSMutableArray alloc] init];
        _uiLevelArray = [[NSMutableArray alloc] init];
        _leakArray = [[NSMutableArray alloc] init];
        _pageEnterMap = [[NSMutableDictionary alloc] init];
        _pageLoadArray = [[NSMutableArray alloc] init];
        _bigFileArray = [[NSMutableArray alloc] init];
        semaphore = dispatch_semaphore_create(1);
    }
    return self;
}

- (void)rebootAppForHealthCheck{
    [[DoraemonCacheManager sharedInstance] saveHealthStart:YES];
    [[DoraemonCacheManager sharedInstance] saveStartTimeSwitch:YES];
    #if __has_include("DoraemonMethodUseTimeManager.h")
    [DoraemonMethodUseTimeManager sharedInstance].on = YES;
    #endif
    [[DoraemonCacheManager sharedInstance] saveNetFlowSwitch:YES];
    [[DoraemonCacheManager sharedInstance] saveSubThreadUICheckSwitch:YES];
    [[DoraemonCacheManager sharedInstance] saveMemoryLeak:YES];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        exit(0);
    });
    
}

- (void)startHealthCheck{
    _start = YES;
    if (_start) {
        if(!_secondTimer){
            _secondTimer = [NSTimer timerWithTimeInterval:0.5f target:self selector:@selector(doSecondFunction) userInfo:nil repeats:YES];
            [[NSRunLoop currentRunLoop] addTimer:_secondTimer forMode:NSRunLoopCommonModes];
            if (!_fpsUtil) {
                _fpsUtil = [[DoraemonFPSUtil alloc] init];
                __weak typeof(self) weakSelf = self;
                [_fpsUtil addFPSBlock:^(NSInteger fps) {
                    [weakSelf handleFPS:fps];
                    
                }];
            }
            [_fpsUtil start];
        }
        [[DoraemonANRManager sharedInstance] start];
        [DoraemonUIProfileManager sharedInstance].enable = YES;
    }
}

- (void)stopHealthCheck{
    _start = NO;
    [[DoraemonHealthCountdownWindow shareInstance] hide];
    [[DoraemonCacheManager sharedInstance] saveHealthStart:NO];
    [[DoraemonCacheManager sharedInstance] saveStartTimeSwitch:NO];
    #if __has_include("DoraemonMethodUseTimeManager.h")
    [DoraemonMethodUseTimeManager sharedInstance].on = NO;
    #endif
    [[DoraemonCacheManager sharedInstance] saveNetFlowSwitch:NO];
    [[DoraemonCacheManager sharedInstance] saveSubThreadUICheckSwitch:NO];
    [[DoraemonCacheManager sharedInstance] saveMemoryLeak:NO];
    [DoraemonUIProfileManager sharedInstance].enable = NO;
    [[DoraemonANRManager sharedInstance] stop];
    if(_secondTimer){
        [_secondTimer invalidate];
        _secondTimer = nil;
    }
    if (_fpsUtil) {
        [_fpsUtil end];
    }
    [self upLoadData];
}

- (void)doSecondFunction{
    //最多采样40个点
    if(_cpuPageArray.count > 40){
        return;
    }
    
    //1、获取当前时间
    NSString *currentTimeInterval = [DoraemonUtil currentTimeInterval];
    
    //2、获取当前cpu占用率
    CGFloat cpuValue = -1;
    cpuValue = [DoraemonCPUUtil cpuUsageForApp];
    if (cpuValue * 100 > 100) {
        cpuValue = 100;
    }else{
        cpuValue = cpuValue * 100;
    }
    
    [_cpuPageArray addObject:@{
        @"time":currentTimeInterval,
        @"value":[NSString stringWithFormat:@"%f",cpuValue]//单位百分比
    }];
    
    //3、获取当前memoryValue使用量
    NSInteger memoryValue = [DoraemonMemoryUtil useMemoryForApp];//单位MB
    [_memoryPageArray addObject:@{
        @"time":currentTimeInterval,
        @"value":[NSString stringWithFormat:@"%zi",memoryValue]//单位MB
    }];
}

- (void)handleFPS:(NSInteger)fps{
    //最多采样40个点
    if (_fpsPageArray.count > 40) {
        return;
    }
    [_fpsPageArray addObject:@{
        @"time":[DoraemonUtil currentTimeInterval],
        @"value":[NSString stringWithFormat:@"%zi",fps]
    }];
}

- (void)upLoadData{
    if (self.caseName.length>0 && self.testPerson.length>0) {
        NSString *testTime = [DoraemonUtil dateFormatNow];
        NSString *phoneName = [DoraemonAppInfoUtil iphoneType];
        NSString *phoneSystem = [[UIDevice currentDevice] systemVersion];
        NSString *appVersion = [[NSBundle mainBundle] objectForInfoDictionaryKey: @"CFBundleShortVersionString"];
        NSString *appName = [DoraemonAppInfoUtil appName];
        
        
        //启动流程
        NSArray *loadArray = nil;
        #if __has_include("DoraemonMethodUseTimeManager.h")
        loadArray = [[DoraemonMethodUseTimeManager sharedInstance] fixLoadModelArrayForHealth];
        #endif
        
        NSDictionary *appStart = @{
            @"costTime" : @(self.startTime),
            @"costDetail" : STRING_NOT_NULL(self.costDetail),
            @"loadFunc" : loadArray ? loadArray : @[]
        };
        
        //大文件扫描
        NSString *homeDir = NSHomeDirectory();
        DoraemonUtil *util = [[DoraemonUtil alloc] init];
        [util getBigSizeFileFormPath:homeDir];
        NSArray *bigFileInfoArray = [self formatInfoByPathArray:util.bigFileArray];
        
        NSDictionary *dic = @{
            @"baseInfo":@{
                    @"caseName":STRING_NOT_NULL(self.caseName),
                    @"testPerson":STRING_NOT_NULL(self.testPerson),
                    @"platform":@"iOS",
                    @"time":testTime,
                    @"phoneMode":phoneName,
                    @"systemVersion":phoneSystem,
                    @"appName":appName,
                    @"appVersion":appVersion,
                    @"dokitVersion":DoKitVersion,
                    @"pId":STRING_NOT_NULL([DoraemonManager shareInstance].pId)
            },
            @"data":@{
                    @"cpu":[_cpuArray copy],
                    @"memory":[_memoryArray copy],
                    @"fps":[_fpsArray copy],
                    @"appStart":appStart,
                    @"network": [_networkArray copy],
                    @"block":[_blockArray copy],
                    @"subThreadUI":[_subThreadUIArray copy],
                    @"uiLevel":[_uiLevelArray copy],
                    @"leak":[_leakArray copy],
                    @"pageLoad":[_pageLoadArray copy],
                    @"bigFile":[bigFileInfoArray copy]
            }
        };
        
        DoKitLog(@"upload info == %@",dic);
        
        if (![DoraemonManager shareInstance].pId) {
            DoKitLog(@"dokik pId empty");
        }

        [DoraemonNetworkUtil postWithUrlString:@"https://www.dokit.cn/healthCheck/addCheckData" params:dic success:^(NSDictionary * _Nonnull result) {
            NSInteger code = [result[@"code"] integerValue];
            if (code == 200) {
                [DoraemonToastUtil showToastBlack:DoraemonLocalizedString(@"数据上传成功")  inView:[UIViewController rootViewControllerForDoraemonHomeWindow].view];
            }else{
                NSString *msg = result[@"msg"];
                if (msg) {
                    [DoraemonToastUtil showToastBlack:msg inView:[UIViewController rootViewControllerForDoraemonHomeWindow].view];
                }
            }

        } error:^(NSError * _Nonnull error) {
            [DoraemonToastUtil showToastBlack:DoraemonLocalizedString(@"数据上传失败")  inView:[UIViewController rootViewControllerForDoraemonHomeWindow].view];
        }];
    }
    
    [_cpuPageArray removeAllObjects];
    [_memoryPageArray removeAllObjects];
    [_fpsPageArray removeAllObjects];
    [_networkPageArray removeAllObjects];
    [_cpuArray removeAllObjects];
    [_memoryArray removeAllObjects];
    [_fpsArray removeAllObjects];
    [_networkArray removeAllObjects];
    [_blockArray removeAllObjects];
    [_subThreadUIArray removeAllObjects];
    [_uiLevelArray removeAllObjects];
    [_leakArray removeAllObjects];
    [_pageLoadArray removeAllObjects];
    [_bigFileArray removeAllObjects];
    _h5UrlString = nil;
}

- (void)startEnterPage:(Class)vcClass{
    if (!_start) {
        return;
    }
    if ([self blackList:vcClass]) {
        return;
    }
    NSString *pageName = NSStringFromClass(vcClass);
    CGFloat beginTime = CACurrentMediaTime();
    [_pageEnterMap setValue:@(beginTime) forKey:pageName];
    DoKitLog(@"yixiang 开始进入页面 == %@ 时间 == %f",pageName,beginTime);
    
}

- (void)enterPage:(Class)vcClass{
    if (!_start) {
        return;
    }
    if ([self blackList:vcClass]) {
        return;
    }
    [[DoraemonHealthCountdownWindow shareInstance] start:10];
    NSString *pageName = NSStringFromClass(vcClass);
    DoKitLog(@"yixiang 已经进入页面 == %@",pageName);
    if (_pageEnterMap[pageName]) {
        CGFloat beginTime = [_pageEnterMap[pageName] floatValue];
        CGFloat endTime = CACurrentMediaTime();
        NSInteger costTime = (NSInteger)((endTime - beginTime)*1000+0.5);//四舍五入 ms
        [_pageLoadArray addObject:@{
            @"page":NSStringFromClass(vcClass),
            @"time":@(costTime)//ms
        }];
    }
    [_pageEnterMap removeObjectForKey:pageName];
    [_cpuPageArray removeAllObjects];
    [_memoryPageArray removeAllObjects];
    [_fpsPageArray removeAllObjects];
    [_networkPageArray removeAllObjects];
}

- (void)leavePage:(Class)vcClass{
    if (!_start) {
        return;
    }
    if ([self blackList:vcClass]) {
        return;
    }
    NSString *pageName = NSStringFromClass(vcClass);
    if (_h5UrlString.length>0) {
        pageName = [NSString stringWithFormat:@"%@(%@)",pageName,_h5UrlString];
        _h5UrlString = nil;
    }
    
    DoKitLog(@"离开页面 == %@",pageName);
    
    if (_networkPageArray.count>0) {
        [_networkArray addObject:@{
            @"page":pageName,
            @"values":[_networkPageArray copy]
        }];
    }
    
    //cpu 内存 fps必须保证每一个页面运行10秒
    if ([[DoraemonHealthCountdownWindow shareInstance] getCountdown] > 0) {
        return;
    }

    if (_cpuPageArray.count>0) {
        [_cpuArray addObject:@{
            @"page":pageName,
            @"values":[_cpuPageArray copy]
        }];
    }
    
    if(_memoryPageArray.count>0) {
        [_memoryArray addObject:@{
            @"page":pageName,
            @"values":[_memoryPageArray copy]
        }];
    }

    if (_fpsPageArray.count>0) {
        [_fpsArray addObject:@{
            @"page":pageName,
            @"values":[_fpsPageArray copy]
        }];
    }
    
    [[DoraemonHealthCountdownWindow shareInstance] hide];
}

- (BOOL)blackList:(Class)vcClass{
    if ([vcClass isSubclassOfClass:[DoraemonBaseViewController class]]) {
        return YES;
    }
    if ([vcClass isSubclassOfClass:[UINavigationController class]] || [vcClass isSubclassOfClass:[UITabBarController class]]) {
        return YES;
    }
    NSString *vcName = NSStringFromClass(vcClass);
    NSArray *blackList = @[
        @"UIViewController",
        @"UIInputWindowController",
        @"UICompatibilityInputViewController",
        @"UIEditingOverlayViewController",
        @"UISystemInputAssistantViewController",
        @"UIPredictionViewController",
        @"_UIRemoteInputViewController",
        @"UIEditingOverlayViewController",
        @"AssistiveTouchController",
        @"UICandidateViewController",
        @"UISystemKeyboardDockController",
        @"UIApplicationRotationFollowingControllerNoTouches"
    ];
    if ([blackList containsObject:vcName]) {
        return YES;
    }
    return NO;
}

- (void)addHttpModel:(DoraemonNetFlowHttpModel *)httpModel{
    if (_start) {
        dispatch_semaphore_wait(semaphore, DISPATCH_TIME_FOREVER);
        [_networkPageArray addObject:@{
            @"time": [DoraemonUtil currentTimeInterval],
            @"url": STRING_NOT_NULL(httpModel.url) ,
            @"up": STRING_NOT_NULL(httpModel.uploadFlow),
            @"down": STRING_NOT_NULL(httpModel.downFlow),
            @"code": STRING_NOT_NULL(httpModel.statusCode),
            @"method": STRING_NOT_NULL(httpModel.method)
        }];
        dispatch_semaphore_signal(semaphore);
    }
}

- (void)addANRInfo:(NSDictionary *)anrInfo{
    if (_start) {
        [_blockArray addObject:@{
            @"page":[self currentTopVC],
            @"blockTime":anrInfo[@"duration"],
            @"detail":anrInfo[@"content"]
        }];
    }
}

- (void)addSubThreadUI:(NSDictionary *)info{
    if (_start) {
        [_subThreadUIArray addObject:@{
            @"page":[self currentTopVC],
            @"detail":info[@"content"]
        }];
    }
}

- (void)addUILevel:(NSDictionary *)info{
    if (_start) {
        [_uiLevelArray addObject:@{
            @"page":STRING_NOT_NULL([self currentTopVC]),
            @"level":info[@"level"],
            @"detail":info[@"detail"]
        }];
    }
}

- (void)addLeak:(NSDictionary *)info{
    if (_start) {
        NSString *viewStack = info[@"viewStack"];
        NSString *retainCycle = info[@"retainCycle"];
        NSString *detail = [NSString stringWithFormat:@"viewStack : \n%@ \n\n retainCycle : \n%@\n\n",STRING_NOT_NULL(viewStack),STRING_NOT_NULL(retainCycle)];
        [_leakArray addObject:@{
            @"page":info[@"className"],
            @"detail":detail
        }];
    }
}

- (void)openH5Page:(NSString *)h5Url{
    if (_start) {
        __weak typeof(self) weakSelf = self;
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1. * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            weakSelf.h5UrlString = h5Url;
        });
    }
}



- (NSString *)currentTopVC{
    UIViewController *vc = [UIViewController topViewControllerForKeyWindow];
    NSString *vcName = NSStringFromClass([vc class]);
    return vcName;
}

- (NSArray *)formatInfoByPathArray:(NSArray *)pathArray{
    NSMutableArray *fileInfoArray = [[NSMutableArray alloc] init];
    for (NSString *path in pathArray) {
        NSDictionary *dict = [[NSFileManager defaultManager] attributesOfItemAtPath:path error:nil];
        NSInteger fileSize = [dict[@"NSFileSize"] integerValue];
        //NSString *fileSizeString = [NSByteCountFormatter stringFromByteCount:fileSize countStyle: NSByteCountFormatterCountStyleFile];
        NSString *fileSizeString = [NSString stringWithFormat:@"%zi",fileSize];
        NSString *fileName = [path lastPathComponent];
        NSString *filePtahFromHomeDir = [self getPathFromHomeDir:path];
        [fileInfoArray addObject:@{
            @"fileName":fileName,
            @"fileSize":fileSizeString,
            @"filePath":filePtahFromHomeDir
        }];
    }
    return fileInfoArray;
}

- (NSString *)getPathFromHomeDir:(NSString *)path{
    NSString *homeDir = NSHomeDirectory();
    NSString *relativePath = @"";
    if ([path hasPrefix:homeDir]) {
        relativePath = [path substringFromIndex:homeDir.length];
    }
    return relativePath;
}
@end
