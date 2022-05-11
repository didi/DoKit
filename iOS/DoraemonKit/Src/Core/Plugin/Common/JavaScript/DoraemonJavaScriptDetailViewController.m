//
//  DoraemonJavaScriptDetailViewController.m
//  DoraemonKit
//
//  Created by carefree on 2022/5/11.
//

#import "DoraemonJavaScriptDetailViewController.h"
#import "DoraemonKit.h"
#import "DoraemonDefine.h"
#import "DoraemonToastUtil.h"
#import "DoraemonCacheManager.h"
#import "DoraemonJavaScriptManager.h"

@interface DoraemonJavaScriptDetailViewController ()

@property (nonatomic, weak) UITextView  *textView;

@end

@implementation DoraemonJavaScriptDetailViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.title = DoraemonLocalizedString(@"脚本执行");
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemPlay target:self action:@selector(runScript)];
    UIEdgeInsets edge = UIEdgeInsetsMake(10, 10, 0, 10);
    CGFloat width = self.view.bounds.size.width - edge.left - edge.right;
    CGFloat height = self.view.bounds.size.height - edge.top - edge.bottom;
    UILabel *titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(edge.left, edge.top + IPHONE_NAVIGATIONBAR_HEIGHT, width, 30)];
    titleLabel.text = DoraemonLocalizedString(@"JS代码");
    
    UITextView *textView = [[UITextView alloc] initWithFrame:CGRectMake(edge.left, CGRectGetMaxY(titleLabel.frame) + edge.top, width, height - 200)];
    textView.layer.borderWidth = 1 / UIScreen.mainScreen.scale;
    textView.layer.borderColor = [[UIColor lightGrayColor] CGColor];
    textView.layer.cornerRadius = 6;
    textView.font = [UIFont systemFontOfSize:16];
    textView.textContainerInset = UIEdgeInsetsMake(8, 3, 8, 3);
    
    [self.view addSubview:titleLabel];
    [self.view addSubview:textView];
    self.textView = textView;
    
    if (self.key.length > 0) {
        self.textView.text = [DoraemonCacheManager.sharedInstance jsHistoricalRecordForKey:self.key];
    }
}

#pragma mark - Private
- (void)runScript {
    NSString *value = self.textView.text;
    if (value.length == 0) {
        [DoraemonToastUtil showToastBlack:@"脚本不能为空" inView:self.view];
        return;
    }
    [DoraemonCacheManager.sharedInstance saveJsHistoricalRecordWithText:value forKey:self.key];
    [DoraemonManager.shareInstance hiddenHomeWindow];
    [DoraemonJavaScriptManager.shareInstance evalJavaScript:value];
}

@end
