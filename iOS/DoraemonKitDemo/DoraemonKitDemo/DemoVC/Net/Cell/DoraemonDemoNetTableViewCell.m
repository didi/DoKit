//
//  DoraemonDemoNetTableViewCell.m
//  DoraemonKitDemo
//
//  Created by didi on 2019/12/10.
//  Copyright © 2019 yixiang. All rights reserved.
//

#import "DoraemonDemoNetTableViewCell.h"
#import "DoraemonDefine.h"

@interface DoraemonDemoNetTableViewCell()

@property (nonatomic, strong) UIButton *button;

@end

@implementation DoraemonDemoNetTableViewCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(nullable NSString *)reuseIdentifier{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if(self){
        _button = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, DoraemonScreenWidth, kDoraemonSizeFrom750_Landscape(104))];
        _button.backgroundColor = [UIColor orangeColor];
        _button.userInteractionEnabled = NO;
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        [self addSubview:_button];
    }
    return self;
}

- (void)renderUIWithTitle:(NSString *)title{
    if(!title)
        title = @"Button";
    [_button setTitle:title forState:UIControlStateNormal];
}


@end
