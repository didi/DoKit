#import <DoraemonKit/DKMoveView.h>

NS_ASSUME_NONNULL_BEGIN

@interface DKMoveView ()

@property (nonatomic, assign) BOOL moved;

@property (nonatomic, strong) UIPanGestureRecognizer *panGestureRecognizer;

- (void)moveViewInit;

- (void)handleGesture:(nullable UIGestureRecognizer *)gestureRecognizer;

- (void)changeFrameWithPoint:(CGPoint)point;

@end

NS_ASSUME_NONNULL_END

@implementation DKMoveView

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    [self moveViewInit];

    return self;
}

- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super initWithCoder:coder];
    if (self = [super initWithCoder:coder]) {
        [self moveViewInit];
    }

    return self;
}

- (void)moveViewInit {
    _movableRect = CGRectNull;
    _panGestureRecognizer = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(handleGesture:)];

    [self addGestureRecognizer:self.panGestureRecognizer];
}

- (void)handleGesture:(UIGestureRecognizer *)gestureRecognizer {
    if (gestureRecognizer != self.panGestureRecognizer) {
        return;
    }
    if (!self.isMoved) {
        self.moved = YES;
    }

    CGPoint offsetPoint = [self.panGestureRecognizer translationInView:gestureRecognizer.view];
    [self viewWillUpdateOffset:self.panGestureRecognizer offset:offsetPoint];
    [self.panGestureRecognizer setTranslation:CGPointZero inView:gestureRecognizer.view];
    [self changeFrameWithPoint:offsetPoint];
    [self viewDidUpdateOffset:self.panGestureRecognizer offset:offsetPoint];
}

- (void)changeFrameWithPoint:(CGPoint)point {
    CGPoint center = self.center;
    center.x += point.x;
    center.y += point.y;
    if (self.isOverflow) {
        center.x = MAX(MIN(center.x, self.superview.bounds.size.width), 0);
        center.y = MAX(MIN(center.y, self.superview.bounds.size.height), 0);
    } else {
        if (center.x < self.frame.size.width / 2) {
            center.x = self.frame.size.width / 2;
        } else if (center.x > self.superview.bounds.size.width - self.frame.size.width / 2) {
            center.x = self.superview.bounds.size.width - self.frame.size.width / 2;
        }
        if (center.y < self.frame.size.height / 2) {
            center.y = self.frame.size.height / 2;
        } else if (center.y > self.superview.bounds.size.height - self.frame.size.height / 2) {
            center.y = self.superview.bounds.size.height - self.frame.size.height / 2;
        }
    }

    if (!CGRectIsNull(self.movableRect)) {
        // movableRect 指的是以 DKMoveView 父视图作为坐标系
        if (!CGRectContainsPoint(self.movableRect, center)) {
            if (center.x < self.movableRect.origin.x) {
                center.x = self.movableRect.origin.x;
            } else if (center.x > self.movableRect.origin.x + self.movableRect.size.width) {
                center.x = self.movableRect.origin.x + self.movableRect.size.width;
            }
            if (center.y < self.movableRect.origin.y) {
                center.y = self.movableRect.origin.y;
            } else if (center.y > self.movableRect.origin.y + self.movableRect.size.height) {
                center.y = self.movableRect.origin.y + self.movableRect.size.height;
            }
        }
    }
    self.center = center;
}

- (void)viewWillUpdateOffset:(UIPanGestureRecognizer *)sender offset:(CGPoint)offsetPoint {

}

- (void)viewDidUpdateOffset:(UIPanGestureRecognizer *)sender offset:(CGPoint)offsetPoint {

}

- (void)setMovable:(BOOL)movable {
    self.panGestureRecognizer.enabled = movable;
}

- (BOOL)isMovable {
    return self.panGestureRecognizer.enabled;
}

@end
