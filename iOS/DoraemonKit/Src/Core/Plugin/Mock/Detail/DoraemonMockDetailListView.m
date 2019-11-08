//
//  DoraemonMockDropdownDetailView.m
//  AFNetworking
//
//  Created by didi on 2019/10/24.
//
#import "DoraemonMockDetailListView.h"
#import "DoraemonDefine.h"
#import "DoraemonMockDetailCell.h"
#import "DoraemonMockDetailModel.h"

@interface DoraemonMockDetailListView()<UITableViewDelegate,UITableViewDataSource>

@property (nonatomic, copy) NSArray *dataArray;
@property (nonatomic, strong) UITableView *tableView;

@end

@implementation DoraemonMockDetailListView

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if(self){
        _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, self.doraemon_width, self.doraemon_height)];
        self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        self.tableView.delegate = self;
        self.tableView.dataSource = self;
        [self addSubview:_tableView];
        
        DoraemonMockDetailModel *model = [[DoraemonMockDetailModel alloc] init];
        DoraemonMockDetailModel *model2 = [[DoraemonMockDetailModel alloc] init];
        DoraemonMockDetailModel *model3 = [[DoraemonMockDetailModel alloc] init];
        _dataArray = @[
            model,model2,model3
        ];
    }
    return self;
}

#pragma mark - UITableView Delegate
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.dataArray.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    DoraemonMockDetailModel* model = [self.dataArray objectAtIndex:indexPath.row];
    return [DoraemonMockDetailCell cellHeightWith:model];
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 0.1;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return 0.1;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *identifer = @"detailcell";
    DoraemonMockDetailCell *cell = [tableView dequeueReusableCellWithIdentifier:identifer];
    if (!cell) {
        cell = [[DoraemonMockDetailCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifer];
    }
    DoraemonMockDetailModel* model = [self.dataArray objectAtIndex:indexPath.row];
    [cell renderCellWithData:model index:indexPath.row];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    DoraemonMockDetailModel* model = [self.dataArray objectAtIndex:indexPath.row];
    model.expand = !model.expand;
    [self.tableView reloadData];
}

@end
