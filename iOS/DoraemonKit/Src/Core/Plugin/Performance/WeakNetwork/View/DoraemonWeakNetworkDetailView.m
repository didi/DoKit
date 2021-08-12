//
//  DoraemonWeakNetworkDetailView.m
//  DoraemonKit
//
//  Created by didi on 2019/12/16.
//

#import "DoraemonWeakNetworkDetailView.h"
#import "DoraemonWeakNetworkManager.h"
#import "DoraemonWeakNetworkLevelView.h"
#import "DoraemonWeakNetworkInputView.h"
#import "DoraemonDefine.h"

@interface DoraemonWeakNetworkDetailView()<DoraemonWeakNetworkLevelViewDelegate>

@property (nonatomic, strong) DoraemonWeakNetworkLevelView *levelView;
@property (nonatomic, strong) DoraemonWeakNetworkInputView *delayInputView;
@property (nonatomic, strong) DoraemonWeakNetworkInputView *upInputView;
@property (nonatomic, strong) DoraemonWeakNetworkInputView *downInputView;
@property (nonatomic, strong) NSArray *weakItemArray;
@property (nonatomic, strong) NSDictionary *inputItemArray;
@property (nonatomic, assign) NSInteger weakSize;
@property (nonatomic, strong) NSString *delayTitle;
@property (nonatomic, strong) NSString *upFlowTitle;
@property (nonatomic, strong) NSString *downFlowTitle;
@property (nonatomic, strong) NSString *flowEpilog;
@property (nonatomic, strong) NSString *timeEpilog;


@end


@implementation DoraemonWeakNetworkDetailView

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if(self){
        CGFloat padding = kDoraemonSizeFrom750_Landscape(68);
        _levelView = [[DoraemonWeakNetworkLevelView alloc] initWithFrame:CGRectMake(0, padding, self.doraemon_width, padding)];
        _levelView.delegate = self;
        [self initWeakItem];
        [_levelView renderUIWithItemArray:_weakItemArray selecte:[DoraemonWeakNetworkManager shareInstance].selecte ? :0];
        [self addSubview:_levelView];
        
        _delayInputView = [[DoraemonWeakNetworkInputView alloc] initWithFrame:CGRectMake(padding * 2, _levelView.doraemon_bottom + padding/2, self.doraemon_width - padding, padding)];
        _upInputView = [[DoraemonWeakNetworkInputView alloc] initWithFrame:CGRectMake(padding * 2, _levelView.doraemon_bottom + padding/2, self.doraemon_width - padding, padding)];
        _downInputView = [[DoraemonWeakNetworkInputView alloc] initWithFrame:CGRectMake(padding * 2, _upInputView.doraemon_bottom , self.doraemon_width - padding, padding)];
        [self renderInputView:[DoraemonWeakNetworkManager shareInstance].selecte];
        
        __weak typeof(self) weakSelf = self;
         [_delayInputView addBlock:^{
             [DoraemonWeakNetworkManager shareInstance].delayTime = [weakSelf.delayInputView getInputValue];
         }];
         [_upInputView addBlock:^{
             [DoraemonWeakNetworkManager shareInstance].upFlowSpeed = [weakSelf.upInputView getInputValue];
         }];
         [_downInputView addBlock:^{
             [DoraemonWeakNetworkManager shareInstance].downFlowSpeed = [weakSelf.downInputView getInputValue];
         }];
        
        [self addSubview:_delayInputView];
        [self addSubview:_upInputView];
        [self addSubview:_downInputView];
        
    }
    return self;
}

- (void)initWeakItem{
    _weakItemArray = @[
        @"断网",
        @"超时",
        @"限速",
        @"延时"
    ];
    
    _delayTitle = [NSString stringWithFormat:@"%@:",DoraemonLocalizedString(@"延时时间")];
    _upFlowTitle = [NSString stringWithFormat:@"%@:",DoraemonLocalizedString(@"请求限速")];
    _downFlowTitle = [NSString stringWithFormat:@"%@:",DoraemonLocalizedString(@"响应限速")];
    _flowEpilog = @"Kb/s";
    _timeEpilog = @"S";
}

- (void)_renderInputHidden:(BOOL)hidden{
    _delayInputView.hidden = !hidden;
    _upInputView.hidden = hidden;
    _downInputView.hidden = hidden;
}

- (void)_renderInputValue{
    [DoraemonWeakNetworkManager shareInstance].delayTime = [_delayInputView getInputValue];
    [DoraemonWeakNetworkManager shareInstance].upFlowSpeed = [_upInputView getInputValue];
    [DoraemonWeakNetworkManager shareInstance].downFlowSpeed = [_downInputView getInputValue];
}

- (void)renderInputView:(NSInteger)select{
    
    switch (select) {
        case 0:
        case 1:
            _delayInputView.hidden = YES;
            _upInputView.hidden = YES;
            _downInputView.hidden = YES;
            break;
        case 2:
            [_upInputView renderUIWithTitle:_upFlowTitle end:_flowEpilog];
            [_upInputView renderUIWithSpeed:[DoraemonWeakNetworkManager shareInstance].upFlowSpeed define:2000];
            [_downInputView renderUIWithTitle:_downFlowTitle end:_flowEpilog];
            [_downInputView renderUIWithSpeed:[DoraemonWeakNetworkManager shareInstance].downFlowSpeed define:2000];
           [self _renderInputHidden:NO];
            break;
        case 3:
            [_delayInputView renderUIWithTitle:_delayTitle end:_timeEpilog];
            [_delayInputView renderUIWithSpeed:[DoraemonWeakNetworkManager shareInstance].delayTime define:10];
            [self _renderInputHidden:YES];
            break;
                
        default:
            break;
    }
}

#pragma mark - DoraemonWeakNetworkLevelViewDelegate
- (void)segmentSelected:(NSInteger)index{

    [DoraemonWeakNetworkManager shareInstance].selecte = index;
    [self renderInputView:index];
    [self _renderInputValue];
}

@end
