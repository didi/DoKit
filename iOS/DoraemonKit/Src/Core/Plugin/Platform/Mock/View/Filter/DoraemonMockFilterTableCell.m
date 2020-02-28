//
//  DoraemonMockFilterTableCell.m
//  AFNetworking
//
//  Created by didi on 2019/10/25.
//

#import "DoraemonMockFilterTableCell.h"
#import "DoraemonDefine.h"

@interface DoraemonMockFilterTableCell()

@property (nonatomic, strong) UILabel *itemTitle;

@end

@implementation DoraemonMockFilterTableCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(nullable NSString *)reuseIdentifier{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if(self){
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        _itemTitle = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, DoraemonScreenWidth, self.doraemon_height)];
        _itemTitle.textAlignment = NSTextAlignmentCenter;
        _itemTitle.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(32)];
        [self addSubview:_itemTitle];
    }
    return self;
}

- (void)renderUIWithTitle:(NSString *)title{
    _itemTitle.text = title;
    _itemTitle.textColor = [UIColor doraemon_black_1];
}

- (void)selectedColor:(BOOL)selected{
    if(selected)
        _itemTitle.textColor = [UIColor doraemon_blue];
    else
        _itemTitle.textColor = [UIColor doraemon_black_1];
}

@end
