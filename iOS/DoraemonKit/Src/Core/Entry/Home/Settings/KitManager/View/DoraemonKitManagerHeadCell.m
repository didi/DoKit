//
//  DoraemonKitManagerHeadCell.m
//  DoraemonKit
//
//  Created by didi on 2020/4/28.
//

#import "DoraemonKitManagerHeadCell.h"
#import "DoraemonDefine.h"

@interface DoraemonKitManagerHeadCell()

@property (nonatomic, strong) UILabel *title;

@end

@implementation DoraemonKitManagerHeadCell

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

- (UILabel *)title {
    if (!_title) {
        _title = [UILabel new];
        _title.textColor = [UIColor doraemon_colorWithString:@"#333333"];
        _title.font = [UIFont boldSystemFontOfSize:kDoraemonSizeFrom750_Landscape(32)];
    }
    
    return _title;
}

- (void)renderUIWithTitle:(NSString *)title{
    _title.text = title;
    [self setNeedsLayout];
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
    [self.title sizeToFit];
    self.title.frame = CGRectMake(kDoraemonSizeFrom750_Landscape(32), self.doraemon_height/2-self.title.doraemon_height/2, self.title.doraemon_width, self.title.doraemon_height);
}


@end
