//
//  DoraemonNetFlowViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/4/9.
//

#import "DoraemonNetFlowViewController.h"
#import "DoraemonCacheManager.h"
#import "DoraemonNetFlowManager.h"
#import "DoraemonDefine.h"
#import "UIView+Doraemon.h"
#import "DoraemonNetFlowListViewController.h"
#import "DoraemonUtil.h"
#import "DoraemonNetFlowSummaryViewController.h"
#import "UIImage+Doraemon.h"
#import "UIColor+Doraemon.h"
#import "DoraemonNetFlowOscillogramWindow.h"
#import "Doraemoni18NUtil.h"
#import "DoraemonCellSwitch.h"
#import "DoraemonCellButton.h"
#import "DoraemonDefine.h"
#import "DoraemonNetFlowTestListViewController.h"


@interface DoraemonNetFlowViewController ()
@property (nonatomic, strong) UITabBarController *tabBar;

@property (nonatomic, strong) DoraemonCellSwitch *switchView;
@property (nonatomic, strong) DoraemonCellButton *cellBtn;

@end

@implementation DoraemonNetFlowViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self initUI];
}

- (void)initUI{
    self.title = DoraemonLocalizedString(@"流量检测");
    
    _switchView = [[DoraemonCellSwitch alloc] initWithFrame:CGRectMake(0, self.bigTitleView.doraemon_bottom, self.view.doraemon_width, kDoraemonSizeFrom750(104))];
    [_switchView renderUIWithTitle:DoraemonLocalizedString(@"流量检测开关") switchOn:[[DoraemonCacheManager sharedInstance] netFlowSwitch]];
    [_switchView needTopLine];
    [_switchView needDownLine];
    _switchView.delegate = self;
    [self.view addSubview:_switchView];
    
    _cellBtn = [[DoraemonCellButton alloc] initWithFrame:CGRectMake(0, _switchView.doraemon_bottom, self.view.doraemon_width, kDoraemonSizeFrom750(104))];
    [_cellBtn renderUIWithTitle:DoraemonLocalizedString(@"查看检测记录")];
    _cellBtn.delegate = self;
    [_cellBtn needDownLine];
    [self.view addSubview:_cellBtn];
    

    
    UIButton *showNetFlowDetailBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    showNetFlowDetailBtn.frame = CGRectMake(kDoraemonSizeFrom750(32), _cellBtn.doraemon_bottom+kDoraemonSizeFrom750(60), self.view.doraemon_width-2*kDoraemonSizeFrom750(32), kDoraemonSizeFrom750(100));
    [showNetFlowDetailBtn setTitle:DoraemonLocalizedString(@"显示流量检测详情") forState:UIControlStateNormal];
    showNetFlowDetailBtn.backgroundColor = [UIColor doraemon_colorWithHexString:@"#337CC4"];
    [showNetFlowDetailBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [showNetFlowDetailBtn addTarget:self action:@selector(showNetFlowDetail) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:showNetFlowDetailBtn];
}

- (BOOL)needBigTitleView{
    return YES;
}

#pragma mark -- DoraemonSwitchViewDelegate
- (void)changeSwitchOn:(BOOL)on sender:(id)sender{
    if (sender == _switchView.switchView) {
        [[DoraemonCacheManager sharedInstance] saveNetFlowSwitch:on];
        if(on){
            [[DoraemonNetFlowManager shareInstance] canInterceptNetFlow:YES];
            [self showOscillogramView];
        }else{
            [[DoraemonNetFlowManager shareInstance] canInterceptNetFlow:NO];
            [self hiddenOscillogramView];
        }
    }
}


#pragma mark -- DoraemonCellButtonDelegate
- (void)cellBtnClick:(id)sender{
    if (sender == _cellBtn) {
        DoraemonNetFlowTestListViewController *vc = [[DoraemonNetFlowTestListViewController alloc] init];
        [self.navigationController pushViewController:vc animated:YES];
    }
}

- (void)showOscillogramView{
    [[DoraemonNetFlowOscillogramWindow shareInstance] show];
}

- (void)hiddenOscillogramView{
    [[DoraemonNetFlowOscillogramWindow shareInstance] hide];
}


- (void)showNetFlowDetail{
    UITabBarController *tabBar = [[UITabBarController alloc] init];
    tabBar.tabBar.backgroundColor = [UIColor whiteColor];
    _tabBar = tabBar;
    
    UIViewController *vc1 = [[DoraemonNetFlowSummaryViewController alloc] init];
    UINavigationController *nav1 = [[UINavigationController alloc] initWithRootViewController:vc1];
    nav1.tabBarItem = [[UITabBarItem alloc] initWithTitle:DoraemonLocalizedString(@"流量检测概要") image:[[[UIImage doraemon_imageNamed:@"doraemon_netflow_summary_unselect"] doraemon_scaledToSize:CGSizeMake(30,30)] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal] selectedImage:[[[UIImage doraemon_imageNamed:@"doraemon_netflow_summary_select"] doraemon_scaledToSize:CGSizeMake(30,30)] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal] ];
    [nav1.tabBarItem setTitleTextAttributes:@{NSForegroundColorAttributeName:[UIColor doraemon_colorWithHex:0x333333],NSFontAttributeName:[UIFont systemFontOfSize:10]} forState:UIControlStateNormal];
    [nav1.tabBarItem setTitleTextAttributes:@{NSForegroundColorAttributeName:[UIColor doraemon_colorWithHex:0x337CC4],NSFontAttributeName:[UIFont systemFontOfSize:10]} forState:UIControlStateSelected];
    
    
    UIViewController *vc2 = [[DoraemonNetFlowListViewController alloc] init];
    UINavigationController *nav2 = [[UINavigationController alloc] initWithRootViewController:vc2];
    nav2.tabBarItem = [[UITabBarItem alloc] initWithTitle:DoraemonLocalizedString(@"流量检测列表") image:[[[UIImage doraemon_imageNamed:@"doraemon_netflow_list_unselect"] doraemon_scaledToSize:CGSizeMake(30,30)] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal] selectedImage:[[[UIImage doraemon_imageNamed:@"doraemon_netflow_list_select"] doraemon_scaledToSize:CGSizeMake(30,30)] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal]];
    [nav2.tabBarItem setTitleTextAttributes:@{NSForegroundColorAttributeName:[UIColor doraemon_colorWithHex:0x333333],NSFontAttributeName:[UIFont systemFontOfSize:10]} forState:UIControlStateNormal];
    [nav2.tabBarItem setTitleTextAttributes:@{NSForegroundColorAttributeName:[UIColor doraemon_colorWithHex:0x337CC4],NSFontAttributeName:[UIFont systemFontOfSize:10]} forState:UIControlStateSelected];
    
    tabBar.viewControllers = @[nav1,nav2];
    
    [self presentViewController:tabBar animated:YES completion:nil];
}

@end
