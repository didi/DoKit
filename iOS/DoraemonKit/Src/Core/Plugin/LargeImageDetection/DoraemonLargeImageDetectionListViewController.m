//
//  DoraemonLargeImageDetectionListViewController.m
//  DoraemonKit
//
//  Created by licd on 2019/5/15.
//

#import "DoraemonLargeImageDetectionListViewController.h"
#import "DoraemonLargeImageDetectionManager.h"

@interface DoraemonLargeImageDetectionListViewController ()<UITableViewDelegate, UITableViewDataSource>
@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, strong) UITextField *textField;
@property (nonatomic, strong) UIButton *filterButton;
@end

@implementation DoraemonLargeImageDetectionListViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self initUI];
}

- (void)initUI {
    
    UILabel *label1 = [[UILabel alloc] initWithFrame: CGRectMake(10, 12, 250, 30)];
    label1.text = @"筛选条件：图片尺寸 >";
    [self.view addSubview: label1];
    
    self.filterButton = [UIButton buttonWithType: UIButtonTypeCustom];
    self.filterButton.backgroundColor = [UIColor redColor];
    self.filterButton.frame = CGRectMake(DoraemonScreenWidth - 80, 12, 50, 30);
    [self.view addSubview:self.filterButton];
    
    self.textField = [[UITextField alloc] initWithFrame: CGRectMake(CGRectGetMaxX(label1.frame), 12, 100, 30)];
    self.textField.backgroundColor = [UIColor orangeColor];
    [self.view addSubview:self.textField];
    
    CGRect tableViewFrame = CGRectMake(0, CGRectGetMaxY(self.textField.frame), DoraemonScreenWidth, DoraemonScreenHeight);
    self.tableView = [[UITableView alloc] initWithFrame:tableViewFrame style: UITableViewStylePlain];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
   
    
    [self.view addSubview:self.tableView];

}
    
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return [DoraemonLargeImageDetectionManager shareInstance].images.count;
}
    
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [[UITableViewCell alloc] init];
    cell.backgroundColor = [UIColor redColor];
    return cell;
}

@end
