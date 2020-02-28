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
#import "DoraemonHomeWindow.h"
#import "DoraemonNetFlowSummaryViewController.h"
#import "UIImage+Doraemon.h"
#import "UIColor+Doraemon.h"
#import "DoraemonNetFlowOscillogramWindow.h"
#import "Doraemoni18NUtil.h"
#import "DoraemonCellSwitch.h"
#import "DoraemonDefine.h"


@interface DoraemonNetFlowViewController ()<DoraemonSwitchViewDelegate, DoraemonOscillogramWindowDelegate>
@property (nonatomic, strong) UITabBarController *tabBar;

@property (nonatomic, strong) DoraemonCellSwitch *switchView;

@end

@implementation DoraemonNetFlowViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self initUI];
    [[DoraemonNetFlowOscillogramWindow shareInstance] addDelegate:self];
}

- (void)initUI{
    self.title = DoraemonLocalizedString(@"流量检测");
    
    _switchView = [[DoraemonCellSwitch alloc] initWithFrame:CGRectMake(0, self.bigTitleView.doraemon_bottom, self.view.doraemon_width, kDoraemonSizeFrom750_Landscape(104))];
    [_switchView renderUIWithTitle:DoraemonLocalizedString(@"流量检测开关") switchOn:[[DoraemonCacheManager sharedInstance] netFlowSwitch]];
    [_switchView needTopLine];
    [_switchView needDownLine];
    _switchView.delegate = self;
    [self.view addSubview:_switchView];

    UIButton *showNetFlowDetailBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    showNetFlowDetailBtn.frame = CGRectMake(kDoraemonSizeFrom750_Landscape(32), _switchView.doraemon_bottom+kDoraemonSizeFrom750_Landscape(60), self.view.doraemon_width-2*kDoraemonSizeFrom750_Landscape(32), kDoraemonSizeFrom750_Landscape(100));
    [showNetFlowDetailBtn setTitle:DoraemonLocalizedString(@"显示流量检测详情") forState:UIControlStateNormal];
    showNetFlowDetailBtn.backgroundColor = [UIColor doraemon_colorWithHexString:@"#337CC4"];
    [showNetFlowDetailBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [showNetFlowDetailBtn addTarget:self action:@selector(showNetFlowDetail) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:showNetFlowDetailBtn];
}

- (BOOL)needBigTitleView{
    return YES;
}

- (void)traitCollectionDidChange:(UITraitCollection *)previousTraitCollection {
    [super traitCollectionDidChange:previousTraitCollection];
    // trait发生了改变
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
    if (@available(iOS 13.0, *)) {
        if ([self.traitCollection hasDifferentColorAppearanceComparedToTraitCollection:previousTraitCollection]) {
            if (UITraitCollection.currentTraitCollection.userInterfaceStyle == UIUserInterfaceStyleDark) {
                UIView *view = [[UIView alloc] initWithFrame:CGRectMake(0, -0.5, CGRectGetWidth(self.tabBar.tabBar.frame), 0.5)];
                view.backgroundColor = [UIColor doraemon_black_3];
                [self.tabBar.tabBar insertSubview:view atIndex:0];
            }
        }
    }
#endif
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

- (void)showOscillogramView{
    [[DoraemonNetFlowOscillogramWindow shareInstance] show];
}

- (void)hiddenOscillogramView{
    [[DoraemonNetFlowOscillogramWindow shareInstance] hide];
}


- (void)showNetFlowDetail {
    UITabBarController *tabBar = [[UITabBarController alloc] init];
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
    if (@available(iOS 13.0, *)) {
        tabBar.tabBar.backgroundColor = [UIColor systemBackgroundColor];
        if (UITraitCollection.currentTraitCollection.userInterfaceStyle == UIUserInterfaceStyleDark) {
            UIView *view = [[UIView alloc] initWithFrame:CGRectMake(0, -0.5, CGRectGetWidth(tabBar.tabBar.frame), 0.5)];
            view.backgroundColor = [UIColor doraemon_black_3];
            [tabBar.tabBar insertSubview:view atIndex:0];
        }
    } else {
#endif
        tabBar.tabBar.backgroundColor = [UIColor whiteColor];
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
    }
#endif
    _tabBar = tabBar;
    
    UIViewController *vc1 = [[DoraemonNetFlowSummaryViewController alloc] init];
    UINavigationController *nav1 = [[UINavigationController alloc] initWithRootViewController:vc1];
    nav1.tabBarItem = [[UITabBarItem alloc] initWithTitle:DoraemonLocalizedString(@"流量检测概要") image:[[[UIImage doraemon_imageNamed:@"doraemon_netflow_summary_unselect"] doraemon_scaledToSize:CGSizeMake(30,30)] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal] selectedImage:[[[UIImage doraemon_imageNamed:@"doraemon_netflow_summary_select"] doraemon_scaledToSize:CGSizeMake(30,30)] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal]];
    [nav1.tabBarItem setTitleTextAttributes:@{NSForegroundColorAttributeName:[UIColor doraemon_colorWithHex:0x333333],NSFontAttributeName:[UIFont systemFontOfSize:10]} forState:UIControlStateNormal];
    [nav1.tabBarItem setTitleTextAttributes:@{NSForegroundColorAttributeName:[UIColor doraemon_colorWithHex:0x337CC4],NSFontAttributeName:[UIFont systemFontOfSize:10]} forState:UIControlStateSelected];
    
    
    UIViewController *vc2 = [[DoraemonNetFlowListViewController alloc] init];
    UINavigationController *nav2 = [[UINavigationController alloc] initWithRootViewController:vc2];
    nav2.tabBarItem = [[UITabBarItem alloc] initWithTitle:DoraemonLocalizedString(@"流量检测列表") image:[[[UIImage doraemon_imageNamed:@"doraemon_netflow_list_unselect"] doraemon_scaledToSize:CGSizeMake(30,30)] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal] selectedImage:[[[UIImage doraemon_imageNamed:@"doraemon_netflow_list_select"] doraemon_scaledToSize:CGSizeMake(30,30)] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal]];
    [nav2.tabBarItem setTitleTextAttributes:@{NSForegroundColorAttributeName:[UIColor doraemon_colorWithHex:0x333333],NSFontAttributeName:[UIFont systemFontOfSize:10]} forState:UIControlStateNormal];
    [nav2.tabBarItem setTitleTextAttributes:@{NSForegroundColorAttributeName:[UIColor doraemon_colorWithHex:0x337CC4],NSFontAttributeName:[UIFont systemFontOfSize:10]} forState:UIControlStateSelected];
    
    tabBar.viewControllers = @[nav1,nav2];
    
    tabBar.modalPresentationStyle = UIModalPresentationFullScreen;
    [self presentViewController:tabBar animated:YES completion:nil];
}

#pragma mark -- DoraemonOscillogramWindowDelegate
- (void)doraemonOscillogramWindowClosed {
    [_switchView renderUIWithTitle:DoraemonLocalizedString(@"流量检测开关") switchOn:[[DoraemonCacheManager sharedInstance] netFlowSwitch]];
}

@end
