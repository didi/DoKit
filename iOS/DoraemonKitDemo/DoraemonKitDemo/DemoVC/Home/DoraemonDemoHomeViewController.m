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
#import "UIView+Doraemon.h"
#import "UIViewController+Doraemon.h"
#import "DoraemonDemoMemoryLeakViewController.h"
#import "DoraemonDemoMultiControlViewController.h"

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
    self.title = DoraemonDemoLocalizedString(@"DoraemonKit");
    self.navigationItem.leftBarButtonItems = nil;
    [self.view addSubview:self.tableView];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return 11;
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
        txt = DoraemonDemoLocalizedString(@"沙盒测试Demo");
    }else if(row==1){
        txt = DoraemonDemoLocalizedString(@"日志测试Demo");
    }else if(row==2){
        txt = DoraemonDemoLocalizedString(@"性能测试Demo");
    }else if(row==3){
        txt = DoraemonDemoLocalizedString(@"视觉测试Demo");
    }else if(row==4){
        txt = DoraemonDemoLocalizedString(@"网络测试Demo");
    }else if(row==5){
        txt = DoraemonDemoLocalizedString(@"模拟位置Demo");
    }else if(row==6){
        txt = DoraemonDemoLocalizedString(@"crash触发Demo");
    }else if(row==7){
        txt = DoraemonDemoLocalizedString(@"通用测试Demo");
    }else if(row==8){
        txt = DoraemonDemoLocalizedString(@"内存泄漏测试");
    }else if(row == 9){
        txt = DoraemonDemoLocalizedString(@"一机多控测试Demo");
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
    }else if(row == 8){
        vc = [[DoraemonDemoMemoryLeakViewController alloc] init];
    }else if(row == 9){
        vc = [[DoraemonDemoMultiControlViewController alloc] init];
    }
    if (vc) {
        [self.navigationController pushViewController:vc animated:YES];
    }
 
    //产生crash
//    NSArray *dataArray = @[@"1",@"2"];
//    NSString *num = dataArray[2];
//    NSLog(@"num == %@",num);
}

- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];

    self.tableView.frame = [self fullscreen];
}

@end
