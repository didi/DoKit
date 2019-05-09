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
#import "DoraemonFPSUtil.h"


@interface DoraemonFPSOscillogramViewController ()

@property (nonatomic, strong) DoraemonOscillogramView *oscillogramView;
@property (nonatomic, strong) DoraemonFPSUtil *fpsUtil;

@end

@implementation DoraemonFPSOscillogramViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    
    self.view.backgroundColor = [UIColor clearColor];
    [self setEdgesForExtendedLayout:UIRectEdgeNone];
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.backgroundColor = [UIColor clearColor];
    titleLabel.text = DoraemonLocalizedString(@"帧率检测");
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
    [_oscillogramView setHightValue:@"60"];
    [self.view addSubview:_oscillogramView];
    
}

- (void)closeBtnClick{
    [[DoraemonCacheManager sharedInstance] saveFpsSwitch:NO];
    [[DoraemonFPSOscillogramWindow shareInstance] hide];
}

- (void)startRecord{
    if (!_fpsUtil) {
        _fpsUtil = [[DoraemonFPSUtil alloc] init];
        __weak typeof(self) weakSelf = self;
        [_fpsUtil addFPSBlock:^(NSInteger fps) {
            // 0~60   对应 高度0~_oscillogramView.doraemon_height
            [weakSelf.oscillogramView addHeightValue:fps*_oscillogramView.doraemon_height/60. andTipValue:[NSString stringWithFormat:@"%zi",fps]];
        }];
    }
    [_fpsUtil start];
}

- (void)endRecord{
    if (_fpsUtil) {
        [_fpsUtil end];
    }
    [_oscillogramView clear];
}

@end
