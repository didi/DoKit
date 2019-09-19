//
//  DoraemonHomeEntry.m
//  DoraemonKit
//
//  Created by dengyouhua on 2019/9/4.
//

#import "DoraemonHomeEntry.h"
#import "UIView+Doraemon.h"
#import "DoraemonUtil.h"
#import "UIColor+Doraemon.h"
#import "DoraemonManager.h"
#import "DoraemonPluginProtocol.h"
#import "DoraemonHomeWindow.h"
#import "DoraemonDefine.h"
#import "DoraemonHomeCell.h"
#import "DoraemonHomeHeadCell.h"
#import "DoraemonHomeFootCell.h"
#import "DoraemonHomeCloseCell.h"
#import "UIViewController+Doraemon.h"

static NSString *DoraemonHomeCellID = @"DoraemonHomeCellID";
static NSString *DoraemonHomeHeadCellID = @"DoraemonHomeHeadCellID";
static NSString *DoraemonHomeFootCellID = @"DoraemonHomeFootCellID";
static NSString *DoraemonHomeCloseCellID = @"DoraemonHomeCloseCellID";

@interface DoraemonHomeEntry () <UICollectionViewDelegate, UICollectionViewDataSource>

@property (nonatomic, strong) UICollectionView *collectionView;
@property (nonatomic,strong) NSMutableArray *dataArray;

@end

@implementation DoraemonHomeEntry

- (UICollectionView *)collectionView {
    if (!_collectionView) {
        UICollectionViewFlowLayout *fl = [[UICollectionViewFlowLayout alloc] init];
        _collectionView = [[UICollectionView alloc] initWithFrame:CGRectZero collectionViewLayout:fl];
        _collectionView.backgroundColor = [UIColor whiteColor];
        _collectionView.showsVerticalScrollIndicator = NO;
        _collectionView.delegate = self;
        _collectionView.dataSource = self;
        [_collectionView registerClass:[DoraemonHomeCell class] forCellWithReuseIdentifier:DoraemonHomeCellID];
        [_collectionView registerClass:[DoraemonHomeCloseCell class] forCellWithReuseIdentifier:DoraemonHomeCloseCellID];
        [_collectionView registerClass:[DoraemonHomeHeadCell class] forSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:DoraemonHomeHeadCellID];
        [_collectionView registerClass:[DoraemonHomeFootCell class] forSupplementaryViewOfKind:UICollectionElementKindSectionFooter withReuseIdentifier:DoraemonHomeFootCellID];
    }
    
    return _collectionView;
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section < _dataArray.count) {
        return CGSizeMake(80, 64);
    } else {
        return CGSizeMake(DoraemonScreenWidth, 60);
    }
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout referenceSizeForHeaderInSection:(NSInteger)section {
    if (section < _dataArray.count) {
        return CGSizeMake(DoraemonScreenWidth, 44);
    } else {
        return CGSizeMake(DoraemonScreenWidth, 0);
    }
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout referenceSizeForFooterInSection:(NSInteger)section {
    if (section < _dataArray.count) {
        return CGSizeMake(DoraemonScreenHeight, 20);
    } else {
        return CGSizeMake(DoraemonScreenHeight, 40);
    }
}

- (void)collectionView:(UICollectionView *)collectionView willDisplaySupplementaryView:(UICollectionReusableView *)view forElementKind:(NSString *)elementKind atIndexPath:(NSIndexPath *)indexPath {
    view.layer.zPosition = 0.0;
}

- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView {
    return _dataArray.count + 1;
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    if (section < self.dataArray.count) {
        NSDictionary *dict = _dataArray[section];
        NSArray *pluginArray = dict[@"pluginArray"];
        return pluginArray.count;
    } else {
        return 1;
    }
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    DoraemonHomeCell *cell = [self.collectionView dequeueReusableCellWithReuseIdentifier:DoraemonHomeCellID forIndexPath:indexPath];
    NSInteger row = indexPath.row;
    NSInteger section = indexPath.section;
    
    if (section < _dataArray.count) {
        NSDictionary *dict = _dataArray[section];
        NSArray *pluginArray = dict[@"pluginArray"];
        NSDictionary *item = pluginArray[row];
        [cell update:item[@"icon"] name:item[@"name"]];
        return cell;
    } else {
        DoraemonHomeCloseCell *closeCell = [collectionView dequeueReusableCellWithReuseIdentifier:DoraemonHomeCloseCellID forIndexPath: indexPath];
        return closeCell;
    }
}

- (UICollectionReusableView *)collectionView:(UICollectionView *)collectionView viewForSupplementaryElementOfKind:(NSString *)kind atIndexPath:(NSIndexPath *)indexPath {
    UICollectionReusableView *view = nil;
    if ([kind isEqualToString:UICollectionElementKindSectionHeader]) {
        DoraemonHomeHeadCell *head = [collectionView dequeueReusableSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:DoraemonHomeHeadCellID  forIndexPath:indexPath];
        head.title.text = @"";
        NSInteger section = indexPath.section;
        if (section < _dataArray.count) {
            NSDictionary *dict = _dataArray[section];
            head.title.text = dict[@"moduleName"];
        }
        
        view = head;
    } else if ([kind isEqualToString:UICollectionElementKindSectionFooter]) {
        DoraemonHomeFootCell *foot = [collectionView dequeueReusableSupplementaryViewOfKind:UICollectionElementKindSectionFooter withReuseIdentifier:DoraemonHomeFootCellID forIndexPath:indexPath];
        foot.backgroundColor = [UIColor doraemon_colorWithString:@"#F4F5F6"];
        
        if(indexPath.section >= _dataArray.count){
            NSString *str = DoraemonLocalizedString(@"当前版本");
            NSString *last = [NSString stringWithFormat:@"%@：%@",str,DokitVersion];
            foot.title.text = last;
            foot.title.textColor = [UIColor doraemon_colorWithString:@"#999999"];
            foot.title.textAlignment = NSTextAlignmentCenter;
            foot.title.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750(24)];
        }
        view = foot;
    }
    
    return view;
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    NSInteger section = indexPath.section;
    if (section < self.dataArray.count) {
        NSDictionary *dict = _dataArray[section];
        NSArray *pluginArray = dict[@"pluginArray"];
        NSDictionary *itemData = pluginArray[indexPath.row];
        NSString *pluginName = itemData[@"pluginName"];
        
        if(pluginName){
            Class pluginClass = NSClassFromString(pluginName);
            id<DoraemonPluginProtocol> plugin = [[pluginClass alloc] init];
            if ([plugin respondsToSelector:@selector(pluginDidLoad)]) {
                [plugin pluginDidLoad];
            }
            if ([plugin respondsToSelector:@selector(pluginDidLoad:)]) {
                [plugin pluginDidLoad:(NSDictionary *)itemData];
            }
            void (^handleBlock)(NSDictionary *itemData) = itemData[@"handleBlock"];
            if (handleBlock) {
                handleBlock(itemData);
            }
        }
    }
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.view.backgroundColor = [UIColor whiteColor];
    _dataArray = [DoraemonManager shareInstance].dataArray;
    [self.view addSubview:self.collectionView];
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = YES;
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
