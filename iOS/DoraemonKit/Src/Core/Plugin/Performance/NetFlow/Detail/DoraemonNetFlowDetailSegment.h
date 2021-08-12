//
//  DoraemonNetFlowDetailSegment.h
//  DoraemonKit
//
//  Created by yixiang on 2018/12/9.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@protocol DoraemonNetFlowDetailSegmentDelegate <NSObject>

- (void)segmentClick:(NSInteger)index;

@end

@interface DoraemonNetFlowDetailSegment : UIView

@property (nonatomic, weak) id<DoraemonNetFlowDetailSegmentDelegate> delegate;

@end

NS_ASSUME_NONNULL_END
