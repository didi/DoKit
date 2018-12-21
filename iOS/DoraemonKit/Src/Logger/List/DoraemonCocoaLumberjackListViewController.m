//
//  DoraemonCocoaLumberjackListViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/12/4.
//

#import "DoraemonCocoaLumberjackListViewController.h"
#import "DoraemonNSLogSearchView.h"
#import "DoraemonDefine.h"
#import "DoraemonCocoaLumberjackLevelView.h"
#import "DoraemonCocoaLumberjackListCell.h"
#import "DoraemonDDLogMessage.h"
#import "DoraemonCocoaLumberjackLogger.h"

@interface DoraemonCocoaLumberjackListViewController ()<DoraemonNSLogSearchViewDelegate,DoraemonCocoaLumberjackLevelViewDelegate,UITableViewDelegate,UITableViewDataSource>

@property (nonatomic, strong) DoraemonNSLogSearchView *searchView;
@property (nonatomic, strong) DoraemonCocoaLumberjackLevelView *levelView;
@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, copy) NSArray *dataArray;
@property (nonatomic, copy) NSArray *origArray;

@end

@implementation DoraemonCocoaLumberjackListViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.title = @"CocoaLumberjack日志记录";
    
    self.origArray = [NSArray arrayWithArray:[DoraemonCocoaLumberjackLogger sharedInstance].messages];
    self.dataArray = [NSArray arrayWithArray:self.origArray];
    
    _searchView = [[DoraemonNSLogSearchView alloc] initWithFrame:CGRectMake(kDoraemonSizeFrom750(32), IPHONE_NAVIGATIONBAR_HEIGHT+kDoraemonSizeFrom750(32), self.view.doraemon_width-2*kDoraemonSizeFrom750(32), kDoraemonSizeFrom750(100))];
    _searchView.delegate = self;
    [self.view addSubview:_searchView];
    
    _levelView = [[DoraemonCocoaLumberjackLevelView alloc] initWithFrame:CGRectMake(0, _searchView.doraemon_bottom+kDoraemonSizeFrom750(32), self.view.doraemon_width, kDoraemonSizeFrom750(68))];
    _levelView.delegate = self;
    [self.view addSubview:_levelView];
    
    self.tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, _levelView.doraemon_bottom+kDoraemonSizeFrom750(32), self.view.doraemon_width, self.view.doraemon_height-_searchView.doraemon_bottom-kDoraemonSizeFrom750(32)) style:UITableViewStylePlain];
    self.tableView.backgroundColor = [UIColor whiteColor];
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    [self.view addSubview:self.tableView];
    
}

#pragma mark - UITableView Delegate
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.dataArray.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    DoraemonCocoaLumberjackListCell* model = [self.dataArray objectAtIndex:indexPath.row];
    return [DoraemonCocoaLumberjackListCell cellHeightWith:model];
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 0.1;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return 0.1;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *identifer = @"httpcell";
    DoraemonCocoaLumberjackListCell *cell = [tableView dequeueReusableCellWithIdentifier:identifer];
    if (!cell) {
        cell = [[DoraemonCocoaLumberjackListCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifer];
    }
    DoraemonDDLogMessage* model = [self.dataArray objectAtIndex:indexPath.row];
    [cell renderCellWithData:model];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    DoraemonDDLogMessage* model = [self.dataArray objectAtIndex:indexPath.row];
    model.expand = !model.expand;
    [self.tableView reloadData];
}

#pragma mark - DoraemonNSLogSearchViewDelegate
- (void)searchViewInputChange:(NSString *)text{
    if (text.length > 0) {
        NSArray *dataArray = self.origArray;
        NSMutableArray *resultArray = [[NSMutableArray alloc] init];
        for(DoraemonDDLogMessage *model in dataArray){
            NSString *content = model.message;
            if ([content containsString:text]) {
                [resultArray addObject:model];
            }
        }
        self.dataArray = [[NSArray alloc] initWithArray:resultArray];
    }else{
        self.dataArray = [[NSArray alloc] initWithArray:self.origArray];
    }
    
    [self.tableView reloadData];
}

#pragma mark - DoraemonCocoaLumberjackLevelViewDelegate
- (void)segmentSelected:(NSInteger)index{
    NSLog(@"%zi",DDLogFlagError);
    NSLog(@"%zi",DDLogFlagWarning);
    NSLog(@"%zi",DDLogFlagInfo);
    NSLog(@"%zi",DDLogFlagDebug);
    NSLog(@"%zi",DDLogFlagVerbose);
    DDLogFlag flag = DDLogFlagVerbose;
    if (index==0) {
        flag = DDLogFlagVerbose;
    }else if(index==1){
        flag = DDLogFlagDebug;
    }else if(index==2){
        flag = DDLogFlagInfo;
    }else if(index==3){
        flag = DDLogFlagWarning;
    }else if(index==4){
        flag = DDLogFlagError;
    }
    
    NSArray *dataArray = self.origArray;
    NSMutableArray *resultArray = [[NSMutableArray alloc] init];
    for(DoraemonDDLogMessage *model in dataArray){
        DDLogFlag *modelFlag = model.flag;
        if (modelFlag <= flag) {
            [resultArray addObject:model];
        }
        self.dataArray = [[NSArray alloc] initWithArray:resultArray];
    }
    [self.tableView reloadData];
    
}


@end
