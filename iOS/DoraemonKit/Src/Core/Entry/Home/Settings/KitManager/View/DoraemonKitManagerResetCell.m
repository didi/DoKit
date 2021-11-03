//
//  DoraemonKitManagerResetCell.m
//  DoraemonKit
//
//  Created by didi on 2020/4/28.
//

#import "DoraemonKitManagerResetCell.h"
#import "DoraemonDefine.h"

@interface DoraemonKitManagerResetCell()

@property (nonatomic, strong) UIButton *resetButton;

@end

@implementation DoraemonKitManagerResetCell

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        [self addSubview:self.resetButton];
    }
    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
    CGFloat x = 5;

    self.resetButton.frame = CGRectMake(x, x, self.doraemon_width - x * 2, 50);
}

- (UIButton *)resetButton {
    if (!_resetButton) {
        _resetButton = [UIButton buttonWithType:UIButtonTypeCustom];
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
        if (@available(iOS 13.0, *)) {
            UIColor *dyColor = [UIColor colorWithDynamicProvider:^UIColor * _Nonnull(UITraitCollection * _Nonnull traitCollection) {
                if (traitCollection.userInterfaceStyle == UIUserInterfaceStyleLight) {
                    return [UIColor whiteColor];
                } else {
                    return [UIColor doraemon_colorWithString:@"#C1C3BF"];
                }
            }];
            _resetButton.backgroundColor = dyColor;
        } else {
#endif
            _resetButton.backgroundColor = [UIColor whiteColor];
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
        }
#endif
        _resetButton.layer.cornerRadius = 5.0;
        _resetButton.layer.masksToBounds = YES;
        [_resetButton setTitle:DoraemonLocalizedString(@"还原到初始状态") forState:UIControlStateNormal];
        [_resetButton setTitleColor:[UIColor doraemon_colorWithString:@"#CC3A4B"] forState:UIControlStateNormal];
        [_resetButton addTarget:self action:@selector(resetButtonHandle) forControlEvents:UIControlEventTouchUpInside];
    }
    
    return _resetButton;
}

- (void)resetButtonHandle{
}

@end
