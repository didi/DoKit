//
//  DoraemonHomeViewController.m
//  DoraemonKitDemo
//
//  Created by yixiang on 2017/12/11.
//  Copyright © 2017年 yixiang. All rights reserved.
//

#import "DoraemonHomeViewController.h"
#import "UIView+DoraemonPositioning.h"
#import "DoraemonHomeCell.h"
#import "DoraemonHomeCellHeader.h"
#import "DoraemonUtil.h"
#import "UIColor+DoreamonKit.h"
#import "DoraemonManager.h"
#import "DoraemonPluginProtocol.h"
#import "DoraemonHomeWindow.h"

@interface DoraemonHomeViewController ()<UICollectionViewDelegate,UICollectionViewDataSource>

@property (nonatomic, strong) UICollectionView *collectionView;

@property (nonatomic,strong) NSMutableArray *dataArray;

@end

@implementation DoraemonHomeViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];
    [self initData];
    [self initUI];
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = YES;
}

- (void)initData{
    _dataArray = [DoraemonManager shareInstance].dataArray;
}

- (void)initUI{
    UIView *clearView = [[UIView alloc]initWithFrame:[UIApplication sharedApplication].statusBarFrame];
    clearView.backgroundColor = [UIColor clearColor];
    [self.view addSubview:clearView];
    
    UICollectionViewFlowLayout *flowLayout = [[UICollectionViewFlowLayout alloc]init];
    flowLayout.minimumLineSpacing      = 0.0f;
    flowLayout.minimumInteritemSpacing = 0.0f;
    
    CGFloat statusBarHeight = [UIApplication sharedApplication].statusBarFrame.size.height;
    _collectionView = [[UICollectionView alloc]initWithFrame:CGRectMake(0.0f, statusBarHeight, self.view.doraemon_width, self.view.doraemon_height - statusBarHeight) collectionViewLayout:flowLayout];
    _collectionView.backgroundColor = [UIColor whiteColor];
    [self.view addSubview:_collectionView];
    
    [_collectionView registerClass:[DoraemonHomeCell class] forCellWithReuseIdentifier:@"doraemonHomeCell"];
    [_collectionView registerClass:[DoraemonHomeCellHeader class] forSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:@"doraemonHomeCellHeader"];
    [_collectionView registerClass:[UICollectionReusableView class] forSupplementaryViewOfKind:UICollectionElementKindSectionFooter withReuseIdentifier:@"doraemonHomeCellFooter"];
    
    _collectionView.delegate = self;
    _collectionView.dataSource = self;
}


- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath{
    DoraemonHomeCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:@"doraemonHomeCell" forIndexPath:indexPath];
    
    if (!cell){
        cell = [[DoraemonHomeCell alloc]init];
    }
    NSDictionary *data= _dataArray[indexPath.section];
    NSArray *pluginArray = [data objectForKey:@"pluginArray"];
    [cell renderUIWithData:pluginArray[indexPath.row]];
    return cell;
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath{
    return CGSizeMake(self.view.doraemon_width / 4, 80.0f);
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout referenceSizeForHeaderInSection:(NSInteger)section{
    return CGSizeMake(self.view.doraemon_width, 40.0f);
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout referenceSizeForFooterInSection:(NSInteger)section{
    return CGSizeMake(self.view.doraemon_width, 10.0f);
}

- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView{
    return _dataArray.count;
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section{
    NSArray *pluginArray = [_dataArray[section] objectForKey:@"pluginArray"];
    return pluginArray.count;
}

- (UICollectionReusableView *)collectionView:(UICollectionView *)collectionView viewForSupplementaryElementOfKind:(NSString *)kind atIndexPath:(NSIndexPath *)indexPath
{
    if ([kind isEqualToString:UICollectionElementKindSectionHeader]){
        DoraemonHomeCellHeader *headerView = [collectionView dequeueReusableSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:@"doraemonHomeCellHeader" forIndexPath:indexPath];
        
        NSString *moduleName = [_dataArray[indexPath.section] objectForKey:@"moduleName"];
        if (moduleName){
            [headerView setTitle:moduleName];
        }
        return headerView;
    }else if ([kind isEqualToString:UICollectionElementKindSectionFooter]){
        UICollectionReusableView *footerView = [collectionView dequeueReusableSupplementaryViewOfKind:UICollectionElementKindSectionFooter withReuseIdentifier:@"doraemonHomeCellFooter" forIndexPath:indexPath];
        footerView.backgroundColor = [UIColor doraemon_colorWithHex:0xE0E0E0];
        return footerView;
    }
    return nil;
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath{   
    //再跳转到新的设置页面
    NSDictionary *data= _dataArray[indexPath.section];
    NSArray *pluginArray = [data objectForKey:@"pluginArray"];
    NSDictionary *itemInfo = pluginArray[indexPath.row];
    NSString *pluginName = itemInfo[@"pluginName"];
    
    if(pluginName){
        Class pluginClass = NSClassFromString(pluginName);
        id<DoraemonPluginProtocol> plugin = [[pluginClass alloc] init];
        [plugin pluginDidLoad];
    }
}
@end
