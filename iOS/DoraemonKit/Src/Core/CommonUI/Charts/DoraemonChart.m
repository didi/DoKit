//
//  Chart.m
//  DoraemonKit
//
//  Created by 0xd on 2019/9/11.
//  Copyright © 2019 000. All rights reserved.
//

#import "DoraemonChart.h"

@implementation DoraemonChart

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.vauleFormatter = [[NSNumberFormatter alloc] init];
        self.vauleFormatter.maximumFractionDigits = 2;
        self.contentInset = UIEdgeInsetsMake(20, 30, 20, 20);
        self.titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(10, 0, frame.size.width - 20, 30)];
        self.backgroundColor = [UIColor whiteColor];
        [self addSubview:self.titleLabel];
    }
    return self;
}

- (void)display {}


@end
