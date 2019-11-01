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
#import "DoraemonDefine.h"
#import "DoraemonNSLogSearchView.h"

@interface DoraemonNetFlowListViewController ()<UITableViewDelegate,UITableViewDataSource,DoraemonNSLogSearchViewDelegate>

@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, copy) NSArray *dataArray;
@property (nonatomic, copy) NSArray *allHttpModelArray;
@property (nonatomic, strong) DoraemonNSLogSearchView *searchView;

@end

@implementation DoraemonNetFlowListViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.title = DoraemonLocalizedString(@"流量监控列表");
    
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
    if (@available(iOS 13.0, *)) {
        self.view.backgroundColor = [UIColor colorWithDynamicProvider:^UIColor * _Nonnull(UITraitCollection * _Nonnull traitCollection) {
            if (traitCollection.userInterfaceStyle == UIUserInterfaceStyleDark) {
                return [UIColor systemBackgroundColor];
            } else {
                return [UIColor whiteColor];
            }
        }];
    } else {
#endif
        self.view.backgroundColor = [UIColor whiteColor];
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
    }
#endif
    
    NSArray *dataArray = [DoraemonNetFlowDataSource shareInstance].httpModelArray;
    _dataArray = [NSArray arrayWithArray:dataArray];
    _allHttpModelArray = [NSArray arrayWithArray:dataArray];

    _searchView = [[DoraemonNSLogSearchView alloc] initWithFrame:CGRectMake(kDoraemonSizeFrom750_Landscape(32), IPHONE_NAVIGATIONBAR_HEIGHT+kDoraemonSizeFrom750_Landscape(32), self.view.doraemon_width-2*kDoraemonSizeFrom750_Landscape(32), kDoraemonSizeFrom750_Landscape(100))];
    _searchView.delegate = self;
    [self.view addSubview:_searchView];
    

    self.tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, _searchView.doraemon_bottom+kDoraemonSizeFrom750_Landscape(30), self.view.doraemon_width, self.view.doraemon_height-_searchView.doraemon_bottom-kDoraemonSizeFrom750_Landscape(30)) style:UITableViewStylePlain];
//    self.tableView.backgroundColor = [UIColor whiteColor];
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

#pragma mark - DoraemonNSLogSearchViewDelegate
- (void)searchViewInputChange:(NSString *)text{
    NSArray *allHttpModelArray = _allHttpModelArray;
    if (text.length == 0) {
        _dataArray = allHttpModelArray;
        [self.tableView reloadData];
        return;
    }
    NSMutableArray *tempArray = [NSMutableArray array];
    for (DoraemonNetFlowHttpModel *httpModel in allHttpModelArray) {
        NSString *url = httpModel.url;
        if ([url containsString:text]) {
            [tempArray addObject:httpModel];
        }
    }
    
    _dataArray = [NSArray arrayWithArray:tempArray];
    [self.tableView reloadData];
}

@end
