//
//  DoraemonAllTestManager.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/4/25.
//

#import "DoraemonAllTestManager.h"
#import "DoraemonCPUUtil.h"
#import "DoraemonMemoryUtil.h"
#import "DoraemonAppInfoUtil.h"
#import "DoraemonMemoryUtil.h"
#import "DoraemonNetFlowManager.h"
#import "DoraemonNetFlowDataSource.h"
#import "DoraemonFPSUtil.h"

@interface DoraemonAllTestManager()

//每秒运行一次
@property (nonatomic, strong) NSTimer *secondTimer;

@property (nonatomic, assign) NSInteger fpsValue;
@property (nonatomic, strong) DoraemonFPSUtil *fpsUtil;

@property (nonatomic, strong) NSMutableArray *commonDataArray;

@property (nonatomic, copy) DoraemonAllTestManagerBlock block;

@end

@implementation DoraemonAllTestManager

+ (DoraemonAllTestManager *)shareInstance{
    static dispatch_once_t once;
    static DoraemonAllTestManager *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonAllTestManager alloc] init];
    });
    return instance;
}

- (void)startRecord{
    [DoraemonAllTestManager shareInstance].startTimeInterval = [[NSDate date] timeIntervalSince1970];
    if(!_secondTimer){
        _secondTimer = [NSTimer timerWithTimeInterval:1.0f target:self selector:@selector(doSecondFunction) userInfo:nil repeats:YES];
        [[NSRunLoop currentRunLoop] addTimer:_secondTimer forMode:NSRunLoopCommonModes];
        if(_fpsSwitchOn){
            if (!_fpsUtil) {
                _fpsUtil = [[DoraemonFPSUtil alloc] init];
                __weak typeof(self) weakSelf = self;
                [_fpsUtil addFPSBlock:^(NSInteger fps) {
                    weakSelf.fpsValue = fps;
                }];
            }
            [_fpsUtil start];
        }
    }
    if(_flowSwitchOn){
        [[DoraemonNetFlowManager shareInstance] canInterceptNetFlow:YES];
    }
}

- (void)upLoadData{
    NSString *testTime = [NSString stringWithFormat:@"%f",_startTimeInterval];;
    NSString *phoneName = [DoraemonAppInfoUtil iphoneType];
    NSString *phoneSystem = [[UIDevice currentDevice] systemVersion];
    NSUInteger totalMemory = [DoraemonMemoryUtil totalMemoryForDevice];
    NSUInteger phoneMemory = totalMemory;//MB为单位
    NSString *appVersion = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleVersion"];
    NSString *appName = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleDisplayName"];
    if (!appName) {
        appName = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleName"];
    }
    
    //流量信息
    NSMutableArray<DoraemonNetFlowHttpModel *> *httpModelArray = [DoraemonNetFlowDataSource shareInstance].httpModelArray;
    NSMutableArray *flowData = [NSMutableArray array];
    for (DoraemonNetFlowHttpModel *httpModel in httpModelArray) {
        NSString *url = httpModel.url;
        NSString *time = [NSString stringWithFormat:@"%f",httpModel.startTime];
        
        NSString *upFlow = httpModel.uploadFlow;
        NSString *downFlow = httpModel.downFlow;
        
        NSDictionary *flowItem = @{
                                   @"url":url,
                                   @"time":time,
                                   @"upFlow":upFlow,
                                   @"downFlow":downFlow
                                   };
        [flowData addObject:flowItem];
    }
    
    NSDictionary *upLoadData = @{
                                 @"testTime":testTime,
                                 @"phoneName":phoneName,
                                 @"phoneSystem":phoneSystem,
                                 @"phoneMemory":@(phoneMemory),
                                 @"appVersion":appVersion,
                                 @"appName":appName,
                                 @"common_data":_commonDataArray,
                                 @"flow_data":flowData
                                 };
    
    // 7、数据处理
    if (self.block) {
        self.block(upLoadData);
    }
}



- (void)endRecord{
    if(_secondTimer){
        [_secondTimer invalidate];
        _secondTimer = nil;
    }
    if (_fpsUtil) {
        [_fpsUtil end];
    }
    [self upLoadData];
    
    _commonDataArray = nil;
    if(_flowSwitchOn){
        [[DoraemonNetFlowManager shareInstance] canInterceptNetFlow:NO];
    }
    
}

- (void)doSecondFunction{
    //1、获取当前时间戳
    NSDate *now = [NSDate date];
    NSString *timeInterval = [NSString stringWithFormat:@"%f",[now timeIntervalSince1970]];
    
    //2、获取当前FPS值
    NSInteger fpsValue = -1;
    if (_fpsSwitchOn) {
        fpsValue = _fpsValue;
    }
    
    
    //3、获取当前cpu占用率
    CGFloat cpuValue = -1;
    if (_cpuSwitchOn) {
        cpuValue = [DoraemonCPUUtil cpuUsageForApp];
        if (cpuValue * 100 > 100) {
            cpuValue = 100;
        }else{
            cpuValue = cpuValue * 100;
        }
    }
    
    //4、获取当前memoryValue使用量
    NSInteger memoryValue = -1;
    if (_memorySwitchOn) {
        memoryValue = [DoraemonMemoryUtil useMemoryForApp];//单位MB
    }
    
    //5、获取当前电流
    //CGFloat current = -1;
    
    //6、组装commonData数据
    NSDictionary *commonItemData = @{
                                     @"time":timeInterval,
                                     @"fps":@(fpsValue),
                                     @"CPU":@(cpuValue),
                                     @"memory":@(memoryValue)
                                     };
    
    if (!_commonDataArray) {
        _commonDataArray = [NSMutableArray array];
    }
    [_commonDataArray addObject:commonItemData];
}


- (void)addPerformanceBlock:(DoraemonAllTestManagerBlock)block{
    self.block = block;
}




@end
