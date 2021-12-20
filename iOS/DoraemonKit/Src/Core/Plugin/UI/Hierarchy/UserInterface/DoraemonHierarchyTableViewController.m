//
//  DoraemonHierarchyTableViewController.m
//  DoraemonKit
//
//  Created by lijiahuan on 2019/11/2.
//

#import "DoraemonHierarchyTableViewController.h"
#import "DoraemonHierarchyCategoryModel.h"
#import "DoraemonHierarchySelectorCell.h"
#import "DoraemonHierarchySwitchCell.h"
#import "DoraemonHierarchyHeaderView.h"
#import "DoraemonHierarchyCellModel.h"
#import "DoraemonDefine.h"

@interface DoraemonHierarchyTableViewController ()

@property (nonatomic, strong) UITableView *tableView;

@property (nonatomic, assign) UITableViewStyle style;

@property (nonatomic, strong) NSMutableArray <DoraemonHierarchyCategoryModel *>*dataArray;

@end

@implementation DoraemonHierarchyTableViewController

#pragma mark - Life cycle
- (instancetype)init
{
    return [self initWithStyle:UITableViewStyleGrouped];
}

- (instancetype)initWithStyle:(UITableViewStyle)style {
    if (self = [super init]) {
        _style = style;
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self.view addSubview:self.tableView];
}

#pragma mark - Over write
- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];
    self.tableView.frame = CGRectMake(0, self.bigTitleView.doraemon_bottom, self.view.doraemon_width, self.view.doraemon_height - self.bigTitleView.doraemon_bottom);
}

#pragma mark - UITableViewDelegate, UITableViewDataSource
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return self.dataArray.count;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.dataArray[section].items.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    DoraemonHierarchyCellModel *model = self.dataArray[indexPath.section].items[indexPath.row];
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:model.cellClass];
    [cell setValue:model forKey:@"model"];
    return cell;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    DoraemonHierarchyCategoryModel *model = self.dataArray[section];
    if (!model.title) {
        return nil;
    }
    DoraemonHierarchyHeaderView *view = [[DoraemonHierarchyHeaderView alloc] initWithFrame:CGRectMake(0, 0, DoraemonScreenWidth, 40)];
    view.titleLabel.text = model.title;
    return view;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    DoraemonHierarchyCategoryModel *model = self.dataArray[section];
    if (!model.title) {
        return CGFLOAT_MIN;
    }
    return 40;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return CGFLOAT_MIN;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    DoraemonHierarchyCellModel *model = self.dataArray[indexPath.section].items[indexPath.row];
    if (model.block) {
        model.block();
    }
}

#pragma mark - Getters and setters
- (UITableView *)tableView {
    if (!_tableView) {
        _tableView = [[UITableView alloc] initWithFrame:CGRectZero style:UITableViewStyleGrouped];
        _tableView.delegate = self;
        _tableView.dataSource = self;
        _tableView.bounces = NO;
        _tableView.separatorInset = UIEdgeInsetsMake(0, 10, 0, 0);
        _tableView.estimatedRowHeight = UITableViewAutomaticDimension;
        _tableView.estimatedSectionFooterHeight = 0;
        _tableView.estimatedSectionHeaderHeight = 0;
        [_tableView registerClass:[DoraemonHierarchySwitchCell class] forCellReuseIdentifier:NSStringFromClass([DoraemonHierarchySwitchCell class])];
        [_tableView registerClass:[DoraemonHierarchyDetailTitleCell class] forCellReuseIdentifier:NSStringFromClass([DoraemonHierarchyDetailTitleCell class])];
        [_tableView registerClass:[DoraemonHierarchySelectorCell class] forCellReuseIdentifier:NSStringFromClass([DoraemonHierarchySelectorCell class])];
        if (@available(iOS 11.0, *)) {
            _tableView.contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentAutomatic;
        }
    }
    return _tableView;
}

- (NSMutableArray<DoraemonHierarchyCategoryModel *> *)dataArray {
    if (!_dataArray) {
        _dataArray = [[NSMutableArray alloc] init];
    }
    return _dataArray;
}

@end
