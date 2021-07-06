#import <DoraemonKit/DKHierarchyPickerView.h>
#import <DoraemonKit/DoraemonHierarchyHelper.h>

NS_ASSUME_NONNULL_BEGIN

@interface DKHierarchyPickerView ()

- (nullable NSArray<UIView *> *)viewForSelectionAtPoint:(CGPoint)tapPointInWindow;

@end

NS_ASSUME_NONNULL_END

@implementation DKHierarchyPickerView

#pragma mark - Over write

- (void)viewDidUpdateOffset:(UIPanGestureRecognizer *)sender offset:(CGPoint)offsetPoint {
    NSArray <UIView *> *views = [self viewForSelectionAtPoint:self.center];
    [self.delegate hierarchyView:self didMoveTo:views];
}

#pragma mark - Primary

- (nullable NSArray<UIView *> *)viewForSelectionAtPoint:(CGPoint)tapPointInWindow {
    // Select in the window that would handle the touch, but don't just use the result of hitTest:withEvent: so we can still select views with interaction disabled.
    // Default to the the application's key window if none of the windows want the touch.
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
    UIWindow *windowForSelection = [[UIApplication sharedApplication] keyWindow];
#pragma clang diagnostic pop
    for (UIWindow *window in [[[DoraemonHierarchyHelper shared] allWindowsIgnorePrefix:@"Doraemon"] reverseObjectEnumerator]) {
        if ([window hitTest:tapPointInWindow withEvent:nil]) {
            windowForSelection = window;
            break;
        }
    }

    // Select the deepest visible view at the tap point. This generally corresponds to what the user wants to select.
    return [self recursiveSubviewsAtPoint:tapPointInWindow inView:windowForSelection skipHiddenViews:YES];
}

- (NSArray<UIView *> *)recursiveSubviewsAtPoint:(CGPoint)pointInView inView:(UIView *)view skipHiddenViews:(BOOL)skipHidden {
    NSMutableArray<UIView *> *subviewsAtPoint = [NSMutableArray array];
    NSEnumerator<__kindof UIView *> *enumerator = [view.subviews reverseObjectEnumerator];
    UIView *subview = nil;
    while ((subview = enumerator.nextObject)) {
        BOOL isHidden = subview.hidden || subview.alpha < 0.01;
        if (skipHidden && isHidden) {
            continue;
        }
        
        BOOL subviewContainsPoint = CGRectContainsPoint(subview.frame, pointInView);
        if (subviewContainsPoint) {
            [subviewsAtPoint addObject:subview];
        }
        
        // If this view doesn't clip to its bounds, we need to check its subviews even if it doesn't contain the selection point.
        // They may be visible and contain the selection point.
        if (subviewContainsPoint || !subview.clipsToBounds) {
            CGPoint pointInSubview = [view convertPoint:pointInView toView:subview];
            [subviewsAtPoint addObjectsFromArray:[self recursiveSubviewsAtPoint:pointInSubview inView:subview skipHiddenViews:skipHidden]];
            break;
        }
    }
    
    return subviewsAtPoint;
}

@end
