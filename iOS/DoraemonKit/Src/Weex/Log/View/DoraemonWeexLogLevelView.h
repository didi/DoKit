//
//  DoraemonWeexLogLevelView.h
//  DoraemonKit
//
//  Created by yixiang on 2019/5/23.
//  Copyright © 2019年 Chameleon-Team. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol DoraemonWeexLogLevelViewDelegate<NSObject>

- (void)segmentSelected:(NSInteger)index;

@end

@interface DoraemonWeexLogLevelView : UIView

@property (nonatomic, weak) id<DoraemonWeexLogLevelViewDelegate> delegate;

@end

