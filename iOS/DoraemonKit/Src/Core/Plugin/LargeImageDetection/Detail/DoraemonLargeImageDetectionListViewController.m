//
//  DoraemonLargeImageDetectionListViewController.m
//  DoraemonKit
//
//  Created by 0xd-cc on 2019/5/15.
//

#import "DoraemonLargeImageDetectionListViewController.h"
#import "DoraemonLargeImageDetectionManager.h"
#import "DoraemonImageDetectionCell.h"
#import "DoraemonDefine.h"

@interface DoraemonLargeImageDetectionListViewController ()<UITableViewDelegate, UITableViewDataSource>
@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, strong) UITextField *textField;
@property (nonatomic, strong) UIButton *filterButton;
@property (nonatomic, strong) NSArray <DoraemonResponseImageModel *> *images;
@end

@implementation DoraemonLargeImageDetectionListViewController

- (instancetype)initWithImages:(NSArray <DoraemonResponseImageModel *> *) images {
    if (self = [super init]) {
        self.images = images;
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self initUI];
}

- (void)initUI {
    
    CGRect tableViewFrame = CGRectMake(0, CGRectGetMaxY(self.textField.frame), DoraemonScreenWidth, DoraemonScreenHeight);
    self.tableView = [[UITableView alloc] initWithFrame: tableViewFrame];
    self.tableView.translatesAutoresizingMaskIntoConstraints = false;
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    self.tableView.allowsSelection = false;
    [self.tableView registerClass: [DoraemonImageDetectionCell class] forCellReuseIdentifier: NSStringFromClass([DoraemonImageDetectionCell class])];
    self.tableView.tableFooterView = [[UIView alloc] init];
    [self.view addSubview:self.tableView];
    [self.tableView reloadData];
    
}

- (void)setting {
    
}
    
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return [DoraemonLargeImageDetectionManager shareInstance].images.count;
}
    
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    DoraemonImageDetectionCell *cell = [tableView dequeueReusableCellWithIdentifier:NSStringFromClass([DoraemonImageDetectionCell class]) forIndexPath:indexPath];
    [cell setupWithModel: self.images[indexPath.item]];
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return [DoraemonImageDetectionCell cellHeight];
}

@end
