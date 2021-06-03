#import <DoraemonKit/DKPickerView.h>

NS_ASSUME_NONNULL_BEGIN

@class DKHierarchyPickerView;

@protocol DKHierarchyViewDelegate <NSObject>

- (void)hierarchyView:(DKHierarchyPickerView *)view didMoveTo:(nullable NSArray <UIView *> *)selectedViews;

@end

@interface DKHierarchyPickerView : DKPickerView

@property (nonatomic, weak, nullable) id <DKHierarchyViewDelegate> delegate;

@end

NS_ASSUME_NONNULL_END
