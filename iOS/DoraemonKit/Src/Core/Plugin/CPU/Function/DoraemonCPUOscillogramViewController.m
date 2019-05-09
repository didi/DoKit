//
//  DoraemonCPUOscillogramViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/1/12.
//

#import "DoraemonCPUOscillogramViewController.h"
#import "DoraemonOscillogramView.h"
#import "DoraemonCPUUtil.h"
#import "DoraemonDefine.h"
#import "DoraemonCacheManager.h"
#import "DoraemonCPUOscillogramWindow.h"

@interface DoraemonCPUOscillogramViewController ()

@property (nonatomic, strong) DoraemonOscillogramView *oscillogramView;

//每秒运行一次
@property (nonatomic, strong) NSTimer *secondTimer;


@end

@implementation DoraemonCPUOscillogramViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor clearColor];
    [self setEdgesForExtendedLayout:UIRectEdgeNone];
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.backgroundColor = [UIColor clearColor];
    titleLabel.text = DoraemonLocalizedString(@"CPU检测");
    titleLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(20)];
    titleLabel.textColor = [UIColor whiteColor];
    [self.view addSubview:titleLabel];
    [titleLabel sizeToFit];
    titleLabel.frame = CGRectMake(kDoraemonSizeFrom750_Landscape(20), IPHONE_TOPSENSOR_HEIGHT + kDoraemonSizeFrom750_Landscape(10), titleLabel.doraemon_width, titleLabel.doraemon_height);
    
    UIButton *closeBtn = [[UIButton alloc] init];
    [closeBtn setImage:[UIImage doraemon_imageNamed:@"doraemon_close_white"] forState:UIControlStateNormal];
    closeBtn.frame = CGRectMake((kInterfaceOrientationPortrait ? DoraemonScreenWidth : DoraemonScreenHeight)-kDoraemonSizeFrom750_Landscape(60), IPHONE_TOPSENSOR_HEIGHT, kDoraemonSizeFrom750_Landscape(60), kDoraemonSizeFrom750_Landscape(60));
    [closeBtn addTarget:self action:@selector(closeBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:closeBtn];
    
    _oscillogramView = [[DoraemonOscillogramView alloc] initWithFrame:CGRectMake(0, titleLabel.doraemon_bottom+kDoraemonSizeFrom750_Landscape(24), (kInterfaceOrientationPortrait ? DoraemonScreenWidth : DoraemonScreenHeight), kDoraemonSizeFrom750_Landscape(400))];
    _oscillogramView.backgroundColor = [UIColor clearColor];
    [_oscillogramView setLowValue:@"0"];
    [_oscillogramView setHightValue:@"100"];
    [self.view addSubview:_oscillogramView];
}

- (void)closeBtnClick{
    [[DoraemonCacheManager sharedInstance] saveCpuSwitch:NO];
    [[DoraemonCPUOscillogramWindow shareInstance] hide];
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

//每一秒钟采样一次cpu使用率
- (void)doSecondFunction{
    CGFloat cpuUsage = [DoraemonCPUUtil cpuUsageForApp];
    if (cpuUsage * 100 > 100) {
        cpuUsage = 100;
    }else{
        cpuUsage = cpuUsage * 100;
    }
    // 0~100   对应 高度0~_oscillogramView.doraemon_height
    NSTimeInterval time = [[NSDate date] timeIntervalSince1970];
    [_oscillogramView addHeightValue:cpuUsage*_oscillogramView.doraemon_height/100. andTipValue:[NSString stringWithFormat:@"%.f",cpuUsage]];
}

- (void)addRecortArray:(NSArray<DoraemonPerformanceInfoModel *> *)recordArray {
    [_oscillogramView addRecortArray:recordArray];
}

@end
