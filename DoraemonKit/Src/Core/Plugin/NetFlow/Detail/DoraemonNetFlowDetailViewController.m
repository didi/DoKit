//
//  DoraemonNetFlowDetailViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/4/13.
//

#import "DoraemonNetFlowDetailViewController.h"
#import "UIView+Positioning.h"
#import "DoraemonNetFlowDetailCell.h"
#import "UIColor+DoreamonKit.h"
#import "DoraemonUrlUtil.h"
#import "DoraemonUtil.h"

typedef NS_ENUM(NSUInteger, NetFlowSelectState) {
    NetFlowSelectStateForRequest = 0,
    NetFlowSelectStateForResponse
};

@interface DoraemonNetFlowDetailViewController ()<UITableViewDelegate,UITableViewDataSource>

@property (nonatomic, strong) UISegmentedControl *segment;
@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, assign) NSInteger selectedSegmentIndex;//当前选中的tab

@property (nonatomic, copy) NSArray* requestArray;
@property (nonatomic, copy) NSArray* responseArray;

@end

@implementation DoraemonNetFlowDetailViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor doraemon_colorWithHex:0xeff0f4];
    
    [self initData];
    
    self.title = @"流量监控详情";
    
    NSArray *dataArray = @[@"请求",@"响应"];
    _segment = [[UISegmentedControl alloc] initWithItems:dataArray];
    _segment.frame = CGRectMake(20, 10, self.view.doraemon_width-40, 30);
    _segment.tintColor = [UIColor orangeColor];
    [_segment setSelectedSegmentIndex:0];
    [_segment addTarget:self action:@selector(segmentChange:) forControlEvents:UIControlEventValueChanged];
    [self.view addSubview:_segment];
    
    CGFloat tabBarHeight = self.tabBarController.tabBar.doraemon_height;
    CGFloat navBarHeight = self.navigationController.navigationBar.doraemon_height+[[UIApplication sharedApplication] statusBarFrame].size.height;
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 50, self.view.doraemon_width, self.view.doraemon_height-50-tabBarHeight-navBarHeight) style:UITableViewStyleGrouped];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    _tableView.estimatedRowHeight = 0.;
    _tableView.estimatedSectionFooterHeight = 0.;
    _tableView.estimatedSectionHeaderHeight = 0.;
    [self.view addSubview:_tableView];
}

- (void)initData{
    
    NSString *requestDataSize = [NSString stringWithFormat:@"数据大小 : %@B",[DoraemonUtil formatByte:[self.httpModel.uploadFlow floatValue]]];
    NSString *method = [NSString stringWithFormat:@"Method : %@",self.httpModel.method];
    NSString *linkUrl = self.httpModel.url;
    NSDictionary<NSString *, NSString *> *allHTTPHeaderFields = self.httpModel.request.allHTTPHeaderFields;
    NSMutableString *allHTTPHeaderString = [NSMutableString string];
    for (NSString *key in allHTTPHeaderFields) {
        NSString *value = allHTTPHeaderFields[key];
        [allHTTPHeaderString appendFormat:@"%@ : %@\r\n",key,value];
    }
    if (allHTTPHeaderString.length == 0) {
        allHTTPHeaderString = @"NULL";
    }
    
    NSString *requestBody = self.httpModel.requestBody;
    if (!requestBody || requestBody.length == 0) {
        requestBody = @"NULL";
    }
    
    _requestArray = @[@{
                          @"sectionTitle":@"消息体",
                          @"dataArray":@[requestDataSize,method]
                          },
                      @{
                          @"sectionTitle":@"链接",
                          @"dataArray":@[linkUrl]
                          },
                      @{
                          @"sectionTitle":@"请求头",
                          @"dataArray":@[allHTTPHeaderString]
                          },
                      @{
                          @"sectionTitle":@"请求行",
                          @"dataArray":@[requestBody]
                          }
                      ];
    
    NSString *respanseDataSize = [NSString stringWithFormat:@"数据大小 : %@",[DoraemonUtil formatByte:[self.httpModel.downFlow floatValue]]];
    NSString *mineType = [NSString stringWithFormat:@"mineType : %@",self.httpModel.mineType];
    NSDictionary<NSString *, NSString *> *responseHeaderFields = (NSHTTPURLResponse *)self.httpModel.response;;
    NSMutableString *responseHeaderString = [NSMutableString string];
    for (NSString *key in allHTTPHeaderFields) {
        NSString *value = allHTTPHeaderFields[key];
        [responseHeaderString appendFormat:@"%@ : %@\r\n",key,value];
    }
    if (responseHeaderString.length == 0) {
        responseHeaderString = @"NULL";
    }
    NSString *responseBody = self.httpModel.responseBody;
    if (!responseBody || requestBody.length == 0) {
        responseBody = @"NULL";
    }
    
    _responseArray = @[@{
                          @"sectionTitle":@"消息体",
                          @"dataArray":@[respanseDataSize,mineType]
                          },
                      @{
                          @"sectionTitle":@"响应头",
                          @"dataArray":@[responseHeaderString]
                          },
                      @{
                          @"sectionTitle":@"响应行",
                          @"dataArray":@[responseBody]
                          }
                      ];
    
    _selectedSegmentIndex = NetFlowSelectStateForRequest;
}

-(void)segmentChange:(UISegmentedControl *)sender{
    NSInteger index = sender.selectedSegmentIndex;
    _selectedSegmentIndex = index;
    [_tableView reloadData];
}

#pragma mark - UITableView Delegate
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    NSInteger section=0;
    if (_selectedSegmentIndex == NetFlowSelectStateForRequest) {
        section = 4;
    }else{
        section = 3;
    }
    return section;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    NSInteger row = 0;
    if (_selectedSegmentIndex == NetFlowSelectStateForRequest) {
        if(section == 0){
            row = 2;
        }else if(section == 1){
            row = 1;
        }else if(section == 2){
            row = 1;
        }else{
            row = 1;
        }
    }else{
        if(section == 0){
            row = 2;
        }else if(section == 1){
            row = 1;
        }else{
            row = 1;
        }
    }
    
    return row;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    NSString *content;
    if (_selectedSegmentIndex == NetFlowSelectStateForRequest) {
        NSDictionary *itemInfo = _requestArray[indexPath.section];
        content = itemInfo[@"dataArray"][indexPath.row];
    }else{
        NSDictionary *itemInfo = _responseArray[indexPath.section];
        content = itemInfo[@"dataArray"][indexPath.row];
    }
    return [DoraemonNetFlowDetailCell cellHeightWithContent:content];
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 30;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return 0.1;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    NSString *title;
    if (_selectedSegmentIndex == NetFlowSelectStateForRequest) {
        NSDictionary *itemInfo = _requestArray[section];
        title = itemInfo[@"sectionTitle"];
    }else{
        NSDictionary *itemInfo = _responseArray[section];
        title = itemInfo[@"sectionTitle"];
    }
    
    UILabel *tipLabel = [[UILabel alloc] init];
    tipLabel.textColor = [UIColor doraemon_colorWithHex:0x666666];
    tipLabel.font = [UIFont systemFontOfSize:14];
    tipLabel.text = [NSString stringWithFormat:@"  %@",title];
    [tipLabel sizeToFit];
    tipLabel.frame = CGRectMake(0, 0, self.view.doraemon_width, 30);
    tipLabel.backgroundColor = [UIColor doraemon_colorWithHex:0xeff0f4];
    
    return tipLabel;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *identifer = @"httpcell";
    DoraemonNetFlowDetailCell *cell = [tableView dequeueReusableCellWithIdentifier:identifer];
    if (!cell) {
        cell = [[DoraemonNetFlowDetailCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifer];
    }
    NSInteger section = indexPath.section;
    NSInteger row = indexPath.row;
    
    NSString *content;
    if (_selectedSegmentIndex == NetFlowSelectStateForRequest) {
        NSDictionary *itemInfo = _requestArray[section];
        content = itemInfo[@"dataArray"][row];
    }else{
        NSDictionary *itemInfo = _responseArray[section];
        content = itemInfo[@"dataArray"][row];
    }
    
    if (section == 0) {
        if (row==0) {
            [cell renderUIWithContent:content isFirst:YES isLast:NO];
        }else if(row==1){
            [cell renderUIWithContent:content isFirst:NO isLast:YES];
        }
    }else if(section == 1){
        [cell renderUIWithContent:content isFirst:YES isLast:YES];
    }else if(section == 2){
        [cell renderUIWithContent:content isFirst:YES isLast:YES];
    }else if(section == 3){
        [cell renderUIWithContent:content isFirst:YES isLast:YES];
    }
    return cell;
}

@end
