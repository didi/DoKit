//
//  DoraemonKitManagerCell.m
//  AFNetworking
//
//  Created by didi on 2020/4/28.
//

#import "DoraemonKitManagerCell.h"
#import "DoraemonDefine.h"

@interface DoraemonKitManagerCell()

@property (nonatomic, strong) UIView *centerView;
@property (nonatomic, strong) UIImageView *icon;
@property (nonatomic, strong) UILabel *name;
@property (nonatomic, strong) UIImageView *select;
@property (nonatomic, strong) UIView *maskView;

@end

@implementation DoraemonKitManagerCell

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
        self.layer.borderWidth = kDoraemonSizeFrom750_Landscape(1);
        self.layer.borderColor = [UIColor doraemon_colorWithHexString:@"#EEEEEE"].CGColor;
        
        [self addSubview:self.centerView];
        [self addSubview:self.maskView];
        [self addSubview:self.select];
        
        [self.centerView addSubview:self.icon];
        [self.centerView addSubview:self.name];
        
        CGFloat centerViewH = self.name.doraemon_bottom;
        self.centerView.frame = CGRectMake(self.centerView.doraemon_left, (self.doraemon_height-centerViewH)/2, self.centerView.doraemon_width, centerViewH);
    }
    return self;
}

- (UIView *)centerView{
    if (!_centerView) {
        _centerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.doraemon_width, 0)];
    }
    return _centerView;
}

- (UIImageView *)icon {
    if (!_icon) {
        CGFloat size = kDoraemonSizeFrom750_Landscape(60);
        _icon = [[UIImageView alloc] initWithFrame:CGRectMake((self.centerView.doraemon_width - size) / 2.0, 0, size, size)];
    }
    
    return _icon;
}

- (UILabel *)name {
    if (!_name) {
        CGFloat height = kDoraemonSizeFrom750_Landscape(33);
        _name = [[UILabel alloc] initWithFrame:CGRectMake(0, self.icon.doraemon_bottom+kDoraemonSizeFrom750_Landscape(16), self.centerView.doraemon_width, height)];
        _name.textAlignment = NSTextAlignmentCenter;
        _name.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(24)];
        _name.adjustsFontSizeToFitWidth = YES;
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
        if (@available(iOS 13.0, *)) {
            _name.textColor = [UIColor labelColor];
        }
#endif
    }
    
    return _name;
}

- (UIImageView *)select{
    if (!_select) {
        CGFloat size = kDoraemonSizeFrom750_Landscape(28);
        _select = [[UIImageView alloc] initWithFrame:CGRectMake(self.doraemon_width-kDoraemonSizeFrom750_Landscape(12)-size, kDoraemonSizeFrom750_Landscape(12), size, size)];
    }
    return _select;
}

- (UIView *)maskView{
    if (!_maskView) {
        _maskView = [[UIView alloc] initWithFrame:self.bounds];
        _maskView.backgroundColor = [UIColor whiteColor];
        _maskView.alpha = 0.5;
    }
    return _maskView;
}


- (void)update:(NSString *)image name:(NSString *)name select:(BOOL)select editStatus:(BOOL)editStatus{
    self.icon.image = [UIImage doraemon_imageNamed:image];
    self.name.text = name;
    if (editStatus) {
        self.select.hidden = NO;
        if (select) {
            self.select.image = [UIImage doraemon_imageNamed:@"doraemon_check_circle_fill"];
            self.maskView.hidden = YES;
        }else{
            self.select.image = [UIImage doraemon_imageNamed:@"doraemon_check_circle"];
            self.maskView.hidden = NO;
        }
    }else{
        self.select.hidden = YES;
        self.maskView.hidden = YES;
    }
}


@end
