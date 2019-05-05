//
//  DoraemonMethodUseTimeListViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2019/1/18.
//

#import "DoraemonMethodUseTimeListViewController.h"
#import <DoraemonLoadAnalyze/DoraemonLoadAnalyze.h>
#import "DoraemonDefine.h"
#import "DoraemonMethodUseTimeListCell.h"

@interface DoraemonMethodUseTimeListViewController ()

@property (nonatomic, copy) NSMutableArray *loadModelArray;
@property (nonatomic, strong) UITableView *tableView;

@end

@implementation DoraemonMethodUseTimeListViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"Load耗时检测记录";
    
    _loadModelArray = [NSMutableArray arrayWithArray:dlaLoadModels];
    [_loadModelArray sortUsingComparator:^NSComparisonResult(id  _Nonnull obj1, id  _Nonnull obj2) {
        CGFloat costA = [obj1[@"cost"] floatValue];
        CGFloat costB = [obj2[@"cost"] floatValue];
        if (costA < costB) {
            return NSOrderedDescending;
        }else{
            return NSOrderedAscending;
        }
    }];
    CGFloat allCost = 0.f;
    if(_loadModelArray && _loadModelArray.count>0){
        for (NSDictionary *dic in _loadModelArray) {
            CGFloat cost = [dic[@"cost"] floatValue];
            allCost += cost;
        }
        NSDictionary *allDic = @{
                                 @"name":@"总共耗时",
                                 @"cost":@(allCost)
                                 };
        [_loadModelArray insertObject:allDic atIndex:0];
    }
    
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
    return _loadModelArray.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return [DoraemonMethodUseTimeListCell cellHeight];
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 0.1;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return 0.1;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *identifer = @"httpcell";
    DoraemonMethodUseTimeListCell *cell = [tableView dequeueReusableCellWithIdentifier:identifer];
    if (!cell) {
        cell = [[DoraemonMethodUseTimeListCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifer];
    }
    NSDictionary* dic = [_loadModelArray objectAtIndex:indexPath.row];
    [cell renderCellWithData:dic];
    return cell;
}

@end
