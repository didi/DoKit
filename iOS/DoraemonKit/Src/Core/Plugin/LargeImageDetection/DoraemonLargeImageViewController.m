//
//  DoraemonLargeImageViewController.m
//  DoraemonKit
//
//  Created by 0xd-cc on 2019/5/15.
//

#import "DoraemonLargeImageViewController.h"
#import "DoraemonCellSwitch.h"
#import "DoraemonCellButton.h"
#import "DoraemonCacheManager.h"
#import "DoraemonNetworkInterceptor.h"
#import "DoraemonLargeImageDetectionManager.h"
#import "DoraemonLargeImageDetectionListViewController.h"
#import "DoraemonCellInput.h"
#import "DoraemonDefine.h"

@interface DoraemonLargeImageViewController() <DoraemonSwitchViewDelegate, DoraemonCellButtonDelegate>

@property (nonatomic, strong) DoraemonCellSwitch *switchView;
@property (nonatomic, strong) DoraemonCellButton *cellBtn;
@end

@implementation DoraemonLargeImageViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self initUI];
}

- (void)initUI {
    self.title = DoraemonLocalizedString(@"大图检测");
    
    _switchView = [[DoraemonCellSwitch alloc] initWithFrame:CGRectMake(0, self.bigTitleView.doraemon_bottom, self.view.doraemon_width, kDoraemonSizeFrom750(104))];
    [_switchView renderUIWithTitle:DoraemonLocalizedString(@"大图检测开关") switchOn:[[DoraemonLargeImageDetectionManager shareInstance] detecting]];
    [_switchView needTopLine];
    [_switchView needDownLine];
    _switchView.delegate = self;
    [self.view addSubview:_switchView];
    
    _cellBtn = [[DoraemonCellButton alloc] initWithFrame:CGRectMake(0, _switchView.doraemon_bottom, self.view.doraemon_width, kDoraemonSizeFrom750(104))];
    [_cellBtn renderUIWithTitle:DoraemonLocalizedString(@"查看检测记录")];
    _cellBtn.delegate = self;
    [_cellBtn needDownLine];
    [self.view addSubview:_cellBtn];
}

- (BOOL)needBigTitleView{
    return YES;
}

- (void)changeSwitchOn:(BOOL)on sender:(id)sender {
    [DoraemonLargeImageDetectionManager shareInstance].detecting = on;
}

- (void)cellBtnClick:(id)sender {
    DoraemonLargeImageDetectionListViewController *vc = [[DoraemonLargeImageDetectionListViewController alloc] initWithImages: [DoraemonLargeImageDetectionManager shareInstance].images];
    [self.navigationController pushViewController:vc animated:YES];
}
@end
