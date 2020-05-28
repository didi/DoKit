//
//  DoraemonPageTimeProfilerListViewController.m
//  DoraemonKit
//
//  Created by Frank on 2020/5/28.
//

#import "DoraemonPageTimeProfilerListViewController.h"
/*ViewController*/

/*View&&Util*/

/*model*/

/*Service*/
#import "DoraemonPageTimeInstance.h"

@interface DoraemonPageTimeProfilerListViewController ()<UITableViewDelegate, UITableViewDataSource>
// UI
@property (nonatomic, strong) UITableView *tableView;

// Data
@property (nonatomic, strong) NSArray *arrayData;


@end

@implementation DoraemonPageTimeProfilerListViewController

#pragma mark - life cycle
- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
}

- (void)viewDidDisappear:(BOOL)animated {
    [super viewDidDisappear:animated];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"页面监控列表";
    self.arrayData = [DoraemonPageTimeInstance sharedInstance].getArrayRecord;
    [self loadUI];
}

- (void)viewDidLayoutSubviews {
}

- (void)loadUI {
    [self.view addSubview:self.tableView];
}


#ifdef DEBUG
- (void)dealloc {
    NSLog(@"%s", __func__);
}
#endif

#pragma mark - public Method

#pragma mark - private method

#pragma mark - event response

#pragma mark - UITableViewDelegate
- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    GKTZeusPageTimeRecord *record = self.arrayData[indexPath.row];
    cell.textLabel.text = [NSString stringWithFormat:@"%@ - totalTime:%f",record.className,record.loadViewTime + record.viewDidLoadTime + record.viewWillAppearTime + record.viewWillDidAppearTime + record.viewDidLayoutSubviewsTime];
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 44;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {

}

#pragma mark - UITableViewDataSource
//...(多个代理方法依次往下写)
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 1;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return self.arrayData.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    return [tableView dequeueReusableCellWithIdentifier:@"_tableViewId" forIndexPath:indexPath];
}
#pragma mark - getters and setters
- (UITableView *)tableView {
    if (!_tableView) {
        _tableView = [[UITableView alloc] initWithFrame:CGRectZero style:UITableViewStylePlain];
        _tableView.delegate = self;
        _tableView.dataSource = self;
        [_tableView registerClass:[UITableViewCell class] forCellReuseIdentifier:@"_tableViewId"];
        _tableView.sectionHeaderHeight = 8;
        _tableView.sectionFooterHeight = 0;
        _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    }
    return _tableView;
}
@end
