//
//  DoraemonNetFlowListViewController.m
//  Aspects
//
//  Created by yixiang on 2018/4/11.
//

#import "DoraemonNetFlowListViewController.h"
#import "DoraemonNetFlowDataSource.h"
#import "DoraemonNetFlowListCell.h"
#import "DoraemonNetFlowHttpModel.h"
#import "DoraemonNetFlowDetailViewController.h"
#import "UIView+Positioning.h"

@interface DoraemonNetFlowListViewController ()<UITableViewDelegate,UITableViewDataSource,UISearchBarDelegate>

@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, strong) UISearchBar *searchBar;
@property (nonatomic, copy) NSArray *dataArray;
@property (nonatomic, copy) NSArray *allHttpModelArray;

@end

@implementation DoraemonNetFlowListViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.title = @"流量监控列表";
    
    NSArray *dataArray = [DoraemonNetFlowDataSource shareInstance].httpModelArray;
    _dataArray = [NSArray arrayWithArray:dataArray];
    _allHttpModelArray = [NSArray arrayWithArray:dataArray];
    
    _searchBar = [[UISearchBar alloc] initWithFrame:CGRectMake(0, 0, self.view.doraemon_width, 60)];
    _searchBar.placeholder = @"支持筛选";
    _searchBar.delegate = self;
    [self.view addSubview:_searchBar];
    
    CGFloat tabBarHeight = self.tabBarController.tabBar.doraemon_height;
    CGFloat navBarHeight = self.navigationController.navigationBar.doraemon_height+[[UIApplication sharedApplication] statusBarFrame].size.height;
    self.tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, _searchBar.doraemon_bottom, self.view.doraemon_width, self.view.doraemon_height-tabBarHeight-navBarHeight-_searchBar.doraemon_height) style:UITableViewStylePlain];
    self.tableView.backgroundColor = [UIColor whiteColor];
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
    DoraemonNetFlowHttpModel* model = [self.dataArray objectAtIndex:indexPath.row];
    return [DoraemonNetFlowListCell cellHeightWithModel:model];
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 0.1;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return 0.1;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *identifer = @"httpcell";
    DoraemonNetFlowListCell *cell = [tableView dequeueReusableCellWithIdentifier:identifer];
    if (!cell) {
        cell = [[DoraemonNetFlowListCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifer];
    }
    DoraemonNetFlowHttpModel* model = [self.dataArray objectAtIndex:indexPath.row];
    [cell renderCellWithModel:model];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    DoraemonNetFlowHttpModel* model = [self.dataArray objectAtIndex:indexPath.row];
    DoraemonNetFlowDetailViewController *detailVc = [[DoraemonNetFlowDetailViewController alloc] init];
    detailVc.httpModel = model;
    
    [self.navigationController pushViewController:detailVc animated:YES];
}

- (void)leftNavBackClick:(id)clickView{
    [self.tabBarController dismissViewControllerAnimated:YES completion:nil];
}

#pragma mark -- UISearchBarDelegate
- (void)searchBar:(UISearchBar *)searchBar textDidChange:(NSString *)searchText{
    NSArray *allHttpModelArray = _allHttpModelArray;
    if (searchText.length == 0) {
        _dataArray = allHttpModelArray;
        [self.tableView reloadData];
        return;
    }
    NSMutableArray *tempArray = [NSMutableArray array];
    for (DoraemonNetFlowHttpModel *httpModel in allHttpModelArray) {
        NSString *url = httpModel.url;
        if ([url containsString:searchText]) {
            [tempArray addObject:httpModel];
        }
    }
    
    _dataArray = [NSArray arrayWithArray:tempArray];
    [self.tableView reloadData];
}

@end
