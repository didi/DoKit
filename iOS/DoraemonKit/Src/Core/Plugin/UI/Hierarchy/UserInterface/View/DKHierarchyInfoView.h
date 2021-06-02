#import <DoraemonKit/DKMoveView.h>

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSUInteger, DKHierarchyInfoViewAction) {
    DKHierarchyInfoViewActionShowParent,
    DKHierarchyInfoViewActionShowSubview,
    DKHierarchyInfoViewActionShowMoreInfo
};

@class DKHierarchyInfoView;

@protocol DKHierarchyInfoViewDelegate <NSObject>

- (void)hierarchyInfoView:(DKHierarchyInfoView *)view didSelectAt:(DKHierarchyInfoViewAction)action;

- (void)hierarchyInfoViewDidSelectCloseButton:(DKHierarchyInfoView *)view;

@end

@interface DKHierarchyInfoView : DKMoveView

@property (nonatomic, weak, nullable) id <DKHierarchyInfoViewDelegate> delegate;

@property (nonatomic, strong, nullable, readonly) UIView *selectedView;

@property (nonatomic, strong, readonly) UIButton *closeButton;

- (void)updateSelectedView:(UIView *)view;

- (instancetype)initWithFrame:(CGRect)frame NS_DESIGNATED_INITIALIZER;

- (nullable instancetype)initWithCoder:(NSCoder *)coder NS_DESIGNATED_INITIALIZER;

@end

NS_ASSUME_NONNULL_END
