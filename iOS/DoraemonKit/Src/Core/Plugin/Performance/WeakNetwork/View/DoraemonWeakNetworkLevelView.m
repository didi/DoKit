//
//  DoraemonWeakNetworkLevelView.m
//  DoraemonKit
//
//  Created by didi on 2019/12/16.
//

#import "DoraemonWeakNetworkLevelView.h"
#import "DoraemonDefine.h"

@interface DoraemonWeakNetworkLevelView()

@property (nonatomic, strong) UISegmentedControl *segment;

@end

@implementation DoraemonWeakNetworkLevelView

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        //NSArray *dataArray = @[@"Verbose",@"Debug",@"Info"];
        _segment = [[UISegmentedControl alloc] init];
        _segment.frame = CGRectMake(kDoraemonSizeFrom750_Landscape(68), self.doraemon_height/2-kDoraemonSizeFrom750_Landscape(68)/2, self.doraemon_width-kDoraemonSizeFrom750_Landscape(68)*2, kDoraemonSizeFrom750_Landscape(68));
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
        if (@available(iOS 13, *)) {
           _segment.selectedSegmentTintColor = [UIColor doraemon_colorWithString:@"#337CC4"];
        } else {
#endif
            _segment.tintColor = [UIColor doraemon_colorWithString:@"#337CC4"];
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
        }
#endif
        [_segment addTarget:self action:@selector(segmentChange:) forControlEvents:UIControlEventValueChanged];
        UIFont *font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(28)];   // 设置字体大小
        NSDictionary *attributes = [NSDictionary dictionaryWithObject:font forKey:NSFontAttributeName];
        [_segment setTitleTextAttributes:attributes forState:UIControlStateNormal];
        [self addSubview:_segment];
    }
    return self;
}

-(void)renderUIWithItemArray:(NSArray *)itemArray selecte:(NSUInteger)selected{
    for (int i = 0; i<itemArray.count; i++) {
        [_segment insertSegmentWithTitle:DoraemonLocalizedString(itemArray[i]) atIndex:i animated:NO];
    }
    [_segment setSelectedSegmentIndex:selected];
}

-(void)segmentChange:(UISegmentedControl *)sender{
    NSInteger index = sender.selectedSegmentIndex;
    if (self.delegate && [self.delegate respondsToSelector:@selector(segmentSelected:)]) {
        [self.delegate segmentSelected:index];
    }
}


@end
