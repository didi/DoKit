//
//  DoraemonHealthInstructionsView.m
//  AFNetworking
//
//  Created by didi on 2020/1/2.
//

#import "DoraemonHealthInstructionsView.h"
#import "DoraemonHealthInstructionsCell.h"
#import "DoraemonHealthManager.h"
#import "DoraemonDefine.h"

@interface DoraemonHealthInstructionsView()<UITableViewDataSource,UITableViewDelegate>

@property(nonatomic, assign) CGFloat cellHeight;
@property(nonatomic, strong) UITableView *tableView;
@property(nonatomic, copy) NSArray *itemTitleArray;
@property(nonatomic, strong) NSString *DoraemonHealthInstructionsCellID;

@end

@implementation DoraemonHealthInstructionsView

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if(self){
        CGFloat bg_y = kDoraemonSizeFrom750_Landscape(89);
        _itemTitleArray = [DoraemonHealthManager sharedInstance].cellTitle;
        _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, bg_y, self.doraemon_width, self.doraemon_height)];
        
        _tableView.delegate = self;
        _tableView.dataSource = self;
        _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        _DoraemonHealthInstructionsCellID = @"DoraemonHealthInstructionsCell";
        [self addSubview:_tableView];
        _cellHeight = kDoraemonSizeFrom750_Landscape(104);

    }
    return self;
}

- (UITableView *)tableView{
    if(!_tableView){
        _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, self.doraemon_width, self.doraemon_height)];
        _tableView.backgroundColor = [UIColor yellowColor];
        [_tableView registerClass:[DoraemonHealthInstructionsCell class] forCellReuseIdentifier:_DoraemonHealthInstructionsCellID];
    }
    return _tableView;
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return 1;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return _itemTitleArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    DoraemonHealthInstructionsCell *tableCell = [self.tableView dequeueReusableCellWithIdentifier:_DoraemonHealthInstructionsCellID];
    if(!tableCell){
       tableCell = [[DoraemonHealthInstructionsCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:_DoraemonHealthInstructionsCellID];
    }
    NSString *title = [NSString stringWithFormat:DoraemonLocalizedString(@"第%d步"),(int)indexPath.section + 1];
    _cellHeight = [tableCell renderUIWithTitle:title item:_itemTitleArray[indexPath.section]];
    
    return  tableCell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return _cellHeight;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    
}

@end
