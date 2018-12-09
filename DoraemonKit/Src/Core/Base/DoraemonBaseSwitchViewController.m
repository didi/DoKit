//
//  DoraemonBaseSwitchViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/1/12.
//

#import "DoraemonBaseSwitchViewController.h"
#import "DoraemonDefine.h"
#import "Doraemoni18NUtil.h"
#import "UIView+Doraemon.h"

static NSString * const kDRESwitchTableViewCellIdentifier = @"DRESwitchTableViewCellIdentifier";

@interface DoraemonBaseSwitchViewController ()<UITableViewDelegate, UITableViewDataSource>

@property (nonatomic, strong) UILabel *tipLabel;

@property (nonatomic, strong) UISwitch *switchView;

@property (nonatomic, strong) UITableView *tableView;

@end

@implementation DoraemonBaseSwitchViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self initUI];
}

- (void)initUI{
    self.title = @"";
    
    [self.view addSubview:self.tipLabel];
    [self.view addSubview:self.switchView];
    [self.view addSubview:self.tableView];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self refreshTableView];
}

- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];
    
    self.tipLabel.doraemon_right = DoraemonScreenWidth/2;
    self.tipLabel.doraemon_top = 15;
    
    self.switchView.doraemon_left = self.tipLabel.doraemon_right + 15;
    self.switchView.doraemon_centerY = self.tipLabel.doraemon_centerY;
    
    CGFloat bottom = MAX(self.tipLabel.doraemon_bottom, self.switchView.doraemon_bottom);
    self.tableView.frame = CGRectMake(0, bottom + 10, DoraemonScreenWidth, self.view.doraemon_height - bottom - 10);
}

- (BOOL)switchViewOn{
    return NO;
}

- (void)switchAction:(id)sender {
    
}

- (void)refreshTableView {
    NSArray *records = [self getLocalRecords];
    NSMutableArray *dataArray = [NSMutableArray array];
    
    for (NSString *item in records) {
        [dataArray addObject:[item componentsSeparatedByString:@"."].firstObject];
    }
    
    [dataArray sortUsingComparator:^NSComparisonResult(NSString * _Nonnull obj1, NSString * _Nonnull obj2) {
        return [obj1 compare:obj2] == NSOrderedAscending ? NSOrderedDescending : NSOrderedAscending;
    }];
    
    self.dataSourceArray = dataArray;
    [self.tableView reloadData];
}

- (NSArray *)getLocalRecords {
     return @[];
}

- (void)didSelectedItemWithMessage:(NSString *)message {}

#pragma mark - UITableViewDelegate && UITableViewDataSource

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 50;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.dataSourceArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:kDRESwitchTableViewCellIdentifier];
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:kDRESwitchTableViewCellIdentifier];
    }
    
    NSString *fileName = [self.dataSourceArray objectAtIndex:indexPath.row];
    NSDate *date = [NSDate dateWithTimeIntervalSince1970:fileName.longLongValue];
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"MM-dd HH:mm:ss"];
    NSString *dateString = [formatter stringFromDate:date];
    
    cell.textLabel.text = DoraemonLocalizedString(@"开始时间");
    cell.detailTextLabel.text = dateString;
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [self didSelectedItemWithMessage:[self.dataSourceArray objectAtIndex:indexPath.row]];
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}

#pragma mark - Getter

- (UILabel *)tipLabel {
    if (!_tipLabel) {
        UILabel *tipLabel = [[UILabel alloc] init];
        tipLabel.font = [UIFont systemFontOfSize:16];
        tipLabel.textColor = [UIColor blackColor];
        tipLabel.text = DoraemonLocalizedString(@"点我右侧😘:  ");
        [tipLabel sizeToFit];
        _tipLabel = tipLabel;
    }
    return _tipLabel;
}

- (UISwitch *)switchView {
    if (!_switchView) {
        UISwitch *switchView = [[UISwitch alloc] init];
        [switchView addTarget:self action:@selector(switchAction:) forControlEvents:UIControlEventValueChanged];
        switchView.on = [self switchViewOn];
        _switchView = switchView;
    }
    return _switchView;
}

- (UITableView *)tableView {
    if (!_tableView) {
        UITableView *tableView = [[UITableView alloc] initWithFrame:CGRectZero style:UITableViewStylePlain];
        _tableView = tableView;
        _tableView.delegate = self;
        _tableView.dataSource = self;
    }
    return _tableView;
}

- (NSMutableArray *)dataSourceArray {
    if (!_dataSourceArray) {
        _dataSourceArray = [NSMutableArray array];
    }
    return _dataSourceArray;
}

@end
