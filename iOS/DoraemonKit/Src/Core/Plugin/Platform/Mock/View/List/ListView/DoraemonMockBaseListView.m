//
//  DoraemonMockBaseListView.m
//  DoraemonKit
//
//  Created by didi on 2019/11/15.
//

#import "DoraemonMockBaseListView.h"

@interface DoraemonMockBaseListView()<UITableViewDelegate,UITableViewDataSource>

@end

@implementation DoraemonMockBaseListView

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if(self){
        _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, self.doraemon_width, self.doraemon_height)];
        self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        self.tableView.delegate = self;
        self.tableView.dataSource = self;
        [self addSubview:_tableView];        
    }
    return self;
}

- (void)reloadUI{
    [self.tableView reloadData];
}

#pragma mark - UITableView Delegate
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.dataArray.count;
}



- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return CGFLOAT_MIN;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return CGFLOAT_MIN;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return CGFLOAT_MIN;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    return [[UITableViewCell alloc] init];
}


- (void)cellExpandClick{
    [self.tableView reloadData];
}

- (void)sceneBtnClick{
    [self.tableView reloadData];
}

- (void)cellSwitchClick{
    [self.tableView reloadData];
}

@end
