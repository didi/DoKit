//
//  DoraemonHealthBtnView.m
//  DoraemonKit
//
//  Created by didi on 2019/12/30.
//

#import "DoraemonHealthBtnView.h"
#import "DoraemonHealthManager.h"
#import "DoraemonDefine.h"

@interface DoraemonHealthBtnView()

@property (nonatomic, strong) UIImageView *healthBtn;

@end

@implementation DoraemonHealthBtnView

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        _healthBtn = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, self.doraemon_width, self.doraemon_height)];
        [_healthBtn setImage:[UIImage doraemon_xcassetImageNamed:@"doraemon_health_start"]];
        [self addSubview:_healthBtn];
        UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tap)];
        [self addGestureRecognizer:tap];
    }
    return self;
}

- (void)tap{
    if (_delegate && [_delegate respondsToSelector:@selector(healthBtnClick:)]) {
        [_delegate healthBtnClick:self];
    }
}

- (void)statusForBtn:(BOOL)start{
    NSString *imgName = @"doraemon_health_start";
    if(start){
        imgName = @"doraemon_health_end";
    }
    [_healthBtn setImage:[UIImage doraemon_xcassetImageNamed:imgName]];
}


@end
