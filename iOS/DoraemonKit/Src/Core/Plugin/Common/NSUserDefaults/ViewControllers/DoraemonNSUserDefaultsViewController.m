//
//  DoraemonNSUserDefaultsViewController.m
//  DoraemonKit
//
//  Created by 0xd-cc on 2019/11/26.
//

#import "DoraemonNSUserDefaultsViewController.h"
#import "DoraemonDefine.h"
#import "DoraemonNSUserDefaultsModel.h"
#import "DoraemonNSUserDefaultsEditViewController.h"

@interface DoraemonNSUserDefaultsViewController ()<UITableViewDelegate,UITableViewDataSource>
@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, strong) NSMutableArray<DoraemonNSUserDefaultsModel *> *modelList;
@end

@implementation DoraemonNSUserDefaultsViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    UIBarButtonItem *item = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemTrash target:self action:@selector(clearUserDefaults)];
    self.navigationItem.rightBarButtonItems = @[item];
    
    self.tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, IPHONE_NAVIGATIONBAR_HEIGHT, self.view.doraemon_width, self.view.doraemon_height-IPHONE_NAVIGATIONBAR_HEIGHT) style:UITableViewStylePlain];
        self.tableView.backgroundColor = [UIColor whiteColor];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    [self.view addSubview:self.tableView];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self reload];
}

- (void)reload {
    NSDictionary<NSString *, id> *dic = [[NSUserDefaults standardUserDefaults] dictionaryRepresentation];
    self.modelList = [NSMutableArray array];
    [dic enumerateKeysAndObjectsUsingBlock:^(NSString * _Nonnull key, id  _Nonnull obj, BOOL * _Nonnull stop) {
        DoraemonNSUserDefaultsModel *model = [[DoraemonNSUserDefaultsModel alloc] init];
        model.key = key;
        model.value = obj;
        [self.modelList addObject:model];
    }];
    [self.modelList sortUsingComparator:^NSComparisonResult(DoraemonNSUserDefaultsModel * _Nonnull obj1, DoraemonNSUserDefaultsModel *  _Nonnull obj2) {
        return [obj1.key.lowercaseString compare:obj2.key.lowercaseString];
    }];
    [self.tableView reloadData];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.modelList.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *identifer = @"DoraemonNSUserDefaultsViewControllerCell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:identifer];
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:identifer];
    }
    DoraemonNSUserDefaultsModel *model = self.modelList[indexPath.row];
    cell.textLabel.text = model.key;
    cell.detailTextLabel.text = [model.value description];
    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    return cell;
}

- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath {
    return UITableViewCellEditingStyleDelete;
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath{
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        DoraemonNSUserDefaultsModel *model = _modelList[indexPath.row];
        [[NSUserDefaults standardUserDefaults] setValue:nil forKey:model.key];
        [self reload];
    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    DoraemonNSUserDefaultsModel *model = self.modelList[indexPath.row];
    DoraemonNSUserDefaultsEditViewController *vc = [[DoraemonNSUserDefaultsEditViewController alloc] initWithModel:model];
    [self.navigationController pushViewController:vc animated:true];
}

- (void)clearUserDefaults {
    [DoraemonAlertUtil handleAlertActionWithVC:self text:@"clear all data?" okBlock:^{
        NSString *bundleIdentifier = NSBundle.mainBundle.bundleIdentifier;
        [[NSUserDefaults standardUserDefaults] removePersistentDomainForName:bundleIdentifier];
        [self reload];
    } cancleBlock:^{
    
    }];
}

@end
