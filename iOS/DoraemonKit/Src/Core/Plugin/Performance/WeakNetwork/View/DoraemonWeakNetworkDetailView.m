//
//  DoraemonWeakNetworkDetailView.m
//  AFNetworking
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
@property (nonatomic, strong) DoraemonWeakNetworkInputView *timeOutInputView;
@property (nonatomic, strong) DoraemonWeakNetworkInputView *upInputView;
@property (nonatomic, strong) DoraemonWeakNetworkInputView *downInputView;
@property (nonatomic, strong) NSArray *weakItemArray;
@property (nonatomic, strong) NSDictionary *inputItemArray;
@property (nonatomic, assign) CGFloat sleepTime;
@property (nonatomic, assign) NSInteger weakSize;
@property (nonatomic, strong) NSString *timeOutTitle;
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
        [_levelView renderUIWithItemArray:_weakItemArray selecte:[DoraemonWeakNetworkManager shareInstance].selecte];
        [self addSubview:_levelView];
        
        _timeOutInputView = [[DoraemonWeakNetworkInputView alloc] initWithFrame:CGRectMake(padding * 2, _levelView.doraemon_bottom + padding/2, self.doraemon_width - padding, padding)];
        _upInputView = [[DoraemonWeakNetworkInputView alloc] initWithFrame:CGRectMake(padding * 2, _levelView.doraemon_bottom + padding/2, self.doraemon_width - padding, padding)];
        _downInputView = [[DoraemonWeakNetworkInputView alloc] initWithFrame:CGRectMake(padding * 2, _upInputView.doraemon_bottom , self.doraemon_width - padding, padding)];
        [self renderInputView:[DoraemonWeakNetworkManager shareInstance].selecte];
        
        [self addSubview:_timeOutInputView];
        [self addSubview:_upInputView];
        [self addSubview:_downInputView];
        
    }
    return self;
}

- (void)initWeakItem{
    _weakItemArray = @[
        @"断网",
        @"超时",
        @"限速"
    ];
    
    _timeOutTitle = [NSString stringWithFormat:@"%@:",DoraemonLocalizedString(@"超时时间")];
    _upFlowTitle = [NSString stringWithFormat:@"%@:",DoraemonLocalizedString(@"请求限速")];
    _downFlowTitle = [NSString stringWithFormat:@"%@:",DoraemonLocalizedString(@"响应限速")];
    _flowEpilog = @"K/s";
    _timeEpilog = @"ms";
    
    _sleepTime = 10.0;
    _weakSize = 1000;
}

- (void)_renderInputHidden:(BOOL)hidden{
    _timeOutInputView.hidden = !hidden;
    _upInputView.hidden = hidden;
    _downInputView.hidden = hidden;
}

- (void)_renderInputValue{
    [DoraemonWeakNetworkManager shareInstance].outTime = [_timeOutInputView getInputValue];
    [DoraemonWeakNetworkManager shareInstance].upFlowSpeed = [_upInputView getInputValue];
    [DoraemonWeakNetworkManager shareInstance].downFlowSpeed = [_downInputView getInputValue];
}

- (void)renderInputView:(NSInteger)select{
    
    switch (select) {
        case 0:
            _timeOutInputView.hidden = YES;
            _upInputView.hidden = YES;
            _downInputView.hidden = YES;
            break;
        case 1:
            [_timeOutInputView renderUIWithTitle:_timeOutTitle end:_timeEpilog];
            [_timeOutInputView changeInput:[DoraemonWeakNetworkManager shareInstance].outTime];
            [self _renderInputHidden:YES];
            break;
        case 2:
            [_upInputView renderUIWithTitle:_upFlowTitle end:_flowEpilog];
            [_upInputView changeInput:[DoraemonWeakNetworkManager shareInstance].upFlowSpeed];
            [_downInputView renderUIWithTitle:_downFlowTitle end:_flowEpilog];
            [_downInputView changeInput:[DoraemonWeakNetworkManager shareInstance].downFlowSpeed];
            [self _renderInputHidden:NO];
            break;
                
        default:
            break;
    }
}

#pragma mark - DoraemonWeakNetworkLevelViewDelegate
- (void)segmentSelected:(NSInteger)index{

    [DoraemonWeakNetworkManager shareInstance].selecte = index;
    [[DoraemonWeakNetworkManager shareInstance] selectWeakItemChange:index sleepTime:_sleepTime weakSize:_weakSize];
    [self renderInputView:index];
    [self _renderInputValue];
}

@end
