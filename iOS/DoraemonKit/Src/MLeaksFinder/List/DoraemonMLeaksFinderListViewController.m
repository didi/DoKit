//
//  DoraemonMLeaksFinderListViewController.m
//  DoraemonKit
//
//  Created by didi on 2019/10/6.
//

#import "DoraemonMLeaksFinderListViewController.h"
#import "DoraemonMemoryLeakData.h"
#import "DoraemonDefine.h"
#import "DoraemonMLeaksFinderListCell.h"
#import "DoraemonMLeaksFinderDetailViewController.h"

@interface DoraemonMLeaksFinderListViewController ()<UITableViewDelegate,UITableViewDataSource>

@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, copy) NSArray *dataArray;

@end

@implementation DoraemonMLeaksFinderListViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"内存泄漏检测结果");
    
    _dataArray = [ NSArray arrayWithArray:[[DoraemonMemoryLeakData shareInstance] getResult]];
    self.tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, self.view.doraemon_width, self.view.doraemon_height) style:UITableViewStylePlain];
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
    return _dataArray.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return [DoraemonMLeaksFinderListCell cellHeight];
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 0.001;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return 0.001;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *identifer = @"DoraemonMLeaksFinderListCellID";
    DoraemonMLeaksFinderListCell *cell = [tableView dequeueReusableCellWithIdentifier:identifer];
    if (!cell) {
        cell = [[DoraemonMLeaksFinderListCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifer];
    }
    NSDictionary* dic = [_dataArray objectAtIndex:indexPath.row];
    [cell renderCellWithData:dic];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    NSDictionary* dic = [_dataArray objectAtIndex:indexPath.row];
    DoraemonMLeaksFinderDetailViewController *vc = [[DoraemonMLeaksFinderDetailViewController alloc] init];
    vc.info = dic;
    [self.navigationController pushViewController:vc animated:YES];
}


@end
