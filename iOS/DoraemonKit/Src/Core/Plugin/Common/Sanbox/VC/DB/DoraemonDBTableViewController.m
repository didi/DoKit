//
//  DoraemonDBTableViewController.m
//  DoraemonKit
//
//  Created by yixiang on 2019/3/31.
//

#import "DoraemonDBTableViewController.h"
#import "DoraemonDBManager.h"
#import "DoraemonDBRowView.h"
#import "DoraemonDBCell.h"
#import "DoraemonDBShowView.h"

@interface DoraemonDBTableViewController ()<UITableViewDelegate , UITableViewDataSource , DoraemonDBRowViewTypeDelegate>

@property (nonatomic, strong) UIScrollView *scrollView;
@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, copy) NSArray *dataAtTable;
@property (nonatomic, strong) DoraemonDBShowView *showView;


@end

@implementation DoraemonDBTableViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = [DoraemonDBManager shareManager].tableName;
    
    NSArray *dataAtTable = [[DoraemonDBManager shareManager] dataAtTable];
    self.dataAtTable = dataAtTable;
    
    UIScrollView *scrollView = [[UIScrollView alloc] initWithFrame:self.view.bounds];
//    scrollView.backgroundColor = [UIColor whiteColor];
    scrollView.bounces  = NO;
    [self.view addSubview:scrollView];
    self.scrollView = scrollView;
    
    UITableView *tableView = [[UITableView alloc] init];
    tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
//    tableView.backgroundColor = [UIColor whiteColor];
    tableView.delegate = self;
    tableView.dataSource = self;
    [self.scrollView addSubview:tableView];
    self.tableView = tableView;
    
}

- (void)viewWillLayoutSubviews{
    [super viewWillLayoutSubviews];
    
    if (self.dataAtTable.count) {
        NSDictionary *dict = self.dataAtTable.firstObject;
        NSUInteger num = [dict allKeys].count;
        self.tableView.frame = CGRectMake(0, 0, num * 200, self.scrollView.frame.size.height);
        self.scrollView.contentSize = CGSizeMake(self.tableView.frame.size.width, self.tableView.bounds.size.height);
    }
}

#pragma mark - UITableViewDelegate,UITableViewDataSource
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return self.dataAtTable.count == 0 ? 0 : 1;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    DoraemonDBRowView *headerView = nil;
    if (headerView == nil) {
        headerView = [[DoraemonDBRowView alloc] init];
    }
    
    NSDictionary *dict = self.dataAtTable.firstObject;
    headerView.dataArray = [dict allKeys];
    
    return headerView;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
    return 44;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.dataAtTable.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    static NSString *identifer = @"db_data";
    DoraemonDBCell *cell = [tableView dequeueReusableCellWithIdentifier:identifer];
    if (cell == nil) {
        cell = [[DoraemonDBCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:identifer];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        cell.rowView.delegate = self;
    }
    
    cell.rowView.type = (indexPath.row % 2 == 0) ? DoraemonDBRowViewTypeForOne : DoraemonDBRowViewTypeForTwo;
    NSDictionary *dict = self.dataAtTable[indexPath.row];
    [cell renderCellWithArray:[dict allValues]];
    
    return cell;
}

#pragma mark -- DoraemonDBRowViewTypeDelegate
- (void)rowView:(DoraemonDBRowView *)rowView didLabelTaped:(UILabel *)label{
    NSString *content = label.text;
    [self showText:content];
}

#pragma mark -- 显示弹出文案
- (void)showText:(NSString *)content{
    if (self.showView) {
        [self.showView removeFromSuperview];
    }
    self.showView = [[DoraemonDBShowView alloc] initWithFrame:self.view.bounds];
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
