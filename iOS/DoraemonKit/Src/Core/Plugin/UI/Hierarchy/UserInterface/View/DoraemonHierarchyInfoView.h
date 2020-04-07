//
//  DoraemonHierarchyInfoView.h
//  DoraemonKit-DoraemonKit
//
//  Created by lijiahuan on 2019/11/2.
//

#import "DoraemonMoveView.h"

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSUInteger, DoraemonHierarchyInfoViewAction) {
    DoraemonHierarchyInfoViewActionShowParent,
    DoraemonHierarchyInfoViewActionShowSubview,
    DoraemonHierarchyInfoViewActionShowMoreInfo
};

@class DoraemonHierarchyInfoView;

@protocol DoraemonHierarchyInfoViewDelegate <NSObject>

- (void)doraemonHierarchyInfoView:(DoraemonHierarchyInfoView *)view didSelectAt:(DoraemonHierarchyInfoViewAction)action;

- (void)doraemonHierarchyInfoViewDidSelectCloseButton:(DoraemonHierarchyInfoView *)view;

@end

@interface DoraemonHierarchyInfoView : DoraemonMoveView

@property (nonatomic, weak, nullable) id<DoraemonHierarchyInfoViewDelegate> delegate;

@property (nonatomic, strong, nullable, readonly) UIView *selectedView;

@property (nonatomic, strong, readonly) UIButton *closeButton;

- (void)updateSelectedView:(UIView *)view;

@end

NS_ASSUME_NONNULL_END
