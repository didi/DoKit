//
//  DoraemonH5ViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/5/4.
//

#import "DoraemonH5ViewController.h"
#import <UIView+Positioning/UIView+Positioning.h>
#import "DoraemonToastUtil.h"
#import "DoraemonDefine.h"

@interface DoraemonH5ViewController ()

@property (nonatomic, strong) UILabel *tipLabel;
@property (nonatomic, strong) UITextView *h5UrlTextView;
@property (nonatomic, strong) UIButton *jumpBtn;

@end

@implementation DoraemonH5ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"H5任意门";
    
    _tipLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 10, self.view.width, 16)];
    _tipLabel.font = [UIFont systemFontOfSize:16];
    _tipLabel.textColor = [UIColor orangeColor];
    _tipLabel.textAlignment = NSTextAlignmentCenter;
    _tipLabel.text = @"请在以下输入框输入网址，让走点击跳转按钮即可😘";
    [self.view addSubview:_tipLabel];
    
    _h5UrlTextView = [[UITextView alloc] initWithFrame:CGRectMake(0, _tipLabel.bottom+10, self.view.width, 160)];
    _h5UrlTextView.layer.borderWidth = 1.;
    _h5UrlTextView.layer.borderColor = [UIColor lightGrayColor].CGColor;
    [self.view addSubview:_h5UrlTextView];
    
    _jumpBtn = [[UIButton alloc] initWithFrame:CGRectMake(0, _h5UrlTextView.bottom+10, self.view.width, 60)];
    _jumpBtn.backgroundColor = [UIColor orangeColor];
    [_jumpBtn setTitle:@"点击跳转" forState:UIControlStateNormal];
    [_jumpBtn addTarget:self action:@selector(jump) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:_jumpBtn];
}

- (void)jump{
    if (_h5UrlTextView.text.length==0) {
        [DoraemonToastUtil showToast:@"h5链接不能为空"];
        return;
    }
    [self leftNavBackClick:nil];
    [[NSNotificationCenter defaultCenter] postNotificationName:DoraemonH5DoorPluginNotification object:nil userInfo:@{@"h5Url":_h5UrlTextView.text}];
}


@end
