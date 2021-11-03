//
//  DoraemonHealthInstructionsView.m
//  DoraemonKit
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
        _itemTitleArray = @[
            @"点击开始体检按钮开始本次的性能数据手机。",
            @"在每一个页面至少停留10秒钟，如果低于10秒钟的话，我们将会丢弃该页面的收集数据。",
            @"测试完毕之后，重新进入该页面，点击结束测试按钮，填写本次的测试用例名称和测试人的名字，即可上传。",
            @"打开dokit.cn平台，进入app健康体检列表，查看本次的数据报告。"
        ];
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
