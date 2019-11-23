//
//  DoraemonNSUserDefaultsViewController.m
//  DoraemonKit
//
//  Created by 0xd-cc on 2019/11/26.
//

#import "DoraemonNSUserDefaultsViewController.h"
#import "DoraemonDefine.h"
#import "DoraemonNSUserDefaultsCell.h"
#import "DoraemonNSUserDefaultsModel.h"
#import "DoraemonNSUserDefaultsEditViewController.h"

@interface DoraemonNSUserDefaultsViewController ()<UITableViewDelegate,UITableViewDataSource>
@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, strong) NSMutableArray<DoraemonNSUserDefaultsModel *> *modelList;
@end

@implementation DoraemonNSUserDefaultsViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    UIBarButtonItem *item = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemTrash target:nil action:nil];
    self.navigationItem.rightBarButtonItems = @[item];
    
    self.tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, IPHONE_NAVIGATIONBAR_HEIGHT, self.view.doraemon_width, self.view.doraemon_height-IPHONE_NAVIGATIONBAR_HEIGHT) style:UITableViewStylePlain];
        self.tableView.backgroundColor = [UIColor whiteColor];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    [self.view addSubview:self.tableView];
    
    NSDictionary<NSString *, id> *dic = [[NSUserDefaults standardUserDefaults] dictionaryRepresentation];
    self.modelList = [NSMutableArray array];
    [dic enumerateKeysAndObjectsUsingBlock:^(NSString * _Nonnull key, id  _Nonnull obj, BOOL * _Nonnull stop) {
        DoraemonNSUserDefaultsModel *model = [[DoraemonNSUserDefaultsModel alloc] init];
        model.key = key;
        model.value = obj;
        [self.modelList addObject:model];
    }];
    [self.tableView reloadData];
}

- (NSDictionary<NSString *, id> *)dictionaryRepresentation {
    return [[NSUserDefaults standardUserDefaults] dictionaryRepresentation];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.modelList.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *identifer = @"DoraemonNSUserDefaultsViewControllerCell";
    DoraemonNSUserDefaultsCell *cell = [tableView dequeueReusableCellWithIdentifier:identifer];
    if (!cell) {
        cell = [[DoraemonNSUserDefaultsCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:identifer];
    }
    DoraemonNSUserDefaultsModel *model = self.modelList[indexPath.row];
    cell.textLabel.text = model.key;
    cell.detailTextLabel.text = [model.value description];
    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    DoraemonNSUserDefaultsModel *model = self.modelList[indexPath.row];
    DoraemonNSUserDefaultsEditViewController *vc = [[DoraemonNSUserDefaultsEditViewController alloc] initWithModel:model];
    [self.navigationController pushViewController:vc animated:true];
}

@end
