//
//  DoraemonNetFlowDetailViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/4/13.
//

#import "DoraemonNetFlowDetailViewController.h"
#import "UIView+Doraemon.h"
#import "DoraemonNetFlowDetailCell.h"
#import "UIColor+Doraemon.h"
#import "DoraemonUrlUtil.h"
#import "DoraemonUtil.h"
#import "DoraemonDefine.h"
#import "DoraemonNetFlowDetailSegment.h"

typedef NS_ENUM(NSUInteger, NetFlowSelectState) {
    NetFlowSelectStateForRequest = 0,
    NetFlowSelectStateForResponse
};

@interface DoraemonNetFlowDetailViewController ()<UITableViewDelegate,UITableViewDataSource,DoraemonNetFlowDetailSegmentDelegate>

@property (nonatomic, strong) DoraemonNetFlowDetailSegment *segmentView;
@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, assign) NSInteger selectedSegmentIndex;//当前选中的tab

@property (nonatomic, copy) NSArray* requestArray;
@property (nonatomic, copy) NSArray* responseArray;

@end

@implementation DoraemonNetFlowDetailViewController

- (void)viewDidLoad {
    [super viewDidLoad];
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
    if (@available(iOS 13.0, *)) {
        self.view.backgroundColor = [UIColor colorWithDynamicProvider:^UIColor * _Nonnull(UITraitCollection * _Nonnull traitCollection) {
            if (traitCollection.userInterfaceStyle == UIUserInterfaceStyleDark) {
                return [UIColor systemBackgroundColor];
            } else {
                return [UIColor doraemon_colorWithHex:0xeff0f4];
            }
        }];
    } else {
#endif
        self.view.backgroundColor = [UIColor doraemon_colorWithHex:0xeff0f4];
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
    }
#endif
    
    [self initData];
    
    self.title = DoraemonLocalizedString(@"流量监控详情");
    
    _segmentView = [[DoraemonNetFlowDetailSegment alloc] initWithFrame:CGRectMake(0, IPHONE_NAVIGATIONBAR_HEIGHT, self.view.doraemon_width, kDoraemonSizeFrom750_Landscape(88))];
    _segmentView.delegate = self;
    [self.view addSubview:_segmentView];
    
    CGFloat tabBarHeight = self.tabBarController.tabBar.doraemon_height;
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, _segmentView.doraemon_bottom, self.view.doraemon_width, self.view.doraemon_height-tabBarHeight-_segmentView.doraemon_bottom) style:UITableViewStyleGrouped];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    _tableView.estimatedRowHeight = 0.;
    _tableView.estimatedSectionFooterHeight = 0.;
    _tableView.estimatedSectionHeaderHeight = 0.;
    [self.view addSubview:_tableView];
}

- (void)initData{
    
    NSString *requestDataSize = [NSString stringWithFormat:DoraemonLocalizedString(@"数据大小 : %@B"),[DoraemonUtil formatByte:[self.httpModel.uploadFlow floatValue]]];
    NSString *method = [NSString stringWithFormat:@"Method : %@",self.httpModel.method];
    NSString *linkUrl = self.httpModel.url;
    NSDictionary<NSString *, NSString *> *allHTTPHeaderFields = self.httpModel.request.allHTTPHeaderFields;
    NSMutableString *allHTTPHeaderString = [NSMutableString string];
    for (NSString *key in allHTTPHeaderFields) {
        NSString *value = allHTTPHeaderFields[key];
        [allHTTPHeaderString appendFormat:@"%@ : %@\r\n",key,value];
    }
    if (allHTTPHeaderString.length == 0) {
        allHTTPHeaderString = [NSMutableString stringWithFormat:@"NULL"];
    }
    
    NSString *requestBody = self.httpModel.requestBody;
    if (!requestBody || requestBody.length == 0) {
        requestBody = @"NULL";
    }
    
    _requestArray = @[@{
                          @"sectionTitle":DoraemonLocalizedString(@"消息体"),
                          @"dataArray":@[requestDataSize,method]
                          },
                      @{
                          @"sectionTitle":DoraemonLocalizedString(@"链接"),
                          @"dataArray":@[linkUrl]
                          },
                      @{
                          @"sectionTitle":DoraemonLocalizedString(@"请求头"),
                          @"dataArray":@[allHTTPHeaderString]
                          },
                      @{
                          @"sectionTitle":DoraemonLocalizedString(@"请求行"),
                          @"dataArray":@[requestBody]
                          }
                      ];
    
    NSString *respanseDataSize = [NSString stringWithFormat:DoraemonLocalizedString(@"数据大小 : %@"),[DoraemonUtil formatByte:[self.httpModel.downFlow floatValue]]];
    NSString *mineType = [NSString stringWithFormat:@"mineType : %@",self.httpModel.mineType];
    NSMutableString *responseHeaderString = [NSMutableString string];
    for (NSString *key in allHTTPHeaderFields) {
        NSString *value = allHTTPHeaderFields[key];
        [responseHeaderString appendFormat:@"%@ : %@\r\n",key,value];
    }
    if (responseHeaderString.length == 0) {
        responseHeaderString = [NSMutableString stringWithFormat:@"NULL"];
    }
    NSString *responseBody = self.httpModel.responseBody;
    if (!responseBody || requestBody.length == 0) {
        responseBody = @"NULL";
    }
    
    _responseArray = @[@{
                          @"sectionTitle":DoraemonLocalizedString(@"消息体"),
                          @"dataArray":@[respanseDataSize,mineType]
                          },
                      @{
                          @"sectionTitle":DoraemonLocalizedString(@"响应头"),
                          @"dataArray":@[responseHeaderString]
                          },
                      @{
                          @"sectionTitle":DoraemonLocalizedString(@"响应行"),
                          @"dataArray":@[responseBody]
                          }
                      ];
    
    _selectedSegmentIndex = NetFlowSelectStateForRequest;
}

#pragma mark - DoraemonNetFlowDetailSegmentDelegate
- (void)segmentClick:(NSInteger)index{
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
    return kDoraemonSizeFrom750_Landscape(100);
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
    
    UIView *view = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.view.doraemon_width, kDoraemonSizeFrom750_Landscape(100))];
    
    UILabel *tipLabel = [[UILabel alloc] init];
    tipLabel.textColor = [UIColor doraemon_colorWithHex:0x337CC4];
    tipLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(32)];
    tipLabel.text = title;
    tipLabel.frame = CGRectMake(kDoraemonSizeFrom750_Landscape(32), 0, self.view.doraemon_width-kDoraemonSizeFrom750_Landscape(32), kDoraemonSizeFrom750_Landscape(100));
    [view addSubview:tipLabel];
    //tipLabel.backgroundColor = [UIColor doraemon_colorWithHex:0xeff0f4];
    
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
    if (@available(iOS 13.0, *)) {
        view.backgroundColor =  [UIColor colorWithDynamicProvider:^UIColor * _Nonnull(UITraitCollection * _Nonnull traitCollection) {
            if (traitCollection.userInterfaceStyle == UIUserInterfaceStyleDark) {
                return [UIColor systemBackgroundColor];
            } else {
                return [UIColor whiteColor];
            }
        }];
    }
#endif
    return view;
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

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath{
    return YES;
}

- (NSString *)tableView:(UITableView *)tableView titleForDeleteConfirmationButtonForRowAtIndexPath:(NSIndexPath *)indexPath{
    return DoraemonLocalizedString(@"复制");
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath{
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
    UIPasteboard *pboard = [UIPasteboard generalPasteboard];
    pboard.string = content;
}
@end
