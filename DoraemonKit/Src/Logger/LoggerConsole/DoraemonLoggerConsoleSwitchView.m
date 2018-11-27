//
//  DoraemonLoggerConsoleSwitchView.m
//  CocoaLumberjack
//
//  Created by yixiang on 2017/12/27.
//

#import "DoraemonLoggerConsoleSwitchView.h"
#import "UIView+DoraemonPositioning.h"

@interface DoraemonLoggerConsoleSwitchView()

@property (nonatomic, strong) UISegmentedControl *segment;

@end

@implementation DoraemonLoggerConsoleSwitchView

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        NSArray *dataArray = @[@"Verbose",@"Debug",@"Info",@"Warn",@"Error"];
        _segment = [[UISegmentedControl alloc] initWithItems:dataArray];
        _segment.frame = CGRectMake(20, self.doraemon_height/2-15, self.doraemon_width-40, 30);
        _segment.tintColor = [UIColor orangeColor];
        [_segment setSelectedSegmentIndex:0];
        [_segment addTarget:self action:@selector(segmentChange:) forControlEvents:UIControlEventValueChanged];
        [self addSubview:_segment];
    }
    return self;
}

-(void)segmentChange:(UISegmentedControl *)sender{
    NSInteger index = sender.selectedSegmentIndex;
    if (self.delegate && [self.delegate respondsToSelector:@selector(segmentSelected:)]) {
        [self.delegate segmentSelected:index];
    }
}

@end
