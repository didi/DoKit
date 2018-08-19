//
//  DoraemonLoggerConsoleTipView.m
//  CocoaLumberjack
//
//  Created by yixiang on 2017/12/27.
//

#import "DoraemonLoggerConsoleTipView.h"

@interface DoraemonLoggerConsoleTipView()

@property (nonatomic, strong) UILabel *tipLabel;

@end

@implementation DoraemonLoggerConsoleTipView

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        _tipLabel = [[UILabel alloc] initWithFrame:self.bounds];
        _tipLabel.font = [UIFont systemFontOfSize:12];
        _tipLabel.textColor = [UIColor blackColor];
        [self addSubview:_tipLabel];
    }
    return self;
}

- (void)showCurrentLog:(NSString *)log{
    _tipLabel.text = log;
}

- (void)showCurrentLogColor:(UIColor *)logColor{
    _tipLabel.textColor = logColor;
}

@end
