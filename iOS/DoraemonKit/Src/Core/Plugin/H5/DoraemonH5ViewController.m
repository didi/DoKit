//
//  DoraemonH5ViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/5/4.
//

#import "DoraemonH5ViewController.h"
#import "UIView+Doraemon.h"
#import "DoraemonToastUtil.h"
#import "DoraemonDefine.h"
#import "Doraemoni18NUtil.h"
#import "UITextView+Placeholder.h"
#import "DoraemonDefaultWebViewController.h"
#import "DoraemonManager.h"

@interface DoraemonH5ViewController ()

@property (nonatomic, strong) UITextView *h5UrlTextView;
@property (nonatomic, strong) UIView *lineView;
@property (nonatomic, strong) UIButton *jumpBtn;

@end

@implementation DoraemonH5ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"H5任意门");
    
    _h5UrlTextView = [[UITextView alloc] initWithFrame:CGRectMake(0, self.bigTitleView.doraemon_bottom, self.view.doraemon_width, kDoraemonSizeFrom750(358))];
    _h5UrlTextView.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750(32)];
    _h5UrlTextView.placeholder = @"请输入网址";
    [self.view addSubview:_h5UrlTextView];
    _h5UrlTextView.keyboardType = UIKeyboardTypeURL;
    _h5UrlTextView.autocorrectionType = UITextAutocorrectionTypeNo;
    _h5UrlTextView.keyboardAppearance = UIKeyboardAppearanceDark;
    _h5UrlTextView.autocapitalizationType = UITextAutocapitalizationTypeNone;
    _lineView = [[UIView alloc] initWithFrame:CGRectMake(0, _h5UrlTextView.doraemon_bottom, self.view.doraemon_width, kDoraemonSizeFrom750(1))];
    _lineView.backgroundColor = [UIColor doraemon_colorWithHex:0x000000 andAlpha:0.1];
    [self.view addSubview:_lineView];
    
    _jumpBtn = [[UIButton alloc] initWithFrame:CGRectMake(kDoraemonSizeFrom750(30), _lineView.doraemon_bottom+kDoraemonSizeFrom750(32), self.view.doraemon_width-2*kDoraemonSizeFrom750(30), kDoraemonSizeFrom750(100))];
    _jumpBtn.backgroundColor = [UIColor doraemon_colorWithHexString:@"#337CC4"];
    [_jumpBtn setTitle:DoraemonLocalizedString(@"点击跳转") forState:UIControlStateNormal];
    [_jumpBtn addTarget:self action:@selector(jump) forControlEvents:UIControlEventTouchUpInside];
    _jumpBtn.layer.cornerRadius = kDoraemonSizeFrom750(8);
    [self.view addSubview:_jumpBtn];
}

- (BOOL)needBigTitleView{
    return YES;
}

- (void)jump{
    if (_h5UrlTextView.text.length==0) {
        [DoraemonToastUtil showToast:DoraemonLocalizedString(@"h5链接不能为空")];
        return;
    }
    NSString *h5Url = _h5UrlTextView.text;
    if ([DoraemonManager shareInstance].h5DoorBlock) {
        [self leftNavBackClick:nil];
        [DoraemonManager shareInstance].h5DoorBlock(h5Url);
    }else{
        DoraemonDefaultWebViewController *vc = [[DoraemonDefaultWebViewController alloc] init];
        vc.h5Url = h5Url;
        [self.navigationController pushViewController:vc animated:YES];
    }
}


@end
