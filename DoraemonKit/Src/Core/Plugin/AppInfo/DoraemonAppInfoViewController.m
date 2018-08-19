//
//  DoraemonAppInfoViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/4/13.
//

#import "DoraemonAppInfoViewController.h"
#import "DoraemonAppInfoCell.h"
#import "DoraemonDefine.h"
#import "DoraemonAppInfoUtil.h"

@interface DoraemonAppInfoViewController ()<UITableViewDelegate,UITableViewDataSource>

@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, strong) NSArray *dataArray;

@end

@implementation DoraemonAppInfoViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"App基本信息";
    
    //获取手机型号
    NSString *iphoneType = [DoraemonAppInfoUtil iphoneType];
    //获取手机系统版本
    NSString *phoneVersion = [[UIDevice currentDevice] systemVersion];
    
    //获取bundle id
    NSString *bundleId = [[NSBundle mainBundle] bundleIdentifier];
    
    //获取App版本号
    NSString *bundleVersionCode = [[[NSBundle mainBundle]infoDictionary] objectForKey:@"CFBundleVersion"];
    
    //获取App版本Code
    NSString *bundleVersion = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
    
    //获取手机是否有地理位置权限
    NSString *locationAuthority = [DoraemonAppInfoUtil locationAuthority];
    
    //获取网络权限
    NSString *netAuthority = [DoraemonAppInfoUtil netAuthority];//暂时没有办法获取
    
    //获取push权限
    NSString *pushAuthority = [DoraemonAppInfoUtil pushAuthority];
    
    //获取拍照权限
    NSString *takePhotoAuthority = [DoraemonAppInfoUtil takePhotoAuthority];
    
    //获取麦克风权限
    NSString *audioAuthority = [DoraemonAppInfoUtil audioAuthority];
    
    NSArray *dataArray = @[
                            @{
                               @"title":@"手机信息",
                               @"array":@[@{
                                            @"title":@"手机型号",
                                            @"value":iphoneType
                                            },
                                        @{
                                            @"title":@"系统版本",
                                            @"value":phoneVersion
                                            }
                                     ]
                               },
                            @{
                                @"title":@"App信息",
                                @"array":@[@{
                                               @"title":@"Bundle ID",
                                               @"value":bundleId
                                               },
                                           @{
                                               @"title":@"version",
                                               @"value":bundleVersion
                                               },
                                           @{
                                               @"title":@"versionCode",
                                               @"value":bundleVersionCode
                                               }
                                           ]
                                },
                            @{
                                @"title":@"权限信息",
                                @"array":@[@{
                                               @"title":@"地理位置权限",
                                               @"value":locationAuthority
                                               },
//                                           @{
//                                               @"title":@"网络权限",
//                                               @"value":netAuthority
//                                               },
                                           @{
                                               @"title":@"推送权限",
                                               @"value":pushAuthority
                                               },
                                           @{
                                               @"title":@"拍照权限",
                                               @"value":takePhotoAuthority
                                               },
                                           @{
                                               @"title":@"麦克风权限",
                                               @"value":audioAuthority
                                               }
                                           ]
                                }
                            ];
    _dataArray = dataArray;
    
    self.tableView = [[UITableView alloc] initWithFrame:self.view.bounds style:UITableViewStyleGrouped];
    self.tableView.backgroundColor = [UIColor whiteColor];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    [self.view addSubview:self.tableView];
}

#pragma mark - UITableView Delegate
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return _dataArray.count;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    NSArray *array = _dataArray[section][@"array"];
    return array.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 60;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 30;
}

- (nullable UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    UILabel *titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(10, 0, DoraemonScreenWidth-20, 20)];
    NSDictionary *dic = _dataArray[section];
    titleLabel.text = dic[@"title"];
    titleLabel.textColor = [UIColor orangeColor];
    return titleLabel;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return 0.1;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *identifer = @"httpcell";
    DoraemonAppInfoCell *cell = [tableView dequeueReusableCellWithIdentifier:identifer];
    if (!cell) {
        cell = [[DoraemonAppInfoCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifer];
    }
    NSArray *array = _dataArray[indexPath.section][@"array"];
    NSDictionary *item = array[indexPath.row];
    [cell renderUIWithData:item];
    return cell;
}



@end
