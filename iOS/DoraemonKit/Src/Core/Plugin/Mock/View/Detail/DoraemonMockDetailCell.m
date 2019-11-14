//
//  DoraemonMockDetailCell.m
//  AFNetworking
//
//  Created by didi on 2019/10/24.
//

#import "DoraemonMockDetailCell.h"
#import "DoraemonDefine.h"
#import "DoraemonUtil.h"
#import "DoraemonMockDetailSwitch.h"
#import "DoraemonMockSceneButton.h"
#import "DoraemonMockManager.h"

@interface DoraemonMockDetailCell()<DoraemonSwitchViewDelegate,DoraemonMockDetailCellDelegate,DoraemonMockSceneButtonDelegate>

@property (nonatomic, strong) DoraemonMockDetailSwitch *detailSwitch;
@property (nonatomic, strong) UILabel *infoLabel;
@property (nonatomic, strong) UIView *sceneView;
@property (nonatomic, strong) DoraemonMockAPI *model;

@end

@implementation DoraemonMockDetailCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        _detailSwitch = [[DoraemonMockDetailSwitch alloc] initWithFrame:CGRectMake(0, 0, DoraemonScreenWidth, kDoraemonSizeFrom750_Landscape(104))];
        [_detailSwitch needDownLine];
        [_detailSwitch needArrow];
        _detailSwitch.delegate = self;
        [self.contentView addSubview:_detailSwitch];
        _detailSwitch.userInteractionEnabled = YES;
        UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(expandClick)];
        [_detailSwitch addGestureRecognizer:tap];

        _infoLabel = [[UILabel alloc] init];
        _infoLabel.textColor = [UIColor doraemon_black_1];
        _infoLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(24)];
        _infoLabel.numberOfLines = 0;
        [self.contentView addSubview:_infoLabel];
        
        _sceneView = [[UIView alloc] init];
        [self.contentView addSubview:_sceneView];
    }
    return self;
}

- (void)renderCellWithData:(DoraemonMockAPI *)model{
    _model = model;
    self.backgroundColor = [UIColor doraemon_bg];
    [_detailSwitch renderUIWithTitle:model.name switchOn:model.selected];
    [_detailSwitch setSwitchFrame];
    [_detailSwitch setArrowDown:model.expand];
    
    if(!model||!model.expand){
        _infoLabel.hidden = YES;
        _sceneView.hidden = YES;
        return ;
    }else{
        _infoLabel.hidden = NO;
        _sceneView.hidden = NO;
        _infoLabel.text = model.info;
        CGSize size = [_infoLabel sizeThatFits:CGSizeMake(DoraemonScreenWidth-kDoraemonSizeFrom750_Landscape(32)*2, CGFLOAT_MAX)];
        _infoLabel.frame = CGRectMake(kDoraemonSizeFrom750_Landscape(32), _detailSwitch.doraemon_bottom+kDoraemonSizeFrom750_Landscape(24), size.width, size.height);
    }
    
    if (_sceneView.subviews.count>0) {
        [_sceneView.subviews makeObjectsPerformSelector:@selector(removeFromSuperview)];
    }
    NSArray *sceneList = model.sceneList;
    if (sceneList.count > 0) {
        _sceneView.frame = CGRectMake(0, _infoLabel.doraemon_bottom, DoraemonScreenWidth, [[self class] sceneViewHeight:model]);
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


+ (CGFloat)cellHeightWith:(DoraemonMockAPI *)model{
    CGFloat cellHeight = kDoraemonSizeFrom750_Landscape(104);
    if (model && model.expand) {
        cellHeight += kDoraemonSizeFrom750_Landscape(24);
        NSString *info = model.info;
        UILabel *tempLabel = [[UILabel alloc] init];
        tempLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(24)];
        tempLabel.text = info;
        tempLabel.numberOfLines = 0;
        CGSize size = [tempLabel sizeThatFits:CGSizeMake(DoraemonScreenWidth-kDoraemonSizeFrom750_Landscape(32)*2, CGFLOAT_MAX)];
        cellHeight += size.height;
        cellHeight += kDoraemonSizeFrom750_Landscape(24);
        
        cellHeight += [self sceneViewHeight:model];
    }
    return cellHeight;
}

+ (CGFloat)sceneViewHeight:(DoraemonMockAPI *)model{
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

#pragma mark - DoraemonMockDetailCellDelegate
- (void)expandClick{
    _model.expand = !_model.expand;
    if (_delegate && [_delegate respondsToSelector:@selector(cellExpandClick)]) {
        [_delegate cellExpandClick];
    }
}

#pragma mark - DoraemonMockSceneButtonDelegate
- (void)sceneBtnClick:(NSInteger)tag{
    NSArray<DoraemonMockScene *> *sceneList = _model.sceneList;
    for (int i=0; i<sceneList.count; i++) {
        DoraemonMockScene *scene = sceneList[i];
        if (i == tag) {
            scene.selected = YES;
        }else {
            scene.selected = NO;
        }
    }
    if (_delegate && [_delegate respondsToSelector:@selector(sceneBtnClick)]) {
        [_delegate sceneBtnClick];
    }
}

#pragma mark -- DoraemonSwitchViewDelegate
- (void)changeSwitchOn:(BOOL)on sender:(id)sender{
    //设置manager的array的index== tag的置为相应的on
    _model.selected = on;
    
    // 1、是否开启mock功能 : mock列表中只要有一个选中就需要打开mock功能
    BOOL needMockOn = NO;
    for (DoraemonMockAPI *api in [DoraemonMockManager sharedInstance].dataArray) {
        if (api.selected) {
            needMockOn = YES;
        }
    }
    
    [DoraemonMockManager sharedInstance].mock = needMockOn;
    
    
    
    if (on) {
        // 2、如果场景没有选中 默认选中第一个
        NSArray<DoraemonMockScene *> *sceneList = _model.sceneList;
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
    
    if (_delegate && [_delegate respondsToSelector:@selector(cellSwitchClick)]) {
        [_delegate cellSwitchClick];
    }
}

@end
