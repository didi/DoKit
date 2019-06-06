//
//  DoraemonWeexInfoViewController.m
//  WeexDemo
//
//  Created by yixiang on 2019/6/4.
//  Copyright © 2019年 taobao. All rights reserved.
//

#import "DoraemonWeexInfoViewController.h"
#import "DoraemonWeexInfoHeaderView.h"
#import "DoraemonWeexInfoDataManager.h"
#import "DoraemonDefine.h"
#import "DoraemonWeexInfoCell.h"

@interface DoraemonWeexInfoViewController ()<UITableViewDelegate,UITableViewDataSource>

@property (nonatomic, strong) DoraemonWeexInfoHeaderView *headerView;
@property (nonatomic, strong) UITableView *tableView;

@end

@implementation DoraemonWeexInfoViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"weex信息查看";
    
    _headerView = [[DoraemonWeexInfoHeaderView alloc] init];
    [_headerView renderUIWithWxBundleUrl:[DoraemonWeexInfoDataManager shareInstance].wxBundleUrl];
    _headerView.frame = CGRectMake(0, self.bigTitleView.doraemon_bottom, _headerView.doraemon_width, _headerView.doraemon_height);
    [self.view addSubview:_headerView];
    
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, _headerView.doraemon_bottom, self.view.doraemon_width, self.view.doraemon_height-_headerView.doraemon_bottom)];
    _tableView.dataSource = self;
    _tableView.delegate = self;
    [self.view addSubview:_tableView];
}

- (BOOL)needBigTitleView{
    return YES;
}

#pragma mark - UITableViewDelegate UITableViewDataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 8;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return [DoraemonWeexInfoCell cellHeight];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *identifer = @"weex_info_cell";
    DoraemonWeexInfoCell *cell = [tableView dequeueReusableCellWithIdentifier:identifer];
    if (!cell) {
        cell = [[DoraemonWeexInfoCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifer];
    }
    
    NSUInteger row = indexPath.row;
    NSString *title,*content;
    if (row == 0) {
        title = @"weexsdk版本号";
        content = [DoraemonWeexInfoDataManager shareInstance].wxSDKVersion;
    }else if(row == 1) {
        title = @"wxJSLib版本号";
        content = [DoraemonWeexInfoDataManager shareInstance].wxJSLibVersion;
    }else if(row == 2) {
        title = @"wxBundleType";
        content = [DoraemonWeexInfoDataManager shareInstance].wxBundleType;
    }else if(row == 3) {
        title = @"wxBundleSize";
        content = [DoraemonWeexInfoDataManager shareInstance].wxBundleSize;
    }else if(row ==4) {
        title = @"请求bundle时间";
        content = [self timeFromStart:[DoraemonWeexInfoDataManager shareInstance].wxStartDownLoadBundle end:[DoraemonWeexInfoDataManager shareInstance].wxEndDownLoadBundle];
    }else if(row ==5) {
        title = @"处理bundle时间";
        content = [self timeFromStart:[DoraemonWeexInfoDataManager shareInstance].wxEndDownLoadBundle end:[DoraemonWeexInfoDataManager shareInstance].wxEndLoadBundle];
    }else if(row ==6) {
        title = @"第一个view出现时间";
        content = [self timeFromStart:[DoraemonWeexInfoDataManager shareInstance].wxEndLoadBundle end:[DoraemonWeexInfoDataManager shareInstance].wxFirstInteractionView];
    }else if(row ==7) {
        title = @"可交互时间";
        content = [self timeFromStart:[DoraemonWeexInfoDataManager shareInstance].wxFirstInteractionView end:[DoraemonWeexInfoDataManager shareInstance].wxInteraction];
    }
    [cell renderCellWithTitle:title content:content];
    return cell;
}

- (NSString *)timeFromStart:(NSString *)start end:(NSString *)end{
    CGFloat duration = end.doubleValue - start.doubleValue;
    return [NSString stringWithFormat:@"%.1f", duration];
}

@end
