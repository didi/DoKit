//
//  DoraemonNetFlowOscillogramViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/5/2.
//

#import "DoraemonNetFlowOscillogramViewController.h"
#import "DoraemonOscillogramView.h"
#import "DoraemonNetFlowDataSource.h"
#import "DoraemonDefine.h"
#import "DoraemonCacheManager.h"
#import "DoraemonNetFlowOscillogramWindow.h"

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

    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.backgroundColor = [UIColor clearColor];
    titleLabel.text = DoraemonLocalizedString(@"流量检测");
    titleLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750(20)];
    titleLabel.textColor = [UIColor whiteColor];
    [self.view addSubview:titleLabel];
    [titleLabel sizeToFit];
    titleLabel.frame = CGRectMake(kDoraemonSizeFrom750(20), IPHONE_TOPSENSOR_HEIGHT + kDoraemonSizeFrom750(10), titleLabel.doraemon_width, titleLabel.doraemon_height);
    
    UIButton *closeBtn = [[UIButton alloc] init];
    [closeBtn setImage:[UIImage doraemon_imageNamed:@"doraemon_close_white"] forState:UIControlStateNormal];
    closeBtn.frame = CGRectMake(self.view.doraemon_width-kDoraemonSizeFrom750(60), IPHONE_TOPSENSOR_HEIGHT, kDoraemonSizeFrom750(60), kDoraemonSizeFrom750(60));
    [closeBtn addTarget:self action:@selector(closeBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:closeBtn];
    
    _oscillogramView = [[DoraemonOscillogramView alloc] initWithFrame:CGRectMake(0, titleLabel.doraemon_bottom+kDoraemonSizeFrom750(24), self.view.doraemon_width, kDoraemonSizeFrom750(400))];
    _oscillogramView.backgroundColor = [UIColor clearColor];
    [_oscillogramView setLowValue:@"0"];
    [_oscillogramView setHightValue:[NSString stringWithFormat:@"%zi",[self highestNetFlow]]];
    [self.view addSubview:_oscillogramView];
}

- (void)closeBtnClick{
    [[DoraemonCacheManager sharedInstance] saveNetFlowSwitch:NO];
    [[DoraemonNetFlowOscillogramWindow shareInstance] hide];
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
    NSUInteger totalNetFlowForDevice = [self highestNetFlow];
    
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
    
    // 0~highestNetFlow   对应 高度0~200
    [_oscillogramView addHeightValue:useNetFlowForApp*_oscillogramView.doraemon_height/totalNetFlowForDevice andTipValue:[NSString stringWithFormat:@"%ziB",useNetFlowForApp]];
}

- (NSUInteger)highestNetFlow {
    return 1000;//10000Byte
}

@end
