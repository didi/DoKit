//
//  DoraemonPageTimeDetailViewController.m
//  DoraemonKit
//
//  Created by Frank on 2020/5/28.
//

#import "DoraemonPageTimeDetailViewController.h"
#import "DoraemonDefine.h"

@interface DoraemonPageTimeDetailViewController ()
@property (nonatomic, strong) UITextView *contentLabel;

@end

@implementation DoraemonPageTimeDetailViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"页面耗时");
        
    _contentLabel = [[UITextView alloc] initWithFrame:self.view.bounds];
    _contentLabel.textColor = [UIColor doraemon_black_2];
    _contentLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(16)];
    _contentLabel.text = [NSString stringWithFormat:@"%@\ntitalTime:%f\n\
                          \n-------------loadView-------------\n%@\
                          \n-------------viewDidLoad-------------\n%@\
                          \n-------------viewWillAppear-------------\n%@\
                          \n-------------viewWillDidAppear-------------\n%@\
                          \n-------------viewDidLayoutSubviews-------------\n%@",
                          self.pageTimeDetail.clsName,
                          self.pageTimeDetail.loadViewTime
                          + self.pageTimeDetail.viewDidLoadTime
                          + self.pageTimeDetail.viewWillAppearTime
                          + self.pageTimeDetail.viewWillDidAppearTime
                          + self.pageTimeDetail.viewDidLayoutSubviewsTime,
                          self.pageTimeDetail.loadViewTimeDict?:@"",
                          self.pageTimeDetail.viewDidLoadTimeDict?:@"",
                          self.pageTimeDetail.viewWillAppearTimeDict?:@"",
                          self.pageTimeDetail.viewWillDidAppearTimeDict?:@"",
                          self.pageTimeDetail.viewDidLayoutSubviewsTimeDict?:@""
                          ];
    [self.view addSubview:_contentLabel];
}

@end
