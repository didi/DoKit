//
//  DoraemonHierarchyPickerView.h
//  DoraemonKit-DoraemonKit
//
//  Created by lijiahuan on 2019/11/2.
//

#import "DoraemonPickerView.h"

NS_ASSUME_NONNULL_BEGIN

@class DoraemonHierarchyPickerView;

@protocol DoraemonHierarchyViewDelegate <NSObject>

- (void)doraemonHierarchyView:(DoraemonHierarchyPickerView *)view didMoveTo:(NSArray <UIView *>*)selectedViews;

@end

@interface DoraemonHierarchyPickerView : DoraemonPickerView

@property (nonatomic, weak, nullable) id<DoraemonHierarchyViewDelegate> delegate;

@end

NS_ASSUME_NONNULL_END
