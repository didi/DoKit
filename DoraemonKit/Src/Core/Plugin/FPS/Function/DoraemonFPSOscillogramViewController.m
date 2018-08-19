//
//  DoraemonFPSOscillogramViewController.m
//  CocoaLumberjack
//
//  Created by yixiang on 2018/1/12.
//

#import "DoraemonFPSOscillogramViewController.h"
#import "DoraemonOscillogramView.h"
#import "UIView+Positioning.h"

@interface DoraemonFPSOscillogramViewController ()

@property (nonatomic, strong) DoraemonOscillogramView *oscillogramView;
@property (nonatomic, strong) CADisplayLink *link;
@property (nonatomic, assign) NSUInteger count;
@property (nonatomic, assign) NSTimeInterval lastTime;

@end

@implementation DoraemonFPSOscillogramViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.view.backgroundColor = [UIColor clearColor];
    [self setEdgesForExtendedLayout:UIRectEdgeNone];
    
    UILabel *titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, self.view.width, 20)];
    titleLabel.backgroundColor = [UIColor lightGrayColor];
    titleLabel.text = @"  帧率检测";
    titleLabel.font = [UIFont systemFontOfSize:12];
    titleLabel.textColor = [UIColor whiteColor];
    [self.view addSubview:titleLabel];
    
    _oscillogramView = [[DoraemonOscillogramView alloc] initWithFrame:CGRectMake(0, titleLabel.bottom+10, self.view.width, 200)];
    _oscillogramView.backgroundColor = [UIColor clearColor];
    [_oscillogramView setLowValue:@"0"];
    [_oscillogramView setHightValue:@"60"];
    [self.view addSubview:_oscillogramView];
    
}

- (void)trigger:(CADisplayLink *)link{
    if (_lastTime == 0) {
        _lastTime = link.timestamp;
        return;
    }
    
    _count++;
    NSTimeInterval delta = link.timestamp - _lastTime;
    if (delta < 1) return;
    _lastTime = link.timestamp;
    CGFloat fps = _count / delta;
    _count = 0;
    
    NSInteger intFps = (NSInteger)(fps+0.5);
    // 0~60   对应 高度0~200
    [_oscillogramView addHeightValue:fps*200./60. andTipValue:[NSString stringWithFormat:@"%zi",intFps]];
    
}

- (void)startRecord{
    if (_link) {
        _link.paused = NO;
    }else{
        _link = [CADisplayLink displayLinkWithTarget:self selector:@selector(trigger:)];
        [_link addToRunLoop:[NSRunLoop mainRunLoop] forMode:NSRunLoopCommonModes];
    }
}

- (void)endRecord{
    if (_link) {
        _link.paused = YES;
        [_link invalidate];
        _link = nil;
        [_oscillogramView clear];
        _lastTime = 0;
        _count = 0;
    }
}

@end
