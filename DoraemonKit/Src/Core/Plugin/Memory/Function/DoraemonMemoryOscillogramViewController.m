//
//  DoraemonMemoryOscillogramViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/1/25.
//

#import "DoraemonMemoryOscillogramViewController.h"
#import "DoraemonOscillogramView.h"
#import "UIView+Positioning.h"
#import "DoraemonMemoryUtil.h"

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
    
    UILabel *titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, self.view.doraemon_width, 20)];
    titleLabel.backgroundColor = [UIColor lightGrayColor];
    titleLabel.text = @"  内存检测";
    titleLabel.font = [UIFont systemFontOfSize:12];
    titleLabel.textColor = [UIColor whiteColor];
    [self.view addSubview:titleLabel];
    
    _oscillogramView = [[DoraemonOscillogramView alloc] initWithFrame:CGRectMake(0, titleLabel.doraemon_bottom+10, self.view.doraemon_width, 200)];
    _oscillogramView.backgroundColor = [UIColor clearColor];
    [_oscillogramView setLowValue:@"0"];
    //[_oscillogramView setHightValue:[NSString stringWithFormat:@"%zi",[DoraemonMemoryUtil totalMemoryForDevice]/2]];
    [_oscillogramView setHightValue:[NSString stringWithFormat:@"%zi",1000]];
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

//每一秒钟采样一次cpu使用率
- (void)doSecondFunction{
    NSUInteger useMemoryForApp = [DoraemonMemoryUtil useMemoryForApp];
    //NSUInteger totalMemoryForDevice = [DoraemonMemoryUtil totalMemoryForDevice]/2;
    NSUInteger totalMemoryForDevice = 1000;
    
    // 0~totalMemoryForDevice   对应 高度0~200
    [_oscillogramView addHeightValue:useMemoryForApp*200./totalMemoryForDevice andTipValue:[NSString stringWithFormat:@"%zi",useMemoryForApp]];
}



@end
