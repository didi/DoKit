//
//  DoraemonMockDetailButton.m
//  AFNetworking
//
//  Created by didi on 2019/11/4.
//

#import "DoraemonMockSceneButton.h"
#import "DoraemonDefine.h"

@interface DoraemonMockSceneButton()

@property (nonatomic, assign) CGFloat imgSize;
@property (nonatomic, strong) UIImageView *img;
@property (nonatomic, strong) UILabel *title;


@end

@implementation DoraemonMockSceneButton

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if(self){
        _imgSize = kDoraemonSizeFrom750_Landscape(32);
        _img = [[UIImageView alloc] init];
        [self addSubview:_img];
        _title = [[UILabel alloc] init];
        _title.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(28)];
        [self addSubview:_title];
        
        self.userInteractionEnabled = YES;
        UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tap)];
        [self addGestureRecognizer:tap];
    }
    return self;
}

- (void) renderTitle:(NSString *)title isSelected:(BOOL)select{
    _img.image = [UIImage doraemon_imageNamed:@"doraemon_mock_cancle"];
    _img.frame = CGRectMake(0, 0, _imgSize, _imgSize);
    
    _title.text = title;
    _isSelected = select;
    [_title sizeToFit];
    _title.frame = CGRectMake(_img.doraemon_right + kDoraemonSizeFrom750_Landscape(8), 0,  _title.doraemon_width, _title.doraemon_height);
    if(select){
        [self didSelected];
    }else{
        [self cancelSelected];
    }
}

- (void) didSelected{
    _isSelected = YES;
    _img.image = [UIImage doraemon_imageNamed:@"doraemon_mock_selected"];
    _title.textColor = [UIColor doraemon_blue];
}

- (void) cancelSelected{
    _isSelected = NO;
    _img.image = [UIImage doraemon_imageNamed:@"doraemon_mock_cancle"];
    _title.textColor = [UIColor doraemon_black_1];
}

- (void)tap{
    if(_isSelected){
        [self cancelSelected];
    }else{
        [self didSelected];
    }
    
    if (_delegate && [_delegate respondsToSelector:@selector(sceneBtnClick:)]) {
        [_delegate sceneBtnClick:self.tag];
    }
}

+ (CGFloat)viewWidth:(NSString *)sceneName{
    CGSize fontSize = [sceneName sizeWithAttributes:@{
        @"NSFontAttributeName" : [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(28)]
    }];
    return kDoraemonSizeFrom750_Landscape(32) + kDoraemonSizeFrom750_Landscape(8) + fontSize.width;
}

@end
