//
//  DoraemonSandboxViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2017/12/11.
//

#import "DoraemonSandboxViewController.h"
#import "DoraemonSandboxModel.h"
#import "DoraemonSandBoxCell.h"
#import "DoraemonSanboxDetailViewController.h"

@interface DoraemonSandboxViewController ()<UITableViewDelegate,UITableViewDataSource>

@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, strong) DoraemonSandboxModel *currentDirModel;
@property (nonatomic, copy) NSArray *dataArray;
@property (nonatomic, copy) NSString *rootPath;

@end

@implementation DoraemonSandboxViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self initData];
    [self initUI];
}

- (void)initData{
    _dataArray = @[];
    _rootPath = NSHomeDirectory();
    [self loadPath:nil];
}

- (void)initUI{
    self.title = @"沙盒浏览器";
    self.tableView = [[UITableView alloc] initWithFrame:self.view.bounds style:UITableViewStylePlain];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    [self.view addSubview:self.tableView];
}


- (void)loadPath : (NSString *)filePath{
    NSFileManager *fm = [NSFileManager defaultManager];
    NSString *targetPath = filePath;
    //该目录信息
    DoraemonSandboxModel *model = [[DoraemonSandboxModel alloc] init];
    if (!targetPath || [targetPath isEqualToString:_rootPath]) {
        targetPath = _rootPath;
        model.name = @"根目录";
        model.type = DoraemonSandboxFileTypeRoot;
    }else{
        model.name = @"返回上一级";
        model.type = DoraemonSandboxFileTypeBack;
    }
    model.path = filePath;
    _currentDirModel = model;
    
    
    //该目录下面的内容信息
    NSMutableArray *files = @[].mutableCopy;
    NSError *error = nil;
    NSArray *paths = [fm contentsOfDirectoryAtPath:targetPath error:&error];
    for (NSString *path in paths) {
        BOOL isDir = false;
        NSString *fullPath = [targetPath stringByAppendingPathComponent:path];
        [fm fileExistsAtPath:fullPath isDirectory:&isDir];
        
        DoraemonSandboxModel *model = [[DoraemonSandboxModel alloc] init];
        model.path = fullPath;
        if (isDir) {
            model.type = DoraemonSandboxFileTypeDirectory;
        }else{
            model.type = DoraemonSandboxFileTypeFile;
        }
        model.name = path;
        
        [files addObject:model];
    }
    
    _dataArray = files.copy;
    
    [self.tableView reloadData];
}


#pragma mark- UITableViewDelegate
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return _dataArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    static NSString *cellId = @"cellId";
    DoraemonSandBoxCell *cell = [tableView dequeueReusableCellWithIdentifier:cellId];
    if (!cell) {
        cell = [[DoraemonSandBoxCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellId];
    }
    DoraemonSandboxModel *model = _dataArray[indexPath.row];
    [cell renderUIWithData:model];
    return cell;
}

#pragma mark- UITableViewDataSource
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return 48;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    DoraemonSandboxModel *model = _dataArray[indexPath.row];
    if (model.type == DoraemonSandboxFileTypeFile) {
        [self handleFileWithPath:model.path];
    }else if(model.type == DoraemonSandboxFileTypeDirectory){
        [self loadPath:model.path];
    }
}


- (void)leftNavBackClick:(id)clickView{
    if (_currentDirModel.type == DoraemonSandboxFileTypeRoot) {
        [super leftNavBackClick:clickView];
    }else{
        [self loadPath:[_currentDirModel.path stringByDeletingLastPathComponent]];
    }
}

- (void)handleFileWithPath:(NSString *)filePath{
    UIAlertController *alertVc = [UIAlertController alertControllerWithTitle:@"请选择操作方式" message:nil preferredStyle:UIAlertControllerStyleActionSheet];
    __weak typeof(self) weakSelf = self;
    UIAlertAction *previewAction = [UIAlertAction actionWithTitle:@"本地预览" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        __strong typeof(self) strongSelf = weakSelf;
        [strongSelf previewFile:filePath];
    }];
    UIAlertAction *shareAction = [UIAlertAction actionWithTitle:@"分享" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        __strong typeof(self) strongSelf = weakSelf;
        [strongSelf shareFileWithPath:filePath];
    }];
    UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
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
    
//    NSURL *url=[NSURL fileURLWithPath:filePath];
//    NSData *data = [[NSData alloc] initWithContentsOfURL:url];
//    //NSString *content=[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
//    NSString *content=[NSString stringWithContentsOfFile:filePath encoding:NSASCIIStringEncoding error:nil];
//    //NSLog(@"文件读取成功: %@",content);
//
//    NSStringEncoding enc;
//    NSError *error;
//    NSString *contents = [NSString stringWithContentsOfFile:filePath usedEncoding:&enc error:&error];
//
//    NSMutableDictionary *dict = [[NSMutableDictionary alloc]initWithContentsOfFile:filePath];
//    //self.provinces = [dict objectForKey:@"address"];
    
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

    [self presentViewController:controller animated:YES completion:nil];
}

@end
