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

@interface DoraemonDemoHomeViewController ()<UITableViewDelegate,UITableViewDataSource>

@end

@implementation DoraemonDemoHomeViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"DoraemonKit");
    self.navigationItem.leftBarButtonItems = nil;
    
    UITableView *tableView = [[UITableView alloc] initWithFrame:self.view.bounds];
    tableView.delegate = self;
    tableView.dataSource = self;
    [self.view addSubview:tableView];
    UIImageView *imageView = [[UIImageView alloc]init];
    imageView.frame = CGRectMake(0, 0, 50, 50);
    [self.view addSubview:imageView];
    imageView.image = [UIImage imageWithData:[NSData dataWithContentsOfURL: [NSURL URLWithString:@"http://hbimg.b0.upaiyun.com/00bc8151242c7d2460d0b7d4b913c6ed97f957cc158f9-SXd0Yk_fw658"]]];
    
    UIImageView *imageView2 = [[UIImageView alloc]init];
    [self.view addSubview:imageView2];
    imageView2.image = [UIImage imageWithData:[NSData dataWithContentsOfURL: [NSURL URLWithString:@"http://thumbs.dreamstime.com/b/tup%EF%BC%8C%E6%B3%B0%E5%9B%BD-%E5%B9%B4-%E6%9C%88-%E6%97%A5%EF%BC%9A%E9%95%BF%E5%B0%BE%E5%B7%B4%E5%B0%8F%E8%88%B9%E7%BE%8E%E5%A5%BD%E7%9A%84%E5%AE%A4%E5%A4%96%E7%9C%8B%E6%B3%95%E5%9C%A8%E5%B2%B8%E7%9A%84%E5%9C%A8%E5%8D%8E%E7%BE%8E%E7%9A%84tup%E6%B5%B7%E5%B2%9B%E4%B8%8A-112496796.jpg"]]];
    
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return 8;
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
    }else{
        vc = [[DoraemonDemoCommonViewController alloc] init];
    }
    [self.navigationController pushViewController:vc animated:YES];
 
    //产生crash
//    NSArray *dataArray = @[@"1",@"2"];
//    NSString *num = dataArray[2];
//    NSLog(@"num == %@",num);
}

@end
