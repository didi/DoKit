//
//  DoraemonDemoMultiSlideView.m
//  DoraemonKitDemo
//
//  Created by wzp on 2021/8/24.
//  Copyright © 2021 yixiang. All rights reserved.
//

#import "DoraemonDemoMultiSlideView.h"
#import <Masonry/Masonry.h>
#import "DoraemonMCCommandGenerator.h"
typedef void(^CommonActionBlock)(id);
@interface DoraemonDemoMultiSlideView ()<UIGestureRecognizerDelegate>

@property (nonatomic, strong) UIView *bgView;
@property (nonatomic, strong) UIView *slideView;
@property (nonatomic, strong) UIButton *slideButton;
@property (nonatomic, assign) CGFloat fMaxSlideValue;
@property (nonatomic, strong) UILabel *lbLockName;
@property (nonatomic, assign) int nUnlockScale;
@property (nonatomic, strong) CommonActionBlock unLockBlock;
@end

@implementation DoraemonDemoMultiSlideView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if(self){
        _nUnlockScale = 70;    // 默认百分之七十
        
        _bgView = [UIView new];
        [_bgView setBackgroundColor:[UIColor redColor]];
        _bgView.layer.cornerRadius = 12;
        _bgView.clipsToBounds = YES;
        [self addSubview:_bgView];
        [_bgView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self);
        }];
        //
        _slideView = [UIView new];
        [_slideView setBackgroundColor:[UIColor grayColor]];
        _slideView.layer.cornerRadius = 12;
        [_bgView addSubview:_slideView];
        [_slideView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self.bgView);
        }];
        //
        _slideButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_slideButton setBackgroundColor:[UIColor orangeColor]];
        [_slideView addSubview:_slideButton];
        [_slideButton mas_makeConstraints:^(MASConstraintMaker *make) {
            make.leading.top.bottom.equalTo(self.slideView);
            make.width.equalTo(@(50));
        }];

        UIFont *font = [UIFont systemFontOfSize:24];
        _lbLockName = [UILabel new];
        [_lbLockName setBackgroundColor:[UIColor grayColor]];
        [_lbLockName setText:@"自定义事件"];
        [_lbLockName setTextColor:[UIColor whiteColor]];
        [_lbLockName setTextAlignment:NSTextAlignmentCenter];
        [_lbLockName setFont:font];
        [_slideView addSubview:_lbLockName];
        [_lbLockName mas_makeConstraints:^(MASConstraintMaker *make) {
            make.leading.equalTo(self.slideView).offset(118);
            make.trailing.equalTo(self.slideView).offset(-80);
            make.height.equalTo(@(80));
            make.center.equalTo(self.slideView);
        }];
        
        UIPanGestureRecognizer *panGestureRecognizer = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(performPanGestureRecognizer:)];
        panGestureRecognizer.delegate = self;
        [_slideButton addGestureRecognizer:panGestureRecognizer];
    }
    
    return self;
}


- (void)setNUnlockScale:(int)nUnlockScale
{
    if(nUnlockScale < 0 ){
        nUnlockScale = 0;
    }else if(nUnlockScale > 100){
        nUnlockScale = 100;
    }
    _nUnlockScale = nUnlockScale;
}



- (void)performPanGestureRecognizer:(UIPanGestureRecognizer *)panGestureRecognizer
{
    _fMaxSlideValue = _bgView.frame.size.width - _slideButton.frame.size.width;
    CGPoint translation = [panGestureRecognizer translationInView:_bgView];
    if(panGestureRecognizer.state == UIGestureRecognizerStateBegan){
        _lbLockName.hidden = YES;
    }else if(panGestureRecognizer.state == UIGestureRecognizerStateChanged){
        if (translation.x == 1) {
            [DoraemonMCCommandGenerator sendCustomMessageWithView:self eventInfo:@{@"eventInfo":@"customType1"} messageType:@"customType1"];
        }
        if(translation.x > 0){
            _lbLockName.hidden = YES;
            CGFloat offset = translation.x;
            if(offset > _fMaxSlideValue){
                offset = _fMaxSlideValue;
            }
            [_slideView mas_remakeConstraints:^(MASConstraintMaker *make) {
                make.leading.equalTo(self.bgView).offset(offset);
                make.top.bottom.trailing.equalTo(self.bgView);
            }];
        }else{
            
        }
    }else if(panGestureRecognizer.state == UIGestureRecognizerStateEnded || panGestureRecognizer.state == UIGestureRecognizerStateCancelled){
        if(panGestureRecognizer.state == UIGestureRecognizerStateEnded) {
            [DoraemonMCCommandGenerator sendCustomMessageWithView:self eventInfo:@{@"eventInfo":@"customType2"} messageType:@"customType2"];
        }
        if(translation.x > _fMaxSlideValue * self.nUnlockScale/100.0){
            [_slideView mas_remakeConstraints:^(MASConstraintMaker *make) {
                make.top.bottom.trailing.equalTo(self.bgView);
                make.leading.equalTo(self.bgView).offset(self.fMaxSlideValue);
            }];
            _lbLockName.hidden = YES;
            if(self.unLockBlock){
                self.unLockBlock(@(YES));
            }
        }else{
            [_slideView mas_remakeConstraints:^(MASConstraintMaker *make) {
                make.edges.equalTo(self.bgView);
            }];
            _lbLockName.hidden = NO;
        }
    }
}

@end
