//
//  DoraemonMockHalfButton.m
//  AFNetworking
//
//  Created by didi on 2019/10/23.
//

#import "DoraemonMockFilterButton.h"
#import "DoraemonDefine.h"

@interface DoraemonMockFilterButton()

@property(nonatomic, strong) UILabel *titleLabel;
@property(nonatomic, strong) UIImageView *arrow;

@end

@implementation DoraemonMockFilterButton

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        _titleLabel = [[UILabel alloc] init];
        _titleLabel.textColor = [UIColor doraemon_black_1];
        _titleLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(28)];
        [self addSubview:_titleLabel];
        _down = NO;
        _arrow = [[UIImageView alloc] init];
        [self addSubview:_arrow];
        
        UITapGestureRecognizer *todo = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tap)];
        [self addGestureRecognizer:todo];
        
        _selectedItemIndex = 0;
    }
    return self;
}


- (void)renderUIWithTitle:(NSString *)title{

    _titleLabel.text = DoraemonLocalizedString(title);
    [_titleLabel sizeToFit];
    _titleLabel.frame = CGRectMake(self.doraemon_width/2-_titleLabel.doraemon_width/3*2, self.doraemon_height/2-_titleLabel.doraemon_height/2, _titleLabel.doraemon_width, _titleLabel.doraemon_height);
    _arrow.image = [UIImage doraemon_imageNamed:@"doraemon_mock_filter_down"];
    _arrow.frame = CGRectMake(_titleLabel.doraemon_right + _arrow.image.size.width, self.doraemon_height/2-_arrow.image.size.height/2, _arrow.image.size.width, _arrow.image.size.height);
}

- (void)setDropdown:(BOOL )isDown{
    NSString *imgName = @"doraemon_mock_filter_down";
    _down = NO;
    if(isDown){
        imgName = @"doraemon_mock_filter_up";
        _down = YES;
    }
    _arrow.image = [UIImage doraemon_imageNamed:imgName];
    _arrow.doraemon_width = _arrow.image.size.width;
    _arrow.doraemon_height = _arrow.image.size.height;
}

- (void)tap{
    if (_delegate && [_delegate respondsToSelector:@selector(filterBtnClick:)]) {
        _down = !_down;
        [self setDropdown:self.down];
        [_delegate filterBtnClick:self];
    }
}

@end
