//
//  DoraemonCocoaLumberjackLevelView.m
//  AFNetworking
//
//  Created by yixiang on 2018/12/6.
//

#import "DoraemonCocoaLumberjackLevelView.h"
#import "DoraemonDefine.h"

@interface DoraemonCocoaLumberjackLevelView()

@property (nonatomic, strong) UISegmentedControl *segment;

@end

@implementation DoraemonCocoaLumberjackLevelView

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        NSArray *dataArray = @[@"Verbose",@"Debug",@"Info",@"Warn",@"Error"];
        _segment = [[UISegmentedControl alloc] initWithItems:dataArray];
        _segment.frame = CGRectMake(kDoraemonSizeFrom750(32), self.doraemon_height/2-kDoraemonSizeFrom750(68)/2, self.doraemon_width-kDoraemonSizeFrom750(32)*2, kDoraemonSizeFrom750(68));
        _segment.tintColor = [UIColor doraemon_colorWithString:@"#337CC4"];
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
