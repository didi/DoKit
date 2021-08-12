//
//  DoraemonMockDropdownListView.m
//  DoraemonKit
//
//  Created by didi on 2019/10/24.
//

#import "DoraemonMockFilterListView.h"
#import "DoraemonMockFilterTableCell.h"
#import "DoraemonDefine.h"

@interface DoraemonMockFilterListView()<UITableViewDataSource,UITableViewDelegate>

@property(nonatomic, strong) UIView *bgroundView;
@property(nonatomic, strong) UITableView *tableView;
@property(nonatomic, assign) CGFloat cellHeight;
@property(nonatomic, copy) NSArray *itemTitleArray;
@property(nonatomic, strong) NSString *DoraemonMockFilterTableCellID;

@end

@implementation DoraemonMockFilterListView

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if(self){
        _bgroundView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.doraemon_width, self.doraemon_height)];
        _bgroundView.alpha = 0;
        _bgroundView.backgroundColor = [UIColor lightGrayColor];
        [self addSubview:_bgroundView];
        UITapGestureRecognizer *todo = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tap)];
        [_bgroundView addGestureRecognizer:todo];
        _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, self.doraemon_width, 0)];
        [self addSubview:_tableView];
        _tableView.delegate = self;
        _tableView.dataSource = self;
        _selectedIndex = 0;
        _cellHeight = kDoraemonSizeFrom750_Landscape(104);
        _DoraemonMockFilterTableCellID = @"DoraemonMockFilterTableCell";
    }
    return self;
}

- (UITableView *)tableView{
    if(!_tableView){
        _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, self.doraemon_width, 0)];
        _tableView.backgroundColor = [UIColor whiteColor];
        [_tableView registerClass:[DoraemonMockFilterTableCell class] forCellReuseIdentifier:_DoraemonMockFilterTableCellID];
    }
    return _tableView;
}

- (void)showList:(NSArray *)itemArray{
    _itemTitleArray = itemArray;
    [_tableView reloadData];
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationDuration:0.25f];
    self.tableView.frame = CGRectMake(0, 0, self.doraemon_width, itemArray.count * _cellHeight);
    self.bgroundView.alpha = 0.5f;
    [UIView commitAnimations];
    self.userInteractionEnabled = YES;
}

- (void)closeList{
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationDuration:0.25f];
    self.bgroundView.alpha = 0;
    self.tableView.frame = CGRectMake(0, 0, self.doraemon_width, 0);
    [UIView commitAnimations];
    self.userInteractionEnabled = NO;
}

- (void)tap{
    if (_delegate && [_delegate respondsToSelector:@selector(filterBgroundClick)]) {
        [_delegate filterBgroundClick];
    }
}

#pragma mark - UITableView Delegate

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return 1;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return _itemTitleArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    DoraemonMockFilterTableCell *tableCell = [self.tableView dequeueReusableCellWithIdentifier:_DoraemonMockFilterTableCellID];
    if(!tableCell){
       tableCell = [[DoraemonMockFilterTableCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:_DoraemonMockFilterTableCellID];
    }
    [tableCell renderUIWithTitle:_itemTitleArray[indexPath.section]];
    [tableCell selectedColor:NO];
    if(_selectedIndex == indexPath.section){
        [tableCell selectedColor:YES];
    }
    return  tableCell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return _cellHeight;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    _selectedIndex = indexPath.section;
    [_tableView reloadData];
    if (_delegate && [_delegate respondsToSelector:@selector(filterSelectedClick)]) {
        [_delegate filterSelectedClick];
    }
}


@end
