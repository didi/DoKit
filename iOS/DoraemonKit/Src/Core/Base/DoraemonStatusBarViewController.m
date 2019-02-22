//
//  DoraemonStatusBarViewController.m
//  AFNetworking
//
//  Created by 张伟 on 2019/2/22.
//

#import "DoraemonStatusBarViewController.h"

@interface DoraemonStatusBarViewController ()

@end

@implementation DoraemonStatusBarViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}
- (BOOL)prefersStatusBarHidden
{
    return NO;
}
- (UIStatusBarStyle)preferredStatusBarStyle
{
    return UIStatusBarStyleDefault;
}
/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
