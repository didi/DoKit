//
//  DoraemonViewAlignView.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/6/16.
//

#import "DoraemonViewAlignView.h"
#import "DoraemonDefine.h"
#import "DoraemonVisualInfoWindow.h"

static CGFloat const kViewCheckSize = 62;

@interface DoraemonViewAlignView()

@property (nonatomic, strong) UIImageView *imageView;
@property (nonatomic, strong) UIView *horizontalLine;//水平线
@property (nonatomic, strong) UIView *verticalLine;//垂直线
@property (nonatomic, strong) UILabel *leftLabel;
@property (nonatomic, strong) UILabel *topLabel;
@property (nonatomic, strong) UILabel *rightLabel;
@property (nonatomic, strong) UILabel *bottomLabel;
@property (nonatomic, strong) DoraemonVisualInfoWindow *infoWindow;
@property (nonatomic, strong) UILabel *infoLbl;
@property (nonatomic, strong) UIButton *closeBtn;

@end

@implementation DoraemonViewAlignView

-(instancetype)init{
    self = [super init];
    if (self) {
        self.frame = CGRectMake(0, 0, DoraemonScreenWidth, DoraemonScreenHeight);
        self.backgroundColor = [UIColor clearColor];
        self.layer.zPosition = FLT_MAX;
        //self.userInteractionEnabled = NO;
        
        
        UIImageView *imageView = [[UIImageView alloc] initWithFrame:CGRectMake(DoraemonScreenWidth/2-kViewCheckSize/2, DoraemonScreenHeight/2-kViewCheckSize/2, kViewCheckSize, kViewCheckSize)];
        imageView.image = [UIImage doraemon_imageNamed:@"doraemon_visual"];
        [self addSubview:imageView];
        _imageView = imageView;
        
        imageView.userInteractionEnabled = YES;
        UIPanGestureRecognizer *pan = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(pan:)];
        [imageView addGestureRecognizer:pan];
        
        _horizontalLine = [[UIView alloc] initWithFrame:CGRectMake(0, imageView.doraemon_centerY-0.25, self.doraemon_width, 0.5)];
        _horizontalLine.backgroundColor = [UIColor doraemon_colorWithHexString:@"#666666"];
        [self addSubview:_horizontalLine];
        
        _verticalLine = [[UIView alloc] initWithFrame:CGRectMake(imageView.doraemon_centerX-0.25, 0, 0.5, self.doraemon_height)];
        _verticalLine.backgroundColor = [UIColor doraemon_colorWithHexString:@"#666666"];
        [self addSubview:_verticalLine];
        
        [self bringSubviewToFront:_imageView];
        
        _leftLabel = [[UILabel alloc] init];
        _leftLabel.font = [UIFont systemFontOfSize:12];
        _leftLabel.textColor = [UIColor doraemon_colorWithHexString:@"#666666"];
        _leftLabel.text = [NSString stringWithFormat:@"%.1f",imageView.doraemon_centerX];
        [self addSubview:_leftLabel];
        [_leftLabel sizeToFit];
        _leftLabel.frame = CGRectMake(imageView.doraemon_centerX/2, imageView.doraemon_centerY-_leftLabel.doraemon_height, _leftLabel.doraemon_width, _leftLabel.doraemon_height);
        
        _topLabel = [[UILabel alloc] init];
        _topLabel.font = [UIFont systemFontOfSize:12];
        _topLabel.textColor = [UIColor doraemon_colorWithHexString:@"#666666"];
        _topLabel.text = [NSString stringWithFormat:@"%.1f",imageView.doraemon_centerY];
        [self addSubview:_topLabel];
        [_topLabel sizeToFit];
        _topLabel.frame = CGRectMake(imageView.doraemon_centerX-_topLabel.doraemon_width, imageView.doraemon_centerY/2, _topLabel.doraemon_width, _topLabel.doraemon_height);
        
        _rightLabel = [[UILabel alloc] init];
        _rightLabel.font = [UIFont systemFontOfSize:12];
        _rightLabel.textColor = [UIColor doraemon_colorWithHexString:@"#666666"];
        _rightLabel.text = [NSString stringWithFormat:@"%.1f",self.doraemon_width-imageView.doraemon_centerX];
        [self addSubview:_rightLabel];
        [_rightLabel sizeToFit];
        _rightLabel.frame = CGRectMake(imageView.doraemon_centerX+(self.doraemon_width-imageView.doraemon_centerX)/2, imageView.doraemon_centerY-_rightLabel.doraemon_height, _rightLabel.doraemon_width, _rightLabel.doraemon_height);
        
        _bottomLabel = [[UILabel alloc] init];
        _bottomLabel.font = [UIFont systemFontOfSize:12];
        _bottomLabel.textColor = [UIColor doraemon_colorWithHexString:@"#666666"];
        _bottomLabel.text = [NSString stringWithFormat:@"%.1f",self.doraemon_height - imageView.doraemon_centerY];
        [self addSubview:_bottomLabel];
        [_bottomLabel sizeToFit];
        _bottomLabel.frame = CGRectMake(imageView.doraemon_centerX-_bottomLabel.doraemon_width, imageView.doraemon_centerY+(self.doraemon_height - imageView.doraemon_centerY)/2, _bottomLabel.doraemon_width, _bottomLabel.doraemon_height);
        
        _infoWindow = [[DoraemonVisualInfoWindow alloc] initWithFrame:CGRectMake(kDoraemonSizeFrom750(30), DoraemonScreenHeight - kDoraemonSizeFrom750(100) - kDoraemonSizeFrom750(30), DoraemonScreenWidth - 2*kDoraemonSizeFrom750(30), kDoraemonSizeFrom750(100))];
        
        CGFloat closeWidth = kDoraemonSizeFrom750(44);
        CGFloat closeHeight = kDoraemonSizeFrom750(44);
        _closeBtn = [[UIButton alloc] initWithFrame:CGRectMake(_infoWindow.bounds.size.width - closeWidth - kDoraemonSizeFrom750(32), (_infoWindow.bounds.size.height - closeHeight) / 2.0, closeWidth, closeHeight)];
        [_closeBtn setBackgroundImage:[UIImage doraemon_imageNamed:@"doraemon_close"] forState:UIControlStateNormal];
        [_closeBtn addTarget:self action:@selector(closeBtnClicked:) forControlEvents:UIControlEventTouchUpInside];
        [_infoWindow addSubview:_closeBtn];
        
        _infoLbl = [[UILabel alloc] initWithFrame:CGRectMake(kDoraemonSizeFrom750(32), 0, _infoWindow.bounds.size.width - 2*kDoraemonSizeFrom750(32) - _closeBtn.doraemon_width , _infoWindow.bounds.size.height)];
        _infoLbl.backgroundColor =[UIColor clearColor];
        _infoLbl.textColor = [UIColor doraemon_black_1];
        _infoLbl.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750(24)];
        [self configInfoLblText];
        [_infoWindow addSubview:_infoLbl];
    }
    return self;
}

- (void)pan:(UIPanGestureRecognizer *)sender{
    //1、获得拖动位移
    CGPoint offsetPoint = [sender translationInView:sender.view];
    //2、清空拖动位移
    [sender setTranslation:CGPointZero inView:sender.view];
    //3、重新设置控件位置
    UIView *panView = sender.view;
    CGFloat newX = panView.doraemon_centerX+offsetPoint.x;
    CGFloat newY = panView.doraemon_centerY+offsetPoint.y;

    CGPoint centerPoint = CGPointMake(newX, newY);
    panView.center = centerPoint;
    
    _horizontalLine.frame = CGRectMake(0, _imageView.doraemon_centerY-0.25, self.doraemon_width, 0.5);
    _verticalLine.frame = CGRectMake(_imageView.doraemon_centerX-0.25, 0, 0.5, self.doraemon_height);
    
    _leftLabel.text = [NSString stringWithFormat:@"%.1f",_imageView.doraemon_centerX];
    [_leftLabel sizeToFit];
    _leftLabel.frame = CGRectMake(_imageView.doraemon_centerX/2, _imageView.doraemon_centerY-_leftLabel.doraemon_height, _leftLabel.doraemon_width, _leftLabel.doraemon_height);
    
    _topLabel.text = [NSString stringWithFormat:@"%.1f",_imageView.doraemon_centerY];
    [_topLabel sizeToFit];
    _topLabel.frame = CGRectMake(_imageView.doraemon_centerX-_topLabel.doraemon_width, _imageView.doraemon_centerY/2, _topLabel.doraemon_width, _topLabel.doraemon_height);
    
    _rightLabel.text = [NSString stringWithFormat:@"%.1f",self.doraemon_width-_imageView.doraemon_centerX];
    [_rightLabel sizeToFit];
    _rightLabel.frame = CGRectMake(_imageView.doraemon_centerX+(self.doraemon_width-_imageView.doraemon_centerX)/2, _imageView.doraemon_centerY-_rightLabel.doraemon_height, _rightLabel.doraemon_width, _rightLabel.doraemon_height);
    
    _bottomLabel.text = [NSString stringWithFormat:@"%.1f",self.doraemon_height - _imageView.doraemon_centerY];
    [_bottomLabel sizeToFit];
    _bottomLabel.frame = CGRectMake(_imageView.doraemon_centerX-_bottomLabel.doraemon_width, _imageView.doraemon_centerY+(self.doraemon_height - _imageView.doraemon_centerY)/2, _bottomLabel.doraemon_width, _bottomLabel.doraemon_height);
    
    [self configInfoLblText];
}

- (BOOL)pointInside:(CGPoint)point withEvent:(UIEvent *)event{
    if(CGRectContainsPoint(_imageView.frame, point)){
        return YES;
    }
    return NO;
}

- (void)configInfoLblText {
    _infoLbl.text = [NSString stringWithFormat:@"位置：左%@  右%@  上%@  下%@", _leftLabel.text, _rightLabel.text, _topLabel.text, _bottomLabel.text];
}

- (void)closeBtnClicked:(id)sender {
    [[NSNotificationCenter defaultCenter] postNotificationName:DoraemonClosePluginNotification object:nil userInfo:nil];

}

- (void)show {
    _infoWindow.hidden = NO;
    self.hidden = NO;
}

- (void)hide {
    _infoWindow.hidden = YES;
    self.hidden = YES;
}

@end
