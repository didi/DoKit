//
//  DoraemonHealthStartingTitle.m
//  AFNetworking
//
//  Created by didi on 2020/1/2.
//

#import "DoraemonHealthStartingTitle.h"
#import "DoraemonDefine.h"

@interface DoraemonHealthStartingTitle()

@property (nonatomic, strong) UILabel *title;

@end

@implementation DoraemonHealthStartingTitle

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        _title = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, self.doraemon_width, self.doraemon_height)];
        _title.textColor = [UIColor doraemon_colorWithString:@"#27BCB7"];
        _title.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(28)];
        _title.textAlignment = NSTextAlignmentCenter;
        [self addSubview:_title];
    }
    return self;
}

- (void)renderUIWithTitle:(NSString *)title{
    if(title){
        _title.text = title;
    }
}

- (void)showUITitle:(BOOL)show{
    if(show){
        _title.hidden = NO;
    }else{
        _title.hidden = YES;
    }
}


@end
