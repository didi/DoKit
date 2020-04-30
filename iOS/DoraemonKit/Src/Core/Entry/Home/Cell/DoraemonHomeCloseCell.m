//
//  DoraemonHomeCloseCell.m
//  DoraemonKit
//
//  Created by dengyouhua on 2019/9/4.
//

#import "DoraemonHomeCloseCell.h"
#import "DoraemonManager.h"
#import "DoraemonHomeWindow.h"
#import "DoraemonDefine.h"
#import "UIViewController+Doraemon.h"

@interface DoraemonHomeCloseCell ()

@property (nonatomic, strong) UIButton *closeButton;

@end

@implementation DoraemonHomeCloseCell

- (void)closeButtonHandle{
    UIAlertController * alertController = [UIAlertController alertControllerWithTitle:DoraemonLocalizedString(@"提示") message:DoraemonLocalizedString(@"Doraemon关闭之后需要重启App才能重新打开") preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:DoraemonLocalizedString(@"取消") style:UIAlertActionStyleCancel handler:nil];
    UIAlertAction *okAction = [UIAlertAction actionWithTitle:DoraemonLocalizedString(@"确定") style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        [[DoraemonManager shareInstance] hiddenDoraemon];
    }];
    [alertController addAction:cancelAction];
    [alertController addAction:okAction];
    [[UIViewController rootViewControllerForKeyWindow] presentViewController:alertController animated:YES completion:nil];

    [[DoraemonHomeWindow shareInstance] hide];
}

- (UIButton *)closeButton {
    if (!_closeButton) {
        _closeButton = [UIButton buttonWithType:UIButtonTypeCustom];
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
        if (@available(iOS 13.0, *)) {
            UIColor *dyColor = [UIColor colorWithDynamicProvider:^UIColor * _Nonnull(UITraitCollection * _Nonnull traitCollection) {
                if (traitCollection.userInterfaceStyle == UIUserInterfaceStyleLight) {
                    return [UIColor whiteColor];
                } else {
                    return [UIColor doraemon_colorWithString:@"#C1C3BF"];
                }
            }];
            _closeButton.backgroundColor = dyColor;
        } else {
#endif
            _closeButton.backgroundColor = [UIColor whiteColor];
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
        }
#endif
        _closeButton.layer.cornerRadius = 5.0;
        _closeButton.layer.masksToBounds = YES;
        [_closeButton setTitle:DoraemonLocalizedString(@"关闭DoraemonKit") forState:UIControlStateNormal];
        [_closeButton setTitleColor:[UIColor doraemon_colorWithString:@"#CC3A4B"] forState:UIControlStateNormal];
        [_closeButton addTarget:self action:@selector(closeButtonHandle) forControlEvents:UIControlEventTouchUpInside];
    }
    
    return _closeButton;
}

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        [self addSubview:self.closeButton];
    }
    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
    CGFloat x = 5;

    self.closeButton.frame = CGRectMake(x, x, self.doraemon_width - x * 2, 50);
}

@end
