//
//  DoraemonMockApiCell.m
//  DoraemonKit
//
//  Created by didi on 2019/11/15.
//

#import "DoraemonMockApiCell.h"
#import "DoraemonMockSceneButton.h"
#import "DoraemonMockAPIModel.h"
#import "DoraemonMockManager.h"

@interface DoraemonMockApiCell()<DoraemonMockSceneButtonDelegate>

@property (nonatomic, strong) UIView *sceneView;

@end

@implementation DoraemonMockApiCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        _sceneView = [[UIView alloc] init];
        [self.contentView addSubview:_sceneView];
    }
    return self;
}

- (void)renderCellWithData:(DoraemonMockBaseModel *)model{
    [super renderCellWithData: model];
    if(!model||!model.expand){
        _sceneView.hidden = YES;
        return ;
    }else{
        _sceneView.hidden = NO;
    }
    if (_sceneView.subviews.count>0) {
        [_sceneView.subviews makeObjectsPerformSelector:@selector(removeFromSuperview)];
    }
    
    DoraemonMockAPIModel *apiModel = (DoraemonMockAPIModel *)self.model;
    
    NSArray *sceneList = apiModel.sceneList;
    if (sceneList.count > 0) {
        _sceneView.frame = CGRectMake(0, self.infoLabel.doraemon_bottom, DoraemonScreenWidth, [[self class] sceneViewHeight:apiModel]);
        CGFloat h = kDoraemonSizeFrom750_Landscape(32);
        CGFloat offsetY = kDoraemonSizeFrom750_Landscape(32);
        CGFloat offsetX = kDoraemonSizeFrom750_Landscape(32);
        NSInteger i = 0;
        for (DoraemonMockScene *scene in sceneList) {
            DoraemonMockSceneButton *btn = [[DoraemonMockSceneButton alloc] init];
            btn.tag = i; i++;
            btn.delegate = self;
            [btn renderTitle:scene.name isSelected:scene.selected];
            
            CGFloat w = [DoraemonMockSceneButton viewWidth:scene.name];
            if (offsetX>DoraemonScreenWidth-kDoraemonSizeFrom750_Landscape(32)) {
                offsetX = kDoraemonSizeFrom750_Landscape(32);
                offsetY += kDoraemonSizeFrom750_Landscape(32)*2;
            }
            btn.frame = CGRectMake(offsetX, offsetY, w, h);
            offsetX += w+kDoraemonSizeFrom750_Landscape(32);
            [_sceneView addSubview:btn];
        }
    }
}

+ (CGFloat)cellHeightWith:(DoraemonMockBaseModel *)model{
    CGFloat cellHeight = [super cellHeightWith:model];
    
    if (model && model.expand){
        DoraemonMockAPIModel *apiModel = (DoraemonMockAPIModel *)model;
        cellHeight += [self sceneViewHeight:apiModel];
    }

    return cellHeight;
}


+ (CGFloat)sceneViewHeight:(DoraemonMockAPIModel *)model{
    NSArray *sceneList = model.sceneList;
    
    CGFloat w = kDoraemonSizeFrom750_Landscape(32);
    CGFloat h = 0;
    if (sceneList.count>0) {
        h = kDoraemonSizeFrom750_Landscape(32);
        h += kDoraemonSizeFrom750_Landscape(32);
    }
    for (DoraemonMockScene *scene in sceneList) {
        w += [DoraemonMockSceneButton viewWidth:scene.name];
        if (w > DoraemonScreenWidth-kDoraemonSizeFrom750_Landscape(32)*2) {
            w = kDoraemonSizeFrom750_Landscape(32);
            h += kDoraemonSizeFrom750_Landscape(32)*2;
        }
        w += kDoraemonSizeFrom750_Landscape(32);
    }
    
    return h;
}

#pragma mark - DoraemonMockSceneButtonDelegate
- (void)sceneBtnClick:(NSInteger)tag{
    DoraemonMockAPIModel *apiModel = (DoraemonMockAPIModel *)self.model;
    NSArray<DoraemonMockScene *> *sceneList = apiModel.sceneList;
    for (int i=0; i<sceneList.count; i++) {
        DoraemonMockScene *scene = sceneList[i];
        if (i == tag) {
            scene.selected = YES;
        }else {
            scene.selected = NO;
        }
    }
    if (self.delegate && [self.delegate respondsToSelector:@selector(sceneBtnClick)]) {
        [self.delegate sceneBtnClick];
    }
}

#pragma mark -- DoraemonSwitchViewDelegate
- (void)changeSwitchOn:(BOOL)on sender:(id)sender{
    self.model.selected = on;
    
    // 1、是否开启mock功能 : mock列表中只要有一个选中就需要打开mock功能
    BOOL needMockOn = NO;
    for (DoraemonMockAPIModel *api in [DoraemonMockManager sharedInstance].mockArray) {
        if (api.selected) {
            needMockOn = YES;
        }
    }
    
    [DoraemonMockManager sharedInstance].mock = needMockOn;
    
    DoraemonMockAPIModel *apiModel = (DoraemonMockAPIModel *)self.model;
    
    if (on) {
        // 2、如果场景没有选中 默认选中第一个
        NSArray<DoraemonMockScene *> *sceneList = apiModel.sceneList;
        BOOL select = NO;
        for (DoraemonMockScene *s in sceneList) {
            if (s.selected) {
                select = s.selected;
            }
        }
        if (!select && sceneList.count>0) {
            DoraemonMockScene *s = sceneList[0];
            s.selected = YES;
        }
    }
    
    if (self.delegate && [self.delegate respondsToSelector:@selector(cellSwitchClick)]) {
        [self.delegate cellSwitchClick];
    }
}

@end
