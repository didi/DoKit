//
//  DoraemonMoveView.m
//  DoraemonKit-DoraemonKit
//
//  Created by lijiahuan on 2019/11/2.
//

#import "DoraemonMoveView.h"
#import "DoraemonDefine.h"
#import "UIView+Doraemon.h"

@interface DoraemonMoveView ()

@property (nonatomic, assign) BOOL moved;

@property (nonatomic, strong) UIPanGestureRecognizer *panGestureRecognizer;

@end

@implementation DoraemonMoveView

- (instancetype)init {
    if (self = [super init]) {
        [self initUI];
    }
    return self;
}

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self initUI];
    }
    return self;
}

- (void)initUI {
    _moveable = YES;
    _moveableRect = CGRectNull;
    // Pan, to moveable.
    self.panGestureRecognizer = [[UIPanGestureRecognizer alloc]initWithTarget:self action:@selector(panGR:)];
    
    [self addGestureRecognizer:self.panGestureRecognizer];
}

- (void)panGR:(UIPanGestureRecognizer *)sender {
    
    if (!self.isMoved) {
        self.moved = YES;
    }

    CGPoint offsetPoint = [sender translationInView:sender.view];
    
    [self viewWillUpdateOffset:sender offset:offsetPoint];
    
    [sender setTranslation:CGPointZero inView:sender.view];
    
    [self changeFrameWithPoint:offsetPoint];
    
    [self viewDidUpdateOffset:sender offset:offsetPoint];
}

- (void)changeFrameWithPoint:(CGPoint)point {
    
    CGPoint center = self.center;
    center.x += point.x;
    center.y += point.y;
    
    if (self.isOverflow) {
        center.x = MIN(center.x, self.superview.doraemon_width);
        center.x = MAX(center.x, 0);
        
        center.y = MIN(center.y, self.superview.doraemon_height);
        center.y = MAX(center.y, 0);
    } else {
        
        if (center.x < self.doraemon_width / 2.0) {
            center.x = self.doraemon_width / 2.0;
        } else if (center.x > self.superview.doraemon_width - self.doraemon_width / 2.0) {
            center.x = self.superview.doraemon_width - self.doraemon_width / 2.0;
        }
        
        if (center.y < self.doraemon_height / 2.0) {
            center.y = self.doraemon_height / 2.0;
        } else if (center.y > self.superview.doraemon_height - self.doraemon_height / 2.0) {
            center.y = self.superview.doraemon_height - self.doraemon_height / 2.0;
        }
    }
    
    if (!CGRectIsNull(_moveableRect)) {
        if (!CGRectContainsPoint(_moveableRect, center)) {
            if (center.x < _moveableRect.origin.x) {
                center.x = _moveableRect.origin.x;
            } else if (center.x > _moveableRect.origin.x + _moveableRect.size.width) {
                center.x = _moveableRect.origin.x + _moveableRect.size.width;
            }
            if (center.y < _moveableRect.origin.y) {
                center.y = _moveableRect.origin.y;
            } else if (center.y > _moveableRect.origin.y + _moveableRect.size.height) {
                center.y = _moveableRect.origin.y + _moveableRect.size.height;
            }
        }
    }

    self.center = center;
}

- (void)viewWillUpdateOffset:(UIPanGestureRecognizer *)sender offset:(CGPoint)offsetPoint {
    
}

- (void)viewDidUpdateOffset:(UIPanGestureRecognizer *)sender offset:(CGPoint)offsetPoint {
    
}

#pragma mark - Primary
- (void)setMoveable:(BOOL)moveable {
    if (_moveable != moveable) {
        _moveable = moveable;
        self.panGestureRecognizer.enabled = moveable;
    }
}

@end
