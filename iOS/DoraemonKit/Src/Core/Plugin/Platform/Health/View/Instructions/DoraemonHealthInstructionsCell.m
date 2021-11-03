//
//  DoraemonHealthInstructionsCell.m
//  DoraemonKit
//
//  Created by didi on 2020/1/2.
//

#import "DoraemonHealthInstructionsCell.h"
#import "DoraemonDefine.h"

@interface DoraemonHealthInstructionsCell()

@property (nonatomic, strong) UILabel *title;
@property (nonatomic, strong) UIImageView *bgImg;
@property (nonatomic, strong) UILabel *itemLable;
@property (nonatomic, assign) CGFloat padding;
@property (nonatomic, strong) NSMutableDictionary *attributes;

@end

@implementation DoraemonHealthInstructionsCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(nullable NSString *)reuseIdentifier{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if(self){
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        _padding = kDoraemonSizeFrom750_Landscape(32);
        _bgImg = [[UIImageView alloc] initWithFrame:CGRectMake(_padding, 0, kDoraemonSizeFrom750_Landscape(108), kDoraemonSizeFrom750_Landscape(48))];
        [_bgImg setImage:[UIImage doraemon_xcassetImageNamed:@"doraemon_health_cell_bg"]];
        
        _title = [[UILabel alloc] initWithFrame:CGRectMake(_padding, _bgImg.doraemon_top + _bgImg.doraemon_height/8, _bgImg.doraemon_width, _padding)];
        _title.textAlignment = NSTextAlignmentCenter;
        _title.textColor = [UIColor whiteColor];
        _title.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(28)];
        
        _itemLable = [[UILabel alloc] initWithFrame:CGRectMake(_padding, _bgImg.doraemon_bottom + _padding/2, DoraemonScreenWidth - _padding*2, _padding)];
        _itemLable.numberOfLines = 0;
        _itemLable.textColor = [UIColor doraemon_black_1];
        _itemLable.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(28)];
        
        NSMutableParagraphStyle *paragraphStyle = [NSMutableParagraphStyle new];
        paragraphStyle.lineSpacing = kDoraemonSizeFrom750_Landscape(8);
        paragraphStyle.alignment = NSTextAlignmentLeft;
        _attributes = [NSMutableDictionary dictionary];
        [_attributes setObject:paragraphStyle forKey:NSParagraphStyleAttributeName];
        
        [self addSubview:_bgImg];
        [self addSubview:_title];
        [self addSubview:_itemLable];
    }
    return self;
}

- (CGFloat)renderUIWithTitle:(NSString *)title item:(NSString *)itemLable{
    
    _title.text = title;
    _itemLable.attributedText = [[NSAttributedString alloc] initWithString:itemLable attributes:_attributes];
    [_itemLable sizeToFit];
    _itemLable.frame = CGRectMake(_itemLable.doraemon_left, _itemLable.doraemon_top, _itemLable.doraemon_width, _itemLable.doraemon_height);
    return _itemLable.doraemon_bottom + _padding*2;
}

@end
