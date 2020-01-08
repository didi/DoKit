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


@interface DoraemonHealthManager()
//每秒运行一次
@property (nonatomic, strong) NSTimer *secondTimer;
@property (nonatomic, strong) DoraemonFPSUtil *fpsUtil;

@property (nonatomic, strong) NSMutableArray *cpuPageArray;
@property (nonatomic, strong) NSMutableArray *cpuArray;
@property (nonatomic, strong) NSMutableArray *memoryPageArray;
@property (nonatomic, strong) NSMutableArray *memoryArray;
@property (nonatomic, strong) NSMutableArray *fpsPageArray;
@property (nonatomic, strong) NSMutableArray *fpsArray;

@end

@implementation DoraemonHealthManager

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
        _start = NO;
        _cpuPageArray = [[NSMutableArray alloc] init];
        _cpuArray = [[NSMutableArray alloc] init];
        _memoryPageArray = [[NSMutableArray alloc] init];
        _memoryArray = [[NSMutableArray alloc] init];
        _fpsPageArray = [[NSMutableArray alloc] init];
        _fpsArray = [[NSMutableArray alloc] init];
    }
    return self;
}



- (void)startHealthCheck{
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
    }
}

- (void)stopHealthCheck{
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
    NSTimeInterval timeInterval = [[NSDate date] timeIntervalSince1970]*1000;
    
    //2、获取当前cpu占用率
    CGFloat cpuValue = -1;
    cpuValue = [DoraemonCPUUtil cpuUsageForApp];
    if (cpuValue * 100 > 100) {
        cpuValue = 100;
    }else{
        cpuValue = cpuValue * 100;
    }
    
    [_cpuPageArray addObject:@{
        @"time":[NSString stringWithFormat:@"%.0f",timeInterval],
        @"value":[NSString stringWithFormat:@"%f",cpuValue]
    }];
    
    //3、获取当前memoryValue使用量
    NSInteger memoryValue = [DoraemonMemoryUtil useMemoryForApp];//单位MB
    [_memoryPageArray addObject:@{
        @"time":[NSString stringWithFormat:@"%.0f",timeInterval],
        @"value":[NSString stringWithFormat:@"%zi",memoryValue]
    }];
}

- (void)handleFPS:(NSInteger)fps{
    //处理fps信息
    NSTimeInterval timeInterval = [[NSDate date] timeIntervalSince1970]*1000;
    
    [_fpsPageArray addObject:@{
        @"time":[NSString stringWithFormat:@"%0.f",timeInterval],
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
    
    NSDictionary *dic = @{
        @"baseInfo":@{
                @"caseName":@"测试caseName",
                @"testPerson":@"易小翔",
                @"platform":@"iOS",
                @"time":testTime,
                @"phoneMode":phoneName,
                @"systemVersion":phoneSystem,
                @"appName":appName,
                @"appVersion":appVersion,
                @"dokitVersion":DoKitVersion,
                @"pId":[DoraemonManager shareInstance].pId ? [DoraemonManager shareInstance].pId : @""
        },
        @"data":@{
                @"cpu":[_cpuArray copy],
                @"memory":[_memoryArray copy],
                @"fps":[_fpsArray copy]
        }
    };
    
    NSLog(@"上传信息 == %@",dic);
    

    [DoraemonNetworkUtil postWithUrlString:@"http://172.23.164.35:80/healthCheck/addCheckData" params:dic success:^(NSDictionary * _Nonnull result) {
       
    } error:^(NSError * _Nonnull error) {
        
    }];

    
    [_cpuPageArray removeAllObjects];
    [_memoryPageArray removeAllObjects];
    [_fpsPageArray removeAllObjects];
    [_cpuArray removeAllObjects];
    [_memoryArray removeAllObjects];
    [_fpsArray removeAllObjects];
}

- (void)enterPage:(Class)vcClass{
    NSString *pageName = NSStringFromClass(vcClass);
    NSLog(@"进入页面 == %@",pageName);
    [_cpuPageArray removeAllObjects];
    [_memoryPageArray removeAllObjects];
    [_fpsPageArray removeAllObjects];
}

- (void)leavePage:(Class)vcClass{
    if ([vcClass isSubclassOfClass:[UINavigationController class]] || [vcClass isSubclassOfClass:[UITabBarController class]]) {
        return;
    }
    NSString *pageName = NSStringFromClass(vcClass);
    NSLog(@"离开页面 == %@",pageName);
    [_cpuArray addObject:@{
        @"page":pageName,
        @"values":[_cpuPageArray copy]
    }];
    [_memoryArray addObject:@{
        @"page":pageName,
        @"values":[_memoryPageArray copy]
    }];
    [_fpsArray addObject:@{
        @"page":pageName,
        @"values":[_fpsPageArray copy]
    }];
}
@end
