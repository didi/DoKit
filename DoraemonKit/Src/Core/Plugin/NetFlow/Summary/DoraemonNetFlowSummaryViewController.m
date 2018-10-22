//
//  DoraemonNetFlowSummaryViewController.m
//  Aspects
//
//  Created by yixiang on 2018/4/12.
//

#import "DoraemonNetFlowSummaryViewController.h"
#import "DoraemonNetFlowSummaryTotalDataView.h"
#import "DoraemonNetFlowSummaryMethodDataView.h"
#import "DoraemonNetFlowSummaryTypeDataView.h"
#import "UIView+Positioning.h"
#import "UIColor+DoreamonKit.h"

@interface DoraemonNetFlowSummaryViewController ()

@property (nonatomic, strong) DoraemonNetFlowSummaryTotalDataView *totalView;//数据概要
@property (nonatomic, strong) DoraemonNetFlowSummaryMethodDataView *methodView;//Http 方法概要
@property (nonatomic, strong) DoraemonNetFlowSummaryTypeDataView *typeView;//数据类型 概要

@end

@implementation DoraemonNetFlowSummaryViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"流量监控概要";
    self.view.backgroundColor = [UIColor doraemon_colorWithHex:0xeff0f4];
    
    CGFloat tabBarHeight = self.tabBarController.tabBar.doraemon_height;
    CGFloat navBarHeight = self.navigationController.navigationBar.doraemon_height+[[UIApplication sharedApplication] statusBarFrame].size.height;
    UIScrollView *scrollView = [[UIScrollView alloc] initWithFrame:CGRectMake(0, 0, self.view.doraemon_width, self.view.doraemon_height-tabBarHeight-navBarHeight)];
    scrollView.contentSize = CGSizeMake(self.view.doraemon_width, 20+160+20+260+20+260);
    [self.view addSubview:scrollView];
    
    _totalView = [[DoraemonNetFlowSummaryTotalDataView alloc] initWithFrame:CGRectMake(10, 20, self.view.doraemon_width-20, 160)];
    [scrollView addSubview:_totalView];
    
    _methodView = [[DoraemonNetFlowSummaryMethodDataView alloc] initWithFrame:CGRectMake(10, _totalView.doraemon_bottom+20, self.view.doraemon_width-20, 260)];
    [scrollView addSubview:_methodView];
    
    _typeView = [[DoraemonNetFlowSummaryTypeDataView alloc] initWithFrame:CGRectMake(10, _methodView.doraemon_bottom+20, self.view.doraemon_width-20, 260)];
    [scrollView addSubview:_typeView];
}

- (void)leftNavBackClick:(id)clickView{
    [self.tabBarController dismissViewControllerAnimated:YES completion:nil];
}

@end
