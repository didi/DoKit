//
//  DoraemonHealthManager.m
//  AFNetworking
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
#import "DoraemonMethodUseTimeManager.h"
#import "DoraemonANRManager.h"
#import "UIViewController+Doraemon.h"
#import "DoraemonUIProfileManager.h"
#import <UIKit/UIKit.h>


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
        semaphore = dispatch_semaphore_create(1);
    }
    return self;
}

- (void)rebootAppForHealthCheck{
    [[DoraemonCacheManager sharedInstance] saveHealthStart:YES];
    [[DoraemonCacheManager sharedInstance] saveStartTimeSwitch:YES];
    [DoraemonMethodUseTimeManager sharedInstance].on = YES;
    [[DoraemonCacheManager sharedInstance] saveNetFlowSwitch:YES];
    [[DoraemonCacheManager sharedInstance] saveSubThreadUICheckSwitch:YES];
    [[DoraemonCacheManager sharedInstance] saveMemoryLeak:YES];
    exit(0);
}

- (void)startHealthCheck{
    _start = YES;
    if (_start) {
        if(!_secondTimer){
            _secondTimer = [NSTimer timerWithTimeInterval:1.0f target:self selector:@selector(doSecondFunction) userInfo:nil repeats:YES];
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
    [[DoraemonCacheManager sharedInstance] saveHealthStart:NO];
    [[DoraemonCacheManager sharedInstance] saveStartTimeSwitch:NO];
    [DoraemonMethodUseTimeManager sharedInstance].on = NO;
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
    //1、获取当前时间
    NSString *currentTimeInterval = [self currentTimeInterval];
    
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
        @"value":[NSString stringWithFormat:@"%f",cpuValue]
    }];
    
    //3、获取当前memoryValue使用量
    NSInteger memoryValue = [DoraemonMemoryUtil useMemoryForApp];//单位MB
    [_memoryPageArray addObject:@{
        @"time":currentTimeInterval,
        @"value":[NSString stringWithFormat:@"%zi",memoryValue]
    }];
}

- (void)handleFPS:(NSInteger)fps{
    [_fpsPageArray addObject:@{
        @"time":[self currentTimeInterval],
        @"value":[NSString stringWithFormat:@"%zi",fps]
    }];
}

- (void)upLoadData{
    NSString *testTime = [DoraemonUtil dateFormatNow];
    NSString *phoneName = [DoraemonAppInfoUtil iphoneType];
    NSString *phoneSystem = [[UIDevice currentDevice] systemVersion];
    NSString *appVersion = [[NSBundle mainBundle] objectForInfoDictionaryKey: @"CFBundleShortVersionString"];
    
    NSString *appName = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleDisplayName"];
    if (!appName) {
        appName = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleName"];
    }
    
    
    //启动流程
    NSArray *loadArray = [[DoraemonMethodUseTimeManager sharedInstance] fixLoadModelArrayForHealth];
    NSDictionary *appStart = @{
        @"costTime" : @(self.startTime),
        @"costDetail" : STRING_NOT_NULL(self.costDetail),
        @"loadFunc" : loadArray ? loadArray : @[]
    };
    
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
                @"pageLoad":[_pageLoadArray copy]
        }
    };
    
    NSLog(@"上传信息 == %@",dic);
    

    [DoraemonNetworkUtil postWithUrlString:@"http://172.23.163.190:80/healthCheck/addCheckData" params:dic success:^(NSDictionary * _Nonnull result) {
       
    } error:^(NSError * _Nonnull error) {
        
    }];

    
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
}

- (void)startEnterPage:(Class)vcClass{
    if ([self blackList:vcClass]) {
        return;
    }
    NSString *pageName = NSStringFromClass(vcClass);
    CGFloat beginTime = CACurrentMediaTime();
    [_pageEnterMap setValue:@(beginTime) forKey:pageName];
    NSLog(@"yixiang 开始进入页面 == %@ 时间 == %f",pageName,beginTime);
    
}

- (void)enterPage:(Class)vcClass{
    if ([self blackList:vcClass]) {
        return;
    }
    NSString *pageName = NSStringFromClass(vcClass);
    NSLog(@"yixiang 已经进入页面 == %@",pageName);
    if (_pageEnterMap[pageName]) {
        CGFloat beginTime = [_pageEnterMap[pageName] floatValue];
        CGFloat endTime = CACurrentMediaTime();
        CGFloat costTime = endTime - beginTime;
        NSLog(@"yixiang 耗时 == %f",endTime);
        NSLog(@"yixiang 耗时 == %f",costTime);
        [_pageLoadArray addObject:@{
            @"page":NSStringFromClass(vcClass),
            @"time":@(costTime)//s
        }];
    }
    [_pageEnterMap removeObjectForKey:pageName];
    [_cpuPageArray removeAllObjects];
    [_memoryPageArray removeAllObjects];
    [_fpsPageArray removeAllObjects];
    [_networkPageArray removeAllObjects];
}

- (void)leavePage:(Class)vcClass{
    if ([self blackList:vcClass]) {
        return;
    }
    NSString *pageName = NSStringFromClass(vcClass);
    NSLog(@"离开页面 == %@",pageName);
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

    if (_networkPageArray.count>0) {
        [_networkArray addObject:@{
            @"page":pageName,
            @"values":[_networkPageArray copy]
        }];
    }
}

- (BOOL)blackList:(Class)vcClass{
    if ([vcClass isSubclassOfClass:[UINavigationController class]] || [vcClass isSubclassOfClass:[UITabBarController class]]) {
        return YES;
    }
    NSString *vcName = NSStringFromClass(vcClass);
    NSArray *blackList = @[
        @"UIViewController",
        @"UIInputWindowController",
        @"UICompatibilityInputViewController"
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
            @"time": [self currentTimeInterval],
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
        [_leakArray addObject:@{
            @"page":info[@"className"],
            @"detail":info[@"viewStack"]
        }];
    }
}

- (NSString *)currentTimeInterval{
    NSTimeInterval timeInterval = [[NSDate date] timeIntervalSince1970]*1000;
    return [NSString stringWithFormat:@"%0.f",timeInterval];
}

- (NSString *)currentTopVC{
    UIViewController *vc = [UIViewController topViewControllerForKeyWindow];
    NSString *vcName = NSStringFromClass([vc class]);
    return vcName;
}
@end
