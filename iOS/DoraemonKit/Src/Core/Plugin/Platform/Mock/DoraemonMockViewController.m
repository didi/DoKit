//
//  DoraemonMockViewController.m
//  AFNetworking
//
//  Created by didi on 2019/10/23.
//

#import "DoraemonMockViewController.h"
#import "DoraemonDefine.h"

#import "DoraemonMockUploadViewController.h"
#import "DoraemonMockAPIViewController.h"

@interface DoraemonMockViewController()

@end

@implementation DoraemonMockViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
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
    
    
    
    UIViewController *vc1 = [[DoraemonMockAPIViewController alloc] init];
    UINavigationController *nav1 = [[UINavigationController alloc] initWithRootViewController:vc1];
    nav1.tabBarItem = [[UITabBarItem alloc] initWithTitle:DoraemonLocalizedString(@"Mock数据") image:[[[UIImage doraemon_imageNamed:@"doraemon_mock_data"] doraemon_scaledToSize:CGSizeMake(30,30)] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal] selectedImage:[[[UIImage doraemon_imageNamed:@"doraemon_mock_data_selected"] doraemon_scaledToSize:CGSizeMake(30,30)] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal]];
    [nav1.tabBarItem setTitleTextAttributes:@{NSForegroundColorAttributeName:[UIColor doraemon_colorWithHex:0x333333],NSFontAttributeName:[UIFont systemFontOfSize:10]} forState:UIControlStateNormal];
    [nav1.tabBarItem setTitleTextAttributes:@{NSForegroundColorAttributeName:[UIColor doraemon_colorWithHex:0x337CC4],NSFontAttributeName:[UIFont systemFontOfSize:10]} forState:UIControlStateSelected];
    
    
    UIViewController *vc2 = [[DoraemonMockUploadViewController alloc] init];
    UINavigationController *nav2 = [[UINavigationController alloc] initWithRootViewController:vc2];
    nav2.tabBarItem = [[UITabBarItem alloc] initWithTitle:DoraemonLocalizedString(@"上传模版") image:[[[UIImage doraemon_imageNamed:@"doraemon_mock_up"] doraemon_scaledToSize:CGSizeMake(30,30)] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal] selectedImage:[[[UIImage doraemon_imageNamed:@"doraemon_mock_up_selected"] doraemon_scaledToSize:CGSizeMake(30,30)] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal]];
    [nav2.tabBarItem setTitleTextAttributes:@{NSForegroundColorAttributeName:[UIColor doraemon_colorWithHex:0x333333],NSFontAttributeName:[UIFont systemFontOfSize:10]} forState:UIControlStateNormal];
    [nav2.tabBarItem setTitleTextAttributes:@{NSForegroundColorAttributeName:[UIColor doraemon_colorWithHex:0x337CC4],NSFontAttributeName:[UIFont systemFontOfSize:10]} forState:UIControlStateSelected];
    
    tabBar.viewControllers = @[nav1,nav2];
    
    tabBar.modalPresentationStyle = UIModalPresentationFullScreen;
    
    [self.navigationController presentViewController:tabBar animated:NO completion:nil];
    
}

@end
