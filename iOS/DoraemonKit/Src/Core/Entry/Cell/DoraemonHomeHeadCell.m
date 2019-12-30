//
//  DoraemonHomeHeadCell.m
//  DoraemonKit
//
//  Created by dengyouhua on 2019/9/4.
//

#import "DoraemonHomeHeadCell.h"
#import "DoraemonDefine.h"

@interface DoraemonHomeHeadCell()

@property (nonatomic, strong) UILabel *title;
@property (nonatomic, strong) UILabel *subTitleLabel;

@end

@implementation DoraemonHomeHeadCell

- (UILabel *)title {
    if (!_title) {
        _title = [UILabel new];
    }
    
    return _title;
}

- (UILabel *)subTitleLabel{
    if (!_subTitleLabel) {
        _subTitleLabel = [UILabel new];
    }
    
    return _subTitleLabel;
}

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
        if (@available(iOS 13.0, *)) {
            self.backgroundColor = [UIColor systemBackgroundColor];
        } else {
#endif
            self.backgroundColor = [UIColor whiteColor];
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
        }
#endif
        [self addSubview:self.title];
    }
    return self;
}

- (void)renderUIWithTitle:(NSString *)title{
    _title.text = title;
    if (_subTitleLabel) {
        [_subTitleLabel removeFromSuperview];
        _subTitleLabel = nil;
    }
    if (title && [title isEqualToString:DoraemonLocalizedString(@"平台工具")]) {
        [self renderUIWithSubTitle:@"(www.dokit.cn)"];
    }
    [self setNeedsLayout];
}

- (void)renderUIWithSubTitle:(NSString *)subTitle{
    if (subTitle && subTitle.length>0) {
        [self addSubview:self.subTitleLabel];
        self.subTitleLabel.textColor = [UIColor redColor];
        self.subTitleLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(24)];
        self.subTitleLabel.text = subTitle;
    }
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
    [self.title sizeToFit];
    self.title.frame = CGRectMake(kDoraemonSizeFrom750_Landscape(32), self.doraemon_height/2-self.title.doraemon_height/2, self.title.doraemon_width, self.title.doraemon_height);
    if (self.subTitleLabel) {
        [self.subTitleLabel sizeToFit];
        self.subTitleLabel.frame = CGRectMake(self.title.doraemon_right+kDoraemonSizeFrom750_Landscape(2), self.doraemon_height/2-self.subTitleLabel.doraemon_height/2, self.subTitleLabel.doraemon_width, self.subTitleLabel.doraemon_height);
    }
}

@end
