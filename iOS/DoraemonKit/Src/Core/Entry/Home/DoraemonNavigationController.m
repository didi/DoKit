//
//  DoraemonNavigationController.m
//  DoraemonKit
//
//  Created by Chunhui Sun on 2020/7/14.
//  Copyright © 2020 YunXIao. All rights reserved.
//

#import "DoraemonNavigationController.h"
#import "DoraemonManager.h"

@interface DoraemonNavigationController ()

@end

@implementation DoraemonNavigationController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (UIInterfaceOrientationMask)supportedInterfaceOrientations {
    return DoraemonManager.shareInstance.supportedInterfaceOrientations;
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
