//
//  DoraemonWeexLogViewController.m
//  DoraemonKit
//
//  Created by yixiang on 2019/5/23.
//  Copyright © 2019年 Chameleon-Team. All rights reserved.
//

#import "DoraemonWeexLogViewController.h"
#import "DoraemonDefine.h"
#import "DoraemonWeexLogSearchView.h"
#import "DoraemonWeexLogLevelView.h"
#import "DoraemonWeexLogCell.h"
#import "DoraemonWeexLogDataSource.h"

@interface DoraemonWeexLogViewController ()<DoraemonWeexLogSearchViewDelegate,DoraemonWeexLogLevelViewDelegate,UITableViewDelegate,UITableViewDataSource>

@property (nonatomic, strong) DoraemonWeexLogSearchView *searchView;
@property (nonatomic, strong) DoraemonWeexLogLevelView *levelView;
@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, copy) NSArray *dataArray;
@property (nonatomic, copy) NSArray *origArray;

@end

@implementation DoraemonWeexLogViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"Weex日志记录");
    
    self.origArray = [NSArray arrayWithArray:[DoraemonWeexLogDataSource shareInstance].logs];
    self.dataArray = [NSArray arrayWithArray:self.origArray];
    
    _searchView = [[DoraemonWeexLogSearchView alloc] initWithFrame:CGRectMake(kDoraemonSizeFrom750(32), self.bigTitleView.doraemon_bottom+kDoraemonSizeFrom750(32), self.view.doraemon_width-2*kDoraemonSizeFrom750(32), kDoraemonSizeFrom750(100))];
    _searchView.delegate = self;
    [self.view addSubview:_searchView];
    
    _levelView = [[DoraemonWeexLogLevelView alloc] initWithFrame:CGRectMake(0, _searchView.doraemon_bottom+kDoraemonSizeFrom750(32), self.view.doraemon_width, kDoraemonSizeFrom750(68))];
    _levelView.delegate = self;
    [self.view addSubview:_levelView];
    
    self.tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, _levelView.doraemon_bottom+kDoraemonSizeFrom750(32), self.view.doraemon_width, self.view.doraemon_height-_searchView.doraemon_bottom-kDoraemonSizeFrom750(32)) style:UITableViewStylePlain];
//    self.tableView.backgroundColor = [UIColor whiteColor];
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    [self.view addSubview:self.tableView];
}

- (BOOL)needBigTitleView{
    return YES;
}

#pragma mark - UITableView Delegate
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.dataArray.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    DoraemonWeexLogModel* model = [self.dataArray objectAtIndex:indexPath.row];
    return [DoraemonWeexLogCell cellHeightWith:model];
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return CGFLOAT_MIN;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return CGFLOAT_MIN;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *identifer = @"nj_log_cell";
    DoraemonWeexLogCell *cell = [tableView dequeueReusableCellWithIdentifier:identifer];
    if (!cell) {
        cell = [[DoraemonWeexLogCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifer];
    }
    DoraemonWeexLogModel* model = [self.dataArray objectAtIndex:indexPath.row];
    [cell renderCellWithData:model];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    DoraemonWeexLogModel* model = [self.dataArray objectAtIndex:indexPath.row];
    model.expand = !model.expand;
    [self.tableView reloadData];
}

#pragma mark -- DoraemonNSLogSearchViewDelegate
- (void)searchViewInputChange:(NSString *)text{
    if (text.length > 0) {
        NSArray *dataArray = self.origArray;
        NSMutableArray *resultArray = [[NSMutableArray alloc] init];
        for(DoraemonWeexLogModel *model in dataArray){
            NSString *content = model.content;
            if ([content containsString:text]) {
                [resultArray addObject:model];
            }
        }
        self.dataArray = [[NSArray alloc] initWithArray:resultArray];
    }else{
        self.dataArray = [[NSArray alloc] initWithArray:self.origArray];
    }
    
    [self.tableView reloadData];
}

#pragma mark - DoraemonNJLogLevelViewDelegate
- (void)segmentSelected:(NSInteger)index{
    WXLogFlag flag;
    if (index == 0) {
        flag = WXLogFlagDebug;
    }else if(index == 1){
        flag = WXLogFlagLog;
    }else if(index == 2){
        flag = WXLogFlagInfo;
    }else if(index == 3){
        flag = WXLogFlagWarning;
    }else{
        flag = WXLogFlagError;
    }
    NSArray *dataArray = self.origArray;
    NSMutableArray *resultArray = [[NSMutableArray alloc] init];
    for(DoraemonWeexLogModel *model in dataArray){
        WXLogFlag modelFlag = model.flag;
        if (modelFlag <= flag) {
            [resultArray addObject:model];
        }
    }
    self.dataArray = [[NSArray alloc] initWithArray:resultArray];
    [self.tableView reloadData];
}

@end
