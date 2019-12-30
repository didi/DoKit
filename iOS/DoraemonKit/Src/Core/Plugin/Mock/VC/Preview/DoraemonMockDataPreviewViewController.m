//
//  DoraemonMockDataPreviewViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by didi on 2019/11/17.
//

#import "DoraemonMockDataPreviewViewController.h"
#import "DoraemonDefine.h"

@interface DoraemonMockDataPreviewViewController ()

@property (nonatomic, strong) UITextView *textView;

@end

@implementation DoraemonMockDataPreviewViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"数据预览";
    CGFloat tabBarHeight = self.tabBarController.tabBar.doraemon_height;
    _textView = [[UITextView alloc] initWithFrame:CGRectMake(0, IPHONE_NAVIGATIONBAR_HEIGHT, self.view.doraemon_width, self.view.doraemon_height-IPHONE_NAVIGATIONBAR_HEIGHT-tabBarHeight)];
    _textView.text = _result;
    [self.view addSubview:_textView];
}



@end
