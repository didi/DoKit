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
#import "DoraemonMockDetailButton.h"

@interface DoraemonMockDetailCell()<DoraemonSwitchViewDelegate,DoraemonMockDetailButtonDelegate>

@property (nonatomic, strong) UILabel *infoLabel;
@property (nonatomic, strong) DoraemonMockDetailSwitch *detailSwitch;
@property (nonatomic, strong) NSMutableDictionary *attributes;
@property (nonatomic, strong) NSMutableParagraphStyle *style;
@property (nonatomic, assign) CGFloat padding;
@property (nonatomic, strong) DoraemonMockDetailButton *leftButton;
@property (nonatomic, strong) DoraemonMockDetailButton *rightButton;

@end

@implementation DoraemonMockDetailCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        _detailSwitch = [[DoraemonMockDetailSwitch alloc] initWithFrame:CGRectMake(0, 0, DoraemonScreenWidth, kDoraemonSizeFrom750_Landscape(104))];
        [_detailSwitch needDownLine];
        [_detailSwitch needRightArrow];
        _detailSwitch.delegate = self;
        [self.contentView addSubview:_detailSwitch];

        _infoLabel = [[UILabel alloc] init];
        _infoLabel.textColor = [UIColor doraemon_black_1];
        _infoLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(24)];
        [self.contentView addSubview:_infoLabel];
        
        _style = [NSMutableParagraphStyle new];
        _style.lineBreakMode = NSLineBreakByTruncatingTail;
        _style.lineSpacing = kDoraemonSizeFrom750_Landscape(12);
        
        _attributes = [NSMutableDictionary dictionary];
        [_attributes setObject:_style forKey:NSParagraphStyleAttributeName];
        _padding = kDoraemonSizeFrom750_Landscape(32);
        
        _leftButton = [[DoraemonMockDetailButton alloc] init];
        _leftButton.delegate = self;
        [self addSubview:_leftButton];
        _rightButton = [[DoraemonMockDetailButton alloc] init];
        _rightButton.delegate = self;
        [self addSubview:_rightButton];
    }
    return self;
}



- (void)renderCellWithData:(DoraemonMockDetailModel *)model index:(NSInteger)index{
    
    //model demo 假数据
    NSInteger line = 3;
    NSString *log = @"xsaxs1212121";
    NSTimeInterval timeInterval = model.timeInterval;
    NSString *time = [DoraemonUtil dateFormatTimeInterval:timeInterval];
    NSString *content = [NSString stringWithFormat:DoraemonLocalizedString(@"熟撒%@\n下刷或许是%@\n触发时间: %@潇洒些"),time,log,time];
    
    //按钮
    NSString *title = @"圣诞节卡潇洒下";
    NSString *title_right = @"诞节潇洒";
    NSString *switchTitle = @"接口1";
    //model demo 假数据
    
    self.backgroundColor = [UIColor doraemon_bg];
    [_detailSwitch renderUIWithTitle:switchTitle switchOn:YES];
    [_detailSwitch setSwitchFrame];
    [_detailSwitch setArrowDown:model.expand];
    _detailSwitch.tag = index;//方便函数manager处理
    
    _rightButton.hidden = !model.expand;
    _leftButton.hidden = !model.expand;
    
    if(!model||!model.expand){
        _infoLabel.frame = CGRectMake(0, _detailSwitch.doraemon_bottom, self.doraemon_width,0);
        return ;
    }
    
    _infoLabel.numberOfLines = line + 2;
    _infoLabel.text = content;
    _infoLabel.attributedText = [[NSAttributedString alloc] initWithString:_infoLabel.text attributes:_attributes];
    _infoLabel.frame = CGRectMake(_padding, _detailSwitch.doraemon_bottom, self.doraemon_width-_padding*2, _padding* _infoLabel.numberOfLines);
    
    
    CGFloat titleWith = title.length * kDoraemonSizeFrom750_Landscape(40);
    if(title.length>4){
        titleWith = kDoraemonSizeFrom750_Landscape(160);
    }
        
    _leftButton.frame = CGRectMake(_padding, _infoLabel.doraemon_bottom + _padding/2 ,titleWith , kDoraemonSizeFrom750_Landscape(32));
    
    
    [_leftButton needImage];
    [_leftButton renderTitle:title isSelected:YES];
    
    if(title_right.length>4){
        titleWith = kDoraemonSizeFrom750_Landscape(160);
    }
        
    _rightButton.frame = CGRectMake(_leftButton.doraemon_bottom + _padding, _infoLabel.doraemon_bottom + _padding/2 ,titleWith , kDoraemonSizeFrom750_Landscape(32));
    
    [_rightButton needImage];
    [_rightButton renderTitle:title_right isSelected:NO];
    
    _leftButton.tag = index;
    _rightButton.tag = index;

}


+ (CGFloat)cellHeightWith:(DoraemonMockDetailModel *)model{
    CGFloat cellHeight = kDoraemonSizeFrom750_Landscape(104);
    if (model && model.expand) {
        CGFloat numberOfLines = 3+2;
        cellHeight +=  kDoraemonSizeFrom750_Landscape(32)*numberOfLines;
        cellHeight += kDoraemonSizeFrom750_Landscape(104);
    }
    return cellHeight;
}



#pragma mark --DoraemonMockDetailButtonDelegate

- (void)detailBtnClick:(id)sender{
    if(sender==_leftButton){
        
    }
    else{
        //_rightButton
        
    }
}


#pragma mark -- DoraemonSwitchViewDelegate

- (void)changeSwitchOn:(BOOL)on sender:(id)sender{
    //设置manager的array的index== tag的置为相应的on
    
}

@end
