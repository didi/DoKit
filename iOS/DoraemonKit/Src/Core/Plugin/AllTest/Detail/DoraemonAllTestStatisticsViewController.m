//
//  DoraemonStatistics.m
//  AFNetworking
//
//  Created by didi on 2019/9/26.
//

#import "DoraemonAllTestStatisticsViewController.h"
#import "DoraemonNavBarItemModel.h"
#import "UIViewController+Doraemon.h"
#import "DoraemonDefine.h"
#import "DoraemonHomeWindow.h"
#import "DoraemonHomeHeadCell.h"
#import "DoraemonHomeFootCell.h"
#import "DoraemonAllTestStatisticsCell.h"
#import "DoraemonAllTestStatisticsManager.h"

static NSString *DoraemonStatisticsCellID = @"DoraemonStatisticsCellID";
static NSString *DoraemonStatisticsHeadCellID = @"DoraemonStatisticsHeadCellID";
static NSString *DoraemonStatisticsFootCellID = @"DoraemonStatisticsFootCellID";

@interface DoraemonAllTestStatisticsViewController ()<UICollectionViewDelegate, UICollectionViewDataSource>

@property (nonatomic, strong) UICollectionView *collectionView;
@property (nonatomic,strong) NSMutableArray *dataArray;

@property (nonatomic, strong) DoraemonAllTestStatisticsManager *statisticsManager;

@end

@implementation DoraemonAllTestStatisticsViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"页面数据");

    _statisticsManager = [DoraemonAllTestStatisticsManager shareInstance];
    _dataArray = [_statisticsManager getLastResultArray];
    
    if(_dataArray.count == 0){
        [DoraemonToastUtil showToast:DoraemonLocalizedString(@"测试记录为空") inView:self.view];
        return;
    }
    
    [self.view addSubview:self.collectionView];
    
}

- (UICollectionView *)collectionView {
    if (!_collectionView) {
        UICollectionViewFlowLayout *flowLayout = [[UICollectionViewFlowLayout alloc] init];
        _collectionView = [[UICollectionView alloc] initWithFrame:CGRectZero collectionViewLayout:flowLayout];
        _collectionView.backgroundColor = [UIColor whiteColor];
        _collectionView.showsVerticalScrollIndicator = NO;
        _collectionView.delegate = self;
        _collectionView.dataSource = self;
        [_collectionView registerClass:[DoraemonAllTestStatisticsCell class] forCellWithReuseIdentifier:DoraemonStatisticsCellID];
        [_collectionView registerClass:[DoraemonHomeHeadCell class] forSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:DoraemonStatisticsHeadCellID];
        [_collectionView registerClass:[DoraemonHomeFootCell class] forSupplementaryViewOfKind:UICollectionElementKindSectionFooter withReuseIdentifier:DoraemonStatisticsFootCellID];
    }
    return _collectionView;
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath {
    return CGSizeMake(DoraemonScreenWidth, kDoraemonSizeFrom750_Landscape(33));
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout referenceSizeForHeaderInSection:(NSInteger)section {
    return CGSizeMake(DoraemonScreenWidth, kDoraemonSizeFrom750_Landscape(88));
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout referenceSizeForFooterInSection:(NSInteger)section {
    return CGSizeMake(DoraemonScreenWidth, kDoraemonSizeFrom750_Landscape(24));
}

- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView {
    return _dataArray.count ;
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    if (section < self.dataArray.count) {
        NSDictionary *dict = _dataArray[section];
        NSArray *pluginArray = dict[@"common_data"];
        return pluginArray.count;
    } else {
        return 0;//1的时候会挂掉；
    }
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    DoraemonAllTestStatisticsCell *cell = [self.collectionView dequeueReusableCellWithReuseIdentifier:DoraemonStatisticsCellID forIndexPath:indexPath];
    NSInteger row = indexPath.item;
    NSInteger section = indexPath.section;
    NSDictionary *dict = _dataArray[section];
    
    NSArray *common_data = dict[@"common_data"];
    NSDictionary *item = common_data[row];
    [cell update:item[@"title"] up:item[@"max"] down:item[@"min"] average:item[@"average"]];
    
    cell.backgroundColor = [UIColor whiteColor];
    return cell;
}

- (UICollectionReusableView *)collectionView:(UICollectionView *)collectionView viewForSupplementaryElementOfKind:(NSString *)kind atIndexPath:(NSIndexPath *)indexPath {
    UICollectionReusableView *view = nil;
    
    if ([kind isEqualToString:UICollectionElementKindSectionHeader]) {
        DoraemonHomeHeadCell *head = [collectionView dequeueReusableSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:DoraemonStatisticsHeadCellID  forIndexPath:indexPath];
        head.title.text = @"";
        NSInteger section = indexPath.section;
        if (section < _dataArray.count) {
            NSDictionary *dict = _dataArray[section];
            head.title.text = dict[@"page"];
        }
        view = head;
        
    } else if ([kind isEqualToString:UICollectionElementKindSectionFooter]) {
        
        DoraemonHomeFootCell *foot = [collectionView dequeueReusableSupplementaryViewOfKind:UICollectionElementKindSectionFooter withReuseIdentifier:DoraemonStatisticsFootCellID forIndexPath:indexPath];
        foot.backgroundColor = [UIColor doraemon_colorWithString:@"#F4F5F6"];
        foot.title.text = nil;
        view = foot;
    }
    return view;
}

-(UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout insetForSectionAtIndex:(NSInteger)section{
    return UIEdgeInsetsMake(0, kDoraemonSizeFrom750_Landscape(0), kDoraemonSizeFrom750_Landscape(40), kDoraemonSizeFrom750_Landscape(0));//分别为上、左、下、右
}

- (void)layoutCollectionView {
    UIInterfaceOrientation orientation = [UIApplication sharedApplication].statusBarOrientation;
    switch (orientation) {
        case UIInterfaceOrientationLandscapeLeft:
        case UIInterfaceOrientationLandscapeRight:
        {
            CGSize size = self.view.doraemon_size;
            if (size.width > size.height) {
                UIEdgeInsets safeAreaInsets = [self safeAreaInset];
                CGRect frame = self.view.frame;
                CGFloat width = self.view.doraemon_width - safeAreaInsets.left - safeAreaInsets.right;
                frame.origin.x = safeAreaInsets.left;
                frame.size.width = width;
                self.collectionView.frame = frame;
            }
        }
            break;
        default:
            self.collectionView.frame = self.view.frame;
            break;
    }
}

- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];

    [self layoutCollectionView];
}


@end
