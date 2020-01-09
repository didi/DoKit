//
//  DoraemonHealthViewController.m
//  AFNetworking
//
//  Created by didi on 2019/12/30.
//

#import "DoraemonHealthViewController.h"
#import "DoraemonHealthHomeView.h"
#import "DoraemonHealthFooterView.h"
#import "DoraemonHealthManager.h"
#import "DoraemonHealthAlertView.h"
#import "DoraemonHealthInstructionsView.h"
#import "DoraemonHealthCountdownWindow.h"
#import "DoraemonDefine.h"

@interface DoraemonHealthViewController()<UIScrollViewDelegate,DoraemonHealthFooterButtonDelegate>

@property (nonatomic, strong) UIScrollView *scrollView;
@property (nonatomic, strong) DoraemonHealthHomeView *homeView;
@property (nonatomic, strong) DoraemonHealthInstructionsView *instructionsView;
@property (nonatomic, strong) DoraemonHealthFooterView *footerView;

@end

@implementation DoraemonHealthViewController 

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"健康体检");
    CGFloat bg_y = kDoraemonSizeFrom750_Landscape(178);
    _homeView = [[DoraemonHealthHomeView alloc] initWithFrame:CGRectMake(0, 0, self.view.doraemon_width, self.view.doraemon_height - bg_y)];
    
    _instructionsView = [[DoraemonHealthInstructionsView alloc] initWithFrame:CGRectMake(0, _homeView.doraemon_bottom, self.view.doraemon_width, self.view.doraemon_height - bg_y)];
    
    _scrollView = [[UIScrollView alloc] initWithFrame:CGRectMake(0, bg_y, self.view.doraemon_width, self.view.doraemon_height - bg_y)];
    _scrollView.backgroundColor = [UIColor whiteColor];
    _scrollView.delegate = self;
    _scrollView.contentSize = CGSizeMake(_homeView.doraemon_width, _homeView.doraemon_height*2);//设置大小
    _scrollView.pagingEnabled = YES;
    
    CGFloat footerHeight = kDoraemonSizeFrom750_Landscape(145);
    CGFloat footerTop = self.view.doraemon_height - footerHeight;
    if (@available(iOS 11.0, *)) {
        footerTop -= self.view.safeAreaInsets.bottom;
    }
    _footerView = [[DoraemonHealthFooterView alloc] initWithFrame:CGRectMake(0, footerTop, self.view.doraemon_width, footerHeight)];
    _footerView.delegate = self;
    [_footerView renderUIWithTitleImg:YES];
    
    
    __weak typeof(self) weakSelf = self;
    [_homeView addBlock:^{
        if([DoraemonHealthManager sharedInstance].start){
            [weakSelf showFooter:YES];
            [[DoraemonHealthCountdownWindow shareInstance] start:10];
        }else{
            [weakSelf showEndAlert:YES];
            [[DoraemonHealthCountdownWindow shareInstance] hide];
        }
        
    }];
    
    [_scrollView addSubview:_homeView];
    [_scrollView addSubview:_instructionsView];
    [self.view addSubview:_scrollView];
    [self.view addSubview:_footerView];
    
    [self showFooter:[DoraemonHealthManager sharedInstance].start];
    [self showEndAlert:[DoraemonHealthManager sharedInstance].alert];
    
}

- (BOOL)needBigTitleView{
    return YES;
}

- (void)showFooter:(BOOL)show{
    _footerView.hidden = show;
    _scrollView.scrollEnabled = !show;
}

- (void)showEndAlert:(BOOL)show{
    if(show){
        __weak typeof(self) weakSelf = self;
        [DoraemonHealthManager sharedInstance].alert = YES;
        DoraemonHealthAlertView *alertView = [[DoraemonHealthAlertView alloc] init];
        [alertView renderUI:@"撒端茶倒水" placeholder:@[@"第一个",@"第二个"] inputTip:@[@"提示1",@"提示2",@"提示3"] ok:DoraemonLocalizedString(@"提交") cancle:DoraemonLocalizedString(@"取消") okBlock:^{
            [weakSelf hiddenEndAlert];
            
            [weakSelf endToast];
            
        } cancleBlock:^{
            [weakSelf hiddenEndAlert];
        }];
        [self.view addSubview:alertView];
    }
}

- (void)hiddenEndAlert{
    [DoraemonHealthManager sharedInstance].alert = NO;
}

- (void)endToast{
    [DoraemonToastUtil showToastBlack:DoraemonLocalizedString(@"提交成功\n恭喜已完成检测！") inView:self.view];
}


#pragma mark - DoraemonHealthFooterButtonDelegate
- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView {
    BOOL scrollToScrollStop = !scrollView.tracking && !scrollView.dragging && !scrollView.decelerating;
    if (scrollToScrollStop) {
        [_footerView renderUIWithTitleImg: scrollView.contentOffset.y <= 0];
        _footerView.hidden = NO;
    }
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView{
    _footerView.hidden = YES;
}

#pragma mark - DoraemonHealthFooterButtonDelegate
- (void)footerBtnClick:(id)sender{
    if(_footerView.top){
        _scrollView.contentOffset = CGPointMake(0, _scrollView.doraemon_height);
    }else{
        _scrollView.contentOffset = CGPointMake(0, 0);
    }
    [_footerView renderUIWithTitleImg:!_footerView.top];
    _footerView.hidden = NO;
}


@end
