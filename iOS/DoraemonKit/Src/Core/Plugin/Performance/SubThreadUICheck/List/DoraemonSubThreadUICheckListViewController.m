//
//  DoraemonSubThreadUICheckListViewController.m
//  DoraemonKit
//
//  Created by yixiang on 2018/9/13.
//

#import "DoraemonSubThreadUICheckListViewController.h"
#import "UIView+Doraemon.h"
#import "DoraemonSubThreadUICheckListCell.h"
#import "DoraemonSubThreadUICheckManager.h"
#import "DoraemonSubThreadUICheckDetailViewController.h"
#import "Doraemoni18NUtil.h"

@interface DoraemonSubThreadUICheckListViewController ()<UITableViewDelegate,UITableViewDataSource>

@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, copy) NSArray *checkArray;

@end

@implementation DoraemonSubThreadUICheckListViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"检测列表");
    
    self.checkArray = [DoraemonSubThreadUICheckManager sharedInstance].checkArray;
    
    CGFloat navBarHeight = self.navigationController.navigationBar.doraemon_height+[[UIApplication sharedApplication] statusBarFrame].size.height;
    self.tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, self.view.doraemon_width, self.view.doraemon_height-navBarHeight) style:UITableViewStylePlain];
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
    return self.checkArray.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return [DoraemonSubThreadUICheckListCell cellHeight];
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 0.1;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return 0.1;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *identifer = @"httpcell";
    DoraemonSubThreadUICheckListCell *cell = [tableView dequeueReusableCellWithIdentifier:identifer];
    if (!cell) {
        cell = [[DoraemonSubThreadUICheckListCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifer];
    }
    NSDictionary* dic = [self.checkArray objectAtIndex:indexPath.row];
    [cell renderCellWithData:dic];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    NSDictionary* dic = [self.checkArray objectAtIndex:indexPath.row];
    
    DoraemonSubThreadUICheckDetailViewController *vc = [[DoraemonSubThreadUICheckDetailViewController alloc] init];
    vc.checkInfo = dic;
    [self.navigationController pushViewController:vc animated:YES];
}


@end
