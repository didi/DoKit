#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface DKMoveView : UIView

@property (nonatomic, assign, getter=isOverflow) BOOL overflow;

// assign
@property (nonatomic, readonly, getter=isMoved) BOOL moved;

@property (nonatomic, getter=isMovable) BOOL movable;

@property (nonatomic, assign) CGRect movableRect;

- (instancetype)initWithFrame:(CGRect)frame NS_DESIGNATED_INITIALIZER;

- (nullable instancetype)initWithCoder:(NSCoder *)coder NS_DESIGNATED_INITIALIZER;

- (void)viewWillUpdateOffset:(UIPanGestureRecognizer *)sender offset:(CGPoint)offsetPoint;

- (void)viewDidUpdateOffset:(UIPanGestureRecognizer *)sender offset:(CGPoint)offsetPoint;

@end

NS_ASSUME_NONNULL_END
