//
//  DoraemonHierarchyCategoryModel.h
//  DoraemonKit-DoraemonKit
//
//  Created by lijiahuan on 2019/11/2.
//

#import <Foundation/Foundation.h>

@class DoraemonHierarchyCellModel;

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonHierarchyCategoryModel : NSObject

@property (nonatomic, strong, readonly, nullable) NSString *title;

@property (nonatomic, strong, readonly) NSArray <DoraemonHierarchyCellModel *>*items;

- (instancetype)initWithTitle:(NSString *_Nullable)title items:(NSArray <DoraemonHierarchyCellModel *>*)items;

@end

NS_ASSUME_NONNULL_END
