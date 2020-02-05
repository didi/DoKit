//
//  DoraemonHealthHomeView.m
//  AFNetworking
//
//  Created by didi on 2020/1/2.
//

#import "DoraemonHealthHomeView.h"
#import "DoraemonHealthBgView.h"
#import "DoraemonHealthManager.h"
#import "DoraemonToastUtil.h"
#import "DoraemonDefine.h"
#import "DoraemonHealthManager.h"

@interface DoraemonHealthHomeView ()<DoraemonHealthButtonDelegate>

@property (nonatomic, strong) DoraemonHealthBgView *bgView;
@property (nonatomic, copy) DoraemonHealthHomeBlock block;

@end

@implementation DoraemonHealthHomeView

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        _bgView = [[DoraemonHealthBgView alloc] initWithFrame:CGRectMake(0, 0, self.doraemon_width, self.doraemon_height)];
        _startingTitle = [[DoraemonHealthStartingTitle alloc] initWithFrame:[_bgView getStartingTitleCGRect]];
        if ([DoraemonHealthManager sharedInstance].start) {
            [_startingTitle renderUIWithTitle:DoraemonLocalizedString(@"正在检测中...")];
        }else{
            [_startingTitle renderUIWithTitle:DoraemonLocalizedString(@"点击开始检测")];
        }
        
        _btnView = [[DoraemonHealthBtnView alloc] initWithFrame:[_bgView getButtonCGRect]];
        [_btnView statusForBtn:[DoraemonHealthManager sharedInstance].start];
        _btnView.delegate = self;
        
        [self addSubview:_bgView];
        [self addSubview:_startingTitle];
        [self addSubview:_btnView];
    }
    return self;
}

- (void)addBlock:(DoraemonHealthHomeBlock)block{
    self.block = block;
}

- (void)_selfHandle{
    if(self.block){
        self.block();
    }
}

#pragma mark - DoraemonHealthButtonDelegate
- (void)healthBtnClick:(nonnull id)sender {
    [self _selfHandle];
}

@end
