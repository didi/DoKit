//
//  DoraemonANRListViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/6/15.
//

#import "DoraemonANRListViewController.h"
#import "DoraemonANRManager.h"
#import "DoraemonANRListCell.h"
#import "DoraemonANRDetailViewController.h"
#import "DoraemonSandboxModel.h"
#import "DoraemonANRTool.h"
#import "DoraemonDefine.h"

@interface DoraemonANRListViewController ()<UITableViewDelegate,UITableViewDataSource>

@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, copy) NSArray *anrArray;

@end

@implementation DoraemonANRListViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"卡顿列表");
    
    [self loadANRData];
    
    self.tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, IPHONE_NAVIGATIONBAR_HEIGHT, self.view.doraemon_width, self.view.doraemon_height-IPHONE_NAVIGATIONBAR_HEIGHT) style:UITableViewStylePlain];
//    self.tableView.backgroundColor = [UIColor whiteColor];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    [self.view addSubview:self.tableView];
}

#pragma mark ANRData
- (void)loadANRData {
    // 获取 ANR 目录
    NSFileManager *manager = [NSFileManager defaultManager];
    NSString *anrDirectory = [DoraemonANRTool anrDirectory];
    
    if (anrDirectory && [manager fileExistsAtPath:anrDirectory]) {
        [self loadPath:anrDirectory];
    }
}

- (void)loadPath:(NSString *)filePath {
    NSFileManager *fm = [NSFileManager defaultManager];
    NSString *targetPath = NSHomeDirectory();
    if ([filePath isKindOfClass:[NSString class]] && (filePath.length > 0)) {
        targetPath = filePath;
    }
    
    // 该目录下面的内容信息
    NSError *error = nil;
    NSArray *paths = [fm contentsOfDirectoryAtPath:targetPath error:&error];
    
    // 对paths按照创建时间的降序进行排列
    NSArray *sortedPaths = [paths sortedArrayUsingComparator:^NSComparisonResult(id  _Nonnull obj1, id  _Nonnull obj2) {
        if ([obj1 isKindOfClass:[NSString class]] && [obj2 isKindOfClass:[NSString class]]) {
            // 获取文件完整路径
            NSString *firstPath = [targetPath stringByAppendingPathComponent:obj1];
            NSString *secondPath = [targetPath stringByAppendingPathComponent:obj2];
            
            // 获取文件信息
            NSDictionary *firstFileInfo = [[NSFileManager defaultManager] attributesOfItemAtPath:firstPath error:nil];
            NSDictionary *secondFileInfo = [[NSFileManager defaultManager] attributesOfItemAtPath:secondPath error:nil];
            
            // 获取文件创建时间
            id firstData = [firstFileInfo objectForKey:NSFileCreationDate];
            id secondData = [secondFileInfo objectForKey:NSFileCreationDate];
            
            // 按照创建时间降序排列
            return [secondData compare:firstData];
        }
        return NSOrderedSame;
    }];
    
    // 构造数据源
    NSMutableArray *files = [NSMutableArray array];
    [sortedPaths enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        if ([obj isKindOfClass:[NSString class]]) {
            NSString *sortedPath = obj;
            
            BOOL isDir = NO;
            NSString *fullPath = [targetPath stringByAppendingPathComponent:sortedPath];
            [fm fileExistsAtPath:fullPath isDirectory:&isDir];
            
            DoraemonSandboxModel *model = [[DoraemonSandboxModel alloc] init];
            model.path = fullPath;
            if (isDir) {
                model.type = DoraemonSandboxFileTypeDirectory;
            } else {
                model.type = DoraemonSandboxFileTypeFile;
            }
            model.name = sortedPath;
            
            [files addObject:model];
        }
    }];
    self.anrArray = files.copy;
    
    [self.tableView reloadData];
}

- (void)deleteByDoraemonSandboxModel:(DoraemonSandboxModel *)model {
    NSFileManager *fm = [NSFileManager defaultManager];
    [fm removeItemAtPath:model.path error:nil];
    
    [self loadANRData];
}

#pragma mark - UITableView Delegate
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.anrArray.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return [DoraemonANRListCell cellHeight];
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 0.1;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return 0.1;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *identifer = @"anrcell";
    DoraemonANRListCell *cell = [tableView dequeueReusableCellWithIdentifier:identifer];
    if (!cell) {
        cell = [[DoraemonANRListCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifer];
    } 
    
    if (indexPath.row < self.anrArray.count) {
        DoraemonSandboxModel *model = [self.anrArray objectAtIndex:indexPath.row];
        [cell renderCellWithData:model];
    }
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    if (indexPath.row < self.anrArray.count) {
        DoraemonSandboxModel *model = [self.anrArray objectAtIndex:indexPath.row];
        if (model.type == DoraemonSandboxFileTypeFile) {
            DoraemonANRDetailViewController *vc = [[DoraemonANRDetailViewController alloc] init];
            vc.filePath = model.path;
            [self.navigationController pushViewController:vc animated:YES];
        } else if (model.type == DoraemonSandboxFileTypeDirectory) {
            [self loadPath:model.path];
        }
    }
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath{
    return YES;
}

- (NSString *)tableView:(UITableView *)tableView titleForDeleteConfirmationButtonForRowAtIndexPath:(NSIndexPath *)indexPath{
    return DoraemonLocalizedString(@"删除");
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath{
    if (indexPath.row < self.anrArray.count) {
        DoraemonSandboxModel *model = self.anrArray[indexPath.row];
        [self deleteByDoraemonSandboxModel:model];
    }
}

@end
