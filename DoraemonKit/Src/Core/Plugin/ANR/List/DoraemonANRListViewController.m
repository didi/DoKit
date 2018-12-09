//
//  DoraemonANRListViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/6/15.
//

#import "DoraemonANRListViewController.h"
#import "DoraemonANRManager.h"
#import "DoraemonANRListCell.h"
#import "DoraemonANRDetailViewController.h"
#import "DoraemonDefine.h"

@interface DoraemonANRListViewController ()<UITableViewDelegate,UITableViewDataSource>

@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, copy) NSArray *anrArray;

@end

@implementation DoraemonANRListViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"卡顿列表");
    
    self.anrArray = [DoraemonANRManager sharedInstance].anrArray;
    
    self.tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, self.view.doraemon_width, self.view.doraemon_height) style:UITableViewStylePlain];
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
    return self.anrArray.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return [DoraemonANRListCell cellHeight];
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 0.1;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return 0.1;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *identifer = @"httpcell";
    DoraemonANRListCell *cell = [tableView dequeueReusableCellWithIdentifier:identifer];
    if (!cell) {
        cell = [[DoraemonANRListCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifer];
    }
    NSDictionary* dic = [self.anrArray objectAtIndex:indexPath.row];
    [cell renderCellWithData:dic];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    NSDictionary* dic = [self.anrArray objectAtIndex:indexPath.row];
    
    DoraemonANRDetailViewController *vc = [[DoraemonANRDetailViewController alloc] init];
    vc.anrInfo = dic;
    [self.navigationController pushViewController:vc animated:YES];
}

@end
