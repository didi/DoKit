//
//  DoraemonKitManagerViewController.m
//  DoraemonKit
//
//  Created by didi on 2020/4/24.
//

#import "DoraemonKitManagerViewController.h"
#import "DoraemonDefine.h"
#import "DoraemonKitManagerCell.h"
#import "DoraemonManager.h"
#import "UIViewController+Doraemon.h"
#import "DoraemonKitManagerHeadCell.h"
#import "DoraemonCacheManager.h"
#import "DoraemonNavBarItemModel.h"

static NSString *DoraemonKitManagerCellID = @"DoraemonKitManagerCellID";
static NSString *DoraemonKitManagerHeadCellID = @"DoraemonKitManagerHeadCellID";

@interface DoraemonKitManagerViewController ()<UICollectionViewDelegate, UICollectionViewDataSource>

@property (nonatomic, strong) UICollectionView *collectionView;
@property (nonatomic, strong) NSMutableArray *currentArray;
@property (nonatomic, strong) UILongPressGestureRecognizer *longPress;

@property (nonatomic, assign) BOOL editStatus;

@end

@implementation DoraemonKitManagerViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"工具管理");
    [self setRightNavTitle:DoraemonLocalizedString(@"编辑")];
    _editStatus = NO;
    [self setRightNavBar];
    _longPress = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(handleLongPress:)];

    //非编辑状态只显示show=YES的Kit
    _currentArray = [self getShowArray];
    
    [self.view addSubview:self.collectionView];
}

- (void)setRightNavBar{
    NSString *title = nil;
    if (_editStatus) {
        title = DoraemonLocalizedString(@"完成");
    }else{
        title = DoraemonLocalizedString(@"编辑");
    }
    DoraemonNavBarItemModel *model1 = [[DoraemonNavBarItemModel alloc] initWithText:title color:[UIColor doraemon_blue] selector:@selector(rightNavTitleClick)];
    DoraemonNavBarItemModel *model2 = [[DoraemonNavBarItemModel alloc] initWithText:DoraemonLocalizedString(@"还原") color:[UIColor doraemon_blue] selector:@selector(reset)];
    [self setRightNavBarItems:@[model1,model2]];
}

- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];
    self.collectionView.frame = [self fullscreen];
}

- (void)leftNavBackClick:(id)clickView{
    if (_editStatus) {
        [DoraemonAlertUtil handleAlertActionWithVC:self text:DoraemonLocalizedString(@"是否保存已编辑的内容") okBlock:^{
            [self rightNavTitleClick];
        } cancleBlock:^{
            
        }];
    }else{
        [super leftNavBackClick:clickView];
    }
}

- (void)reset{
    __weak typeof(self) weakSelf = self;
    [DoraemonAlertUtil handleAlertActionWithVC:self text:DoraemonLocalizedString(@"是否还原到初始状态") okBlock:^{
        __strong typeof(self) strongSelf = weakSelf;
        [[DoraemonCacheManager sharedInstance] saveKitManagerData:[DoraemonManager shareInstance].dataArray];
        strongSelf.currentArray = [self getAllArray];
        [strongSelf.collectionView reloadData];
    } cancleBlock:^{
        
    }];
}

- (void)rightNavTitleClick{
    _editStatus = !_editStatus;
    if (_editStatus) {
        //点击进入编辑状态
        [self setRightNavBar];
        [self.collectionView addGestureRecognizer:_longPress];
        _currentArray = [self getAllArray];
        [self.collectionView reloadData];
    }else{
        //点击进入完成状态
        [self setRightNavBar];
        [self.collectionView removeGestureRecognizer:_longPress];
        [[DoraemonCacheManager sharedInstance] saveKitManagerData:_currentArray];
        _currentArray = [self getShowArray];
        [self.collectionView reloadData];
        
        [DoraemonToastUtil showToastBlack:DoraemonLocalizedString(@"保存成功") inView:self.view];
    }
}

- (NSMutableArray *)getAllArray{
    NSMutableArray *dataArray = [[DoraemonCacheManager sharedInstance] kitManagerData];
    if (ARRAY_IS_NULL(dataArray)) {
        [[DoraemonCacheManager sharedInstance] saveKitManagerData:[DoraemonManager shareInstance].dataArray];
        dataArray = [[DoraemonCacheManager sharedInstance] kitManagerData];
    }
    return dataArray;
}

- (NSMutableArray *)getShowArray{
    NSMutableArray *dataArray = [[DoraemonCacheManager sharedInstance] kitShowManagerData];
    if (ARRAY_IS_NULL(dataArray)) {
        [[DoraemonCacheManager sharedInstance] saveKitManagerData:[DoraemonManager shareInstance].dataArray];
        dataArray = [[DoraemonCacheManager sharedInstance] kitShowManagerData];
    }
    return dataArray;
}


#pragma mark -- UICollectionView
- (UICollectionView *)collectionView {
    if (!_collectionView) {
        UICollectionViewFlowLayout *fl = [[UICollectionViewFlowLayout alloc] init];
        fl.minimumLineSpacing = CGFLOAT_MIN;
        fl.minimumInteritemSpacing = CGFLOAT_MIN;
        _collectionView = [[UICollectionView alloc] initWithFrame:CGRectZero collectionViewLayout:fl];
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
        if (@available(iOS 13.0, *)) {
            _collectionView.backgroundColor = [UIColor systemBackgroundColor];
        } else {
#endif
            _collectionView.backgroundColor = [UIColor whiteColor];
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
        }
#endif
        _collectionView.showsVerticalScrollIndicator = NO;
        _collectionView.delegate = self;
        _collectionView.dataSource = self;
        [_collectionView registerClass:[DoraemonKitManagerCell class] forCellWithReuseIdentifier:DoraemonKitManagerCellID];
        [_collectionView registerClass:[DoraemonKitManagerHeadCell class] forSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:DoraemonKitManagerHeadCellID];
    }
    
    return _collectionView;
}

- (void)handleLongPress:(UILongPressGestureRecognizer *)longPress {
    switch (longPress.state) {
        case UIGestureRecognizerStateBegan:
        {
            NSIndexPath *indexPath = [self.collectionView indexPathForItemAtPoint:[longPress locationInView:self.collectionView]];
            if (![self canMove:indexPath]) {
                break;
            }
            if (@available(iOS 9.0, *)) {
                [self.collectionView beginInteractiveMovementForItemAtIndexPath:indexPath];
            } else {
                // Fallback on earlier versions
            }
        }
            break;
        case UIGestureRecognizerStateChanged:
        {
            if (@available(iOS 9.0, *)) {
                [self.collectionView updateInteractiveMovementTargetPosition:[longPress locationInView:self.collectionView]];
            } else {
                // Fallback on earlier versions
            }
        }
            break;
        case UIGestureRecognizerStateEnded:
        {
            if (@available(iOS 9.0, *)) {
                [self.collectionView endInteractiveMovement];
            } else {
                // Fallback on earlier versions
            }
        }
            break;
        default:
        {
            if (@available(iOS 9.0, *)) {
                [self.collectionView cancelInteractiveMovement];
            } else {
                // Fallback on earlier versions
            }
        }
            break;
    }
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath {
    CGFloat w = kDoraemonSizeFrom750_Landscape(750)/4;
    CGFloat h = w/187*209;
    
    CGSize size = CGSizeMake(w, h);
    return size;
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout referenceSizeForHeaderInSection:(NSInteger)section {
    CGFloat w = DoraemonScreenWidth;
    CGFloat h = kDoraemonSizeFrom750_Landscape(102);
    CGSize size = CGSizeMake(w, h);
    return size;
}

- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView {
    return _currentArray.count;
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    NSDictionary *dict = _currentArray[section];
    NSArray *pluginArray = dict[@"pluginArray"];
    return pluginArray.count;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    DoraemonKitManagerCell *cell = [self.collectionView dequeueReusableCellWithReuseIdentifier:DoraemonKitManagerCellID forIndexPath:indexPath];
    NSInteger row = indexPath.row;
    NSInteger section = indexPath.section;
    
    NSDictionary *dict = _currentArray[section];
    NSArray *pluginArray = dict[@"pluginArray"];
    NSDictionary *item = pluginArray[row];
    [cell update:item[@"icon"] name:item[@"name"] select:[item[@"show"] boolValue] editStatus:_editStatus];
    [cell updateImage:item[@"image"]];
    return cell;
}

- (UICollectionReusableView *)collectionView:(UICollectionView *)collectionView viewForSupplementaryElementOfKind:(NSString *)kind atIndexPath:(NSIndexPath *)indexPath {
    UICollectionReusableView *view;
    if ([kind isEqualToString:UICollectionElementKindSectionHeader]) {
        DoraemonKitManagerHeadCell *head = [collectionView dequeueReusableSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:DoraemonKitManagerHeadCellID  forIndexPath:indexPath];
        [head renderUIWithTitle:nil];
        NSInteger section = indexPath.section;
        if (section < _currentArray.count) {
            NSDictionary *dict = _currentArray[section];
            [head renderUIWithTitle:dict[@"moduleName"]];
        }
        
        view = head;
    }else{
        view = [[UICollectionReusableView alloc] init];
    }
    
    return view;
}

- (BOOL)collectionView:(UICollectionView *)collectionView canMoveItemAtIndexPath:(NSIndexPath *)indexPath {
    return _editStatus;
}

- (void)collectionView:(UICollectionView *)collectionView moveItemAtIndexPath:(NSIndexPath *)sourceIndexPath toIndexPath:(NSIndexPath *)destinationIndexPath {
    if (!_editStatus) {
        return;
    }
    NSInteger sourceSection = sourceIndexPath.section;
    NSInteger sourceRow = sourceIndexPath.row;
    
    
    NSDictionary *sourceDict = _currentArray[sourceSection];
    NSMutableArray *sourcePluginArray = sourceDict[@"pluginArray"];
    NSDictionary *sourceItem = sourcePluginArray[sourceRow];
    
    [sourcePluginArray removeObjectAtIndex:sourceRow];
    
    NSInteger destinationSection = destinationIndexPath.section;
    NSInteger destinationRow = destinationIndexPath.row;
    NSDictionary *destinationDict = _currentArray[destinationSection];
    NSMutableArray *destinationPluginArray = destinationDict[@"pluginArray"];
    [destinationPluginArray insertObject:sourceItem atIndex:destinationRow];
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    if (!_editStatus) {
        return;
    }
    NSInteger section = indexPath.section;
    NSMutableDictionary *dict = _currentArray[section];
    NSMutableArray *pluginArray = dict[@"pluginArray"];
    NSMutableDictionary *itemData = pluginArray[indexPath.row];
    BOOL show = [itemData[@"show"] boolValue];
    if (show){
        //取消的时候要观察是不是这个section中的最后一个
        NSInteger showCount = 0;
        for (NSDictionary *subDic in pluginArray){
            BOOL show = [subDic[@"show"] boolValue];
            if (show) {
                showCount++;
            }
        }
        if (showCount == 1) {
            [DoraemonToastUtil showToastBlack:DoraemonLocalizedString(@"每一个分组至少保留一项") inView:self.view];
            return;
        }
    }
    itemData[@"show"] = @(!show);
    
    [self.collectionView reloadData];
}

#pragma mark -- private
- (BOOL)canMove:(NSIndexPath *)indexPath{
    if (!indexPath) {
        return NO;
    }
    NSInteger section = indexPath.section;
    NSMutableDictionary *dict = _currentArray[section];
    NSMutableArray *pluginArray = dict[@"pluginArray"];
    NSInteger showCount = 0;
    for (NSDictionary *subDic in pluginArray){
        BOOL show = [subDic[@"show"] boolValue];
        if (show) {
            showCount++;
        }
    }
    if (showCount <= 1) {
        [DoraemonToastUtil showToastBlack:DoraemonLocalizedString(@"每一个分组至少保留一项") inView:self.view];
        return NO;
    }
    return YES;
}


@end
