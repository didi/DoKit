//
//  DoraemonNSLogListViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/11/26.
//

#import "DoraemonNSLogListViewController.h"
#import "DoraemonNSLogManager.h"
#import "UIView+Doraemon.h"
#import "DoraemonNSLogListCell.h"
#import "DoraemonNSLogModel.h"
#import "DoraemonNSLogSearchView.h"
#import "DoraemonDefine.h"

@interface DoraemonNSLogListViewController ()<UITableViewDelegate,UITableViewDataSource,DoraemonNSLogSearchViewDelegate>

@property (nonatomic, strong) DoraemonNSLogSearchView *searchView;
@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, copy) NSArray *dataArray;

@end

@implementation DoraemonNSLogListViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"NSLog日志记录");
    
    //按照时间倒序排列
    self.dataArray = [[[DoraemonNSLogManager sharedInstance].dataArray reverseObjectEnumerator] allObjects];
    
  
    _searchView = [[DoraemonNSLogSearchView alloc] initWithFrame:CGRectMake(kDoraemonSizeFrom750(32), IPHONE_NAVIGATIONBAR_HEIGHT+kDoraemonSizeFrom750(32), self.view.doraemon_width-2*kDoraemonSizeFrom750(32), kDoraemonSizeFrom750(100))];
    _searchView.delegate = self;
    [self.view addSubview:_searchView];
    
    self.tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, _searchView.doraemon_bottom+kDoraemonSizeFrom750(32), self.view.doraemon_width, self.view.doraemon_height-_searchView.doraemon_bottom-kDoraemonSizeFrom750(32)) style:UITableViewStylePlain];
    self.tableView.backgroundColor = [UIColor whiteColor];
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    [self.view addSubview:self.tableView];
}

#pragma mark - UITableView Delegate
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.dataArray.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    DoraemonNSLogModel* model = [self.dataArray objectAtIndex:indexPath.row];
    return [DoraemonNSLogListCell cellHeightWith:model];
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 0.1;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return 0.1;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *identifer = @"httpcell";
    DoraemonNSLogListCell *cell = [tableView dequeueReusableCellWithIdentifier:identifer];
    if (!cell) {
        cell = [[DoraemonNSLogListCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifer];
    }
    DoraemonNSLogModel* model = [self.dataArray objectAtIndex:indexPath.row];
    [cell renderCellWithData:model];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    DoraemonNSLogModel* model = [self.dataArray objectAtIndex:indexPath.row];
    model.expand = !model.expand;
    [self.tableView reloadData];
}

#pragma mark - DoraemonNSLogSearchViewDelegate
- (void)searchViewInputChange:(NSString *)text{
    if (text.length > 0) {
        NSArray *dataArray = [[[DoraemonNSLogManager sharedInstance].dataArray reverseObjectEnumerator] allObjects];
        NSMutableArray *resultArray = [[NSMutableArray alloc] init];
        for(DoraemonNSLogModel *model in dataArray){
            NSString *content = model.content;
            if ([content containsString:text]) {
                [resultArray addObject:model];
            }
        }
        self.dataArray = [[NSArray alloc] initWithArray:resultArray];
    }else{
        self.dataArray = [[[DoraemonNSLogManager sharedInstance].dataArray reverseObjectEnumerator] allObjects];
    }

    [self.tableView reloadData];
}


@end
