//
//  DoraemonCopyLabel.m
//  DoraemonKit
//
//  Created by didi on 2020/2/26.
//

#import "DoraemonCopyLabel.h"

@implementation DoraemonCopyLabel

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self pressAction];
    }
    return self;
}


- (void)pressAction {
    self.userInteractionEnabled = YES;
    UILongPressGestureRecognizer *longPress = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(longPressAction:)];
    longPress.minimumPressDuration = 1;
    [self addGestureRecognizer:longPress];
}

// 使label能够成为响应事件
- (BOOL)canBecomeFirstResponder {
     return YES;
 }

// 控制响应的方法
- (BOOL)canPerformAction:(SEL)action withSender:(id)sender {
     return action == @selector(customCopy:);
}

- (void)customCopy:(id)sender {
  UIPasteboard *pasteboard = [UIPasteboard generalPasteboard];
  pasteboard.string = self.text;
}

- (void)longPressAction:(UIGestureRecognizer *)recognizer {
    [self becomeFirstResponder];
    UIMenuItem *copyItem = [[UIMenuItem alloc] initWithTitle:@"copy" action:@selector(customCopy:)];
    
    [[UIMenuController sharedMenuController] setMenuItems:[NSArray arrayWithObjects:copyItem, nil]];
    [[UIMenuController sharedMenuController] setTargetRect:CGRectMake(0, 0, 100, 20) inView:self];
    [[UIMenuController sharedMenuController] setMenuVisible:YES animated:YES];
}

@end
