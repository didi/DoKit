//
//  DoraemonMockDetailButton.m
//  AFNetworking
//
//  Created by didi on 2019/11/4.
//

#import "DoraemonMockDetailButton.h"
#import "DoraemonDefine.h"

@interface DoraemonMockDetailButton()

@property (nonatomic, assign) CGFloat imgSize;
@property (nonatomic, strong) UIImageView *img;
@property (nonatomic, strong) UILabel *title;


@end

@implementation DoraemonMockDetailButton

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if(self){
        _imgSize = kDoraemonSizeFrom750_Landscape(32);
        _img = [[UIImageView alloc] init];
        [self addSubview:_img];
        _title = [[UILabel alloc] init];
        _title.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(28)];
        [self addSubview:_title];
        
        UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tap)];
        [self addGestureRecognizer:tap];
    }
    return self;
}

- (void) needImage{
    _img.image = [UIImage doraemon_imageNamed:@"doraemon_mock_cancle"];
    _img.frame = CGRectMake(0, 0, _imgSize, _imgSize);
}

- (void) renderTitle:(NSString *)title isSelected:(BOOL)select{
    _title.text = title;
    _isSelected = select;
    _title.frame = CGRectMake(_img.doraemon_bottom + kDoraemonSizeFrom750_Landscape(8), 0, self.doraemon_width - _imgSize , self.doraemon_height);
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
    if (_delegate && [_delegate respondsToSelector:@selector(detailBtnClick:)]) {
        [_delegate detailBtnClick:self];
    }
}

@end
