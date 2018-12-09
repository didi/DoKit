//
//  DoraemonColorPickInfoView.m
//  DoraemonKit
//
//  Created by wenquan on 2018/12/3.
//

#import "DoraemonColorPickInfoView.h"
#import "DoraemonDefine.h"

@interface DoraemonColorPickInfoView ()

@property (nonatomic, strong) UIView *colorView;
@property (nonatomic, strong) UILabel *colorValueLbl;
@property (nonatomic, strong) UIButton *closeBtn;

@end

@implementation DoraemonColorPickInfoView

#pragma mark - Lifecycle

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        [self commonInit];
    }
    return self;
}

- (void)commonInit {
    self.backgroundColor = [UIColor whiteColor];
    self.layer.cornerRadius = kDoraemonSizeFrom750(8);
    self.layer.borderWidth = 1.;
    self.layer.borderColor = [UIColor doraemon_colorWithHex:0x999999 andAlpha:0.2].CGColor;
    
    [self addSubview:self.colorView];
    [self addSubview:self.colorValueLbl];
    [self addSubview:self.closeBtn];
}

#pragma mark - Layout

- (void)layoutSubviews {
    [super layoutSubviews];
    
    CGFloat colorWidth = kDoraemonSizeFrom750(28);
    CGFloat colorHeight = kDoraemonSizeFrom750(28);
    self.colorView.frame = CGRectMake(kDoraemonSizeFrom750(32), (self.doraemon_height - colorHeight) / 2.0, colorWidth, colorHeight);
    
    CGFloat colorValueWidth = kDoraemonSizeFrom750(150);
    self.colorValueLbl.frame = CGRectMake(self.colorView.doraemon_right + kDoraemonSizeFrom750(20), 0, colorValueWidth, self.doraemon_height);
    
    CGFloat closeWidth = kDoraemonSizeFrom750(44);
    CGFloat closeHeight = kDoraemonSizeFrom750(44);
    self.closeBtn.frame = CGRectMake(self.doraemon_width - closeWidth - kDoraemonSizeFrom750(32), (self.doraemon_height - closeHeight) / 2.0, closeWidth, closeHeight);
}

#pragma mark - Public

- (void)setCurrentColor:(NSString *)hexColor{
    self.colorView.backgroundColor = [UIColor doraemon_colorWithHexString:hexColor];
    self.colorValueLbl.text = hexColor;
}

#pragma mark - Actions

- (void)closeBtnClicked:(id)sender {
    if ([self.delegate respondsToSelector:@selector(closeBtnClicked:onColorPickInfoView:)]) {
        [self.delegate closeBtnClicked:sender onColorPickInfoView:self];
    }
}

#pragma mark - Private

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
//    NSLog(@"PickInfoView---触摸开始");
}

- (void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event {
    
    UITouch *touch = [touches anyObject];
    
    CGPoint currentPoint = [touch locationInView:self];
    // 获取上一个点
    CGPoint prePoint = [touch previousLocationInView:self];
    CGFloat offsetX = currentPoint.x - prePoint.x;
    CGFloat offsetY = currentPoint.y - prePoint.y;
    
//    NSLog(@"PickInfoView----当前位置:%@---之前的位置:%@",NSStringFromCGPoint(currentPoint),NSStringFromCGPoint(prePoint));
    self.transform = CGAffineTransformTranslate(self.transform, offsetX, offsetY);
}

- (void)touchesEnded:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
//    NSLog(@"PickInfoView---触摸结束");
}

#pragma mark - Getter

- (UIView *)colorView {
    if (!_colorView) {
        _colorView = [[UIView alloc] init];
        _colorView.layer.borderWidth = 1.;
        _colorView.layer.borderColor = [UIColor doraemon_colorWithHex:0x999999 andAlpha:0.2].CGColor;
    }
    return _colorView;
}

- (UILabel *)colorValueLbl {
    if (!_colorValueLbl) {
        _colorValueLbl = [[UILabel alloc] init];
        _colorValueLbl.textColor = [UIColor doraemon_black_1];
        _colorValueLbl.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750(28)];
    }
    return _colorValueLbl;
}

- (UIButton *)closeBtn {
    if (!_closeBtn) {
        _closeBtn = [[UIButton alloc] init];
        [_closeBtn setBackgroundImage:[UIImage doraemon_imageNamed:@"doraemon_close"] forState:UIControlStateNormal];
        [_closeBtn addTarget:self action:@selector(closeBtnClicked:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _closeBtn;
}

@end
