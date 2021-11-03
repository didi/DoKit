//
//  DoraemonWeexStorageViewController.m
//  DoraemonKit
//
//  Created by yixiang on 2019/5/30.
//  Copyright © 2019年 taobao. All rights reserved.
//

#import "DoraemonWeexStorageViewController.h"
#import "DoraemonWeexStorageResolver.h"
#import "DoraemonDefine.h"
#import "DoraemonWeexStorageRowView.h"
#import "DoraemonWeexStorageCell.h"
#import "DoraemonWeexStorageShowView.h"

@interface DoraemonWeexStorageViewController ()<UITableViewDataSource,UITableViewDelegate,DoraemonWeexStorageRowViewDelegate>

@property (nonatomic, copy) NSDictionary *storageInfo;
@property (nonatomic, copy) NSArray *allKeys;

@property (nonatomic, strong) DoraemonWeexStorageShowView *showView;

@end

@implementation DoraemonWeexStorageViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"Weex缓存");
    DoraemonWeexStorageResolver *resolver = [[DoraemonWeexStorageResolver alloc] init];
    _storageInfo = [resolver getWeexStorageInfo];
    _allKeys = [_storageInfo allKeys];
    
    UITableView *tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, self.bigTitleView.doraemon_bottom, self.view.doraemon_width, self.view.doraemon_height-self.bigTitleView.doraemon_bottom)];
    tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
//    tableView.backgroundColor = [UIColor whiteColor];
    tableView.delegate = self;
    tableView.dataSource = self;
    [self.view addSubview:tableView];
}

- (BOOL)needBigTitleView{
    return YES;
}

#pragma mark - UITableViewDelegate,UITableViewDataSource
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    DoraemonWeexStorageRowView *headerView = nil;
    if (headerView == nil) {
        headerView = [[DoraemonWeexStorageRowView alloc] init];
    }
    
    headerView.dataArray = @[@"Key",@"Value"];
    
    return headerView;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
    return 44;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return _allKeys.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return 44;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    static NSString *identifer = @"DoraemonWeexStorageCellID";
    DoraemonWeexStorageCell *cell = [tableView dequeueReusableCellWithIdentifier:identifer];
    if (cell == nil) {
        cell = [[DoraemonWeexStorageCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:identifer];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        cell.rowView.delegate = self;
    }
    
    cell.rowView.type = (indexPath.row % 2 == 0) ? DoraemonWeexStorageRowViewTypeForOne : DoraemonWeexStorageRowViewTypeForTwo;
    NSString *key = _allKeys[indexPath.row];
    NSString *value = _storageInfo[key];
    [cell renderCellWithArray:@[key,value]];
    
    return cell;
}

#pragma mark -- DoraemonWeexStorageRowViewDelegate
- (void)rowView:(DoraemonWeexStorageRowView *)rowView didLabelTaped:(UILabel *)label{
    NSString *content = label.text;
    //NSLog(@"%@",content);
    [self showText:content];
}

#pragma mark -- 显示弹出文案
- (void)showText:(NSString *)content{
    if (self.showView) {
        [self.showView removeFromSuperview];
    }
    self.showView = [[DoraemonWeexStorageShowView alloc] initWithFrame:self.view.bounds];
    [self.view addSubview:self.showView];
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(dismissView)];
    self.showView.userInteractionEnabled = YES;
    [self.showView addGestureRecognizer:tap];
    
    [self.showView showText:content];
}

- (void)dismissView{
    if (self.showView) {
        [self.showView removeFromSuperview];
    }
}

@end
