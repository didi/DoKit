//
//  DoraemonMoveView.h
//  DoraemonKit-DoraemonKit
//
//  Created by lijiahuan on 2019/11/2.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonMoveView : UIView

@property (nonatomic, assign, getter=isOverflow) BOOL overflow;

@property (nonatomic, assign, readonly, getter=isMoved) BOOL moved;

@property (nonatomic, assign, getter=isMoveable) BOOL moveable;

/// Moveable range.
@property (nonatomic, assign) CGRect moveableRect;

- (void)initUI;

- (void)viewWillUpdateOffset:(UIPanGestureRecognizer *)sender offset:(CGPoint)offsetPoint;

- (void)viewDidUpdateOffset:(UIPanGestureRecognizer *)sender offset:(CGPoint)offsetPoint;

@end

NS_ASSUME_NONNULL_END
