//
//  DoraemonNetFlowOscillogramViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/5/2.
//

#import "DoraemonNetFlowOscillogramViewController.h"
#import "DoraemonOscillogramView.h"
#import "UIView+Positioning.h"
#import "DoraemonNetFlowDataSource.h"

@interface DoraemonNetFlowOscillogramViewController ()

@property (nonatomic, strong) DoraemonOscillogramView *oscillogramView;

//每秒运行一次
@property (nonatomic, strong) NSTimer *secondTimer;

@end

@implementation DoraemonNetFlowOscillogramViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor clearColor];
    [self setEdgesForExtendedLayout:UIRectEdgeNone];
    
    UILabel *titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, self.view.doraemon_width, 20)];
    titleLabel.backgroundColor = [UIColor lightGrayColor];
    titleLabel.text = @"  流量检测";
    titleLabel.font = [UIFont systemFontOfSize:12];
    titleLabel.textColor = [UIColor whiteColor];
    [self.view addSubview:titleLabel];
    
    _oscillogramView = [[DoraemonOscillogramView alloc] initWithFrame:CGRectMake(0, titleLabel.doraemon_bottom+10, self.view.doraemon_width, 200)];
    _oscillogramView.backgroundColor = [UIColor clearColor];
    [_oscillogramView setLowValue:@"0"];
    [_oscillogramView setHightValue:[NSString stringWithFormat:@"%zi",10000]];
    [self.view addSubview:_oscillogramView];
    
}

- (void)startRecord{
    if(!_secondTimer){
        _secondTimer = [NSTimer timerWithTimeInterval:1.0f target:self selector:@selector(doSecondFunction) userInfo:nil repeats:YES];
        [[NSRunLoop currentRunLoop] addTimer:_secondTimer forMode:NSRunLoopCommonModes];
    }
}

- (void)endRecord{
    if(_secondTimer){
        [_secondTimer invalidate];
        _secondTimer = nil;
        [_oscillogramView clear];
    }
}

//每一秒钟采样一次流量情况
- (void)doSecondFunction{
    NSUInteger useNetFlowForApp = 0.;
    NSUInteger totalNetFlowForDevice = 10000;
    
    NSTimeInterval now = [[NSDate date] timeIntervalSince1970];
    NSTimeInterval start = now - 1;
    
    NSMutableArray<DoraemonNetFlowHttpModel *> *httpModelArray = [DoraemonNetFlowDataSource shareInstance].httpModelArray;
    
    NSInteger totalNetFlow = 0.;
    for (DoraemonNetFlowHttpModel *httpModel in httpModelArray) {
        NSTimeInterval netFlowEndTime = httpModel.endTime;
        if (netFlowEndTime >= start && netFlowEndTime <= now) {
            NSString *upFlow = httpModel.uploadFlow;
            NSString *downFlow = httpModel.downFlow;
            NSUInteger upFlowInt = [upFlow integerValue];
            NSUInteger downFlowInt = [downFlow integerValue];
            totalNetFlow += (upFlowInt + downFlowInt);
        }
    }
    
    useNetFlowForApp = totalNetFlow;
    
    // 0~10000Byte   对应 高度0~200
    [_oscillogramView addHeightValue:useNetFlowForApp*200./totalNetFlowForDevice andTipValue:[NSString stringWithFormat:@"%ziB",useNetFlowForApp]];
}

@end
