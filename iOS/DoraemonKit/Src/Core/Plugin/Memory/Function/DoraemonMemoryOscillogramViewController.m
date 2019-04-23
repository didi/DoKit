//
//  DoraemonMemoryOscillogramViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/1/25.
//

#import "DoraemonMemoryOscillogramViewController.h"
#import "DoraemonOscillogramView.h"
#import "UIView+Doraemon.h"
#import "DoraemonMemoryUtil.h"
#import "DoraemonDefine.h"
#import "DoraemonCacheManager.h"
#import "DoraemonMemoryOscillogramWindow.h"

@interface DoraemonMemoryOscillogramViewController ()

@property (nonatomic, strong) DoraemonOscillogramView *oscillogramView;

//每秒运行一次
@property (nonatomic, strong) NSTimer *secondTimer;

@end

@implementation DoraemonMemoryOscillogramViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor clearColor];
    [self setEdgesForExtendedLayout:UIRectEdgeNone];
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.backgroundColor = [UIColor clearColor];
    titleLabel.text = DoraemonLocalizedString(@"内存检测");
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
    [_oscillogramView setHightValue:[NSString stringWithFormat:@"%zi",[self deviceMemory]]];
    [self.view addSubview:_oscillogramView];
    
}

- (void)closeBtnClick{
    [[DoraemonCacheManager sharedInstance] saveMemorySwitch:NO];
    [[DoraemonMemoryOscillogramWindow shareInstance] hide];
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

//每一秒钟采样一次内存使用率
- (void)doSecondFunction{
    NSUInteger useMemoryForApp = [DoraemonMemoryUtil useMemoryForApp];
    NSUInteger totalMemoryForDevice = [self deviceMemory];
    
    // 0~totalMemoryForDevice   对应 高度0~_oscillogramView.doraemon_height
    [_oscillogramView addHeightValue:useMemoryForApp*_oscillogramView.doraemon_height/totalMemoryForDevice andTipValue:[NSString stringWithFormat:@"%zi",useMemoryForApp]];
}

- (NSUInteger)deviceMemory {
    return 1000;
}

- (void)addRecortArray:(NSArray *)recordArray {
    [_oscillogramView addRecortArray:recordArray];
}

@end
