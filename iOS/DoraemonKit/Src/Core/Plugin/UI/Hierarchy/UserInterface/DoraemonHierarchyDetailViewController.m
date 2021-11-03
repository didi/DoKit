//
//  DoraemonHierarchyDetailViewController.m
//  DoraemonKit
//
//  Created by lijiahuan on 2019/11/2.
//

#import "DoraemonHierarchyDetailViewController.h"
#import "UIViewController+DoraemonHierarchy.h"
#import "DoraemonHierarchyDetailTitleCell.h"
#import "DoraemonHierarchyFormatterTool.h"
#import "DoraemonHierarchyCategoryModel.h"
#import "NSObject+DoraemonHierarchy.h"
#import "DoraemonHierarchyCellModel.h"
#import "UIView+Doraemon.h"
#import "DoraemonDefine.h"

@interface DoraemonHierarchyDetailViewController ()

@property (nonatomic, strong) UISegmentedControl *segmentedControl;

@property (nonatomic, strong) NSMutableArray *objectDatas;

@property (nonatomic, strong) NSMutableArray *sizeDatas;

@end

@implementation DoraemonHierarchyDetailViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    NSAssert(self.selectView, @"SelectView can't be nil");
    
    [self setTitle:DoraemonLocalizedString(@"UI结构")];
    self.objectDatas = [[NSMutableArray alloc] init];
    self.sizeDatas = [[NSMutableArray alloc] init];
    
    UIView *headerView = ({
        UIView *view = [[UIView alloc] initWithFrame:CGRectMake(0, 0, DoraemonScreenWidth, 30 + 10 * 2)];
        view;
    });
    
    [headerView addSubview:self.segmentedControl];
    
    self.tableView.tableHeaderView = headerView;
    
    [self loadData];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveDoraemonHierarchyChangeNotification:) name:DoraemonHierarchyChangeNotificationName object:nil];
}

#pragma mark - Over write
- (BOOL)needBigTitleView {
    return YES;
}

- (void)leftNavBackClick:(id)clickView {
    [self dismissViewControllerAnimated:YES completion:nil];
}

#pragma mark - UITableViewDataSource
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [super tableView:tableView cellForRowAtIndexPath:indexPath];
    if ([cell isKindOfClass:[DoraemonHierarchyDetailTitleCell class]]) {
        DoraemonHierarchyDetailTitleCell *detailCell = (DoraemonHierarchyDetailTitleCell *)cell;
        detailCell.detailLabel.textAlignment = NSTextAlignmentLeft;
    }
    DoraemonHierarchyCellModel *model = self.dataArray[indexPath.section].items[indexPath.row];
    cell.separatorInset = model.separatorInsets;
    return cell;
}

#pragma mark - NSNotifications
- (void)didReceiveDoraemonHierarchyChangeNotification:(NSNotification *)notification {
    [self loadData];
}

#pragma mark - Event responses
- (void)segmentedControlValueChanged:(UISegmentedControl *)sender {
    [self reloadTableView];
}

#pragma mark - Primary
- (void)loadData {
    [self.objectDatas removeAllObjects];
    NSArray *models = [self.selectView doraemon_hierarchyCategoryModels];
    [self.objectDatas addObjectsFromArray:models];
    
    [self.sizeDatas removeAllObjects];
    NSArray *sizeModels = [self.selectView doraemon_sizeHierarchyCategoryModels];
    [self.sizeDatas addObjectsFromArray:sizeModels];
    
    [self reloadTableView];
}

- (void)reloadTableView {
    [self.dataArray removeAllObjects];
    if (self.segmentedControl.selectedSegmentIndex == 0) {
        [self.dataArray addObjectsFromArray:self.objectDatas];
    } else if (self.segmentedControl.selectedSegmentIndex == 1) {
        [self.dataArray addObjectsFromArray:self.sizeDatas];
    }
    [self.tableView reloadData];
}

#pragma mark - Getters and setters
- (UISegmentedControl *)segmentedControl {
    if (!_segmentedControl) {
        _segmentedControl = [[UISegmentedControl alloc] initWithItems:@[@"Object", @"Size"]];
        _segmentedControl.frame = CGRectMake(10, 10, self.view.doraemon_width - 10 * 2, 30);
        [_segmentedControl addTarget:self action:@selector(segmentedControlValueChanged:) forControlEvents:UIControlEventValueChanged];
        _segmentedControl.selectedSegmentIndex = 0;
    }
    return _segmentedControl;
}

@end
