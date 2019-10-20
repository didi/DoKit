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
#import "DoraemonDefine.h"
#import "DoraemonAllTestWindow.h"
#import "DoraemonAllTestStatisticsManager.h"
#import "UIViewController+Doraemon.h"

@interface DoraemonAllTestManager()

//每秒运行一次
@property (nonatomic, strong) NSTimer *secondTimer;

@property (nonatomic, assign) NSInteger fpsValue;
@property (nonatomic, strong) DoraemonFPSUtil *fpsUtil;

@property (nonatomic, strong) NSMutableArray *commonDataArray;

@property (nonatomic, copy) DoraemonAllTestManagerBlock block;

@property (nonatomic, strong) NSDateFormatter *dateFormatter;

@property (nonatomic, strong) NSMutableDictionary *windowDic;
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

- (NSDateFormatter *)dateFormatter {
    if (!_dateFormatter) {
        _dateFormatter = [[NSDateFormatter alloc] init];
        [_dateFormatter setDateFormat: @"yyyy-MM-dd HH:mm:ss:SSS"];
    }
    return _dateFormatter;
}

- (void)startRecord{
    [DoraemonAllTestManager shareInstance].startTime = [NSDate date];
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
    NSString *testTime = [self.dateFormatter stringFromDate: self.startTime];
    NSString *phoneName = [DoraemonAppInfoUtil iphoneType];
    NSString *phoneSystem = [[UIDevice currentDevice] systemVersion];
    NSUInteger totalMemory = [DoraemonMemoryUtil totalMemoryForDevice];
    NSUInteger phoneMemory = totalMemory;//MB为单位
    NSString *appVersion = [[NSBundle mainBundle] objectForInfoDictionaryKey: @"CFBundleShortVersionString"];
    
    NSString *appName = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleDisplayName"];
    if (!appName) {
        appName = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleName"];
    }
    
    //流量信息
    NSMutableArray<DoraemonNetFlowHttpModel *> *httpModelArray = [DoraemonNetFlowDataSource shareInstance].httpModelArray;
    NSMutableArray *flowData = [NSMutableArray array];
    for (DoraemonNetFlowHttpModel *httpModel in httpModelArray) {
        NSString *url = httpModel.url;
        NSString *time = [self.dateFormatter stringFromDate: [NSDate dateWithTimeIntervalSince1970: httpModel.startTime]];
        NSString *upFlow = httpModel.uploadFlow;
        NSString *downFlow = httpModel.downFlow;
        NSString *topVc = httpModel.topVc;
        
        NSDictionary *flowItem = @{
                                   @"url":url,
                                   @"time":time,
                                   @"upFlow":upFlow,
                                   @"downFlow":downFlow,
                                   @"page":STRING_NOT_NULL(topVc)
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
    
    // 7、发送给DoraemonStatisticsManager
    [DoraemonAllTestStatisticsManager shareInstance].resultDic = upLoadData;
    
    // 8、回调给外部block
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
    //1、获取当前时间
    NSString *timeString = [self.dateFormatter stringFromDate: [NSDate date]];
    
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
    
    //6、获取顶层VC
    UIViewController *vc = [UIViewController topViewControllerForKeyWindow];
    NSString *vcName = NSStringFromClass([vc class]);
    
    //7、组装commonData数据
    NSDictionary *commonItemData = @{
                                     @"time":timeString,
                                     @"fps":@(fpsValue),
                                     @"CPU":@(cpuValue),
                                     @"memory":@(memoryValue),
                                     @"page":STRING_NOT_NULL(vcName)
                                     };
    
    if (!_commonDataArray) {
        _commonDataArray = [NSMutableArray array];
    }
    [_commonDataArray addObject:commonItemData];
    
    
    if(self.realTimeSwitchOn){
        [DoraemonAllTestWindow shareInstance].hidden = NO;
        [self allTestWindowShow:memoryValue CPU:cpuValue fps:fpsValue];
    }else{
        [DoraemonAllTestWindow shareInstance].hidden = YES;
    }
}


- (void)addPerformanceBlock:(DoraemonAllTestManagerBlock)block{
    self.block = block;
}

- (void)allTestWindowShow:(NSInteger)memoryValue CPU:(CGFloat)cpuValue fps:(NSInteger)fpsValue{
    
    _windowDic = [NSMutableDictionary dictionary];
    if(_memorySwitchOn){
      _windowDic[@"memory"] = [NSString stringWithFormat:@"%@ : %ldM",DoraemonLocalizedString(@"内存"),memoryValue];
    }
    if(_cpuSwitchOn){
        _windowDic[@"CPU"] = [NSString stringWithFormat:@"%@ : %.1f%@",@"CPU",cpuValue,@"%"];
    }
    if(_fpsSwitchOn){
        _windowDic[@"fps"] = [NSString stringWithFormat:@"%@ : %ld",@"FPS",fpsValue];
    }
    if(_flowSwitchOn){
        if(![DoraemonAllTestWindow shareInstance].flowChanged)
            [[DoraemonAllTestWindow shareInstance] updateFlowValue:@"0" downFlow:@"0"];
    }else{
        [[DoraemonAllTestWindow shareInstance] hideFlowValue];
    }
    
    [[DoraemonAllTestWindow shareInstance] updateCommonValue:_windowDic[@"memory"] cpu:_windowDic[@"CPU"] fps:_windowDic[@"fps"]];
}

@end
