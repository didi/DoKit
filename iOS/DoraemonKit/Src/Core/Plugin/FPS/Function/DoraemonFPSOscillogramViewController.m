//
//  DoraemonFPSOscillogramViewController.m
//  CocoaLumberjack
//
//  Created by yixiang on 2018/1/12.
//

#import "DoraemonFPSOscillogramViewController.h"
#import "DoraemonOscillogramView.h"
#import "DoraemonDefine.h"
#import "DoraemonCacheManager.h"
#import "DoraemonFPSOscillogramWindow.h"


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
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.backgroundColor = [UIColor clearColor];
    titleLabel.text = DoraemonLocalizedString(@"帧率检测");
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
    [_oscillogramView setHightValue:@"60"];
    [self.view addSubview:_oscillogramView];
    
}

- (void)closeBtnClick{
    [[DoraemonCacheManager sharedInstance] saveFpsSwitch:NO];
    [[DoraemonFPSOscillogramWindow shareInstance] hide];
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
    // 0~60   对应 高度0~_oscillogramView.doraemon_height
    [_oscillogramView addHeightValue:fps*_oscillogramView.doraemon_height/60. andTipValue:[NSString stringWithFormat:@"%zi",intFps]];
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

- (void)addRecortArray:(NSArray *)recordArray {
    [_oscillogramView addRecortArray:recordArray];
}

@end
