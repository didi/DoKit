//
//  DoraemonMockBaseCell.m
//  AFNetworking
//
//  Created by didi on 2019/11/15.
//

#import "DoraemonMockBaseCell.h"

@interface DoraemonMockBaseCell()<DoraemonSwitchViewDelegate>

@end

@implementation DoraemonMockBaseCell

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
    }
    return self;
}

- (void)renderCellWithData:(DoraemonMockBaseModel *)model{
    _model = model;
    self.backgroundColor = [UIColor doraemon_bg];
    
    [_detailSwitch renderUIWithTitle:model.name switchOn:model.selected];
    [_detailSwitch setSwitchFrame];
    [_detailSwitch setArrowDown:model.expand];
    
    if(!model||!model.expand){
        _infoLabel.hidden = YES;
        return ;
    }else{
        _infoLabel.hidden = NO;
        _infoLabel.text = model.info;
        CGSize size = [_infoLabel sizeThatFits:CGSizeMake(DoraemonScreenWidth-kDoraemonSizeFrom750_Landscape(32)*2, CGFLOAT_MAX)];
        _infoLabel.frame = CGRectMake(kDoraemonSizeFrom750_Landscape(32), _detailSwitch.doraemon_bottom+kDoraemonSizeFrom750_Landscape(24), size.width, size.height);
    }
}

+ (CGFloat)cellHeightWith:(DoraemonMockBaseModel *)model{
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
        
    }
    return cellHeight;
}

- (void)expandClick{
    _model.expand = !_model.expand;
    if (_delegate && [_delegate respondsToSelector:@selector(cellExpandClick)]) {
        [_delegate cellExpandClick];
    }
}


#pragma mark -- DoraemonSwitchViewDelegate
- (void)changeSwitchOn:(BOOL)on sender:(id)sender{
}

@end
