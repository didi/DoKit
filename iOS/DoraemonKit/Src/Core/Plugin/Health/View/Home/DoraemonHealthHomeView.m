//
//  DoraemonHealthHomeView.m
//  AFNetworking
//
//  Created by didi on 2020/1/2.
//

#import "DoraemonHealthHomeView.h"
#import "DoraemonHealthBtnView.h"
#import "DoraemonHealthBgView.h"
#import "DoraemonHealthManager.h"
#import "DoraemonHealthStartingTitle.h"
#import "DoraemonToastUtil.h"
#import "DoraemonDefine.h"
#import "DoraemonHealthManager.h"

@interface DoraemonHealthHomeView ()<DoraemonHealthButtonDelegate>

@property (nonatomic, strong) DoraemonHealthBgView *bgView;
@property (nonatomic, strong) DoraemonHealthBtnView *btnView;
@property (nonatomic, strong) DoraemonHealthStartingTitle *startingTitle;
@property (nonatomic, copy) DoraemonHealthHomeBlock block;

@end

@implementation DoraemonHealthHomeView

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        _bgView = [[DoraemonHealthBgView alloc] initWithFrame:CGRectMake(0, 0, self.doraemon_width, self.doraemon_height)];
        _startingTitle = [[DoraemonHealthStartingTitle alloc] initWithFrame:[_bgView getStartingTitleCGRect]];
        [_startingTitle renderUIWithTitle:DoraemonLocalizedString(@"正在检测中...")];
        [_startingTitle showUITitle:[DoraemonHealthManager sharedInstance].start];//manager
        
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
    [_startingTitle showUITitle:_btnView.start];

    if(self.block){
        self.block();
    }
}

#pragma mark - DoraemonHealthButtonDelegate
- (void)healthBtnClick:(nonnull id)sender {
    [self _selfHandle];
    
    if(!_btnView.start){//提示框确认之后合入
        [DoraemonToastUtil showToastBlack:DoraemonLocalizedString(@"提交成功\n恭喜已完成检测！") inView:self];
        [[DoraemonHealthManager sharedInstance] stopHealthCheck];
    }else{
        [[DoraemonHealthManager sharedInstance] startHealthCheck];
    }
}

@end
