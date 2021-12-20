//
//  DoraemonWeexStorageCell.m
//  DoraemonKit
//
//  Created by yixiang on 2019/5/30.
//  Copyright © 2019年 taobao. All rights reserved.
//

#import "DoraemonWeexStorageCell.h"

@implementation DoraemonWeexStorageCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        _rowView = [[DoraemonWeexStorageRowView alloc] init];
        [self.contentView addSubview:_rowView];
    }
    return self;
}

- (void)layoutSubviews{
    [super layoutSubviews];
    _rowView.frame = self.contentView.bounds;
}

- (void)renderCellWithArray:(NSArray *)array{
    _rowView.dataArray = array;
}

@end
