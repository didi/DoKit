//
//  DoraemonNetFlowDetailSegment.m
//  DoraemonKit
//
//  Created by yixiang on 2018/12/9.
//

#import "DoraemonNetFlowDetailSegment.h"
#import "DoraemonDefine.h"

@interface DoraemonNetFlowDetailSegment()

@property (nonatomic, strong) UILabel *leftLabel;
@property (nonatomic, strong) UILabel *rightLabel;
@property (nonatomic, strong) UIView *selectLine;

@end

@implementation DoraemonNetFlowDetailSegment

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor whiteColor];
        _leftLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, self.doraemon_width/2, self.doraemon_height)];
        _leftLabel.textColor = [UIColor doraemon_colorWithHexString:@"337CC4"];
        _leftLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(32)];
        _leftLabel.text = DoraemonLocalizedString(@"请求");
        _leftLabel.textAlignment = NSTextAlignmentCenter;
        [self addSubview:_leftLabel];
        
        UITapGestureRecognizer *leftTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(leftTap)];
        _leftLabel.userInteractionEnabled = YES;
        [_leftLabel addGestureRecognizer:leftTap];
        
        _rightLabel = [[UILabel alloc] initWithFrame:CGRectMake(self.doraemon_width/2, 0, self.doraemon_width/2, self.doraemon_height)];
        _rightLabel.textColor = [UIColor doraemon_colorWithHexString:@"333333"];
        _rightLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(32)];
        _rightLabel.text = DoraemonLocalizedString(@"响应");
        _rightLabel.textAlignment = NSTextAlignmentCenter;
        [self addSubview:_rightLabel];
        
        UITapGestureRecognizer *rightTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(rightTap)];
        _rightLabel.userInteractionEnabled = YES;
        [_rightLabel addGestureRecognizer:rightTap];
        
        _selectLine = [[UIView alloc] initWithFrame:CGRectMake(self.doraemon_width/4-kDoraemonSizeFrom750_Landscape(128)/2, self.doraemon_height-kDoraemonSizeFrom750_Landscape(4), kDoraemonSizeFrom750_Landscape(128), kDoraemonSizeFrom750_Landscape(4))];
        _selectLine.backgroundColor = [UIColor doraemon_colorWithHexString:@"337CC4"];
        [self addSubview:_selectLine];
        
    }
    return self;
}

- (void)leftTap{
    if (_delegate && [_delegate respondsToSelector:@selector(segmentClick:)]) {
        [_delegate segmentClick:0];
    }
    _leftLabel.textColor = [UIColor doraemon_colorWithHexString:@"337CC4"];
    _rightLabel.textColor = [UIColor doraemon_colorWithHexString:@"333333"];
    _selectLine.frame = CGRectMake(self.doraemon_width/4-kDoraemonSizeFrom750_Landscape(128)/2, self.doraemon_height-kDoraemonSizeFrom750_Landscape(4), kDoraemonSizeFrom750_Landscape(128), kDoraemonSizeFrom750_Landscape(4));
}

- (void)rightTap{
    if (_delegate && [_delegate respondsToSelector:@selector(segmentClick:)]) {
        [_delegate segmentClick:1];
    }
    _leftLabel.textColor = [UIColor doraemon_colorWithHexString:@"333333"];
    _rightLabel.textColor = [UIColor doraemon_colorWithHexString:@"337CC4"];
    _selectLine.frame = CGRectMake(self.doraemon_width*3/4-kDoraemonSizeFrom750_Landscape(128)/2, self.doraemon_height-kDoraemonSizeFrom750_Landscape(4), kDoraemonSizeFrom750_Landscape(128), kDoraemonSizeFrom750_Landscape(4));
}


@end
