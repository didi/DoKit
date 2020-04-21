//
//  DoraemonHierarchyCategoryModel.m
//  DoraemonKit-DoraemonKit
//
//  Created by lijiahuan on 2019/11/2.
//

#import "DoraemonHierarchyCategoryModel.h"

@implementation DoraemonHierarchyCategoryModel

- (instancetype)initWithTitle:(NSString *)title items:(NSArray <DoraemonHierarchyCellModel *>*)items {
    if (self = [super init]) {
        _title = title;
        _items = [items copy];
    }
    return self;
}

@end
