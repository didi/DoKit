//
//  DoraemonViewAlignView.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/6/16.
//

#import "DoraemonViewAlignView.h"
#import "UIImage+DoraemonKit.h"
#import "DoraemonDefine.h"
#import "UIColor+DoreamonKit.h"
#import <UIView+Positioning/UIView+Positioning.h>

static CGFloat const kViewCheckSize = 40;

@interface DoraemonViewAlignView()

@property (nonatomic, strong) UIImageView *imageView;
@property (nonatomic, strong) UIView *horizontalLine;//水平线
@property (nonatomic, strong) UIView *verticalLine;//垂直线
@property (nonatomic, strong) UILabel *leftLabel;
@property (nonatomic, strong) UILabel *topLabel;
@property (nonatomic, strong) UILabel *rightLabel;
@property (nonatomic, strong) UILabel *bottomLabel;

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
        imageView.image = [UIImage doraemon_imageNamed:@"doraemon_finger"];
        imageView.layer.cornerRadius = kViewCheckSize/2;
        imageView.layer.masksToBounds = YES;
        imageView.layer.borderColor = [UIColor orangeColor].CGColor;
        imageView.layer.borderWidth = 1;
        [self addSubview:imageView];
        _imageView = imageView;
        
        imageView.userInteractionEnabled = YES;
        UIPanGestureRecognizer *pan = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(pan:)];
        [imageView addGestureRecognizer:pan];
        
        _horizontalLine = [[UIView alloc] initWithFrame:CGRectMake(0, imageView.centerY-0.25, self.width, 0.5)];
        _horizontalLine.backgroundColor = [UIColor doraemon_colorWithHexString:@"#666666"];
        [self addSubview:_horizontalLine];
        
        _verticalLine = [[UIView alloc] initWithFrame:CGRectMake(imageView.centerX-0.25, 0, 0.5, self.height)];
        _verticalLine.backgroundColor = [UIColor doraemon_colorWithHexString:@"#666666"];
        [self addSubview:_verticalLine];
        
        [self bringSubviewToFront:_imageView];
        
        _leftLabel = [[UILabel alloc] init];
        _leftLabel.font = [UIFont systemFontOfSize:12];
        _leftLabel.textColor = [UIColor doraemon_colorWithHexString:@"#666666"];
        _leftLabel.text = [NSString stringWithFormat:@"%.1f",imageView.centerX];
        [self addSubview:_leftLabel];
        [_leftLabel sizeToFit];
        _leftLabel.frame = CGRectMake(imageView.centerX/2, imageView.centerY-_leftLabel.height, _leftLabel.width, _leftLabel.height);
        
        _topLabel = [[UILabel alloc] init];
        _topLabel.font = [UIFont systemFontOfSize:12];
        _topLabel.textColor = [UIColor doraemon_colorWithHexString:@"#666666"];
        _topLabel.text = [NSString stringWithFormat:@"%.1f",imageView.centerY];
        [self addSubview:_topLabel];
        [_topLabel sizeToFit];
        _topLabel.frame = CGRectMake(imageView.centerX-_topLabel.width, imageView.centerY/2, _topLabel.width, _topLabel.height);
        
        _rightLabel = [[UILabel alloc] init];
        _rightLabel.font = [UIFont systemFontOfSize:12];
        _rightLabel.textColor = [UIColor doraemon_colorWithHexString:@"#666666"];
        _rightLabel.text = [NSString stringWithFormat:@"%.1f",self.width-imageView.centerX];
        [self addSubview:_rightLabel];
        [_rightLabel sizeToFit];
        _rightLabel.frame = CGRectMake(imageView.centerX+(self.width-imageView.centerX)/2, imageView.centerY-_rightLabel.height, _rightLabel.width, _rightLabel.height);
        
        _bottomLabel = [[UILabel alloc] init];
        _bottomLabel.font = [UIFont systemFontOfSize:12];
        _bottomLabel.textColor = [UIColor doraemon_colorWithHexString:@"#666666"];
        _bottomLabel.text = [NSString stringWithFormat:@"%.1f",self.height - imageView.centerY];
        [self addSubview:_bottomLabel];
        [_bottomLabel sizeToFit];
        _bottomLabel.frame = CGRectMake(imageView.centerX-_bottomLabel.width, imageView.centerY+(self.height - imageView.centerY)/2, _bottomLabel.width, _bottomLabel.height);
        
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
    CGFloat newX = panView.centerX+offsetPoint.x;
    CGFloat newY = panView.centerY+offsetPoint.y;

    CGPoint centerPoint = CGPointMake(newX, newY);
    panView.center = centerPoint;
    
    _horizontalLine.frame = CGRectMake(0, _imageView.centerY-0.25, self.width, 0.5);
    _verticalLine.frame = CGRectMake(_imageView.centerX-0.25, 0, 0.5, self.height);
    
    _leftLabel.text = [NSString stringWithFormat:@"%.1f",_imageView.centerX];
    [_leftLabel sizeToFit];
    _leftLabel.frame = CGRectMake(_imageView.centerX/2, _imageView.centerY-_leftLabel.height, _leftLabel.width, _leftLabel.height);
    
    _topLabel.text = [NSString stringWithFormat:@"%.1f",_imageView.centerY];
    [_topLabel sizeToFit];
    _topLabel.frame = CGRectMake(_imageView.centerX-_topLabel.width, _imageView.centerY/2, _topLabel.width, _topLabel.height);
    
    _rightLabel.text = [NSString stringWithFormat:@"%.1f",self.width-_imageView.centerX];
    [_rightLabel sizeToFit];
    _rightLabel.frame = CGRectMake(_imageView.centerX+(self.width-_imageView.centerX)/2, _imageView.centerY-_rightLabel.height, _rightLabel.width, _rightLabel.height);
    
    _bottomLabel.text = [NSString stringWithFormat:@"%.1f",self.height - _imageView.centerY];
    [_bottomLabel sizeToFit];
    _bottomLabel.frame = CGRectMake(_imageView.centerX-_bottomLabel.width, _imageView.centerY+(self.height - _imageView.centerY)/2, _bottomLabel.width, _bottomLabel.height);
}

- (BOOL)pointInside:(CGPoint)point withEvent:(UIEvent *)event{
    if(CGRectContainsPoint(_imageView.frame, point)){
        return YES;
    }
    return NO;
}


@end
