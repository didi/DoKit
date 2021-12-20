//
//  DoraemonMLeaksFinderDetailViewController.m
//  DoraemonKit
//
//  Created by didi on 2019/10/7.
//

#import "DoraemonMLeaksFinderDetailViewController.h"
#import "DoraemonDefine.h"

@interface DoraemonMLeaksFinderDetailViewController ()

@property (nonatomic, strong) UILabel *contentLabel;

@end

@implementation DoraemonMLeaksFinderDetailViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"内存泄漏详情");
    
    _contentLabel = [[UILabel alloc] init];
    _contentLabel.textColor = [UIColor doraemon_black_2];
    _contentLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(16)];
    _contentLabel.numberOfLines = 0;
    _contentLabel.text = [_info description];
    
    CGSize fontSize = [_contentLabel sizeThatFits:CGSizeMake(self.view.doraemon_width-40, MAXFLOAT)];
    _contentLabel.frame = CGRectMake(20, IPHONE_NAVIGATIONBAR_HEIGHT, fontSize.width, fontSize.height);
    [self.view addSubview:_contentLabel];
}


@end
