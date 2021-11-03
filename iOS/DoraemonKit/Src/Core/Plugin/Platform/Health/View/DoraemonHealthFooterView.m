//
//  DoraemonHealthFooterView.m
//  DoraemonKit
//
//  Created by didi on 2020/1/1.
//

#import "DoraemonHealthFooterView.h"
#import "DoraemonDefine.h"

@interface DoraemonHealthFooterView ()

@property (nonatomic, strong) UILabel *footerTitle;
@property (nonatomic, strong) UIImageView *footerImg;
@property (nonatomic, strong) NSDictionary *titleDictionary;

@end

@implementation DoraemonHealthFooterView

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        
        _footerTitle = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, self.doraemon_width, kDoraemonSizeFrom750_Landscape(36))];
        _footerTitle.textAlignment = NSTextAlignmentCenter;
        _footerTitle.textColor = [UIColor doraemon_colorWithString:@"#27BCB7"];
        [_footerTitle setFont:[UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(24)]];
        CGFloat size = kDoraemonSizeFrom750_Landscape(56);
        _footerImg = [[UIImageView alloc] initWithFrame:CGRectMake((self.doraemon_width - size)/2, size, size, size)];
        _footerImg.userInteractionEnabled = YES;
        
        [self addSubview:_footerTitle];
        [self addSubview:_footerImg];
        
        UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tap)];
        [_footerImg addGestureRecognizer:tap];
    }
    return self;
}

- (void)renderUIWithTitleImg:(NSString *)title img:(NSString *)imgName{
    [_footerTitle setText:title];
    [_footerImg setImage:[UIImage doraemon_xcassetImageNamed:imgName]];
}

- (void)renderUIWithTitleImg:(BOOL)top{
    NSString *title;
    NSString *icon;
    _top = top;
    if(top){
        title = self.titleDictionary[@"top"];
        icon = self.titleDictionary[@"top_icon"];
    }else{
        title = self.titleDictionary[@"bottom"];
        icon = self.titleDictionary[@"bottom_icon"];
    }
    [_footerTitle setText:DoraemonLocalizedString(title)];
    [_footerImg setImage:[UIImage doraemon_xcassetImageNamed:icon]];
}

- (NSDictionary *)titleDictionary{
    if(!_titleDictionary){
        _titleDictionary =  @{
            @"top":@"向下滑动查看功能使用说明",
            @"bottom":@"回到顶部",
            @"top_icon":@"doraemon_health_slide",
            @"bottom_icon":@"doraemon_health_slideTop",
        };
    }
    return _titleDictionary;
}

- (void)tap{
    if (_delegate && [_delegate respondsToSelector:@selector(footerBtnClick:)]) {
        [_delegate footerBtnClick:self];
    }
}


@end
