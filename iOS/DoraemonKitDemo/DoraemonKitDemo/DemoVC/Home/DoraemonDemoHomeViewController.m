//
//  DoraemonDemoHomeViewController.m
//  DoraemonKitDemo
//
//  Created by yixiang on 2018/5/15.
//  Copyright © 2018年 yixiang. All rights reserved.
//

#import "DoraemonDemoHomeViewController.h"
#import "DoraemonDemoLoggerViewController.h"
#import "DoraemonDemoPerformanceViewController.h"
#import "DoraemonDemoSanboxViewController.h"
#import "DoraemonDemoUIViewController.h"
#import "DoraemonDemoNetViewController.h"
#import "DoraemonDemoMockGPSViewController.h"
#import "DoraemonDemoCrashViewController.h"
#import "DoraemonDemoCommonViewController.h"
#import <objc/runtime.h>
#import "Doraemoni18NUtil.h"
#import "UIView+Doraemon.h"
#import "UIViewController+Doraemon.h"
#import "DoraemonDemoMemoryLeakViewController.h"

@interface DoraemonDemoHomeViewController ()<UITableViewDelegate,UITableViewDataSource>

@property (strong, nonatomic) UITableView *tableView;

@end

@implementation DoraemonDemoHomeViewController


- (UITableView *)tableView {
    if (!_tableView) {
        _tableView = [[UITableView alloc] initWithFrame:self.view.bounds];
        _tableView.delegate = self;
        _tableView.dataSource = self;
    }
    
    return _tableView;
}


- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"DoraemonKit");
    self.navigationItem.leftBarButtonItems = nil;
    [self.view addSubview:self.tableView];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return 9;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    static NSString *cellId = @"cellId";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:cellId];
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellId];
    }
    NSString *txt = nil;
    NSInteger row = indexPath.row;
    if (row==0) {
        txt = DoraemonLocalizedString(@"沙盒测试Demo");
    }else if(row==1){
        txt = DoraemonLocalizedString(@"日记测试Demo");
    }else if(row==2){
        txt = DoraemonLocalizedString(@"性能测试Demo");
    }else if(row==3){
        txt = DoraemonLocalizedString(@"视觉测试Demo");
    }else if(row==4){
        txt = DoraemonLocalizedString(@"网络测试Demo");
    }else if(row==5){
        txt = DoraemonLocalizedString(@"模拟位置Demo");
    }else if(row==6){
        txt = DoraemonLocalizedString(@"crash触发Demo");
    }else if(row==7){
        txt = DoraemonLocalizedString(@"通用测试Demo");
    }else if(row==8){
        txt = @"内存泄漏测试";
    }
    cell.textLabel.text = txt;
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    NSInteger row = indexPath.row;
    UIViewController *vc = nil;
    if (row == 0) {
        vc = [[DoraemonDemoSanboxViewController alloc] init];
    }else if(row == 1){
        vc = [[DoraemonDemoLoggerViewController alloc] init];
    }else if(row == 2){
        vc = [[DoraemonDemoPerformanceViewController alloc] init];
    }else if(row == 3){
        vc = [[DoraemonDemoUIViewController alloc] init];
    }else if(row == 4){
        vc = [[DoraemonDemoNetViewController alloc] init];
    }else if(row == 5){
        vc = [[DoraemonDemoMockGPSViewController alloc] init];
    }else if(row == 6){
        vc = [[DoraemonDemoCrashViewController alloc] init];
    }else if(row == 7){
        vc = [[DoraemonDemoCommonViewController alloc] init];
    }else{
        vc = [[DoraemonDemoMemoryLeakViewController alloc] init];
    }
    [self.navigationController pushViewController:vc animated:YES];
 
    //产生crash
//    NSArray *dataArray = @[@"1",@"2"];
//    NSString *num = dataArray[2];
//    NSLog(@"num == %@",num);
}

- (void)layouttableView {
    UIInterfaceOrientation orientation = [UIApplication sharedApplication].statusBarOrientation;
    switch (orientation) {
        case UIInterfaceOrientationLandscapeLeft:
        case UIInterfaceOrientationLandscapeRight:
        {
            CGSize size = self.view.doraemon_size;
            if (size.width > size.height) {
                UIEdgeInsets safeAreaInsets = [self safeAreaInset];
                CGRect frame = self.view.frame;
                CGFloat width = self.view.doraemon_width - safeAreaInsets.left - safeAreaInsets.right;
                frame.origin.x = safeAreaInsets.left;
                frame.size.width = width;
                self.tableView.frame = frame;
            }
        }
            break;
        default:
            self.tableView.frame = self.view.frame;
            break;
    }
}

- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];

    [self layouttableView];
}

@end
