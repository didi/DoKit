//
//  DoraemonCocoaLumberjackListViewController.m
//  DoraemonKit
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
#import "DoraemonNavBarItemModel.h"

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
    
    self.title = DoraemonLocalizedString(@"日志记录");
    DoraemonNavBarItemModel *model1 = [[DoraemonNavBarItemModel alloc] initWithText:DoraemonLocalizedString(@"清除") color:[UIColor doraemon_blue] selector:@selector(clear)];
    DoraemonNavBarItemModel *model2 = [[DoraemonNavBarItemModel alloc] initWithText:DoraemonLocalizedString(@"导出") color:[UIColor doraemon_blue] selector:@selector(export)];
    [self setRightNavBarItems:@[model1,model2]];
    
    self.origArray = [NSArray arrayWithArray:[DoraemonCocoaLumberjackLogger sharedInstance].messages];
    self.dataArray = [NSArray arrayWithArray:self.origArray];
    
    _searchView = [[DoraemonNSLogSearchView alloc] initWithFrame:CGRectMake(kDoraemonSizeFrom750_Landscape(32), IPHONE_NAVIGATIONBAR_HEIGHT+kDoraemonSizeFrom750_Landscape(32), self.view.doraemon_width-2*kDoraemonSizeFrom750_Landscape(32), kDoraemonSizeFrom750_Landscape(100))];
    _searchView.delegate = self;
    [self.view addSubview:_searchView];
    
    _levelView = [[DoraemonCocoaLumberjackLevelView alloc] initWithFrame:CGRectMake(0, _searchView.doraemon_bottom+kDoraemonSizeFrom750_Landscape(32), self.view.doraemon_width, kDoraemonSizeFrom750_Landscape(68))];
    _levelView.delegate = self;
    [self.view addSubview:_levelView];
    
    self.tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, _levelView.doraemon_bottom+kDoraemonSizeFrom750_Landscape(32), self.view.doraemon_width, self.view.doraemon_height-_searchView.doraemon_bottom-kDoraemonSizeFrom750_Landscape(32)) style:UITableViewStylePlain];
//    self.tableView.backgroundColor = [UIColor whiteColor];
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    [self.view addSubview:self.tableView];
    
}

- (void)clear {
    [[DoraemonCocoaLumberjackLogger sharedInstance].messages removeAllObjects];
    self.origArray = @[];
    self.dataArray = @[];
    [self.tableView reloadData];
}

- (void)export {
    NSArray<DoraemonDDLogMessage *> *dataArray = [[DoraemonCocoaLumberjackLogger sharedInstance].messages copy];
    NSMutableString *log = [[NSMutableString alloc] init];
    for (DoraemonDDLogMessage *model in dataArray) {
        NSString *time = [NSString stringWithFormat:@"[%@]",[DoraemonUtil dateFormatNSDate:model.timestamp]];
        [log appendString:time];
        [log appendString:@" "];
        [log appendString:model.message];
        [log appendString:@"\n"];
    }
    
    [DoraemonUtil shareText:log formVC:self];
}

#pragma mark - UITableView Delegate
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.dataArray.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    DoraemonDDLogMessage* model = [self.dataArray objectAtIndex:indexPath.row];
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

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath{
    return YES;
}

- (NSString *)tableView:(UITableView *)tableView titleForDeleteConfirmationButtonForRowAtIndexPath:(NSIndexPath *)indexPath{
    return DoraemonLocalizedString(@"复制");
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    DoraemonDDLogMessage* model = [self.dataArray objectAtIndex:indexPath.row];
    NSString *content = model.message;
    if (content.length>0) {
        UIPasteboard *pboard = [UIPasteboard generalPasteboard];
        pboard.string = content;
    }
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
        flag = DDLogFlagVerbose;//16
    }else if(index==1){
        flag = DDLogFlagDebug;//8
    }else if(index==2){
        flag = DDLogFlagInfo;//4
    }else if(index==3){
        flag = DDLogFlagWarning;//2
    }else if(index==4){
        flag = DDLogFlagError;//1
    }
    
    NSArray *dataArray = self.origArray;
    NSMutableArray *resultArray = [[NSMutableArray alloc] init];
    for(DoraemonDDLogMessage *model in dataArray){
        DDLogFlag modelFlag = model.flag;
        if (modelFlag <= flag) {
            [resultArray addObject:model];
        }
    }
    self.dataArray = [[NSArray alloc] initWithArray:resultArray];
    [self.tableView reloadData];
    
}


@end
