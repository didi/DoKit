//
//  DoraemonHealthViewController.m
//  DoraemonKit
//
//  Created by didi on 2019/12/30.
//

#import "DoraemonHealthViewController.h"
#import "DoraemonHealthHomeView.h"
#import "DoraemonHealthFooterView.h"
#import "DoraemonHealthManager.h"
#import "DoraemonHealthAlertView.h"
#import "DoraemonHealthInstructionsView.h"
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
    CGFloat offset_y = self.bigTitleView.doraemon_bottom;
    _homeView = [[DoraemonHealthHomeView alloc] initWithFrame:CGRectMake(0, 0, self.view.doraemon_width, self.view.doraemon_height - offset_y)];
    
    _instructionsView = [[DoraemonHealthInstructionsView alloc] initWithFrame:CGRectMake(0, _homeView.doraemon_bottom, self.view.doraemon_width, self.view.doraemon_height - offset_y)];
    
    _scrollView = [[UIScrollView alloc] initWithFrame:CGRectMake(0, offset_y, self.view.doraemon_width, self.view.doraemon_height - offset_y)];
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
        BOOL currentStatus = [DoraemonHealthManager sharedInstance].start;
        if (currentStatus) {
            [weakSelf showEndAlert:YES];
        }else{
            [DoraemonAlertUtil handleAlertActionWithVC:weakSelf text:@"是否重启App开启健康体检" okBlock:^{
                [[DoraemonHealthManager sharedInstance] rebootAppForHealthCheck];
            } cancleBlock:^{}];
            
        }
    }];
    
    [_scrollView addSubview:_homeView];
    [_scrollView addSubview:_instructionsView];
    [self.view addSubview:_scrollView];
    [self.view addSubview:_footerView];
    
    [self showFooter:![DoraemonHealthManager sharedInstance].start];
    
}

- (BOOL)needBigTitleView{
    return YES;
}

- (void)showFooter:(BOOL)show{
    _footerView.hidden = !show;
    _scrollView.scrollEnabled = show;
}

- (void)showEndAlert:(BOOL)show{
    if(show){
        __weak typeof(self) weakSelf = self;
        DoraemonHealthAlertView *alertView = [[DoraemonHealthAlertView alloc] init];
        [alertView renderUI:DoraemonLocalizedString(@"结束前请完善下列信息") placeholder:@[] inputTip:@[DoraemonLocalizedString(@"测试用例名称"),DoraemonLocalizedString(@"测试人名称")] ok:DoraemonLocalizedString(@"提交") quit:DoraemonLocalizedString(@"丢弃") cancle:DoraemonLocalizedString(@"取消") okBlock:^{
            
            NSArray *result = [alertView getInputText];
            if (result.count == 2) {
                [DoraemonHealthManager sharedInstance].caseName = result[0];
                [DoraemonHealthManager sharedInstance].testPerson = result[1];
                [weakSelf showFooter:YES];
                [weakSelf.homeView.btnView statusForBtn:NO];
                [weakSelf.homeView.startingTitle renderUIWithTitle:DoraemonLocalizedString(@"点击开始检测")];
                [[DoraemonHealthManager sharedInstance] stopHealthCheck];
            }
        } quitBlock:^{
            [DoraemonHealthManager sharedInstance].caseName = @"";
            [DoraemonHealthManager sharedInstance].testPerson = @"";
            [weakSelf showFooter:YES];
            [weakSelf.homeView.btnView statusForBtn:NO];
            [weakSelf.homeView.startingTitle renderUIWithTitle:DoraemonLocalizedString(@"点击开始检测")];
            [[DoraemonHealthManager sharedInstance] stopHealthCheck];
        } cancleBlock:^{
        }];
        [self.view addSubview:alertView];
    }
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
