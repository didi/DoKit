//
//  DoraemonSettingViewController.m
//  AFNetworking
//
//  Created by didi on 2020/4/24.
//

#import "DoraemonSettingViewController.h"
#import "DoraemonDefine.h"
#import "DoraemonCellButton.h"
#import "DoraemonKitManagerViewController.h"
#import <AFNetworking/AFNetworking.h>
#import "DoraemonSettingCell.h"
#import "DoraemonDefaultWebViewController.h"

@interface DoraemonSettingViewController ()<DoraemonCellButtonDelegate, UITableViewDelegate, UITableViewDataSource>

@property (nonatomic, strong) DoraemonCellButton *kitManagerBtn;
@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, strong) NSArray *dataArr;

@end

@implementation DoraemonSettingViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"更多");
    
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    [self loadData];
    [self.tableView registerClass:[DoraemonSettingCell class] forCellReuseIdentifier:@"dokit.setting.cell"];
    [self.view addSubview:self.tableView];
}

- (void)loadData {
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    manager.requestSerializer = [AFHTTPRequestSerializer serializer];// 请求
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];// 响应
    manager.responseSerializer.acceptableContentTypes = [NSSet setWithObjects:@"application/json",@"text/json",@"text/javascript",@"text/html", nil];
    
    WEAKSELF(weakSelf)
    [manager GET:@"http://star.xiaojukeji.com/config/get.node?city=-1&areaid=&name=group" parameters:nil headers:nil progress:nil success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        NSDictionary *dataDic = [NSJSONSerialization JSONObjectWithData:responseObject options:kNilOptions error:nil];
        weakSelf.dataArr = [[dataDic objectForKey:@"data"] objectForKey:@"group"];
        [weakSelf.tableView reloadData];
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        NSLog(@"failure");
    }];
}

#pragma mark -- DoraemonCellButtonDelegate
- (void)cellBtnClick:(id)sender{
    DoraemonKitManagerViewController *vc = [[DoraemonKitManagerViewController alloc] init];
    [self.navigationController pushViewController:vc animated:YES];
}

#pragma mark -- UITableViewDelegate

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return self.dataArr.count;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    NSArray *listArr = [self.dataArr[section] objectForKey:@"list"];
    return listArr.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 100;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    DoraemonSettingCell *cell = [tableView dequeueReusableCellWithIdentifier:@"dokit.setting.cell" forIndexPath:indexPath];
    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    cell.cellData = self.dataArr[indexPath.section][@"list"][indexPath.row];
    return cell;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    return [self.dataArr[section] objectForKey:@"group"];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    NSDictionary *cellData = self.dataArr[indexPath.section][@"list"][indexPath.row];
    
    if ([[cellData objectForKey:@"type"] isEqualToString:@"native"]) {
        DoraemonKitManagerViewController *vc = [[DoraemonKitManagerViewController alloc] init];
        [self.navigationController pushViewController:vc animated:YES];
    } else if ([[cellData objectForKey:@"type"] isEqualToString:@"web"]) {
        DoraemonDefaultWebViewController *webVc = [[DoraemonDefaultWebViewController alloc] init];
        webVc.h5Url = cellData[@"link"];
        [self.navigationController pushViewController:webVc animated:YES];
    }
}
#pragma mark -- Getter

- (UITableView *)tableView {
    if (!_tableView) {
        _tableView = [[UITableView alloc] initWithFrame:[UIScreen mainScreen].bounds style:UITableViewStyleGrouped];
    }
    return _tableView;
}

@end
