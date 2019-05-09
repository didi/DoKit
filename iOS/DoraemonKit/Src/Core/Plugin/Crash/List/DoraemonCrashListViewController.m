//
//  DoraemonCrashListViewController.m
//  DoraemonKit
//
//  Created by wenquan on 2018/11/22.
//

#import "DoraemonCrashListViewController.h"
#import "UIView+Doraemon.h"
#import "DoreamonCrashListCell.h"
#import "Doraemoni18NUtil.h"
#import "DoraemonSanboxDetailViewController.h"
#import "DoraemonSandboxModel.h"
#import "DoraemonCrashTool.h"
#import "DoraemonDefine.h"

static NSString *const kDoreamonCrashListCellIdentifier = @"kDoreamonCrashListCellIdentifier";

@interface DoraemonCrashListViewController () <UITableViewDelegate,UITableViewDataSource>

@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, copy) NSArray<DoraemonSandboxModel *> *dataArray;

@end

@implementation DoraemonCrashListViewController

#pragma mark - View Lifecycle

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    [self commonInit];
    
    [self loadCrashData];
}

- (void)commonInit {
    self.dataArray = [NSArray array];
    
    self.title = DoraemonLocalizedString(@"Crash日志列表");
    [self.view addSubview:self.tableView];
}

#pragma mark - Layout

- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];
    self.tableView.frame = CGRectMake(0, IPHONE_NAVIGATIONBAR_HEIGHT, self.view.doraemon_width, self.view.doraemon_height-IPHONE_NAVIGATIONBAR_HEIGHT);
}

#pragma mark - Private

#pragma mark CrashData

- (void)loadCrashData {
    // 获取crash目录
    NSFileManager *manager = [NSFileManager defaultManager];
    NSString *crashDirectory = [DoraemonCrashTool crashDirectory];
    
    if (crashDirectory && [manager fileExistsAtPath:crashDirectory]) {
        [self loadPath:crashDirectory];
    }
}

- (void)loadPath:(NSString *)filePath{
    NSFileManager *fm = [NSFileManager defaultManager];
    NSString *targetPath = NSHomeDirectory();
    if ([filePath isKindOfClass:[NSString class]] && (filePath.length > 0)) {
        targetPath = filePath;
    }
    
    //该目录下面的内容信息
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
            
            BOOL isDir = false;
            NSString *fullPath = [targetPath stringByAppendingPathComponent:sortedPath];
            [fm fileExistsAtPath:fullPath isDirectory:&isDir];
            
            DoraemonSandboxModel *model = [[DoraemonSandboxModel alloc] init];
            model.path = fullPath;
            if (isDir) {
                model.type = DoraemonSandboxFileTypeDirectory;
            }else{
                model.type = DoraemonSandboxFileTypeFile;
            }
            model.name = sortedPath;
            
            [files addObject:model];
        }
    }];
    self.dataArray = files.copy;
    
    [self.tableView reloadData];
}

- (void)deleteByDoraemonSandboxModel:(DoraemonSandboxModel *)model{
    NSFileManager *fm = [NSFileManager defaultManager];
    [fm removeItemAtPath:model.path error:nil];
    
    [self loadCrashData];
}

#pragma mark HandleFile

- (void)handleFileWithPath:(NSString *)filePath{
    UIAlertControllerStyle style;
    if ([DoraemonAppInfoUtil isIpad]) {
        style = UIAlertControllerStyleAlert;
    }else{
        style = UIAlertControllerStyleActionSheet;
    }
    
    UIAlertController *alertVc = [UIAlertController alertControllerWithTitle:DoraemonLocalizedString(@"请选择操作方式") message:nil preferredStyle:style];
    __weak typeof(self) weakSelf = self;
    UIAlertAction *previewAction = [UIAlertAction actionWithTitle:DoraemonLocalizedString(@"本地预览") style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        __strong typeof(self) strongSelf = weakSelf;
        [strongSelf previewFile:filePath];
    }];
    UIAlertAction *shareAction = [UIAlertAction actionWithTitle:DoraemonLocalizedString(@"分享") style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        __strong typeof(self) strongSelf = weakSelf;
        [strongSelf shareFileWithPath:filePath];
    }];
    UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:DoraemonLocalizedString(@"取消") style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
    }];
    [alertVc addAction:previewAction];
    [alertVc addAction:shareAction];
    [alertVc addAction:cancelAction];
    
    [self presentViewController:alertVc animated:YES completion:nil];
}

- (void)previewFile:(NSString *)filePath{
    DoraemonSanboxDetailViewController *detalVc = [[DoraemonSanboxDetailViewController alloc] init];
    detalVc.filePath = filePath;
    [self.navigationController pushViewController:detalVc animated:YES];
}

- (void)shareFileWithPath:(NSString *)filePath{
    NSURL *url = [NSURL fileURLWithPath:filePath];
    NSArray *objectsToShare = @[url];
    
    UIActivityViewController *controller = [[UIActivityViewController alloc] initWithActivityItems:objectsToShare applicationActivities:nil];
    NSArray *excludedActivities = @[UIActivityTypePostToTwitter, UIActivityTypePostToFacebook,
                                    UIActivityTypePostToWeibo,
                                    UIActivityTypeMessage, UIActivityTypeMail,
                                    UIActivityTypePrint, UIActivityTypeCopyToPasteboard,
                                    UIActivityTypeAssignToContact, UIActivityTypeSaveToCameraRoll,
                                    UIActivityTypeAddToReadingList, UIActivityTypePostToFlickr,
                                    UIActivityTypePostToVimeo, UIActivityTypePostToTencentWeibo];
    controller.excludedActivityTypes = excludedActivities;
    
    if([DoraemonAppInfoUtil isIpad]){
        if ( [controller respondsToSelector:@selector(popoverPresentationController)] ) {
            controller.popoverPresentationController.sourceView = self.view;
        }
        [self presentViewController:controller animated:YES completion:nil];
    }else{
        [self presentViewController:controller animated:YES completion:nil];
    }
}

#pragma mark - Delegate

#pragma mark <UITableViewDataSource>

// Default is 1 if not implemented
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.dataArray.count;
}

// Row display. Implementers should *always* try to reuse cells by setting each cell's reuseIdentifier and querying for available reusable cells with dequeueReusableCellWithIdentifier:
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    DoreamonCrashListCell *cell = [tableView dequeueReusableCellWithIdentifier:kDoreamonCrashListCellIdentifier forIndexPath:indexPath];
    if (!cell) {
        cell = [[DoreamonCrashListCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:kDoreamonCrashListCellIdentifier];
    }
    
    if (indexPath.row < self.dataArray.count) {
        DoraemonSandboxModel *model = [self.dataArray objectAtIndex:indexPath.row];
        [cell renderUIWithData:model];
    }
    
    return cell;
}

#pragma mark - <UITableViewDelegate>

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return [DoreamonCrashListCell cellHeight];
}

// Called after the user changes the selection.
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.row < self.dataArray.count) {
        DoraemonSandboxModel *model = [self.dataArray objectAtIndex:indexPath.row];
        if (model.type == DoraemonSandboxFileTypeFile) {
            [self handleFileWithPath:model.path];
        }else if(model.type == DoraemonSandboxFileTypeDirectory){
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
    if (indexPath.row < self.dataArray.count) {
        DoraemonSandboxModel *model = self.dataArray[indexPath.row];
        [self deleteByDoraemonSandboxModel:model];
    }
}

#pragma mark - Getter

- (UITableView *)tableView {
    if (!_tableView) {
        _tableView = [[UITableView alloc] initWithFrame:CGRectZero style:UITableViewStylePlain];
        [_tableView registerClass:[DoreamonCrashListCell class] forCellReuseIdentifier:kDoreamonCrashListCellIdentifier];
        _tableView.backgroundColor = [UIColor whiteColor];
        _tableView.delegate = self;
        _tableView.dataSource = self;
    }
    return _tableView;
}

@end
