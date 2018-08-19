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
#import "A.h"
#import <objc/runtime.h>

@interface DoraemonDemoHomeViewController ()<UITableViewDelegate,UITableViewDataSource>

@end

@implementation DoraemonDemoHomeViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"DoraemonKit";
    self.navigationItem.leftBarButtonItems = nil;
    
    UITableView *tableView = [[UITableView alloc] initWithFrame:self.view.bounds];
    tableView.delegate = self;
    tableView.dataSource = self;
    [self.view addSubview:tableView];
    
    A *a = [[A alloc] init];
    [a methodA];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return 6;
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
        txt = @"沙盒测试Demo";
    }else if(row==1){
        txt = @"日记测试Demo";
    }else if(row==2){
        txt = @"性能测试Demo";
    }else if(row==3){
        txt = @"视觉测试Demo";
    }else if(row==4){
        txt = @"网络测试Demo";
    }else if(row==5){
        txt = @"模拟位置Demo";
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
    }else{
        vc = [[DoraemonDemoMockGPSViewController alloc] init];
    }
    [self.navigationController pushViewController:vc animated:YES];
 
    //产生crash
//    NSArray *dataArray = @[@"1",@"2"];
//    NSString *num = dataArray[2];
//    NSLog(@"num == %@",num);
}

@end
