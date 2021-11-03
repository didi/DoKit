//
//  DoraemonMockGPSCenterView.m
//  DoraemonKit
//
//  Created by yixiang on 2018/12/2.
//

#import "DoraemonMockGPSCenterView.h"
#import "DoraemonDefine.h"

@interface DoraemonMockGPSCenterView()

@property (nonatomic, strong) UIView *circleView;
@property (nonatomic, strong) UIImageView *locationIconView;
@property (nonatomic, strong) UILabel *gpsLabel;
@property (nonatomic, strong) UIImageView *arrowImageView;

@end

@implementation DoraemonMockGPSCenterView

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        _circleView = [[UIView alloc] initWithFrame:CGRectMake(self.doraemon_width/2-kDoraemonSizeFrom750_Landscape(100)/2, self.doraemon_height/2-kDoraemonSizeFrom750_Landscape(100)/2, kDoraemonSizeFrom750_Landscape(100), kDoraemonSizeFrom750_Landscape(100))];
        _circleView.layer.cornerRadius = kDoraemonSizeFrom750_Landscape(50);
        _circleView.backgroundColor = [UIColor doraemon_colorWithHex:0xFFA511 andAlpha:0.37];
        [self addSubview:_circleView];
        
        _locationIconView = [[UIImageView alloc] initWithImage:[UIImage doraemon_xcassetImageNamed:@"doraemon_location"]];
        _locationIconView.frame = CGRectMake(self.circleView.center.x-_locationIconView.doraemon_width/2, self.circleView.center.y-_locationIconView.doraemon_height, _locationIconView.doraemon_width, _locationIconView.doraemon_height);
        [self addSubview:_locationIconView];
        
        _gpsLabel = [[UILabel alloc] init];
        _gpsLabel.textColor = [UIColor doraemon_black_1];
        _gpsLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(24)];
        _gpsLabel.backgroundColor = [UIColor whiteColor];
        _gpsLabel.textAlignment = NSTextAlignmentCenter;
        [self addSubview:_gpsLabel];
        
        _arrowImageView = [[UIImageView alloc] initWithImage:[UIImage doraemon_xcassetImageNamed:@"doraemon_arrow_down"]];
        _arrowImageView.frame = CGRectMake(self.doraemon_width/2-_arrowImageView.doraemon_width/2, _locationIconView.doraemon_top-kDoraemonSizeFrom750_Landscape(20)-_arrowImageView.doraemon_height, _arrowImageView.doraemon_width, _arrowImageView.doraemon_height);
        [self addSubview:_arrowImageView];
        
    }
    return self;
}

- (void)renderUIWithGPS:(NSString *)gps{
    _gpsLabel.text = gps;
    [_gpsLabel sizeToFit];
    CGFloat w = _gpsLabel.doraemon_width + kDoraemonSizeFrom750_Landscape(30)*2;
    CGFloat h = _gpsLabel.doraemon_height + kDoraemonSizeFrom750_Landscape(12)*2;
    _gpsLabel.frame = CGRectMake(self.doraemon_width/2-w/2, _arrowImageView.doraemon_top-h+kDoraemonSizeFrom750_Landscape(10), w, h);
    _gpsLabel.layer.cornerRadius = h/2;
    _gpsLabel.clipsToBounds = YES;
}

- (void)hiddenGPSInfo:(BOOL)hidden{
    _gpsLabel.hidden = hidden;
    _arrowImageView.hidden = hidden;
}

- (UIView *)hitTest:(CGPoint)point withEvent:(UIEvent *)event{
    UIView *hitView = [super hitTest:point withEvent:event];
    
    if (hitView == self) {
        return nil;
    }
    return hitView;
}

@end
