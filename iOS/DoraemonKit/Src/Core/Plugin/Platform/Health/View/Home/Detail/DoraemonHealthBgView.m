//
//  DoraemonHealthBgView.m
//  DoraemonKit
//
//  Created by didi on 2019/12/31.
//

#import "DoraemonHealthBgView.h"
#import "DoraemonDefine.h"

@interface DoraemonHealthBgView()

@property (nonatomic, strong) UIImageView *bgImgView;

@end

@implementation DoraemonHealthBgView

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        CGFloat bg_x = kDoraemonSizeFrom750_Landscape(98);
        CGFloat bg_width = self.doraemon_width - bg_x * 2;
        _bgImgView = [[UIImageView alloc] initWithFrame:CGRectMake(bg_x,kDoraemonSizeFrom750_Landscape(89), bg_width, bg_width*16/9)];
        [_bgImgView setImage:[UIImage doraemon_xcassetImageNamed:@"doraemon_health_bg"]];
        [self addSubview:_bgImgView];
        self.userInteractionEnabled = NO;
        [self sendSubviewToBack:_bgImgView];
    }
    return self;
}

- (CGRect)getStartingTitleCGRect{
    CGRect rect;
    rect.size.width = self.doraemon_width; rect.size.height = kDoraemonSizeFrom750_Landscape(40);
    rect.origin.x = 0; rect.origin.y = _bgImgView.doraemon_y + _bgImgView.doraemon_height* 11/60;//根据图片比例获取
    return rect;
}

- (CGRect)getButtonCGRect{
    CGPoint point = CGPointMake(_bgImgView.doraemon_x + _bgImgView.doraemon_width/2, _bgImgView.doraemon_y + _bgImgView.doraemon_height * 7/10);//根据图片比例获取
    CGRect rect;
    rect.size.width = _bgImgView.doraemon_width*2/5; rect.size.height = rect.size.width;
    rect.origin.x = point.x - rect.size.width/2 ; rect.origin.y = point.y - rect.size.width/2 + kDoraemonSizeFrom750_Landscape(5);

    return rect;
}

@end
